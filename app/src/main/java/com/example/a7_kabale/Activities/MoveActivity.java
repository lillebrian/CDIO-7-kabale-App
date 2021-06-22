package com.example.a7_kabale.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.a7_kabale.Adapters.MoveAdapter;
import com.example.a7_kabale.Logic.AppController;
import com.example.a7_kabale.Logic.Card;
import com.example.a7_kabale.Other.Sound;
import com.example.a7_kabale.R;

import java.util.ArrayList;
import java.util.LinkedList;

public class MoveActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences savedVars;
    int LAUNCH_STACK = 0;
    int LAUNCH_DECK = 0;
    int LAUNCH_CHECK = 0;
    ArrayList<String> resultStacks;
    ArrayList<String> resultDeck;
    AppController logic;
    LinkedList<Card> moves = new LinkedList<>();
    Button nextStep;
    Sound sound;
    TextView instruct;

    private RecyclerView moveRecyclerView;
    private RecyclerView.Adapter moveAdapter;
    private RecyclerView.LayoutManager recyclerViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move);

        sound = new Sound(getApplicationContext());

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
        moveAdapter = new MoveAdapter(moves);
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
        this.deleteSharedPreferences("saveCards");
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
            if(resultCode == this.RESULT_OK){
                resultStacks = data.getStringArrayListExtra("result");
                System.out.println(resultStacks);

                /* Launching intent to scan the 24 cards for the deck */
                Intent i = new Intent(this, ScannerActivity.class);
                i.putExtra("amount", 24);
                startActivityForResult(i, LAUNCH_DECK);
            }
            if (resultCode == this.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }

        if (requestCode == LAUNCH_DECK) {
            if(resultCode == this.RESULT_OK) {
                resultDeck = data.getStringArrayListExtra("result");
                System.out.println(resultDeck);

                logic.RunAlgorithm(resultStacks, resultDeck);
                extractMovesToScreen(logic.returnBestMoves());

                Intent i = new Intent(this, ScannerActivity.class);
                if (logic.cardsToTurn() != 0) {
                    i.putExtra("amount", logic.cardsToTurn());
                    startActivityForResult(i, LAUNCH_CHECK);
                }
            }
        }

        if (requestCode == LAUNCH_CHECK) {
            if(resultCode == this.RESULT_OK) {

                logic.RunAlgorithm(data.getStringArrayListExtra("result"));

                if(logic.isWon()) {
                    instruct.setText("Congratulations! The algorithm solved the game.");
                    nextStep.setOnClickListener(v -> {
                        this.onDestroy();
                    });
                }
                else if (logic.isLost()) {
                    instruct.setText("Couldn't be solved by our algorithm >:(");
                    nextStep.setOnClickListener(v -> {
                        this.onDestroy();
                    });
                } else
                    extractMovesToScreen(logic.returnBestMoves());

                Intent i = new Intent(this, ScannerActivity.class);
                nextStep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (logic.cardsToTurn() != 0) {
                            i.putExtra("amount", logic.cardsToTurn());
                            startActivityForResult(i, LAUNCH_CHECK);
                        }
                    }
                });
            }
        }
    }

    public void extractMovesToScreen(LinkedList<Card> cards) {
        moves.addAll(cards);
    }
}