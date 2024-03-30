package com.zzangse.ghostgame.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zzangse.ghostgame.databinding.ActivityHelpBinding;

public class HelpActivity extends AppCompatActivity {
    ActivityHelpBinding helpBinding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    private void initView() {
        helpBinding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(helpBinding.getRoot());
    }

}
