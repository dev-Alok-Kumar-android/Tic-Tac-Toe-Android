package com.alokkumar.tictactoegame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button startGameButton, about, settings, history;
    Spinner btnSpinner;
    int spinnerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startGameButton = findViewById(R.id.startGameButton);
        about = findViewById(R.id.about);
        settings = findViewById(R.id.settings);
        history = findViewById(R.id.history);

        btnSpinner = findViewById(R.id.btnSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.game_modes,android.R.layout.simple_spinner_item);

        btnSpinner.setAdapter(adapter);
        btnSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerPosition=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startGameButton.setOnClickListener(v->{
                    Intent intent = new Intent(MainActivity.this, Game.class);
                    intent.putExtra("Game Mode",spinnerPosition);
                    startActivity(intent);
        });

       about.setOnClickListener(v -> {
            Intent intent = new Intent(this,About_Activity.class);
            startActivity(intent);
       });

        settings.setOnClickListener(v -> {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
        });

        history.setOnClickListener(v -> {
            Intent intent = new Intent(this,History_Activity.class);
            startActivity(intent);
        });
    }
}