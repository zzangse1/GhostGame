package com.zzangse.ghostgame;

public class GameAdd {
    private String playerName;

    public GameAdd(String playerName) {
        this.playerName = playerName;
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

    public String getValue() {
        return playerName;
    }

    public int compareTo(GameAdd other) {
        // 문자열을 유니코드 값으로 비교합니다.
        return other.getValue().compareTo(this.playerName);
    }

}
