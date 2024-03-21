package com.zzangse.ghostgame;

public class GameModify {
    private String playerName;

    public GameModify(String playerName) {
        this.playerName = playerName;
    }
    public GameModify( ) {

    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public String toString() {
        return playerName;
    }
}
