package com.kieranflay.myportfolioapp.spotifystreamer.searchartist;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.kieranflay.myportfolioapp.R;
import com.kieranflay.myportfolioapp.spotifystreamer.selecttopten.SelectSongFragment;

public class SearchArtistActivity extends ActionBarActivity {

    boolean tabletView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_artist);

        SelectSongFragment selectSongFragment;

        if (findViewById(R.id.select_song_container) != null) {

            tabletView = true;
            selectSongFragment = new SelectSongFragment(tabletView);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.select_song_container, selectSongFragment)
                        .commit();
            }
        } else {
            tabletView = false;
        }
    }
}

