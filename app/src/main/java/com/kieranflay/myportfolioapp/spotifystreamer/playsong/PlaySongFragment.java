package com.kieranflay.myportfolioapp.spotifystreamer.playsong;




import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kieranflay.myportfolioapp.R;
import com.kieranflay.myportfolioapp.spotifystreamer.playerservice.PlaySongService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

public class PlaySongFragment extends Fragment {

    static View rootView;
    Toast toast;

    static Track CURRENT_TRACK;
    static Integer SONG_LIST_POSITION;
    static ArrayList<String> LIST_TOP_SONGS_ID;
    static TextView tvArtistName;
    static TextView tvAlbumName;
    static TextView tvTrackName;
    static TextView tvTrackStartTime;
    static TextView tvTrackEndTime;
    static ImageView ivAlbumArt;
    static SeekBar sbTrackPlayBar;
    static ImageButton btnPreviousSong;
    static ImageButton btnPlayPauseSong;
    static ImageButton btnNextSong;

    PlaySongService mService;
    boolean mBound = false;
    Handler seekBarHandler = new Handler();

    public PlaySongFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
            Intent playIntent = new Intent(getActivity(), PlaySongService.class);
            getActivity().bindService(playIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Ref: http://stackoverflow.com/questions/16188398/how-to-stop-runnable-on-button-click-in-android
        seekBarHandler.removeCallbacksAndMessages(seekBarRunnable);
        // Unbind from the service
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_play_song, container, false);

        if (getArguments() == null){
            Log.v("Error", "Arguments are null.");
        } else {
            SONG_LIST_POSITION = getArguments().getInt("song_list_position");
            LIST_TOP_SONGS_ID = getArguments().getStringArrayList("list_of_songs");
        }

        // Reference for line below: http://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents
        tvArtistName = (TextView) rootView.findViewById(R.id.tvSongPlayerArtist);
        tvAlbumName = (TextView) rootView.findViewById(R.id.tvSongPlayerAlbumName);
        tvTrackName = (TextView) rootView.findViewById(R.id.tvSongPlayerSongName);
        tvTrackStartTime = (TextView) rootView.findViewById(R.id.tvSongStartTime);
        tvTrackEndTime = (TextView) rootView.findViewById(R.id.tvSongEndTime);
        ivAlbumArt = (ImageView) rootView.findViewById(R.id.ivAlbumImage);
        sbTrackPlayBar = (SeekBar) rootView.findViewById(R.id.sbSongProgressBar);


        sbTrackPlayBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                                                      @Override
                                                      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                          if (mService != null && fromUser) {
                                                              mService.seekToPlayer(progress);
                                                          }
                                                      }

                                                      @Override
                                                      public void onStartTrackingTouch(SeekBar seekBar) {
                                                      }

                                                      @Override
                                                      public void onStopTrackingTouch(SeekBar seekBar) {
                                                      }
                                                  }
        );
        sbTrackPlayBar.setActivated(true);

        btnPreviousSong = (ImageButton) rootView.findViewById(R.id.btnSongPrevious);
        btnPreviousSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousSong();
            }
        });

        btnPlayPauseSong = (ImageButton) rootView.findViewById(R.id.btnSongPlayPause);
        btnPlayPauseSong.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mService.isTrackPlaying()) {
                    updatePlayPauseButtonResToPlay(true);

                    mService.pauseSong();
                } else {
                    updatePlayPauseButtonResToPlay(false);

                    mService.resumeSong();
                }
            }
        });

        btnNextSong = (ImageButton) rootView.findViewById(R.id.btnSongNext);
        btnNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });

        GetTrackTask selectedTrack = new GetTrackTask();
        selectedTrack.execute(LIST_TOP_SONGS_ID.get(SONG_LIST_POSITION));

        return rootView;
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlaySongService.LocalBinder binder = (PlaySongService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void playSong(){
        // Service
        mService.playSong(CURRENT_TRACK);
        sbTrackPlayBar.setMax(mService.getTrackDuration());

        // Text fields and buttons
        updatePlayPauseButtonResToPlay(false);
        tvTrackStartTime.setText("00:00");
        tvTrackEndTime.setText(String.format("00:%d", mService.getTrackDuration() / 1000));
        sbTrackPlayBar.setProgress(mService.getPlayerPosition());
        seekBarHandler.postDelayed(seekBarRunnable, 1000);
    }

    Runnable seekBarRunnable = new Runnable() {

        @Override
        public void run() {
            // Stop the interaction if bound
            if (mBound){
                sbTrackPlayBar.setProgress(mService.getPlayerPosition());
                seekBarHandler.postDelayed(seekBarRunnable, 1000);
            }


        }
    };

    public void updatePlayPauseButtonResToPlay(Boolean aSetPlay) {
        String image;
        if (aSetPlay){
            image = "@android:drawable/ic_media_play";
        } else {
            image = "@android:drawable/ic_media_pause";
        }
        int imageResource = getResources().getIdentifier(image, null, getActivity().getPackageName());
        Drawable drawable = getResources().getDrawable(imageResource);
        btnPlayPauseSong.setImageDrawable(drawable);
    }



    public void playNextSong() {
        if ((SONG_LIST_POSITION + 1) < LIST_TOP_SONGS_ID.size()) {
            SONG_LIST_POSITION = SONG_LIST_POSITION + 1;
            GetTrackTask getNextTrack = new GetTrackTask();
            getNextTrack.execute(LIST_TOP_SONGS_ID.get(SONG_LIST_POSITION));
        } else {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(getActivity(), R.string.end_of_list, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void playPreviousSong() {
        if ((SONG_LIST_POSITION - 1) > 0) {
            SONG_LIST_POSITION = SONG_LIST_POSITION - 1;
            GetTrackTask getPreviousTrack = new GetTrackTask();
            getPreviousTrack.execute(LIST_TOP_SONGS_ID.get(SONG_LIST_POSITION));
        } else {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(getActivity(), R.string.end_of_list, Toast.LENGTH_LONG);
            toast.show();
        }

    }

    public class GetTrackTask extends AsyncTask<String, Integer, Track> {

        @Override
        protected Track doInBackground(String... params) {

            try{
                final SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                return spotify.getTrack(params[0]);

            }catch(Exception e){
                Log.v("Error" , "Cannot process request");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Track aTrack) {

            tvArtistName.setText(aTrack.artists.get(0).name);
            tvAlbumName.setText(aTrack.album.name);
            tvTrackName.setText(aTrack.name);

            if (aTrack.album.images.size() == 0) {
                Picasso.with(getActivity()).load(R.drawable.ic_launcher).into(ivAlbumArt);
            } else {
                Picasso.with(getActivity()).load(aTrack.album.images.get(0).url).into(ivAlbumArt);
            }

            // Assign the new current Track object
            CURRENT_TRACK = aTrack;
            playSong();
        }
    }



}



