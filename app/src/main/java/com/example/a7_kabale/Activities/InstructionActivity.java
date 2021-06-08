package com.example.a7_kabale.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.a7_kabale.R;

public class InstructionActivity extends AppCompatActivity implements View.OnClickListener{

    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        nextBtn = (Button) findViewById(R.id.instructionNextBtn);

        nextBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.equals(nextBtn)) {
            Intent i = new Intent(this, ScannerActivity.class);
            startActivity(i);
        }
    }
}