package com.zzangse.ghostgame.activity;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
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
    private Balloon balloon;
    private static final String EMPTY_INPUT_MESSAGE = "팀 이름 또는 플레이어 이름을 입력하세요";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initialize();
        initData();
        initRecyclerView();
        initTooltip();
        setupToolbarBackButton();
        showTooltip();
    }

    private void initTooltip() {
        balloon = new Balloon.Builder(this)
                .setArrowSize(10)
                //.setIconDrawable(ContextCompat.getDrawable(this, R.drawable.ic_info))
                .setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setArrowOrientation(ArrowOrientation.END)
                .setArrowPosition(0.3f)
                //.setArrowVisible(true)
                .setWidthRatio(0.8f) // 말풍선 넓이
                .setHeight(60)
                .setTextSize(14f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setText("그룹이름이 이미 존재하면 인원 추가 가능합니다")
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build();
    }

    private void showTooltip() {
        binding.ibTooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balloon.showAlignStart(binding.ibTooltip);
            }
        });

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


    private void setupToolbarBackButton() {
        binding.toolbarAdd.setNavigationOnClickListener(view -> finish());
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
        binding.activityAddBtnAdd.setOnClickListener(view -> addPlayerToRecyclerView());
    }
}
