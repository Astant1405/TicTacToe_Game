package ru.school21.TicTacToe.dto;

import java.util.UUID;

public class LeaderBoardDTO {
    private UUID userId;
    private String username;
    private Long wins;
    private Long losses;
    private Long draws;
    private Double winRatio;

    public LeaderBoardDTO(UUID userId, String username, Long wins, Long losses, Long draws, Double winRatio) {
        this.userId = userId;
        this.username = username;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.winRatio = winRatio;
    }

    public LeaderBoardDTO() {
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getWinRatio() {
        return winRatio;
    }

    public void setWinRatio(Double winRatio) {
        this.winRatio = winRatio;
    }

    public Long getWins() {
        return wins;
    }

    public void setWins(Long wins) {
        this.wins = wins;
    }

    public Long getLosses() {
        return losses;
    }

    public void setLosses(Long losses) {
        this.losses = losses;
    }

    public Long getDraws() {
        return draws;
    }

    public void setDraws(Long draws) {
        this.draws = draws;
    }
}
