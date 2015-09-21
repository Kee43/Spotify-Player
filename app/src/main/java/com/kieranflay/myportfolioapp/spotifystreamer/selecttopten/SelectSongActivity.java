package com.kieranflay.myportfolioapp.spotifystreamer.selecttopten;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.kieranflay.myportfolioapp.R;

public class SelectSongActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_song);

        Intent intent = getIntent();
        String id = intent.getStringExtra("artist_id");

        Bundle bundle = new Bundle();
        bundle.putString("artist_id", id);

        SelectSongFragment selectSongFragment = new SelectSongFragment(false);
        selectSongFragment.setArguments(bundle);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.select_song_container, selectSongFragment)
                    .commit();
        }
    }

}
