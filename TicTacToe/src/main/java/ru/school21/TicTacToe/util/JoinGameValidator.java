package ru.school21.TicTacToe.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.school21.TicTacToe.domain.model.Game;
import ru.school21.TicTacToe.domain.service.TicTacService;
import ru.school21.TicTacToe.dto.JoinGameDTO;

import java.util.UUID;


@Component
public class JoinGameValidator implements Validator {
    private final TicTacService ticTacService;

    @Autowired
    public JoinGameValidator(TicTacService ticTacService) {
        this.ticTacService = ticTacService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return JoinGameDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        JoinGameDTO joinGameDTO = (JoinGameDTO) target;
        String gameIdString = joinGameDTO.getGameId();

        if (gameIdString == null || gameIdString.trim().isEmpty()) {
            errors.rejectValue("gameId", "gameId.empty", "ID игры не может быть пустым");
            return;
        }

        try {
            UUID gameId = UUID.fromString(gameIdString);

            try {
                Game game = ticTacService.getGame(gameId);

                if (!"в ожидании".equals(game.getStatus())) {
                    errors.rejectValue("gameId", "game.notAvailable", "Игра уже заполнена или недоступна");
                }

            } catch (org.springframework.expression.ExpressionException e) {
                errors.rejectValue("gameId", "game.notFound", "Игра с ID " + gameId + " не найдена");
            }

        } catch (IllegalArgumentException e) {
            errors.rejectValue("gameId", "game.invalidFormat", "Неверный формат ID игры");
        } catch (Exception e) {
            errors.rejectValue("gameId", "game.error", "Ошибка при проверке игры: " + e.getMessage());
        }
    }
}
