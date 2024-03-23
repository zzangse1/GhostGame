package com.zzangse.ghostgame.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface TeamInfoDAO {
    @Query("SELECT *FROM TeamInfo")
    Single<List<TeamInfo>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(TeamInfo... teamInfo);

    @Query("SELECT * FROM teaminfo GROUP BY teamName")
    Single<List<TeamInfo>> showTeam_Team();

    // @Query("DELETE FROM teaminfo WHERE teamName IN(:teamName) AND playerName IN(:playerName)")
    // LiveData<List<TeamInfo>> deletePlayer(String teamName, String playerName);

    @Query("DELETE FROM teaminfo WHERE teamName =:teamName AND playerName =:playerName")
    Completable deletePlayer(String teamName, String playerName);

    // team이름을 가져와서 팀을 삭제 하는 문법
    @Query("DELETE FROM teaminfo WHERE teamName IN(:teamName)")
    Completable deleteItem(String teamName);

    @Query("SELECT * FROM teaminfo GROUP BY teamName")
    LiveData<List<TeamInfo>> showTeam();

    @Query("SELECT * FROM teaminfo WHERE teamName = :teamName")
    LiveData<List<TeamInfo>> getPlayerByTeam(String teamName);

}
