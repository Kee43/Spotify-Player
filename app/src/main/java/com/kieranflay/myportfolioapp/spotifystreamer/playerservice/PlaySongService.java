package com.kieranflay.myportfolioapp.spotifystreamer.playerservice;


import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Kieran on 19/07/2015.
 */
public class PlaySongService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    int savedPlayerPosition;
    public MediaPlayer mediaPlayer;
    private final IBinder mBinder = new LocalBinder();

    @Override
    public boolean onError(MediaPlayer mp, int code, int extra) {
        Log.e("PlaySongService", "Error code: " + code + "Details: " + extra);
        return false;
    }


    public class LocalBinder extends Binder {
        public PlaySongService getService() {
            return PlaySongService.this;
        }
    }

    public void initMusicPlayer(){
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public void playSong(Track track){
            mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(track.preview_url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    public void seekToPlayer(Integer pos){
       mediaPlayer.seekTo(pos);
        mediaPlayer.start();
    }

    public Boolean isTrackPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void pauseSong(){
        savedPlayerPosition = getPlayerPosition();
       mediaPlayer.pause();
    }

    public void resumeSong(){
        mediaPlayer.seekTo(savedPlayerPosition);
        mediaPlayer.start();
    }

    public Integer getTrackDuration(){
        if (isTrackPlaying()){
            return mediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public int getPlayerPosition(){
        return mediaPlayer.getCurrentPosition();
    }
}