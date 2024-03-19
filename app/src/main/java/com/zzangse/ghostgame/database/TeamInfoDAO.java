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

    @Query("SELECT * FROM teaminfo WHERE teamName = :teamName")
    Single<List<TeamInfo>> getPlayerByTeam2(String teamName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(TeamInfo... teamInfo);

    @Query("DELETE FROM TeamInfo")
    Completable deleteAll();

    @Query("SELECT * FROM teaminfo GROUP BY teamName")
    Single<List<TeamInfo>> showTeam_Team();

    @Query("DELETE FROM teaminfo WHERE teamName IN(:teamName)")
    Completable deleteItem(String teamName);

    @Query("SELECT * FROM teaminfo GROUP BY teamName")
    LiveData<List<TeamInfo>> findTeam();

    @Query("SELECT * FROM teaminfo GROUP BY teamName")
    LiveData<List<TeamInfo>> showTeam();

    @Query("SELECT * FROM teaminfo WHERE teamName = :teamName")
    LiveData<List<TeamInfo>> getPlayerByTeam(String teamName);

}
