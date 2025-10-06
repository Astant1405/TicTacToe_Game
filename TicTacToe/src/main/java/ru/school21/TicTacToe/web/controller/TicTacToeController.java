package ru.school21.TicTacToe.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.school21.TicTacToe.domain.model.Game;
import ru.school21.TicTacToe.domain.model.Person;
import ru.school21.TicTacToe.domain.service.PersonDetailsService;
import ru.school21.TicTacToe.domain.service.TicTacService;
import ru.school21.TicTacToe.dto.JoinGameDTO;
import ru.school21.TicTacToe.dto.MoveRequestDTO;
import ru.school21.TicTacToe.util.JoinGameValidator;
import ru.school21.TicTacToe.util.MoveRequestValidator;
import ru.school21.TicTacToe.web.mapper.GameWebMapper;
import ru.school21.TicTacToe.web.model.GameView;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/game")
public class TicTacToeController {

    private final TicTacService ticTacService;
    private final GameWebMapper gameWebMapper;
    private final PersonDetailsService personDetailsService;
    private final MoveRequestValidator moveRequestValidator;
    private final JoinGameValidator joinGameValidator;

    @Autowired
    public TicTacToeController(TicTacService ticTacService, GameWebMapper gameWebMapper,
                               PersonDetailsService personDetailsService, MoveRequestValidator moveRequestValidator,
                               JoinGameValidator joinGameValidator) {
        this.ticTacService = ticTacService;
        this.gameWebMapper = gameWebMapper;
        this.personDetailsService = personDetailsService;
        this.moveRequestValidator = moveRequestValidator;
        this.joinGameValidator = joinGameValidator;
    }

    @PostMapping("/create/offline")
    public String createNewGameOffline(Principal principal) {
        Person currentPerson = personDetailsService.getCurrentPerson(principal);
        Game newGame = ticTacService.createNewGame();
        newGame.setFirstPerson(currentPerson);
        newGame.setTimeOfCreate(ZonedDateTime.now());
        newGame.setType("offline");
        newGame.setStatus("В процессе");
        ticTacService.saveGame(newGame);
        return "redirect:/game/" + newGame.getId();
    }

    @PostMapping("/create/online")
    public String createNewGameOnline(Principal principal) {
        Person currentPerson = personDetailsService.getCurrentPerson(principal);
        Game newGame = ticTacService.createNewGame();
        newGame.setFirstPerson(currentPerson);
        newGame.setTimeOfCreate(ZonedDateTime.now());
        newGame.setType("online");
        newGame.setStatus("в ожидании");
        ticTacService.saveGame(newGame);
        return "redirect:/game/" + newGame.getId();
    }

    @PostMapping("/join")
    public String joinGame(Model model) {
        model.addAttribute("joinGameDTO", new JoinGameDTO());
        return "join";
    }

    @PostMapping("/connect")
    public String connectGame(@ModelAttribute("joinGameDTO") JoinGameDTO joinGameDTO, BindingResult bindingResult,
                              Principal principal, Model model) {
        joinGameValidator.validate(joinGameDTO, bindingResult);
        if(bindingResult.hasErrors()) {
            model.addAttribute("joinGameDTO", joinGameDTO);
            return "join";
        }

        try {
            Person currentPerson = personDetailsService.getCurrentPerson(principal);
            UUID gameId = UUID.fromString(joinGameDTO.getGameId());
            Game game = ticTacService.getGame(gameId);

            if (game.getFirstPerson().getId().equals(currentPerson.getId())) {
                bindingResult.rejectValue("gameId", "", "Нельзя присоединиться к своей собственной игре");
                model.addAttribute("joinGameDTO", joinGameDTO);
                return "join";
            }

            game.setSecondPerson(currentPerson);
            game.setStatus("В процессе");
            ticTacService.saveGame(game);

            return "redirect:/game/" + gameId;
        } catch (Exception e) {
            return "join";
        }
    }

    @GetMapping("/home")
    public String gameHome() {
        return "home";
    }

    @GetMapping("/{gameId}")
    public String showGame(@PathVariable UUID gameId, Model model, Principal principal) {
        Game game = ticTacService.getGame(gameId);
        GameView gameView = gameWebMapper.toGameView(game);
        Person currentPerson = personDetailsService.getCurrentPerson(principal);
        UUID currentUserId = currentPerson.getId();

        if (!model.containsAttribute("moveRequest")) {
            model.addAttribute("moveRequest", new MoveRequestDTO());
        }

        model.addAttribute("game", gameView);
        model.addAttribute("gameId", gameId);
        model.addAttribute("currentUserId", currentUserId);

        if(!game.getStatus().isEmpty() && !game.getStatus().equals("В процессе") && !game.getStatus().equals("в ожидании")) {
            return "final";
        }

        return "game";
    }

    @PostMapping("/{gameId}/move")
    public String makeMove(@PathVariable UUID gameId, @ModelAttribute("moveRequest") MoveRequestDTO moveRequest,
                           BindingResult bindingResult, Principal principal, Model model) {
        moveRequest.setGameId(gameId);
        moveRequestValidator.validate(moveRequest, bindingResult);
        if(bindingResult.hasErrors()) {
            Game game = ticTacService.getGame(gameId);
            GameView gameView = gameWebMapper.toGameView(game);

            model.addAttribute("game", gameView);
            model.addAttribute("gameId", gameId);
            model.addAttribute("org.springframework.validation.BindingResult.moveRequest", bindingResult);
            return "redirect:/game/" + moveRequest.getGameId();
        }

        try {
            ticTacService.makeMove(moveRequest.getGameId(), moveRequest.getRow(), moveRequest.getCol(), principal);
            return "redirect:/game/" + moveRequest.getGameId();
        } catch (Exception e) {
            bindingResult.reject("move.error", e.getMessage());

            Game game = ticTacService.getGame(gameId);
            GameView gameView = gameWebMapper.toGameView(game);

            model.addAttribute("game", gameView);
            model.addAttribute("gameId", gameId);
            model.addAttribute("org.springframework.validation.BindingResult.moveRequest", bindingResult);

            return "redirect:/game/" + moveRequest.getGameId();
        }
    }
}
