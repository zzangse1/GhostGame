package com.zzangse.ghostgame.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.zzangse.ghostgame.R;
import com.zzangse.ghostgame.databinding.ActivityMainBinding;
import com.zzangse.ghostgame.fragment.HomeFragment;
import com.zzangse.ghostgame.fragment.SettingFragment;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private HomeFragment homeFragment;
    private SettingFragment settingFragment;

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