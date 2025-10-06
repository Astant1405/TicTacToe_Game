package ru.school21.TicTacToe.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.school21.TicTacToe.domain.model.Game;
import ru.school21.TicTacToe.domain.service.TicTacService;
import ru.school21.TicTacToe.dto.MoveRequestDTO;

import java.util.UUID;

@Component
public class MoveRequestValidator implements Validator {
    private final TicTacService ticTacService;

    @Autowired
    public MoveRequestValidator(TicTacService ticTacService) {
        this.ticTacService = ticTacService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return MoveRequestDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MoveRequestDTO moveRequestDTO = (MoveRequestDTO) target;
        UUID gameId = moveRequestDTO.getGameId();
        int row = moveRequestDTO.getRow();
        int col = moveRequestDTO.getCol();

        try {
            Game game = ticTacService.getGame(gameId);
            if (game.getField().getElement(row, col) != 0) {
                errors.rejectValue("row", "", "Ячейка уже занята");
            }

        } catch (Exception e) {
            errors.reject("game.error", "Ошибка при проверке игры: " + e.getMessage());
        }
    }
}
