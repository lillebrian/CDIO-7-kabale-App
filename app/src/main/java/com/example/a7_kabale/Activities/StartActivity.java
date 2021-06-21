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

public class StartActivity extends AppCompatActivity implements Button.OnClickListener {

    private Button startBtn;
    private Button aboutBtn;
    private Sound sound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //sound = new Sound(getWindow().getDecorView().getRootView());
        startBtn = (Button) findViewById(R.id.startBtn);
        aboutBtn = (Button) findViewById(R.id.aboutBtn);

        startBtn.setOnClickListener(this);
        aboutBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.equals(startBtn)) {
            Intent i = new Intent(this, InstructionActivity.class);
            //sound.playRandomSwipe();
            startActivity(i);
        } else if(aboutBtn.equals(v)) {
            Intent i = new Intent(this, AboutActivity.class);
            //sound.playRandomSwipe();
            startActivity(i);
        }
    }
}