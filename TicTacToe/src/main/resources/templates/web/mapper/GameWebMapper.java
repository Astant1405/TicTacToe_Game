package ru.school21.morrasha2.web.mapper;

import org.springframework.stereotype.Component;
import ru.school21.morrasha2.domain.model.Game;
import ru.school21.morrasha2.web.model.GameView;

@Component
public class GameWebMapper {
    public GameView toGameView(Game game) {
        GameView gameView = new GameView();
        gameView.setGameId(game.getId());
        gameView.setBoard(game.getField().getField());
        gameView.setStatus(game.getStatus());
        gameView.setFirstPerson(game.getFirstPerson());
        gameView.setSecondPerson(game.getSecondPerson());
        return gameView;
    }
}
