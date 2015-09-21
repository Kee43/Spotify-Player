package com.kieranflay.myportfolioapp.spotifystreamer.playsong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;

import com.kieranflay.myportfolioapp.R;

/**
 * Created by Kieran on 19/07/2015.
 */
public class PlaySongDialogActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Remove the title for the dialog
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();

        PlaySongFragment playSongFragment = new PlaySongFragment();
        playSongFragment.setArguments(bundle);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.spotifyPlaySongContainer, playSongFragment)
                    .commit();
        }
    }

}

