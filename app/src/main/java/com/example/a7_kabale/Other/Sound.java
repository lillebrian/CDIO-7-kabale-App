package com.example.a7_kabale.Other;

import android.media.MediaPlayer;
import android.view.View;
import com.example.a7_kabale.R;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;



public class Sound extends AppCompatActivity {

    MediaPlayer swipe1;
    MediaPlayer swipe2;
    MediaPlayer swipe3;
    MediaPlayer shuffle;

    public Sound(View view) {
        MediaPlayer swipe1 = MediaPlayer.create(view.getContext(), R.raw.cardswipe1);
        MediaPlayer swipe2 = MediaPlayer.create(view.getContext(), R.raw.cardswipe2);
        MediaPlayer swipe3 = MediaPlayer.create(view.getContext(), R.raw.cardswipe3);
        MediaPlayer shuffle = MediaPlayer.create(view.getContext(), R.raw.cardshuffle);
    }

    public void playRandomSwipe() {
        Random r = new Random();
        int i  = r.nextInt(3)+1;
        switch (i) {
            case 1 :
                swipe1.start();
                break;
            case 2 :
                swipe2.start();
                break;
            case 3 :
                swipe3.start();
                break;
            default:
                break;
        }
    }

    public void playShuffle() {
        shuffle.start();
    }
}

