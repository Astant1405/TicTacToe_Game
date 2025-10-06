package ru.school21.TicTacToe.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;
import ru.school21.TicTacToe.domain.model.Field;
import ru.school21.TicTacToe.domain.model.Game;
import ru.school21.TicTacToe.domain.model.Person;
import ru.school21.TicTacToe.domain.repository.GameRepository;

import ru.school21.TicTacToe.dto.LeaderBoardDTO;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicTacService {
    private final GameRepository gameRepository;
    private final PersonDetailsService personDetailsService;

    @Autowired
    public TicTacService(GameRepository gameRepository, PersonDetailsService personDetailsService) {
        this.gameRepository = gameRepository;
        this.personDetailsService = personDetailsService;
    }

    public Game createNewGame() {
        Field field = new Field();
        Game game = new Game(UUID.randomUUID(), field);
        gameRepository.save(game);
        return game;
    }

    public void saveGame(Game game) {
        gameRepository.save(game);
    }

    public Game getGame(UUID gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new ExpressionException("Game not found: " + gameId));
    }

    public List<Game> getCompleteGames(UUID personId) {
        List<Game> games = gameRepository.findAllCompleteGames();
        List<Game> filterGames = new ArrayList<>();
        for(Game game: games){
            if(game.getType().equals("offline")){
                if(game.getFirstPerson().getId() == personId){
                    filterGames.add(game);
                }
            }
            else{
                if(game.getFirstPerson().getId() == personId || game.getSecondPerson().getId() == personId){
                    filterGames.add(game);
                }
            }
        }
        return filterGames;
    }

    public List<LeaderBoardDTO> getLeaderBoard(int count) {
            List<LeaderBoardDTO> leaderBoard = gameRepository.findLeaderBoard();
            return leaderBoard.stream().limit(count).collect(Collectors.toList());
    }

    public void makeMoveAi(Game game, Field currentField) {
        int[] computerMove = minimax(currentField);
        if (computerMove != null) {
            currentField.setElement(computerMove[0], computerMove[1], -1);
            game.setField(currentField);
            int gameResult = isGameOver(currentField);
            if (gameResult == -1) {
                game.setStatus("Компьютер");
            } else if (gameResult == 2) {
                game.setStatus("Ничья");
            }
        }
    }

    public void makeMove(UUID gameId, int row, int col, Principal principal) throws Exception {
        Game game = getGame(gameId);
        Field currentField = game.getField();

        int gameResult = isGameOver(currentField);
        if (gameResult != 0) {
            throw new Exception("Игра уже завершена: " + game.getStatus());
        }

        Person currentPerson = personDetailsService.getCurrentPerson(principal);

        int playerSymbol;
        if (currentPerson.getId().equals(game.getFirstPerson().getId())) {
            playerSymbol = 1;
        } else if (game.getSecondPerson() != null && currentPerson.getId().equals(game.getSecondPerson().getId())) {
            playerSymbol = -1;
        } else {
            throw new Exception("Вы не участник этой игры");
        }

        int currentTurn = determineCurrentTurn(currentField);

        if (currentTurn != playerSymbol) {
            String expectedPlayer = currentTurn == 1 ? game.getFirstPerson().getUsername() :
                    (game.getSecondPerson() != null ? game.getSecondPerson().getUsername() : "Компьютер");
            throw new Exception("Сейчас не ваш ход! Ожидается ход: " + expectedPlayer);
        }

        if (currentField.getElement(row, col) != 0) {
            throw new Exception("Ячейка уже занята");
        }

        currentField.setElement(row, col, playerSymbol);
        game.setField(currentField);

        gameResult = isGameOver(currentField);
        if (gameResult == 1) {
            game.setStatus("Победил игрок " + game.getFirstPerson().getUsername());
        } else if (gameResult == -1) {
            if (game.getType().equals("online")) {
                game.setStatus("Победил игрок " + game.getSecondPerson().getUsername());
            } else {
                game.setStatus("Победил компьютер");
            }
        } else if (gameResult == 2) {
            game.setStatus("Ничья");
        } else {
            game.setStatus("В процессе");

            if (game.getType().equals("offline") && playerSymbol == 1) {
                makeMoveAi(game, currentField);
            }
        }

        saveGame(game);
    }

    public int determineCurrentTurn(int[][] board) {
        int xCount = 0;
        int oCount = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 1) xCount++;
                if (board[i][j] == -1) oCount++;
            }
        }
        return xCount <= oCount ? 1 : -1;
    }

    public int determineCurrentTurn(Field field) {
        return determineCurrentTurn(field.getField());
    }

    private Field copyField(Field original) {
        Field copy = new Field();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                copy.setElement(i, j, original.getElement(i, j));
            }
        }
        return copy;
    }

    public int[] minimax(Field originalField) {
        Field field = copyField(originalField);
        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field.getElement(i, j) == 0) {
                    field.setElement(i, j, -1);
                    int score = minimax(field, 0, false);
                    field.setElement(i, j, 0);

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{i, j};
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax(Field field, int depth, boolean isMaximizing) {
        int result = isGameOver(field);
        if (result != 0) {
            return evaluateScore(result, depth);
        }

        if (depth >= 5) {
            return 0;
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (field.getElement(i, j) == 0) {
                        field.setElement(i, j, -1);
                        int score = minimax(field, depth + 1, false);
                        field.setElement(i, j, 0);
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (field.getElement(i, j) == 0) {
                        field.setElement(i, j, 1);
                        int score = minimax(field, depth + 1, true);
                        field.setElement(i, j, 0);
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    private int evaluateScore(int gameResult, int depth) {
        if (gameResult == -1) {
            return 10 - depth;
        } else if (gameResult == 1) {
            return depth - 10;
        } else {
            return 0;
        }
    }

    public int isGameOver(Field field) {
        for (int i = 0; i < 3; i++) {
            if (field.getElement(i, 0) != 0 &&
                    field.getElement(i, 0) == field.getElement(i, 1) &&
                    field.getElement(i, 1) == field.getElement(i, 2)) {
                return field.getElement(i, 0);
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field.getElement(0, i) != 0 &&
                    field.getElement(0, i) == field.getElement(1, i) &&
                    field.getElement(1, i) == field.getElement(2, i)) {
                return field.getElement(0, i);
            }
        }

        if (field.getElement(0, 0) != 0 &&
                field.getElement(0, 0) == field.getElement(1, 1) &&
                field.getElement(1, 1) == field.getElement(2, 2)) {
            return field.getElement(0, 0);
        }

        if (field.getElement(0, 2) != 0 &&
                field.getElement(0, 2) == field.getElement(1, 1) &&
                field.getElement(1, 1) == field.getElement(2, 0)) {
            return field.getElement(0, 2);
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field.getElement(i, j) == 0) {
                    return 0;
                }
            }
        }
        return 2;
    }
}
