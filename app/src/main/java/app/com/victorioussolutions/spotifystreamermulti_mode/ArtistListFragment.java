package app.com.victorioussolutions.spotifystreamermulti_mode;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * A list fragment representing a list of Artists. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ArtistDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ArtistListFragment extends ListFragment {

    public ArtistsAdapter artistArrayAdapter = null;
    private static ArrayList<Artist> artistList = new ArrayList<Artist>();
    private static final String TAG = "SpotifyMain";

    private View v;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // We're returning a custom layout that we'd like to use for each of the items.
        //if(v ==null) {
            v = inflater.inflate(R.layout.fragment_artist_list_with_search, container, false);
            //v.findViewById(r.ID)
//        } else {
//            container.removeAllViews();
        //}
        return v;
    }

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity mThisActivity = getActivity();
        final Context mThisContext = mThisActivity.getApplicationContext();

        // Once the Fragment is loaded, wire up the method to do the artist search.
        EditText searchBox = (EditText)mThisActivity.findViewById(R.id.input_ArtistSearch);
        // Watch to see when someone hits done on the soft keyboard then perform search.
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String input;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    input = v.getText().toString();

                    //Do the search itself.
                    new FetchArtistsTask().execute(input);

                    //TODO: I can probably remove this
                    CharSequence text = "Searching";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(mThisContext, text, duration);
                    toast.show();

                    return true;
                }
                return false;
            }
        });

    }

    private int selectedValue;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        // Attach the adapter to the list so that it has some data.
        Activity mThisActivity = getActivity();
        Context mThisContext = mThisActivity.getApplicationContext();
        artistArrayAdapter = new ArtistsAdapter(mThisContext, artistList);
        setListAdapter(artistArrayAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);


        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        Artist selectedArtist = artistArrayAdapter.getItem(position);
        mCallbacks.onItemSelected(selectedArtist.id);
        //setActivatedPosition(position);
        mActivatedPosition = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);


    }

    private void setActivatedPosition(int position) {

        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
            //getListView().setSelector(R.drawable.selector);
            getListView().setSelector(R.color.green);
        } else {
            getListView().setItemChecked(position, true);
            //getListView().setSelector(R.drawable.selector);
            getListView().setSelector(R.color.green);
        }
        mActivatedPosition = position;
    }

    private class FetchArtistsTask extends AsyncTask<String, Void, List<Artist>> {

        private static final String TAG = "FetchArtistsTask";


        @Override
        protected void onPostExecute(List<Artist> artists) {
            super.onPostExecute(artists);

            if(artists == null) {
                CharSequence text = "oops something seems to have gone wrong. Please try again.";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(getActivity().getApplicationContext(), text, duration);
                toast.show();
            } else {
                if(artists.size() == 0) {

                    CharSequence text = "Artist not found. Please try again";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), text, duration);
                    toast.show();

                } else {

                    artistArrayAdapter.clear();
                    for (Artist artist : artists) {
                        Log.d(TAG, "Add Artist: " + artist.name);
                        artistArrayAdapter.add(artist);
                    }
                }
            }


        }

        protected List<Artist> doInBackground(String... artistsToSearchFor) {
            List<Artist> zArtists = null;
            String searchForArtist = artistsToSearchFor[0];
            try {
                Log.d(TAG, "Performing artist search");
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();
                ArtistsPager object = spotify.searchArtists(searchForArtist);
                zArtists = object.artists.items;

            } catch (Exception ex) {
                Log.d(TAG, "Error while doing artist search: " + ex.getMessage());
            }

            return zArtists;
        }
    }

}
