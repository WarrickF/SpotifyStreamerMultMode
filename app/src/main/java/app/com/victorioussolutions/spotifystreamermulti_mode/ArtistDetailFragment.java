package app.com.victorioussolutions.spotifystreamermulti_mode;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
//import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TrackSimple;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A fragment representing a single Artist detail screen.
 * This fragment is either contained in a {@link ArtistListActivity}
 * in two-pane mode (on tablets) or a {@link ArtistDetailActivity}
 * on handsets.
 */
public class ArtistDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "artist_id";
    TopTrackAdapter topTrackAdapter = null;
    ArrayList<Track> tracksArrayList = new ArrayList<>();
    boolean mIsLargeLayout;
    Tracks _tracks;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_artist_top10_tracks, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        topTrackAdapter = new TopTrackAdapter(getActivity(), tracksArrayList);

        ListView listView = (ListView)getActivity().findViewById(R.id.listView_topTracks);
        listView.setAdapter(topTrackAdapter);

        String mSelectedArtist = getArguments().getString(ARG_ITEM_ID);
        if(tracksArrayList.size() == 0) {
            new updateTopTrackList().execute(mSelectedArtist);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track mTrack = (Track) parent.getItemAtPosition(position);
                String mPreviewURL = mTrack.preview_url;

                FragmentManager fragmentManager = getFragmentManager();
                PlayerDialogFragment newFragment = new PlayerDialogFragment();

                ArrayList<ParcelableTrack> mTrackArrayList = new ArrayList<ParcelableTrack>();
                Integer mTrackCount = topTrackAdapter.getCount();
                for (Integer i = 0; i < mTrackCount; i++) {
                    ParcelableTrack mParcelTrack = new ParcelableTrack(topTrackAdapter.getItem(i));
                    mTrackArrayList.add(mParcelTrack);
                }

                Bundle args = new Bundle();
                args.putString("PreviewURL", mPreviewURL);
                args.putParcelableArrayList("tracks", mTrackArrayList);
                args.putInt("position", position);
                newFragment.setArguments(args);

                if (mIsLargeLayout) {

                    newFragment.show(fragmentManager, "dialog");

                } else {

                    // The device is smaller, so show the fragment fullscreen
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    // For a little polish, specify a transition animation
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    // To make it fullscreen, use the 'content' root view as the container
                    // for the fragment, which is always the root view for the activity
                    transaction.add(android.R.id.content, newFragment)
                            .addToBackStack(null).commit();
                }

            }
        });


    }

    private class updateTopTrackList extends AsyncTask<String, Void, Tracks> {

        private static final String TAG = "Spotify.Top10";




        @Override
        protected void onPostExecute(Tracks tracks) {
            super.onPostExecute(tracks);

            if(tracks == null) {

                CharSequence text = "Opps something went wrong, please try again.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), text, duration);
                toast.show();

            } else {

                topTrackAdapter.clear();
                if (tracks.tracks.size() > 0) {
                    for (Track track : tracks.tracks) {
                        topTrackAdapter.add(track);
                    }
                } else {
                    TextView zView = (TextView) getActivity().findViewById(R.id.noTracksMessage);
                    zView.setText("Sorry, we were unable to determine the top tracks");
                }
            }
        }

        protected Tracks doInBackground(String... artistsToSearchFor) {

            Tracks tracks = null;
            String artistId = artistsToSearchFor[0];

            TrackDbHelper mDBHelper = new TrackDbHelper(getActivity().getApplicationContext());
            //mDBHelper.

            SQLiteDatabase db = null;
            try {
                db = mDBHelper.getReadableDatabase();
            } catch (Exception ex) {
                String error = ex.getMessage();
                Log.e("DB",ex.getMessage());
            }


            String[] projection = {
                    TracksContract.TrackEntry._ID,
                    TracksContract.TrackEntry.COLUMN_NAME_ARTIST_ID,
                    TracksContract.TrackEntry.COLUMN_NAME_ARTIST_NAME,
                    TracksContract.TrackEntry.COLUMN_NAME_ALBUM_NAME,
                    TracksContract.TrackEntry.COLUMN_NAME_ALBUM_ARTWORK,
                    TracksContract.TrackEntry.COLUMN_NAME_TRACK_NAME,
                    TracksContract.TrackEntry.COLUMN_NAME_PREVIEW_URL
            };

            String sortOrder =
                    TracksContract.TrackEntry.COLUMN_NAME_ARTIST_NAME + " DESC";

            String selection = TracksContract.TrackEntry.COLUMN_NAME_ARTIST_ID + " = ?";
// Specify arguments in placeholder order.
            String[] selectionArgs = { artistId };

            Cursor c = db.query(
                    TracksContract.TrackEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            Tracks tmpTracks = new Tracks();
            //tmpTracks.tracks = new List<Track>();
            List<Track> xxx = new ArrayList<Track>();
            tmpTracks.tracks = xxx;


            if(c.getCount() >0 ) {
                //c.moveToFirst();
                while (c.moveToNext()) {
                    try {
                        String martistId = c.getString(c.getColumnIndexOrThrow(TracksContract.TrackEntry.COLUMN_NAME_ARTIST_ID));
                        String martistName = c.getString(c.getColumnIndexOrThrow(TracksContract.TrackEntry.COLUMN_NAME_ARTIST_NAME));
                        String malbumName = c.getString(c.getColumnIndexOrThrow(TracksContract.TrackEntry.COLUMN_NAME_ALBUM_NAME));
                        String malbumImage = c.getString(c.getColumnIndexOrThrow(TracksContract.TrackEntry.COLUMN_NAME_ALBUM_ARTWORK));
                        String mtrackName = c.getString(c.getColumnIndexOrThrow(TracksContract.TrackEntry.COLUMN_NAME_TRACK_NAME));
                        String mpreviewURL = c.getString(c.getColumnIndexOrThrow(TracksContract.TrackEntry.COLUMN_NAME_PREVIEW_URL));

                        Track tmpTrack = new Track();
                        //tmpTrack.artists.get(0).name = martistName;
                        ArtistSimple zArtist = new ArtistSimple();
                        zArtist.name = martistName;
                        List<ArtistSimple> zArtistList = new ArrayList<>();
                        zArtistList.add(zArtist);
                        tmpTrack.artists = zArtistList;

                        AlbumSimple zAlbum = new AlbumSimple();
                        zAlbum.name = malbumName;
                        //zAlbum.
                        kaaes.spotify.webapi.android.models.Image zImage = new kaaes.spotify.webapi.android.models.Image();
                        zImage.url = malbumImage;
                        List<kaaes.spotify.webapi.android.models.Image> zImages = new ArrayList<kaaes.spotify.webapi.android.models.Image>();
                        zImages.add(zImage);
                        zAlbum.images = zImages;
                        tmpTrack.album = zAlbum;
                        //tmpTrack.album.name = malbumName;
                        //tmpTrack.album.images.get(0).url = malbumImage;
                        tmpTrack.name = mtrackName;
                        tmpTrack.preview_url = mpreviewURL;

                        tmpTracks.tracks.add(tmpTrack);


                    } catch (Exception ex) {
                        String xyz = ex.getMessage();
                    }
                }

            }

            /*
            long itemId = c.getLong(
                    c.getColumnIndexOrThrow(TracksContract.TrackEntry._ID);
            );
            */

            if(tmpTracks.tracks.size() > 0) {
                tracks = tmpTracks;
            } else {


                try {
                    Log.d(TAG, "Performing artist top track search");
                    SpotifyApi api = new SpotifyApi();
                    SpotifyService spotify = api.getService();
                    Map<String, Object> params = new HashMap<>();
                    params.put("country", "SE");
                    tracks = spotify.getArtistTopTrack(artistId, params);

                    SQLiteDatabase dbx = mDBHelper.getWritableDatabase();
                    dbx.delete(TracksContract.TrackEntry.TABLE_NAME, null, null);
                    //dbx.execSQL("DROP TABLE "+TracksContract.TrackEntry.TABLE_NAME);
                    for (Track track : tracks.tracks) {
                        ContentValues mValues = new ContentValues();
                        mValues.put(TracksContract.TrackEntry.COLUMN_NAME_ARTIST_ID, artistId);
                        mValues.put(TracksContract.TrackEntry.COLUMN_NAME_ARTIST_NAME, track.artists.get(0).name);
                        mValues.put(TracksContract.TrackEntry.COLUMN_NAME_ALBUM_NAME, track.album.name);
                        mValues.put(TracksContract.TrackEntry.COLUMN_NAME_ALBUM_ARTWORK, track.album.images.get(0).url);
                        mValues.put(TracksContract.TrackEntry.COLUMN_NAME_TRACK_NAME, track.name);
                        mValues.put(TracksContract.TrackEntry.COLUMN_NAME_TRACK_DURATION, 30000);
                        mValues.put(TracksContract.TrackEntry.COLUMN_NAME_PREVIEW_URL, track.preview_url);

                        long newRowId;
                        newRowId = dbx.insert(
                                TracksContract.TrackEntry.TABLE_NAME,
                                null,
                                mValues);
                    }

                } catch (Exception ex) {
                    Log.d(TAG, "Error while doing artist top track search: " + ex.getMessage());
                }
            }

            return tracks;
        }
    }

    public class TopTrackAdapter extends ArrayAdapter<Track> {
        public TopTrackAdapter(Context context, ArrayList<Track> artists) {
            super(context, 0, artists);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Track track = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_track_list_item, parent, false);
            }
            // Lookup view for data population
            ImageView trackImage = (ImageView) convertView.findViewById(R.id.topTrackAlbumImage);
            TextView trackName = (TextView) convertView.findViewById(R.id.trackName);
            // Populate the data into the template view using the data object
            String imageURL = null;

            if(track.album.images.size() > 0) {
                imageURL = track.album.images.get(0).url;
                Picasso.with(getContext()).load(imageURL).into(trackImage);
            } else {
                // imageURL = "No Image";
            }

            //artistImage.setText(imageURL);
            trackName.setText(track.name + "\r\n" + track.album.name);
            // Return the completed view to render on screen
            return convertView;
        }
    }

}
