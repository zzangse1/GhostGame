package com.zzangse.ghostgame.fragment;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.zzangse.ghostgame.GameEditPenalty;
import com.zzangse.ghostgame.R;
import com.zzangse.ghostgame.TeamInfoViewModel;
import com.zzangse.ghostgame.activity.GameActivity;
import com.zzangse.ghostgame.adapter.EditAdapter;
import com.zzangse.ghostgame.database.RoomDB;
import com.zzangse.ghostgame.database.TeamInfo;
import com.zzangse.ghostgame.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

public class HomeFragment extends Fragment {
    private RoomDB roomDB = null;
    private ArrayList<String> teamNameList;
    private EditAdapter adapter;
    private TeamInfoViewModel teamInfoViewModel;
    private FragmentHomeBinding homeBinding;
    private Disposable disposable;
    private ArrayList<GameEditPenalty> gameEditPenaltyList = new ArrayList<>();
    private String mGroupName;
    private String mGameName;
    private Balloon balloon;
    private int teamInfoSize=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomDB = RoomDB.getInstance(requireContext().getApplicationContext());
        teamInfoViewModel = new ViewModelProvider(requireActivity()).get(TeamInfoViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeBinding = FragmentHomeBinding.inflate(inflater);

        return homeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeRoomDB();
        initSpinner();
        setSpinnerEvents();
        initRecycler();
        setBtnClickEvent();
        moveToAddGameActivity();
        initTooltip();
        showTooltip();
        test();
    }


    private void initTooltip() {
        balloon = new Balloon.Builder(requireContext())
                .setArrowSize(10)
                //.setIconDrawable(ContextCompat.getDrawable(this, R.drawable.ic_info))
                .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                .setArrowOrientation(ArrowOrientation.END)
                .setArrowPosition(0.3f)
                //.setArrowVisible(true)
                .setWidthRatio(0.3f) // 말풍선 넓이
                .setHeight(50)
                .setTextSize(14f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setText("사용 설명서")
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build();

    }
    private void initializeRoomDB() {
        roomDB = RoomDB.getInstance(getContext());
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeBinding = null;
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

    // gameName을 저장
    private void setGameName() {
        mGameName = homeBinding.etGameName.getText().toString();
        Log.d("setGameName", "mGameName: " + mGameName);
    }

    private void initSpinner() {
        homeBinding.spinnerHome.setTitle("그룹을 선택해주세요");
        homeBinding.spinnerHome.setPositiveButton("취소");
        teamInfoViewModel.getShowTeam().observe(getViewLifecycleOwner(), new Observer<List<TeamInfo>>() {
            @Override
            public void onChanged(List<TeamInfo> teamInfoList) {
                teamNameList = new ArrayList<>();
                // teamNameList에 각각의 팀 이름 저장
                for (TeamInfo teamInfo : teamInfoList) {
                    String teamName = teamInfo.getTeamName();
                    teamNameList.add(teamName);
                }
                // spinner에 값 삽입
                updateSpinner(teamNameList);
            }
        });
    }

    private void initRecycler() {
        RecyclerView recyclerView = homeBinding.rvGameEdit;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new EditAdapter(getContext(), gameEditPenaltyList);
        recyclerView.setAdapter(adapter);

        adapter.setOnclick(new EditAdapter.EditAdapterClick() {
            @Override
            public void onClickDelete(GameEditPenalty gameEditPenalty) {
                AlertDialog dialog = createDialog(gameEditPenalty.getPenalty(), gameEditPenaltyList.indexOf(gameEditPenalty));
                Log.d("onClickDelete",gameEditPenalty.getPenalty());
                dialog.show();
            }

            @Override
            public void onClickInfo(GameEditPenalty gameEditPenalty) {
                setRandomText(gameEditPenalty.getPenalty());
            }
        });

    }

    // text 정보를 보여줌
    private void setRandomText(String text) {
        Toast.makeText(getContext(), "벌칙: " + text, Toast.LENGTH_SHORT).show();
    }

    // 삭제 버튼 클릭시 다이얼로그 창 띄움
    public AlertDialog createDialog(String randName, int pos) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_title)
                .setIcon(R.drawable.ic_delete)
                .setNegativeButton(R.string.cancel, (dialogInterface, i) ->
                        Toast.makeText(getContext(), R.string.cancel_message, Toast.LENGTH_SHORT).show())
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("createDialog", randName);
                        Toast.makeText(getContext(), "벌칙 [ " + randName + " ] 이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        deleteItem(pos);
                    }
                })
                .create();

        String deleteEditMsg = "해당 벌칙을 삭제하시겠습니까? [ "+randName+" ] 삭제 후 되돌릴 수 없습니다!";
        dialog.setMessage(getHtmlFormattedText(deleteEditMsg, randName));
        return dialog;
    }

    private void test() {
        homeBinding.ibTooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = createDialog();
                dialog.show();
            }
        });
    }
    // 설명
    public AlertDialog createDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_title)
                .setIcon(R.drawable.ic_delete)
                .setNegativeButton(R.string.cancel, (dialogInterface, i) ->
                        Toast.makeText(getContext(), R.string.cancel_message, Toast.LENGTH_SHORT).show())
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getContext(), "벌칙 [ " + "ㄴㅇ" + " ] 이 삭제되었습니다", Toast.LENGTH_SHORT).show();

                    }
                })
                .create();

//        String deleteEditMsg = "해당 벌칙을 삭제하시겠습니까? [ "+"randName"+" ] 삭제 후 되돌릴 수 없습니다!";
//        dialog.setMessage(getHtmlFormattedText(deleteEditMsg, randName));
        return dialog;
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

    // 리싸이클러뷰 아이템 삭제
    private void deleteItem(int pos) {
        if (pos != RecyclerView.NO_POSITION) {
            gameEditPenaltyList.remove(pos);
            adapter.notifyItemRemoved(pos);
        }
    }

    private void showTooltip() {
        balloon.showAlignStart(homeBinding.ibTooltip);
    }

    private void updateSpinner(ArrayList<String> teamNameList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, teamNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        homeBinding.spinnerHome.setAdapter(adapter);
    }

    // 스피너에서 아이템 선택했을 때 이벤트 처리
    private void setSpinnerEvents() {
        homeBinding.spinnerHome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGroupName = homeBinding.spinnerHome.getItemAtPosition(position).toString();
                Log.d("HomeFragment_onItemSelected", "spinner: " + mGroupName);
                countTeam(mGroupName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    // 그룹에 속해있는 팀원의 개수를 보여주는 로직
    private void countTeam(String teamName) {
        teamInfoViewModel.getPlayerByTeam(teamName).observe(this, new Observer<List<TeamInfo>>() {
            @Override
            public void onChanged(List<TeamInfo> teamInfo) {
                homeBinding.tvGroupCount.setText(teamInfo.size() + " 명");
                teamInfoSize = teamInfo.size();
                Log.d("countTeam: ",""+teamInfoSize);
            }
        });
    }

    // game결과 화면으로 이동
    private void setBtnClickEvent() {
        homeBinding.ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    addEditTextItem();
                    clearEditTextItem();
            }
        });

    }


    // editText 내용 지우기
    private void clearEditTextItem() {
        homeBinding.etRandomLabelHint.getText().clear();
    }

    // editText에 넣은 값 리싸이클러뷰에 넣기
    private void addEditTextItem() {
        String randomLabel = homeBinding.etRandomLabelHint.getText().toString();
        if (!randomLabel.isEmpty()) {
           addPenalty(randomLabel);
        } else {
            Toast.makeText(getContext(), "벌칙을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    private void addPenalty(String randomLabel) {
        GameEditPenalty newItem = new GameEditPenalty(randomLabel);
        if (teamInfoSize > gameEditPenaltyList.size()) {
            gameEditPenaltyList.add(newItem);
            adapter.notifyItemInserted(gameEditPenaltyList.size() - 1);
        } else {
            Toast.makeText(getContext(),"벌칙은 팀원보다 많을 수 없습니다.",Toast.LENGTH_SHORT).show();
        }

    }

    private void sendIntent() {
        Intent intent = new Intent(requireActivity(), GameActivity.class);
        intent.putExtra("gameNameKey", mGameName);
        intent.putExtra("groupNameKey", mGroupName);

        for (int i = 0; i < gameEditPenaltyList.size(); i++) {
            Log.d("getPenalty", gameEditPenaltyList.get(i).toString());
            intent.putExtra("gamePenalty" + i, gameEditPenaltyList.get(i).toString());
        }
        intent.putExtra("gamePenaltySize", gameEditPenaltyList.size());
        Log.d("sendIntent: ", mGameName + ", " + mGroupName + ", " + gameEditPenaltyList.size());

        startActivity(intent);
    }



    private void moveToAddGameActivity() {
        homeBinding.btnGameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeBinding.etRandomLabelHint.clearFocus();

                setGameName();
                someMethodWhereYouWantToSendData();
                sendIntent();
            }
        });
    }

    private void someMethodWhereYouWantToSendData() {
        // gameName과 groupName은 프래그먼트에서 얻은 값으로 가정합니다.
        teamInfoViewModel.setGameName(mGameName);
        teamInfoViewModel.setGroupName(mGroupName);

        Log.d("someMethod", "mGameName: " + mGameName + ", mGroupName: " + mGroupName);
    }

}
