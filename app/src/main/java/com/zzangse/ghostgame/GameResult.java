package com.zzangse.ghostgame;

public class GameResult {
    String playerName;
    String penalty;

    public GameResult(String playerName, String penalty) {
        this.playerName = playerName;
        this.penalty = penalty;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

}
