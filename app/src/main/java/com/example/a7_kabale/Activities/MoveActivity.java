package com.example.a7_kabale.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.a7_kabale.Adapters.MoveAdapter;
import com.example.a7_kabale.Items.MoveItem;
import com.example.a7_kabale.R;

import java.util.ArrayList;

public class MoveActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences savedVars;
    int LAUNCH_SECOND_ACTIVITY = 1;

    private RecyclerView moveRecyclerView;
    private RecyclerView.Adapter moveAdapter;
    private RecyclerView.LayoutManager recyclerViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move);

        TextView title = findViewById(R.id.moveTitle);
        Button nextStep = findViewById(R.id.nextStepButton);
        ArrayList<MoveItem> exampleList = new ArrayList<>();
        exampleList.add(new MoveItem(7.1f, "♥️6 → ♠️7" ));
        exampleList.add(new MoveItem(4.3f, "♥️Q → ♣️️K" ));
        exampleList.add(new MoveItem(2.0f, "♠2️ → ️♦️7" ));
        exampleList.add(new MoveItem(1.0f, "Draw" ));

        moveRecyclerView = findViewById(R.id.moveRecyclerView);
        moveRecyclerView.setHasFixedSize(true);
        recyclerViewManager = new LinearLayoutManager(this);
        moveAdapter = new MoveAdapter(exampleList);
        moveRecyclerView.setLayoutManager(recyclerViewManager);
        moveRecyclerView.setAdapter(moveAdapter);

        nextStep.setOnClickListener(this);
        //Logic Initialised

        savedVars = getSharedPreferences("Results", MODE_PRIVATE);
        int inter = savedVars.getInt("inter", 0);
        System.out.println(inter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        savedVars = getSharedPreferences("Results", MODE_PRIVATE);
        int inter = savedVars.getInt("inter", 0);
        System.out.println(inter);
        //Call for empty cards check!
            //If empty cards attempt to Send in cards
            //else
                //Call to Run algorithm
                //Call fo empty cards check!
                //if empty cards call the scanner
                //else
                //Check for if Won
        //
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        //Move have been performed by player and is ready to continue to next move
        //int emptyCards = Logic.getEmpty()
        Intent i = new Intent(this, ScannerActivity.class);
        //intent.putExtra("amount", emptyCards);

        //For Testing
        i.putExtra("amount", 1);
        startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == this.RESULT_OK){
                ArrayList<String> result = data.getStringArrayListExtra("result");
                System.out.println(result);
            }
            if (resultCode == this.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    } //onActivityResul
}