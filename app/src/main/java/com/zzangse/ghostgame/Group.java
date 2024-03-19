package com.zzangse.ghostgame;

import androidx.annotation.NonNull;

public class Group {
    private String groupName;
    private String playerName;

    public Group() {
    }

    public Group(String playerName) {

        this.playerName = playerName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @NonNull
    @Override
    public String toString() {
        return groupName;
    }
}
