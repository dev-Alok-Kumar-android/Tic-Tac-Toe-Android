package com.alokkumar.tictactoegame;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.alokkumar.tictactoegame.databinding.ActivityAboutBinding;

public class About_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.alokkumar.tictactoegame.databinding.ActivityAboutBinding binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.fab.setOnClickListener(view -> WebUtils.openWebPage(this,view,"https://www.google.com"));
    }
}