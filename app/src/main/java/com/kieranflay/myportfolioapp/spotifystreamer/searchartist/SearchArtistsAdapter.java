package com.kieranflay.myportfolioapp.spotifystreamer.searchartist;

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

public class SearchArtistsAdapter extends ArrayAdapter<String>{

    private final Activity context;
    private final ArrayList<String> artistNames;
    private final ArrayList<String> imageIds;

    public SearchArtistsAdapter(Activity context, ArrayList<String> artistNames, ArrayList<String> imageId) {
        super(context, R.layout.list_item_listview_artists, artistNames);
        this.context = context;
        this.artistNames = artistNames;
        this.imageIds = imageId;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // A holder will hold the references
        // to your views.
        ArtistViewHolder holder;

        View rowView = convertView;

        if(rowView == null) {
            holder = new ArtistViewHolder();
            rowView = inflater.inflate(R.layout.list_item_listview_artists, parent, false);
            holder.artistNameTextView = (TextView) rowView.findViewById(R.id.list_item_artist_name);
            holder.artistImageView = (ImageView) rowView.findViewById(R.id.list_item_artist_icon);
            rowView.setTag(holder);
        }
        else {
            holder = (ArtistViewHolder) convertView.getTag();
        }
        holder.artistNameTextView.setText(artistNames.get(position));
        if (imageIds.get(position) == ""){
            Picasso.with(getContext()).load(R.drawable.ic_launcher).resize(130, 130).centerCrop().into(holder.artistImageView);
        } else {
            Picasso.with(getContext()).load(imageIds.get(position)).resize(130, 130).centerCrop().into(holder.artistImageView);
        }

        return rowView;
    }

    public static class ArtistViewHolder {
        // declare your views here
        public TextView artistNameTextView;
        public ImageView artistImageView;

    }

}

