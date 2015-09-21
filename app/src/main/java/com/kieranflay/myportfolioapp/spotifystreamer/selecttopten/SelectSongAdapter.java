package com.kieranflay.myportfolioapp.spotifystreamer.selecttopten;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kieranflay.myportfolioapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Kieran on 14/06/2015.
 */
public class SelectSongAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> songList;
    private final ArrayList<String> albumList;
    private final ArrayList<String> albumImageId;

    public SelectSongAdapter(Activity context, ArrayList<String> songs, ArrayList<String> albums, ArrayList<String> imageId) {
        super(context, R.layout.list_item_listview_songs, songs);
        this.context = context;
        this.songList = songs;
        this.albumList = albums;
        this.albumImageId = imageId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        SongViewHolder holder;
        View rowView = convertView;

        if(rowView == null) {
            holder = new SongViewHolder();
            rowView = inflater.inflate(R.layout.list_item_listview_songs, parent, false);
            holder.albumTextView = (TextView) rowView.findViewById(R.id.list_item_album_name);
            holder.songTextView = (TextView) rowView.findViewById(R.id.list_item_song_name);
            holder.albumImageView = (ImageView) rowView.findViewById(R.id.list_item_song_album_icon);
            rowView.setTag(holder);
        }
        else {
            holder = (SongViewHolder) convertView.getTag();
        }

        holder.songTextView.setText(songList.get(position));
        holder.albumTextView.setText(albumList.get(position));
        if (albumImageId.get(position) == ""){
            Picasso.with(getContext()).load(R.drawable.ic_launcher).resize(130, 130).centerCrop().into(holder.albumImageView);
        } else {
            Picasso.with(getContext()).load(albumImageId.get(position)).resize(130, 130).centerCrop().into(holder.albumImageView);
        }

        return rowView;
    }

    public static class SongViewHolder {

        public TextView songTextView;
        public TextView albumTextView;
        public ImageView albumImageView;

    }


}
