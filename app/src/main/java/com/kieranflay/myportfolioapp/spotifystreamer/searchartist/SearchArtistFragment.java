package com.kieranflay.myportfolioapp.spotifystreamer.searchartist;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.kieranflay.myportfolioapp.R;
import com.kieranflay.myportfolioapp.spotifystreamer.playsong.PlaySongFragment;
import com.kieranflay.myportfolioapp.spotifystreamer.selecttopten.SelectSongActivity;
import com.kieranflay.myportfolioapp.spotifystreamer.selecttopten.SelectSongFragment;

import java.util.ArrayList;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by Kieran on 14/06/2015.
 */
public class SearchArtistFragment extends Fragment {

    static SearchArtistsAdapter searchArtistsAdapter;
    static ListView listViewArtists;
    static View rootView;
    Toast toast;


    public SearchArtistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_search_artist, container, false);
        listViewArtists = (ListView) rootView.findViewById(R.id.listview_artists);


        final SearchView svSearchArtist = (SearchView) rootView.findViewById(R.id.sv_search_artist);
        svSearchArtist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.v("onClick", "searching");
                if (isNetworkAvailable()){
                    GetArtistsTask getArtists = new GetArtistsTask();
                    getArtists.execute(svSearchArtist.getQuery().toString());
                    return true;
                } else {
                    Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            //Based on a stackoverflow snippet
            private boolean isNetworkAvailable() {
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        });
        return rootView;
    }

    public class GetArtistsTask extends AsyncTask<String, Integer, ArtistsPager> {

        @Override
        protected ArtistsPager doInBackground(String... params) {

            try{
                final SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                return spotify.searchArtists(params[0]);

            }catch(Exception e){
                Log.v("Error" , "Cannot process request");
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArtistsPager artists) {

            final ArrayList<String> artist_names = new ArrayList<String>();
            ArrayList<String> artist_image_url = new ArrayList<String>();
            final ArrayList<String> artist_id = new ArrayList<String>();

            if (artists == null || artists.artists.items.isEmpty()) {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(getActivity(), R.string.no_artists_found, Toast.LENGTH_LONG);
                toast.show();
            } else {
                for (Artist artist : artists.artists.items) {
                    artist_names.add(artist.name);
                    if (artist.images.size() == 0) {
                        artist_image_url.add("");
                    } else {
                        artist_image_url.add(artist.images.get(0).url);
                    }
                    artist_id.add(artist.id);
                }

                searchArtistsAdapter = new SearchArtistsAdapter(getActivity(), artist_names, artist_image_url);
                listViewArtists.setAdapter(searchArtistsAdapter);
                searchArtistsAdapter.notifyDataSetChanged();
                listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {


                        SelectSongFragment selectSongFragment = (SelectSongFragment) getFragmentManager()
                                .findFragmentById(R.id.select_song_container);

                        String selected_artist_id = artist_id.get(position);

                        // If the song fragment is not null, it is the two pane layout
                        // Hence, we can add the fragment without changing activities
                        if (selectSongFragment != null){

                            Bundle bundle= new Bundle();
                            bundle.putString("artist_id", selected_artist_id);

                            selectSongFragment = new SelectSongFragment(true);
                            selectSongFragment.setArguments(bundle);
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.select_song_container, selectSongFragment)
                                        .commit();
                        } else {
                            // It is not the two pane layout, so start a new activity
                            Intent intent = new Intent(getActivity(), SelectSongActivity.class)
                                    .putExtra("artist_id", selected_artist_id);
                            startActivity(intent);
                        }
                    }
                });

            }

        }
    }
}
