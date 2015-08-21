package app.com.victorioussolutions.spotifystreamermulti_mode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Warrick on 8/15/2015.
 */
public class ArtistsAdapter extends ArrayAdapter<Artist> {
    public ArtistsAdapter(Context context, ArrayList<Artist> artists) {
        super(context, 0, artists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Artist artist = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_artist_list_item, parent, false);
        }
        // Lookup view for data population
        ImageView artistImage = (ImageView) convertView.findViewById(R.id.artistImage);
        TextView artistName = (TextView) convertView.findViewById(R.id.artistName);
        // Populate the data into the template view using the data object
        String imageURL = null;
        if(artist.images.size() > 0) {
            imageURL = artist.images.get(0).url;
            //imageURL = "x";
            Picasso.with(getContext()).load(imageURL).into(artistImage);
        } else {
            imageURL = "No Image";
        }

        //artistImage.setText(imageURL);
        artistName.setText(artist.name);
        // Return the completed view to render on screen
        //convertView.setBackgroundResource(R.drawable.selector);

        return convertView;
    }

}
