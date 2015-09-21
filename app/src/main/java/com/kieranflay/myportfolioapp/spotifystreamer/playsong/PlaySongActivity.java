package com.kieranflay.myportfolioapp.spotifystreamer.playsong;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.kieranflay.myportfolioapp.R;

public class PlaySongActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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
