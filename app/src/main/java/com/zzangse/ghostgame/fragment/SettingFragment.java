package com.zzangse.ghostgame.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.zzangse.ghostgame.Group;
import com.zzangse.ghostgame.R;
import com.zzangse.ghostgame.activity.AddActivity;
import com.zzangse.ghostgame.activity.ModifyActivity;
import com.zzangse.ghostgame.adapter.SettingAdapter;
import com.zzangse.ghostgame.database.RoomDB;
import com.zzangse.ghostgame.database.TeamInfo;
import com.zzangse.ghostgame.databinding.FragmentSettingBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingFragment extends Fragment {
    private int playerCount = 0;
    private RoomDB roomDB = null;
    private ArrayList<Group> groupList = new ArrayList<>();
    private SettingAdapter adapter;
    private ArrayList<String> playerNameList;
    private Disposable disposable;
    private FragmentSettingBinding settingBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomDB = RoomDB.getInstance(requireContext().getApplicationContext());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //settingBinding = FragmentSettingBinding.inflate(inflater, container, false);
        settingBinding = FragmentSettingBinding.inflate(inflater);
        return settingBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeRoomDB();
        initRecycler();
        moveToAddActivity(); // 생성 액티비티로 이동
        moveToModifyActivity(); // 수정 액티비티로 이동
        onSwipe();
        dataLoad(); // 데이터 로드 메소드 호출
    }



    // rootView를 null로 초기화하여 뷰를 해제
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        settingBinding = null;
        Log.d("test1234","onDestroyView");
    }


    @Override
    public void onStart() {
        super.onStart();
        dataLoad();
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

    private void initRecycler() {
        RecyclerView recyclerView = settingBinding.rvSetting;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SettingAdapter(requireContext(), groupList);
        recyclerView.setAdapter(adapter);

        // adapter 기능 구현
        adapter.setOnclick(new SettingAdapter.SettingAdapterClick() {
            @Override
            public void onClickDelete(Group group) {
                //removeItem(holder.itemView.toString(),pos);
               // Group group = groupList.get(pos);
                removeItem(group.getGroupName(),groupList.indexOf(group));
            }

//            @Override
//            public void onClickInfo(View v, int pos) {
//                Group group = groupList.get(pos);
//                dataLoadAll(group.getGroupName());
//            }


            @Override
            public void onClickInfo(Group group) {
                //dataLoadAll(holder.itemView.toString());
                //Group group = groupList.get(pos);
                dataLoadAll(group.getGroupName());
            }
        });
    }

    // 팀 이름 가져오기 (중복제거)
    @SuppressLint("CheckResult")
    public void dataLoadAll(String targetTeamName) {
        Log.d("dataLoadAll", "targetTeamName: "+targetTeamName);
        roomDB.getTeamInfoDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // 데이터를 받아와서 업데이트 메소드 호출
                        teamList -> {
                            playerNameList = new ArrayList<>();
                            for (TeamInfo team : teamList) {
                                if (team.getTeamName().equals(targetTeamName)) {
                                    playerNameList.add(team.getPlayerName());
                                }
                            }

                            for (String playerName : playerNameList) {
                                System.out.println("player Name: " + playerName);
                            }

                            StringBuilder playerStringBuilder = new StringBuilder();
                            for (String playerName : playerNameList) {
                                playerStringBuilder.append("[ "+playerName+" ]").append("\n");
                                playerCount++;
                            }
                            // 다이얼로그 생성 및 설정
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle(Html.fromHtml("[ " + targetTeamName + " ]     멤버\t<b>[ " + playerCount + " ] 명</b>"))
                                    .setIcon(R.drawable.ic_groups)
                                    .setMessage(playerStringBuilder.toString())
                                    .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())
                                    .show();
                        },
                        throwable -> Log.e("dataLoadAll", "Error loading data: " + throwable.getMessage())
                );
        playerCount = 0;
    }

    private void initializeRoomDB() {
        roomDB = RoomDB.getInstance(getContext());
    }

    // 새로고침
    public void onSwipe() {
        SwipeRefreshLayout swipeRefreshLayout = settingBinding.swip;
        swipeRefreshLayout.setOnRefreshListener(() -> {
            dataLoad(); // 새로고침 시 데이터 로드
            Toast.makeText(requireActivity(), "새로고침", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            countTeam();
        });
    }


    // 팀 이름 가져오기 (중복제거)
    @SuppressLint("CheckResult")
    public void dataLoad() {
        roomDB.getTeamInfoDao().showTeam_Team()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::updateData, // 데이터를 받아와서 업데이트 메소드 호출
                        throwable -> Log.e("dataLoad", "Error loading data: " + throwable.getMessage())
                );
    }

    // 업데이트
    private void updateData(List<TeamInfo> teamInfoList) {
        groupList.clear();
        for (TeamInfo teamInfo : teamInfoList) {
            TeamInfoListAdd(teamInfo);
        }
        adapter.notifyDataSetChanged(); // 데이터 변경 시 어댑터에 알려줌
        countTeam();
    }

    // group에 teamName을 넣어줌
    private void TeamInfoListAdd(TeamInfo teamInfo) {
        Group group = new Group();
        //Log.e("TeamInfoListAdd", teamInfo.getTeamName() + ".." + teamInfo.getPlayerName());
        Log.d("SettingFragment_TeamInfoListAdd", teamInfo.getTeamName() + ".." + teamInfo.getPlayerName());
        group.setGroupName(teamInfo.getTeamName());
        groupList.add(group);
    }

    public void removeItem(String teamName, int pos) {
        createDialog(teamName, pos).show();
    }

    private AlertDialog createDialog(String teamName, int pos) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_title)
                .setIcon(R.drawable.ic_delete)
                .setNegativeButton(R.string.cancel, (dialogInterface, i) ->
                        Toast.makeText(getContext(), R.string.cancel_message, Toast.LENGTH_SHORT).show())
                .create();

        String deleteGroupMsg = getContext().getString(R.string.delete_group_message);
        dialog.setMessage(getHtmlFormattedText(deleteGroupMsg, teamName));
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "삭제", (dialogInterface, i) -> {
            clickDelete(teamName, pos);
            Toast.makeText(getContext(), "그룹 [ " + teamName + " ] 이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        });
        return dialog;
    }


    @SuppressLint("CheckResult")
    private void clickDelete(String teamName, int pos) {
        roomDB.getTeamInfoDao().deleteItem(teamName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            groupList.removeIf(group -> group.getGroupName().equals(teamName));
                            adapter.notifyItemRemoved(pos);
                            settingBinding.tvGroupCountFragmentSetting.setText("그룹: "+groupList.size()+" 팀");
                        },
                        throwable -> Log.e("SettingAdapter", "Error deleting item: " + throwable.getMessage())
                );
    }

    private Spanned getHtmlFormattedText(String messageTemplate, String teamName) {
        String formattedMessage = String.format(messageTemplate, teamName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return HtmlCompat.fromHtml(formattedMessage, HtmlCompat.FROM_HTML_MODE_COMPACT);
        } else {
            @SuppressWarnings("deprecation")
            Spanned spanned = Html.fromHtml(formattedMessage);
            return spanned;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @SuppressLint("CheckResult")
    public void countTeam() {
        TextView tv = settingBinding.tvGroupCountFragmentSetting;
        disposable = roomDB.getTeamInfoDao()
                .showTeam_Team()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        teamInfoList -> {
                            int teamCount = teamInfoList.size();
                            if (teamInfoList.isEmpty()) {
                                tv.setText("그룹: " + 0 + " 팀");
                            } else {
                                tv.setText("그룹: " + teamCount + " 팀");
                            }
                        },
                        throwable -> Log.e("countTeam", "Error counting teams: " + throwable.getMessage())
                );
    }


    private void moveToAddActivity() {
        settingBinding.fragmentSettingBtnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddActivity.class);
            startActivity(intent);
        });
    }

    private void moveToModifyActivity() {
        settingBinding.fragmentSettingBtnModify.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ModifyActivity.class);
            startActivity(intent);
        });
    }

}
