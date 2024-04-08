package com.zzangse.ghostgame.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.zzangse.ghostgame.R;
import com.zzangse.ghostgame.databinding.ActivityMainBinding;
import com.zzangse.ghostgame.fragment.HomeFragment;
import com.zzangse.ghostgame.fragment.SettingFragment;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private HomeFragment homeFragment;
    private SettingFragment settingFragment;
    private long backBtnTime  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        homeFragment = new HomeFragment();
        settingFragment = new SettingFragment();


        replace(new HomeFragment());
        BottomNavSelect bottomNavSelect = new BottomNavSelect();
        binding.bottomNav.setOnItemSelectedListener(bottomNavSelect);
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);
    }

    // 뒤로가기   API 33 이상 버전
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            long curTime = System.currentTimeMillis();
            long gapTime = curTime - backBtnTime;
            if (gapTime >= 0 && gapTime <= 2000) {
                Log.d("Back_BTN","handleOnBackPressed");
                finish();
            } else {
                backBtnTime=curTime;
                Snackbar.make(binding.fragmentHome,"한번 더 누르면 종료됩니다.",Snackbar.LENGTH_SHORT).show();
            }
        }
    };


    // 뒤로가기  API 33 미만 버전
    @Override
    public void onBackPressed() {

        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if (gapTime >= 0 && gapTime <= 2000) {
            Log.d("Back_BTN","onBackPressed");
            super.onBackPressed();
        } else {
            backBtnTime = curTime;
            Snackbar.make(binding.fragmentHome, "한번 더 누르면 종료됩니다.", Snackbar.LENGTH_SHORT).show();
        }
    }



    class BottomNavSelect implements NavigationBarView.OnItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.menu_game) {
                replace(homeFragment);
            } else if (item.getItemId() == R.id.menu_setting) {
                replace(settingFragment);
            }
            return true;
        }
    }

    public SettingFragment getSettingFragment() {
        return settingFragment;
    }


    private void replace(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_home, fragment);
        transaction.commit();
    }
}