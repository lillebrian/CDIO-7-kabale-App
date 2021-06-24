package com.example.a7_kabale.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a7_kabale.Other.Sound;
import com.example.a7_kabale.R;
/**
 * @author Gruppe 13 - Jacob Christensen s174130, Mads Hansen s195456, Mikkel Johansen s175194, Shania Hau s195477, Stefan Luxhøj s195467
 **/
public class StartActivity extends AppCompatActivity implements Button.OnClickListener {

    private Button startBtn;
    private Button aboutBtn;
    private Sound sound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        sound = new Sound(getApplicationContext());
        startBtn = (Button) findViewById(R.id.startBtn);
        aboutBtn = (Button) findViewById(R.id.aboutBtn);

        startBtn.setOnClickListener(this);
        aboutBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.equals(startBtn)) {
            sound.playRandomSwipe();
            Intent i = new Intent(this, InstructionActivity.class);
            startActivity(i);
        } else if(aboutBtn.equals(v)) {
            sound.playRandomSwipe();
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        }
    }
}