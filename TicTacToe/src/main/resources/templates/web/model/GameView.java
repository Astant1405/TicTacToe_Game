package ru.school21.morrasha2.web.model;

import org.springframework.stereotype.Component;
import ru.school21.morrasha2.domain.model.Person;

import java.util.UUID;

@Component
public class GameView {
    private int[][] board;
    private UUID gameId;
    private String status;
    private Person firstPerson;
    private Person secondPerson;

    public GameView() {
        this.board = new int[3][3];
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getGameId() {
        return gameId;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public int getElement(int x, int y) {
        return board[x][y];
    }

    public void setElement(int x, int y, int value) {
        board[x][y] = value;
    }

    public Person getFirstPerson() {
        return firstPerson;
    }

    public void setFirstPerson(Person firstPerson) {
        this.firstPerson = firstPerson;
    }

    public Person getSecondPerson() {
        return secondPerson;
    }

    public void setSecondPerson(Person secondPerson) {
        this.secondPerson = secondPerson;
    }
}
