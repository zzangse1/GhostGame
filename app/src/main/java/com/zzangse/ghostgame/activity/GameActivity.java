package com.zzangse.ghostgame.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzangse.ghostgame.GameResult;
import com.zzangse.ghostgame.R;
import com.zzangse.ghostgame.TeamInfoViewModel;
import com.zzangse.ghostgame.adapter.GameResultAdapter;
import com.zzangse.ghostgame.database.RoomDB;
import com.zzangse.ghostgame.database.TeamInfo;
import com.zzangse.ghostgame.databinding.ActivityGameBinding;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GameActivity extends AppCompatActivity {
    private RoomDB roomDB;
    private ArrayList<GameResult> gameResultArrayList = new ArrayList<>();
    private ArrayList<String> mPlayerNameList = new ArrayList<>();
    private ArrayList<String> mGamePenaltyList = new ArrayList<>();
    private Disposable disposable;
    private ActivityGameBinding binding;
    private String mGameName, mGroupName;
    private TeamInfoViewModel teamInfoViewModel;
    private RecyclerView recyclerView;
    private GameResultAdapter adapter;
    private int mGamePenaltySize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // GameActivity 안에
        initRoomDB();
        initView();
        initViewModel();
        initRecyclerView();
        getData();
        initIntent();
        setGameName();
        setupToolbarBackButton();
       // getPlayer(mGroupName);
        setButtonClickListeners();
        test();
    }

    private void setupToolbarBackButton() {
        binding.toolbarHome.setNavigationOnClickListener(v -> finish());
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mGameName = intent.getStringExtra("gameNameKey");
            mGroupName = intent.getStringExtra("groupNameKey");
            mGamePenaltySize = intent.getIntExtra("gamePenaltySize", -1);
            for (int i = 0; i < mGamePenaltySize; i++) {
                mGamePenaltyList.add(intent.getStringExtra("gamePenalty" + i));
                Log.d("initIntent_for_1: ", mGamePenaltyList.get(i));
            }
            Log.d("initIntent", mGameName + ", " + mGroupName + ", " + mGamePenaltySize);
        }
    }


    private void shufflePlayerAndPenaltyList() {
        Collections.shuffle(mPlayerNameList); // player가 들어있는 리스트를 섞어줌
        Collections.shuffle(mGamePenaltyList);// penalty가 들어있는 리스트를 섞어줌
    }

    private void addPlayerToRecyclerView() {
        recyclerView.setAdapter(adapter);
        for (int i = 0; i < mPlayerNameList.size(); i++) {
            GameResult newItem = new GameResult(mPlayerNameList.get(i), mGamePenaltyList.get(i));
            gameResultArrayList.add(newItem);
        }
        adapter.notifyItemInserted(gameResultArrayList.size() - 1);
    }

    private void setGameName() {
        binding.tvGameName.setText(mGameName);
    }

    private void initView() {
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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

    private void setButtonClickListeners() {
        BtnClick btnClick = new BtnClick();
        binding.btnResult.setOnClickListener(btnClick);
        binding.btnResultOk.setOnClickListener(btnClick);
    }

    class BtnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_result) {
                shufflePlayerAndPenaltyList();
                onClickBtnResult();
            } else if (id == R.id.btn_result_ok) {
                onClickBtnResultOk();
            }
        }
    }

    private void onClickBtnResult() {
        addPlayerToRecyclerView();
        binding.btnResult.setVisibility(View.GONE);
        binding.btnResultOk.setVisibility(View.VISIBLE);
    }

    private void onClickBtnResultOk() {
        AlertDialog dialog = createDialog();
        dialog.show();

    }

    private void clearRecycler() {
        gameResultArrayList.clear();
        adapter.notifyDataSetChanged();
    }


    private void initViewModel() {
        teamInfoViewModel = new ViewModelProvider(this).get(TeamInfoViewModel.class);
    }

    private void getData() {
        teamInfoViewModel.getGameName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String gameName) {
                mGameName = gameName;
            }
        });

        teamInfoViewModel.getGroupName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String groupName) {
                mGroupName = groupName;
            }
        });
    }

    private void initRoomDB() {
        roomDB = RoomDB.getInstance(getApplicationContext());
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.rv_game_result);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GameResultAdapter(this, gameResultArrayList);
        recyclerView.setAdapter(adapter);

    }

    public AlertDialog createDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setIcon(R.drawable.ic_game)
                .setNegativeButton(R.string.cancel_message, (dialogInterface, i) ->
                        Toast.makeText(GameActivity.this, R.string.cancel_message, Toast.LENGTH_SHORT).show())
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.btnResultOk.setVisibility(View.GONE);
                        binding.btnResult.setVisibility(View.VISIBLE);
                        clearRecycler();
                        binding.btnResult.setText("다시하기");
                        Toast.makeText(GameActivity.this, "결과를 초기화 합니다", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();

        String deleteEditMsg = "결과를 초기화 합니다.";
        dialog.setMessage(deleteEditMsg);
        return dialog;
    }

/*    질문 mPlayerNameList가 0인 이유
    private void test2() {
        Log.d("check",mPlayerNameList.size()+"");
    }
    @SuppressLint("CheckResult")
    private void getPlayer(String targetTeamName) {
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
                        }
                );

    }*/

    private void test() {
        getPlayer(mGroupName,()->{
            Log.d("check: ",mPlayerNameList.size()+"");
            String nullStr = "통과";
            for (int i = 0; i < mPlayerNameList.size()-mGamePenaltySize; i++){
                mGamePenaltyList.add(nullStr);
            }
            for (String a : mGamePenaltyList) {
                Log.d("penalty: ",a);
            }
            for (String a : mPlayerNameList) {
                Log.d("palyer: ",a);
            }
        });
    }



    @SuppressLint("CheckResult")
    private void getPlayer(String targetTeamName, Runnable onComplete) {
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
                            onComplete.run();
                        }
                );

    }


}
