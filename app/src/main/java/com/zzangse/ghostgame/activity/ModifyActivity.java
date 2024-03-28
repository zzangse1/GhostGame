package com.zzangse.ghostgame.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.zzangse.ghostgame.GameModify;
import com.zzangse.ghostgame.R;
import com.zzangse.ghostgame.TeamInfoViewModel;
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
    private ArrayList<String> mPlayerNameList;
    private ArrayList<String> teamNameList = new ArrayList<>();
    private ModifyAdapter adapter;
    private RecyclerView recyclerView;
    private RoomDB roomDB;
    private String mGroupName;
    private String mPlayerName;
    private int teamInfoSize = 0;
    private int nowSpinnerPos = 0;

    /*
     * 스피너 가져오기
     * 스피너가 가리키고있는 팀 이름 가져오기
     * 팀이름을 기반으로 기존에 있던 playerName을 ArrayList에 넣기
     * ArrayList와 어댑터 연결해서 기존의 이름 리싸이클러뷰에 보이기
     * 추가 / 삭제 구현
     * 멤버이름 중복 불가능
     * 그룹 중복 생성 금지
     * 팀생성 추가를 누르면 팀이름 못 바꾸게
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
        countTeam(mGroupName);
        // }
    }


    @SuppressLint("CheckResult")
    private void deleteRoom(String teamName, String playerName, int pos) {
        roomDB.getTeamInfoDao().deletePlayer(teamName, playerName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            deleteItem(pos);
                        }
                );
    }

    private void initRoomDB() {
        roomDB = RoomDB.getInstance(this.getApplicationContext());
    }

    private void initViewModel() {
        teamInfoViewModel = new ViewModelProvider(this).get(TeamInfoViewModel.class);
    }


    private void initRecyclerView() {
        // 리싸이클러뷰 레이아웃 매니어 설정
        recyclerView = modifyBinding.rvModify;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 리싸이클러뷰 어댑터 설정
        adapter = new ModifyAdapter(this, gameModifyArrayList);

        adapter.setOnClick(new ModifyAdapter.ModifyAdapterClick() {
            @Override
            public void onClickDelete(GameModify gameModify) {
                mPlayerName = gameModify.getPlayerName();
                Log.d("mPlayerName", mPlayerName);
                AlertDialog dialog = createDialog(gameModify.getPlayerName(), gameModifyArrayList.indexOf(gameModify));
                dialog.show();

            }


            @Override
            public void onClickInfo(GameModify gameModify) {
                setRandomText(gameModify.getPlayerName());
            }
        });


        recyclerView.setAdapter(adapter);
    }


    public AlertDialog createDialog(String playerName, int pos) {
        String deleteEditMsg = "해당 멤버 [ " + playerName + " ] 을 삭제하시겠습니까? " +
                "<br/><font color='#ff0000'>삭제 후 되돌릴 수 없습니다!</font color>";
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(HtmlCompat.fromHtml(deleteEditMsg, HtmlCompat.FROM_HTML_MODE_LEGACY))
                .setIcon(R.drawable.ic_delete)
                .setNegativeButton(R.string.cancel_message, (dialogInterface, i) ->
                        Toast.makeText(this, R.string.cancel_message, Toast.LENGTH_SHORT).show())
                .setPositiveButton("삭제", (DialogInterface, i) -> {
                    Log.d("createDialog", playerName);
                    Toast.makeText(getApplicationContext(), "멤버 [ " + playerName + " ] 이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                    deleteRoom(mGroupName, mPlayerName, pos);
                })
                .create();
        return dialog;
    }


    private void setupToolbarBackButton() {
        modifyBinding.toolbarModify.setNavigationOnClickListener(v -> finish());
    }

    private void setRandomText(String text) {
        Toast.makeText(getApplicationContext(), "멤버: " + text, Toast.LENGTH_SHORT).show();
    }

    // 새로운 멤버 추가
    // onClickAddBtn 메서드 내에서 스피너 값을 업데이트합니다.
    private void onClickAddBtn() {
        modifyBinding.activityModifyBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPlayerName = modifyBinding.etPlayerInput.getText().toString();
                if (!newPlayerName.isEmpty() && isPlayerNameUniqueCheck(newPlayerName)) {
                    saveTeamInfo(newPlayerName);
                    modifyBinding.etPlayerInput.getText().clear();
                } else {
                    Toast.makeText(getApplicationContext(), "멤버 이름을 작성해주세요", Toast.LENGTH_SHORT).show();
                }
                Log.d("onClick: ", mGroupName);
            }
        });
    }

    // 새로운 멤버 추가 중복 체크
    private boolean isPlayerNameUniqueCheck(String newPlayerName) {
        for (String str : mPlayerNameList) {
            if (newPlayerName.equals(str)) {
                Toast.makeText(ModifyActivity.this, "[ " + newPlayerName + " ] 멤버가 이미 존재합니다", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
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
        for (int i = 0; i < mPlayerNameList.size(); i++) {
            GameModify newItem = new GameModify(mPlayerNameList.get(i));
            gameModifyArrayList.add(newItem);
            Log.d("addPlayerToRecyclerView", gameModifyArrayList.get(i).toString());
        }
        // 어댑터 새로고침 (리싸이클러뷰의 아이템과 크기가 전부 변경되기때문)
        adapter.notifyDataSetChanged();
    }

    private void setSpinnerEvents() {
        modifyBinding.spinnerModify.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGroupName = modifyBinding.spinnerModify.getItemAtPosition(position).toString();
                nowSpinnerPos = position;
                Log.d("setSpinnerEvents", "spinner: " + mGroupName);
                gameModifyArrayList.clear(); // 스피너 값을 선택시 list를 비우고 새로선택한 스피너의 값을 띄움
                countTeam(mGroupName);
                getPlayer(mGroupName);
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
                    Log.d("teamNameList: ", ": " + teamNameList.size());
                }
                // spinner에 값 삽입
                updateSpinner(teamNameList);
            }
        });
    }


}
