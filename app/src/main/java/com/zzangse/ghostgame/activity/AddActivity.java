package com.zzangse.ghostgame.activity;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzangse.ghostgame.GameAdd;
import com.zzangse.ghostgame.R;
import com.zzangse.ghostgame.TeamInfoViewModel;
import com.zzangse.ghostgame.adapter.AddAdapter;
import com.zzangse.ghostgame.database.RoomDB;
import com.zzangse.ghostgame.database.TeamInfo;
import com.zzangse.ghostgame.databinding.ActivityAddBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class AddActivity extends AppCompatActivity {
    private ActivityAddBinding binding;
    private final ArrayList<TeamInfo> teamInfoArrayList = new ArrayList<>();
    private ArrayList<GameAdd> playerNameList = new ArrayList<>();
    private ArrayList<String> teamNameList;
    private AddAdapter adapter;
    private RoomDB roomDB;
    private String mTeamName;
    private TeamInfoViewModel teamInfoViewModel;
    private static final String EMPTY_INPUT_MESSAGE = "팀 이름 또는 플레이어 이름을 입력하세요";
    /*
     * 팀이름이 겹치면 생성불가능 코드
     * */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initRoomDB();
        initRecyclerView();
        initViewModel();
        getAllTeamName();
        onClickBtnAdd();
        onClickToolbarBtn();
        onClickBackBtn();
        onClickBtnSave();
    }


    private void initRoomDB() {
        roomDB = RoomDB.getInstance(this);
    }


    private void initView() {
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_add);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddAdapter(this, playerNameList);
        recyclerView.setAdapter(adapter);
    }

    private void initViewModel() {
        teamInfoViewModel = new ViewModelProvider(this).get(TeamInfoViewModel.class);
    }

    private void onClickBackBtn() {
        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog dialog = createDialog();
                dialog.show();
            }
        });
    }

    private void showDialog() {
        AlertDialog dialog = createDialog();
        dialog.show();
    }

    private AlertDialog createDialog() {
        String mainMsg = "저장을 누르지 않고 뒤로 돌아가면<br/><font color ='#ff0000'>" +
                " 저장되지 않습니다!</font color>";
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("정보")
                .setMessage(HtmlCompat.fromHtml(mainMsg, HtmlCompat.FROM_HTML_MODE_LEGACY))
                .setIcon(R.drawable.ic_groups)
                .setNegativeButton(R.string.cancel_message, (DialogInterface, i) ->
                        //취소 로직
                        Toast.makeText(this, R.string.cancel_message, Toast.LENGTH_SHORT).show())
                .setPositiveButton(R.string.ok_message, (DialogInterface, i) -> {
                    Toast.makeText(this, R.string.ok_message, Toast.LENGTH_SHORT).show();
                    //확인 로직
                    finish();
                })
                .create();
        return dialog;
    }

    // 툴바 뒤로가기 버튼 이벤트 로직
    private void onClickToolbarBtn() {
        binding.toolbarAdd.setNavigationOnClickListener(v ->
                showDialog()
        );
    }

    @SuppressLint("CheckResult")
    private void saveTeamInfo() {
        setGameAddToTeamInfo();
        setTeamInfoToRoomDB();
        finish();
    }

    // teamInfoArrayList roomDB에 넣어주는 로직
    @SuppressLint("CheckResult")
    private void setTeamInfoToRoomDB() {
        Observable.fromIterable(teamInfoArrayList)
                .flatMapCompletable(data ->
                        roomDB.getTeamInfoDao().insert(data)
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    // 모든 데이터가 삽입된 후에 실행되는 작업
                    Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                }, throwable -> {
                    // 오류 발생 시 처리하는 작업
                    throwable.printStackTrace();
                });

//        for (TeamInfo teamInfo : teamInfoArrayList) {
//            Log.d("teamInfo: ", teamInfo.getTeamName() + ", " + teamInfo.getPlayerName());
//            roomDB.getTeamInfoDao().insert(teamInfo)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe();
//        }
        //     Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    // playerNameList를 teamInfo에 넣어주는 로직
    private void setGameAddToTeamInfo() {
        for (GameAdd gameAdd : playerNameList) {
            TeamInfo teamInfo = new TeamInfo();
            teamInfo.setTeamName(mTeamName);
            teamInfo.setPlayerName(gameAdd.getPlayerName());
            teamInfoArrayList.add(teamInfo);
            Log.d("teamInfo1: ", teamInfo.getPlayerName());
        }
    }


    private void onClickBtnAdd() {
        binding.activityAddBtnAdd.setOnClickListener(v -> {
            mTeamName = binding.etGroupInput.getText().toString();
            String playerName = binding.etInput.getText().toString();
            if (isInputValid(playerName))
                if (isPlayerNameUniqueCheck(playerName) && isTeamNameUniqueCheck()) {
                    setRecyclerItem(playerName);
                }
        });
    }

    // roomDB에서 팀이름을 전부 가져오는 로직
    private void getAllTeamName() {
        teamInfoViewModel.getShowTeam().observe(this, new Observer<List<TeamInfo>>() {
            @Override
            public void onChanged(List<TeamInfo> teamInfoList) {
                teamNameList = new ArrayList<>();
                for (TeamInfo teamInfo : teamInfoList) {
                    String teamName = teamInfo.getTeamName();
                    teamNameList.add(teamName);
                    Log.d("teamCHeck: ", teamName);
                }
            }
        });
    }

    private boolean isTeamNameUniqueCheck() {
        for (String teamName : teamNameList) {
            if (teamName.equals(mTeamName)) {
                Toast.makeText(AddActivity.this, "[ " + mTeamName + " ] 팀명이 이미 존재합니다", Toast.LENGTH_SHORT).show();
                binding.etGroupInput.setEnabled(true);
                binding.etGroupInput.getText().clear();
                binding.etGroupInput.requestFocus(); // etGroupInput으로 포커스 이동
                return false;
            }
        }
        return true;
    }

    // playerName 중복 체크
    private boolean isPlayerNameUniqueCheck(String playerName) {
        for (GameAdd gameAdd : playerNameList) {
            if (playerName.equals(gameAdd.getPlayerName())) {
                Toast.makeText(AddActivity.this, "[ " + playerName + " ] 중복된 이름을 넣을 수 없습니다.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void setRecyclerItem(String playerName) {
        playerNameList.add(new GameAdd(playerName));
        int playerCount = playerNameList.size();
        binding.toolbarAdd.setTitle(playerCount + " 명");
        adapter.notifyItemInserted(playerNameList.size() - 1);
    }

    // editText에 값이 유요한지 확인하는 로직
    private boolean isInputValid(String playerName) {
        if (TextUtils.isEmpty(binding.etGroupInput.getText()) || TextUtils.isEmpty(playerName)) {
            Toast.makeText(AddActivity.this, EMPTY_INPUT_MESSAGE, Toast.LENGTH_SHORT).show();
            return false;
        }
        clearEditText();
        return true;
    }

    //
    private void clearEditText() {
        binding.etInput.getText().clear();
        binding.etGroupInput.setEnabled(false);
    }

    private void onClickBtnSave() {
        binding.btnSave.setOnClickListener(v -> {
            saveTeamInfo();
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("onConfigurationChanged", "가로");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.e("onConfigurationChanged", "세로");
        }
    }

}