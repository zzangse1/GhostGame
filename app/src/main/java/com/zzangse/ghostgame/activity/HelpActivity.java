package com.zzangse.ghostgame.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.zzangse.ghostgame.ListItem;
import com.zzangse.ghostgame.ListItemModel;
import com.zzangse.ghostgame.R;
import com.zzangse.ghostgame.ViewPager2Adapter;
import com.zzangse.ghostgame.databinding.ActivityHelpBinding;

import java.util.ArrayList;

public class HelpActivity extends AppCompatActivity {
    ActivityHelpBinding helpBinding;
    private int[] img = new int[]{

    };
    private ArrayList<ListItem> list;
    private ArrayList<String> nameList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        onClickBackBtn();
        initialize();
        listAdd();
        viewPagerConnection();
//        viewPagerSlideEvent();
    }

    private void initView() {
        helpBinding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(helpBinding.getRoot());
    }

    private void onClickBackBtn() {
        helpBinding.toolbarHome.setNavigationOnClickListener(v -> finish());
    }

    private void listAdd() {
        String imageUri = "drawable://";
        addItem(imageUri+R.drawable.bottom_nav);
        addItem(imageUri+R.drawable.setting_screen_before);
        addItem(imageUri+R.drawable.setting_screen_add);
        addItem(imageUri+R.drawable.group_plain_screen);
        addItem(imageUri+R.drawable.group_info_screen);
        addItem(imageUri+R.drawable.group_delete_screen);
        addItem(imageUri+R.drawable.group_modify_plain_screen);
        addItem(imageUri+R.drawable.group_modify_add_screen);
        addItem(imageUri+R.drawable.group_modify_delete_screen);
        addItem(imageUri+R.drawable.home_screen);
        addItem(imageUri+R.drawable.home_choice_screen);
        addItem(imageUri+R.drawable.home_add_screen);
        addItem(imageUri+R.drawable.game_result_plain_screen);
        addItem(imageUri+R.drawable.game_result_screen);
        addItem(imageUri+R.drawable.game_result_init_screen);

    }

    private void initialize(){
        list = new ArrayList<>();
    }


    private void addItem(String imagePath) {
        ListItemModel listItemModel = new ListItemModel();
        listItemModel.setImagePath(imagePath);

        ListItem listItem = new ListItem();

        ArrayList<ListItemModel> items = new ArrayList<>();

        items.add(listItemModel);

        listItem.setList(items);

        list.add(listItem);
    }

    private void viewPagerConnection(){
        helpBinding.viewPager2.setAdapter(new ViewPager2Adapter(this, this, list));
        new TabLayoutMediator(helpBinding.tabLayout, helpBinding.viewPager2, (tab, position) -> {

        }).attach();

    }

    /**
     * @DESC: 슬라이드 이벤트 발생
     */
    private void viewPagerSlideEvent(){
        helpBinding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                showToast(nameList.get(position));
            }
        });
    }

    private void showToast(String msg){
        Toast.makeText(this, msg+" 선택됨", Toast.LENGTH_SHORT).show();
    }
}
