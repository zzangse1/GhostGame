package com.zzangse.ghostgame.activity;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzangse.ghostgame.R;
import com.zzangse.ghostgame.adapter.AddAdapter;
import com.zzangse.ghostgame.database.RoomDB;
import com.zzangse.ghostgame.database.TeamInfo;
import com.zzangse.ghostgame.databinding.ActivityAddBinding;
import com.zzangse.ghostgame.fragment.SettingFragment;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class AddActivity extends AppCompatActivity {
    private final ArrayList<TeamInfo> teamInfoArrayList = new ArrayList<>();
    private ActivityAddBinding binding;
    private RecyclerView recyclerView;
    private AddAdapter adapter;
    private RoomDB roomDB;
    private TeamInfo teamInfo;
    private static final String EMPTY_INPUT_MESSAGE = "팀 이름 또는 플레이어 이름을 입력하세요";
    /*
     * 팀이름이 겹치면 생성불가능 코드
     * */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initialize();
        initData();
        initRecyclerView();
        onClickToolbarBtn();
    }


    private void initialize() {
        roomDB = RoomDB.getInstance(this);
    }


    private void initView() {
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.rv_add);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddAdapter(this,teamInfoArrayList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        // AddActivity에서 뒤로가기를 눌렀을 때 SettingFragment 업데이트
        super.onBackPressed();
        updateSettingFragment();
    }


    private void updateSettingFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SettingFragment settingFragment = (SettingFragment) fragmentManager.findFragmentById(R.id.fragment_home);

        if (settingFragment != null) {
            settingFragment.onResume(); // 또는 업데이트를 수행하는 다른 메서드 호출
        }
    }
    private void showDialog() {
        AlertDialog dialog = createDialog();
        dialog.show();
    }

    private AlertDialog createDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("정보")
                .setMessage("저장을 누르지 않고 뒤로 돌아가면 저장되지 않습니다!")
                .setIcon(R.drawable.ic_groups)
                .setNegativeButton("취소", (DialogInterface, i) ->
                        //취소 로직
                        Toast.makeText(this, "cancel ", Toast.LENGTH_SHORT).show())
                .setPositiveButton("확인", (DialogInterface, i) -> {
                    Toast.makeText(this, "pos", Toast.LENGTH_SHORT).show();
                    //확인 로직
                    finish();
                })
                .create();
        return dialog;
    }

    private void onClickToolbarBtn() {
        binding.toolbarAdd.setNavigationOnClickListener(view ->
                showDialog()
        );
    }



    @SuppressLint("CheckResult")
    private void saveTeamInfo(String playerName) {
        if (teamInfo == null) {
            teamInfo = new TeamInfo();
        }

        String teamName = binding.etGroupInput.getText().toString();
        if (TextUtils.isEmpty(teamName) || TextUtils.isEmpty(playerName)) {
            Toast.makeText(this, EMPTY_INPUT_MESSAGE, Toast.LENGTH_SHORT).show();
            return;
        }

        teamInfo.setTeamName(teamName);
        teamInfo.setPlayerName(playerName);

        roomDB.getTeamInfoDao().insert(teamInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() ->
                        Toast.makeText(this, "데이터 삽입 완료\n" + teamName + "\n" + playerName, Toast.LENGTH_SHORT).show());
    }

    private void addPlayerToRecyclerView() {
        EditText etPlayer = binding.etInput;
        String playerName = etPlayer.getText().toString();

        if (TextUtils.isEmpty(binding.etGroupInput.getText()) || TextUtils.isEmpty(playerName)) {
            Toast.makeText(this, EMPTY_INPUT_MESSAGE, Toast.LENGTH_SHORT).show();
            return;
        }

        binding.etGroupInput.setEnabled(false);
        RecyclerView recyclerView = binding.rvAdd;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AddAdapter adapter = new AddAdapter(this,teamInfoArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        saveTeamInfo(playerName);
        teamInfoArrayList.add(new TeamInfo(playerName));
        etPlayer.getText().clear();

        int playerCount = teamInfoArrayList.size();
        binding.toolbarAdd.setTitle(playerCount + " 명");
        adapter.notifyItemInserted(teamInfoArrayList.size() - 1);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("onConfigurationChanged", "가로");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.e("onConfigurationChanged","세로");
        }
    }
    private void initData() {
        binding.activityAddBtnAdd.setOnClickListener(view ->{
            addPlayerToRecyclerView();

        } );
    }
}