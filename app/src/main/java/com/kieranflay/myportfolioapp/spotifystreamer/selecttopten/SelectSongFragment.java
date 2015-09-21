package com.kieranflay.myportfolioapp.spotifystreamer.selecttopten;



import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kieranflay.myportfolioapp.R;
import com.kieranflay.myportfolioapp.spotifystreamer.playsong.PlaySongActivity;
import com.kieranflay.myportfolioapp.spotifystreamer.playsong.PlaySongDialogActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class SelectSongFragment extends Fragment {

    static String SELECTED_ARTIST_ID;
    ListView listViewTopTenSongs;
    View rootView;
    Toast toast;
    ArrayList<String> songIdList;
    ArrayList<Track> trackArrayList;
    boolean tabletMode;

    public SelectSongFragment(boolean aTabletView) {
        tabletMode = aTabletView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_select_song, container, false);

        if (getArguments() != null){
            SELECTED_ARTIST_ID = getArguments().getString("artist_id");
        } else {
            return rootView;
        }

        listViewTopTenSongs = (ListView) rootView.findViewById(R.id.listview_songs);

        GetTopSongsTask getTopSong = new GetTopSongsTask();
        getTopSong.execute();

        listViewTopTenSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Log.v("tabletMode", "Tablet mode is: " + tabletMode);
                if (tabletMode){
                    Intent intent = new Intent(getActivity(), PlaySongDialogActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("song_list_position", position);
                    bundle.putStringArrayList("list_of_songs", songIdList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), PlaySongActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("song_list_position", position);
                    bundle.putStringArrayList("list_of_songs", songIdList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    public static String getArtistId(){
        return SELECTED_ARTIST_ID;
    }

    public class GetTopSongsTask extends AsyncTask<String, Integer, Tracks> {

        @Override
        protected void onPreExecute(){
            //TODO - Maybe add a loading dialog in the future
        }

        @Override
        protected Tracks doInBackground(String... params) {

            try{
                SpotifyApi api = new SpotifyApi();

                SpotifyService spotify = api.getService();
                HashMap<String,Object> spotifySettings = new HashMap<>();
                spotifySettings.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());

                return spotify.getArtistTopTrack(getArtistId(), spotifySettings);

            }catch(Exception e){
                Log.v("Error" , "Cannot process request - " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... params) {
            //TODO - Maybe add a loading dialog in the future
        }

        @Override
        protected void onPostExecute(Tracks tracks) {

            final ArrayList<String> artist_id = new ArrayList<String>();
            final ArrayList<String> song_names = new ArrayList<String>();
            final ArrayList<String> album_names = new ArrayList<String>();
            final ArrayList<String> song_album_url = new ArrayList<String>();
            final ArrayList<String> song_preview_url = new ArrayList<String>();

            songIdList = new ArrayList<String>();

            trackArrayList = new ArrayList<Track>();

            if (tracks == null || tracks.tracks.isEmpty()){
                if (toast != null){
                    toast.cancel();
                }
                toast = Toast.makeText(getActivity(), R.string.no_songs_found, Toast.LENGTH_LONG);
                toast.show();
            } else {
                for (Track track : tracks.tracks) {
                    songIdList.add(track.id);
                    trackArrayList.add(track);

                    song_names.add(track.name);
                    album_names.add(track.album.name);
                    artist_id.add(getArtistId());
                    song_preview_url.add(track.preview_url);
                    Log.v("TopTenSong", "Artist: " + getArtistId() + " - Track: " + track.name + " - Album: " + track.album.name);
                    if (track.album.images.size() == 0){
                        song_album_url.add("");
                    } else {
                        song_album_url.add(track.album.images.get(0).url);
                    }
                }

                final SelectSongAdapter searchTopSongsAdapter = new
                        SelectSongAdapter(getActivity(), song_names, album_names, song_album_url);
                listViewTopTenSongs = (ListView) rootView.findViewById(R.id.listview_songs);
                listViewTopTenSongs.setAdapter(searchTopSongsAdapter);

            }
        }
    }
}