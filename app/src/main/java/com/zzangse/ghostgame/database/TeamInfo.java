package com.zzangse.ghostgame.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TeamInfo")
public class TeamInfo {

    public TeamInfo() {
    }

    public TeamInfo(String teamName, String playerName) {
        this.teamName = teamName;
        this.playerName = playerName;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String teamName;
    private String playerName;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
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
        return "TeamInfo{" +
                "teamName'" + teamName + '\'' +
                ", playerName='" + playerName + '\'' +
               // ", randomPenalty='" + randomPenalty + '\'' +
                '}';
    }

}
