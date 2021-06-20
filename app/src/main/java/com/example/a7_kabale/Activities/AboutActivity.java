package com.example.a7_kabale.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a7_kabale.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextView;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        backBtn = findViewById(R.id.aboutBack);
        mTextView = (TextView) findViewById(R.id.text);

        backBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, StartActivity.class);
        startActivity(i);
    }
}