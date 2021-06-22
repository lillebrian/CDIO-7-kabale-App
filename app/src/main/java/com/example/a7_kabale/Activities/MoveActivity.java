package com.example.a7_kabale.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.a7_kabale.Adapters.MoveAdapter;
import com.example.a7_kabale.Logic.AppController;
import com.example.a7_kabale.Logic.Card;
import com.example.a7_kabale.Logic.Logic;
import com.example.a7_kabale.Other.Sound;
import com.example.a7_kabale.R;

import java.util.ArrayList;
import java.util.LinkedList;

public class MoveActivity extends AppCompatActivity implements View.OnClickListener {


    int LAUNCH_STACK = 0;
    int LAUNCH_DECK = 1;
    int LAUNCH_CHECK = 2;
    ArrayList<String> resultStacks;
    ArrayList<String> resultDeck;
    AppController logic;
    LinkedList<Card> moves = new LinkedList<>();
    Button nextStep;
    Sound sound;
    TextView instruct;
    Context context;

    private RecyclerView moveRecyclerView;
    private RecyclerView.Adapter moveAdapter;
    private RecyclerView.LayoutManager recyclerViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move);
        this.deleteSharedPreferences("SavedCards");
        sound = new Sound(getApplicationContext());
        logic = new AppController();

        instruct = findViewById(R.id.instructBestMove);
        instruct.setText("The sequence of best moves will be showed here when cards have been scanned. ");
        nextStep = findViewById(R.id.nextStepButton);
//        moves.add(new Card(true, 7, 2));
//        moves.add(new Card(true, 6, 0));
//        moves.add(new Card(true, 5, 3));
//        moves.add(new Card(true, 4, 1));
//        moves.add(new Card(true, 12, 1));
//        moves.add(new Card(true,13 , 2));
//        moves.add(new Card(true, 2, 1));
//        moves.add(new Card(true, 3, 2));
        moveRecyclerView = findViewById(R.id.moveRecyclerView);
        moveRecyclerView.setHasFixedSize(true);
        recyclerViewManager = new LinearLayoutManager(this);


        nextStep.setOnClickListener(this);
        //Logic Initialised

        //request for camera permission
        if (ContextCompat.checkSelfPermission(MoveActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            //directly ask for the permissions
            ActivityCompat.requestPermissions(MoveActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //this.deleteSharedPreferences("saveCards");
        startActivity(new Intent(this, StartActivity.class));
    }

    @Override
    public void onClick(View v) {
        //Move have been performed by player and is ready to continue to next move
        Intent i = new Intent(this, ScannerActivity.class);
        sound.playRandomSwipe();
        instruct.setText("");
        /* Starting intent to scan the 7 cards from the buildstacks */
        i.putExtra("amount", 7);
        startActivityForResult(i, LAUNCH_STACK);
    }


    /**
     * @author Mads H. S195456
     * @author Mikkel J. S175149
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_STACK) {
            if (resultCode == this.RESULT_OK) {
                resultStacks = data.getStringArrayListExtra("result");
                System.out.println(resultStacks);

                /* Launching intent to scan the 24 cards for the deck */
                Intent i = new Intent(context, ScannerActivity.class);
                i.putExtra("amount", 24);
                startActivityForResult(i, LAUNCH_DECK);
            }
        } else if (requestCode == LAUNCH_DECK) {
            if (resultCode == this.RESULT_OK) {
                resultDeck = data.getStringArrayListExtra("result");
                System.out.println(resultDeck);

                logic.RunAlgorithm(resultStacks, resultDeck);
                extractMovesToScreen(logic.returnBestMoves());

                nextStep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, ScannerActivity.class);
                        i.putExtra("amount", logic.cardsToTurn());
                        startActivityForResult(i, LAUNCH_CHECK);

                    }
                });

            }
        } else if (requestCode == LAUNCH_CHECK) {
            if (resultCode == this.RESULT_OK) {

                do {


                logic.RunAlgorithm(data.getStringArrayListExtra("result"));

                if (logic.isWon()) {
                    instruct.setText("Congratulations! The algorithm solved the game.");
                    nextStep.setOnClickListener(v -> {
                        this.onDestroy();
                    });
                } else if (logic.isLost()) {
                    instruct.setText("Couldn't be solved by our algorithm >:(");
                    nextStep.setOnClickListener(v -> {
                        this.onDestroy();
                    });
                } else
                    extractMovesToScreen(logic.returnBestMoves());
                }
                while (logic.cardsToTurn() == 0);
                Intent i = new Intent(context, ScannerActivity.class);
                moves = new LinkedList<Card>();
                nextStep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        i.putExtra("amount", logic.cardsToTurn());
                        startActivityForResult(i, LAUNCH_CHECK);
                    }
                });
            }
        }

    }

    public void extractMovesToScreen(LinkedList<Card> cards) {
        moves.addAll(cards);
        moveAdapter = new MoveAdapter(moves);
        moveRecyclerView.setLayoutManager(recyclerViewManager);
        moveRecyclerView.setAdapter(moveAdapter);

    }
}