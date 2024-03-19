package com.zzangse.ghostgame.activity;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzangse.ghostgame.GameModify;
import com.zzangse.ghostgame.GameResult;
import com.zzangse.ghostgame.Group;
import com.zzangse.ghostgame.R;
import com.zzangse.ghostgame.TeamInfoViewModel;
import com.zzangse.ghostgame.adapter.AddAdapter;
import com.zzangse.ghostgame.adapter.GameResultAdapter;
import com.zzangse.ghostgame.adapter.ModifyAdapter;
import com.zzangse.ghostgame.database.RoomDB;
import com.zzangse.ghostgame.database.TeamInfo;
import com.zzangse.ghostgame.databinding.ActivityModifyBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ModifyActivity extends AppCompatActivity {
    ActivityModifyBinding modifyBinding;
    private TeamInfoViewModel teamInfoViewModel;
    private TeamInfo teamInfo;
    private ArrayList<GameModify> gameModifyArrayList = new ArrayList<>();
    private ArrayList<String> teamNameList = new ArrayList<>();
    private ArrayList<String> mPlayerNameList;
    private ModifyAdapter adapter;
    private RecyclerView recyclerView;
    private RoomDB roomDB;
    private String mGroupName;
    private int teamInfoSize = 0;
    private int nowSpinnerPos=0;

    /*
     * 스피너 가져오기
     * 스피너가 가리키고있는 팀 이름 가져오기
     * 팀이름을 기반으로 기존에 있던 playerName을 ArrayList에 넣기
     * ArrayList와 어댑터 연결해서 기존의 이름 리싸이클러뷰에 보이기
     * 추가 / 삭제 구현
     * */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRoomDB();
        initView();
        initViewModel();
        initSpinner();
        setSpinnerEvents();
        initRecyclerView();
        setupToolbarBackButton();
        onClickAddBtn();
    }

    private void deleteItem(int pos) {
       // if (pos != RecyclerView.NO_POSITION) {
            gameModifyArrayList.remove(pos);
            adapter.notifyItemRemoved(pos);
       // }
    }
    private void initRoomDB() {
        roomDB = RoomDB.getInstance(this.getApplicationContext());
    }

    private void initViewModel() {
        teamInfoViewModel = new ViewModelProvider(this).get(TeamInfoViewModel.class);
    }


    private void initRecyclerView() {
        // 리싸이클러뷰 레이아웃 매니어 설정
        recyclerView = findViewById(R.id.rv_modify);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 리싸이클러뷰 어댑터 설정
        adapter = new ModifyAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
        adapter.setOnClick(new ModifyAdapter.ModifyAdapterClick() {
//            @Override
//            public void onClickDelete(GameModify gameModify) {
//                deleteItem(gameModify.getPlayerName().indexOf(0));
//            }

            @Override
            public void onClickDelete(View v, int pos) {
                deleteItem(pos);
            }

            @Override
            public void onClickInfo(GameModify gameModify) {
                setRandomText(gameModify.getPlayerName());
            }
        });
    }

    private void setupToolbarBackButton() {
        modifyBinding.toolbarModify.setNavigationOnClickListener(v->finish());
    }
    private void setRandomText(String text) {
        Toast.makeText(getApplicationContext(), "벌칙: " + text, Toast.LENGTH_SHORT).show();
    }

    // 새로운 멤버 추가
    // onClickAddBtn 메서드 내에서 스피너 값을 업데이트합니다.
    private void onClickAddBtn() {
        modifyBinding.activityModifyBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPlayerName = modifyBinding.etInput.getText().toString();
                if (!newPlayerName.isEmpty()) {
                    saveTeamInfo(newPlayerName);
                    modifyBinding.etInput.getText().clear();
                } else {
                    Toast.makeText(getApplicationContext(),"멤버 이름을 작성해주세요",Toast.LENGTH_SHORT).show();
                }
                Log.d("onClick: ", mGroupName);
            }
        });
    }


    @SuppressLint("CheckResult")
    private void saveTeamInfo(String playerName) {
        if (teamInfo == null) {
            teamInfo = new TeamInfo();
        }
        teamInfo.setTeamName(mGroupName);
        teamInfo.setPlayerName(playerName);

        roomDB.getTeamInfoDao().insert(teamInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() ->
                        Toast.makeText(this, "데이터 삽입 완료\n" + mGroupName + "\n" + playerName, Toast.LENGTH_SHORT).show());
    }

    private void addPlayerToRecyclerView() {
        RecyclerView recyclerView = modifyBinding.rvModify;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ModifyAdapter(this, gameModifyArrayList);
        recyclerView.setAdapter(adapter);
        for (int i = 0; i < mPlayerNameList.size(); i++) {
            GameModify newItem = new GameModify(mPlayerNameList.get(i));
            gameModifyArrayList.add(newItem);
            Log.d("addPlayerToRecyclerView",gameModifyArrayList.get(i).toString());
        }
        adapter.notifyItemInserted(gameModifyArrayList.size() - 1);
    }

    private void setSpinnerEvents() {
        modifyBinding.spinnerModify.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGroupName = modifyBinding.spinnerModify.getItemAtPosition(position).toString();
                nowSpinnerPos = position;
                Log.d("setSpinnerEvents", "spinner: " + mGroupName);
                countTeam(mGroupName);
                getPlayer(mGroupName);
                gameModifyArrayList.clear(); // 스피너 값을 선택시 list를 비우고 새로선택한 스피너의 값을 띄움
                modifyBinding.spinnerModify.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @SuppressLint("CheckResult")
    private void getPlayer(String targetTeamName) {
        mPlayerNameList = new ArrayList<>();
        roomDB.getTeamInfoDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        playerList -> {
                            for (TeamInfo team : playerList) {
                                if (team.getTeamName().equals(targetTeamName)) {
                                    mPlayerNameList.add(team.getPlayerName());
                                }
                            }
                            for (String playerName : mPlayerNameList) {
                                Log.d("getPlayer", "playerName: " + playerName);
                            }
                            addPlayerToRecyclerView();
                        }
                );
    }

    private void countTeam(String spinnerGroupName) {
        teamInfoViewModel.getPlayerByTeam(spinnerGroupName).observe(this, new Observer<List<TeamInfo>>() {
            @Override
            public void onChanged(List<TeamInfo> teamInfo) {
                modifyBinding.tvGroupCount.setText(teamInfo.size() + " 명");
                teamInfoSize = teamInfo.size();
                Log.d("countTeam: ", "" + teamInfoSize);
            }
        });
    }

    private void initView() {
        modifyBinding = ActivityModifyBinding.inflate(getLayoutInflater());
        setContentView(modifyBinding.getRoot());
    }

    // 휴대폰이 자동회전이 되어도 꺼짐을 막아
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("onConfigurationChanged", "가로");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.e("onConfigurationChanged", "세로");
        }
    }


    private void updateSpinner(ArrayList<String> teamNameList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teamNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modifyBinding.spinnerModify.setAdapter(adapter);
        // newPlayer를 추가해도 스피너의 값이 처음으로 돌아가는 것을 방지하는 코드
        modifyBinding.spinnerModify.setSelection(nowSpinnerPos);
    }

    private void initSpinner() {
        modifyBinding.spinnerModify.setTitle("그룹을 선택해주세요");
        modifyBinding.spinnerModify.setPositiveButton("취소");
        teamInfoViewModel.getShowTeam().observe(this, new Observer<List<TeamInfo>>() {
            @Override
            public void onChanged(List<TeamInfo> teamInfoList) {
                teamNameList = new ArrayList<>();
                // teamNameList에 각각의 팀 이름 저장
                for (TeamInfo teamInfo : teamInfoList) {
                    String teamName = teamInfo.getTeamName();
                    teamNameList.add(teamName);
                    Log.d("teamNameList: ",": "+teamNameList.size());
                }
                // spinner에 값 삽입
                updateSpinner(teamNameList);
            }
        });
    }


}
