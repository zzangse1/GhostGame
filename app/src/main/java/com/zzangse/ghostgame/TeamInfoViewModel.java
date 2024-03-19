package com.zzangse.ghostgame;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.zzangse.ghostgame.database.RoomDB;
import com.zzangse.ghostgame.database.TeamInfo;

import java.lang.reflect.Array;
import java.util.List;

public class TeamInfoViewModel extends AndroidViewModel {
    RoomDB roomDB;
    private MutableLiveData<String> gameName = new MutableLiveData<>();
    private MutableLiveData<String> groupName = new MutableLiveData<>();

    // 첫 번째 String 값을 설정하는 메서드
    public void setGameName(String data) {
        gameName.setValue(data);
    }

    // 두 번째 String 값을 설정하는 메서드
    public void setGroupName(String data) {
        groupName.setValue(data);
    }

    // 첫 번째 String 값을 가져오는 LiveData
    public LiveData<String> getGameName() {
        return gameName;
    }

    // 두 번째 String 값을 가져오는 LiveData
    public LiveData<String> getGroupName() {
        return groupName;
    }

    public TeamInfoViewModel(@NonNull Application application) {
        super(application);
        roomDB = RoomDB.getInstance(application.getApplicationContext());
    }

    public LiveData<List<TeamInfo>> getAllTeam() {
        return roomDB.getTeamInfoDao().findTeam();
    }

    public LiveData<List<TeamInfo>> getShowTeam() {
        return roomDB.getTeamInfoDao().showTeam();
    }

    public LiveData<List<TeamInfo>> getPlayerByTeam(String teamName) {
        return roomDB.getTeamInfoDao().getPlayerByTeam(teamName);
    }


}
