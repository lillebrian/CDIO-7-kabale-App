package com.example.a7_kabale.Other;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;
import com.example.a7_kabale.R;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;


/**
 * @author Gruppe 13 - Jacob Christensen s174130, Mads Hansen s195456, Mikkel Johansen s175194, Shania Hau s195477, Stefan Luxhøj s195467
 **/
public class Sound extends AppCompatActivity {

    MediaPlayer swipe1;
    MediaPlayer swipe2;
    MediaPlayer swipe3;
    MediaPlayer shuffle;

    public Sound(Context context) {
        this.swipe1 = MediaPlayer.create(context, R.raw.cardswipe1);
        this.swipe2 = MediaPlayer.create(context, R.raw.cardswipe2);
        this.swipe3 = MediaPlayer.create(context, R.raw.cardswipe3);
        this.shuffle = MediaPlayer.create(context, R.raw.cardshuffle);
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

