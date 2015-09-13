package app.com.victorioussolutions.spotifystreamermulti_mode;

import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Warrick on 8/16/2015.
 */
public class PlayerDialogFragment extends DialogFragment{

    String mPlayerState = "new";
    ImageButton mPlayButton = null;
    ArrayList<ParcelableTrack> tracks;
    Integer postition;
    boolean isPausable = false;
    boolean isPaused = false;
    boolean isUsingManualSeek = false;


    void updateTrack() {

        getArguments().putInt("CurrentlyPlayingPosition", postition);
        String mPreviewURL = ((ParcelableTrack) tracks.get(postition)).getPreview_url();
        updateLables();
        if(PlayerService.currentlyPlayingURL != mPreviewURL) {
            PlayerService.playNew(mPreviewURL);
        }
    }

    void setButtonState_pausable() {
        try {
            ((ImageButton)getView().findViewById(R.id.playButton)).setImageResource(android.R.drawable.ic_media_pause);
            isPausable = true;
        } catch (Exception ex) {
            // Not really a problem the dialog may have exited.
        }

    }

    void setButtonState_playable() {
        try {
            ((ImageButton)getView().findViewById(R.id.playButton)).setImageResource(android.R.drawable.ic_media_play);
            isPausable = false;
        } catch (Exception ex) {
            // Not really a problem the dialog may have exited.
        }

    }



    void updateLables() {
        ((TextView)getView().findViewById(R.id.label_artist_name)).setText(((ParcelableTrack) tracks.get(postition)).getArtist_name());
        ((TextView)getView().findViewById(R.id.label_album_name)).setText(((ParcelableTrack) tracks.get(postition)).getAlbum_name());
        ((TextView)getView().findViewById(R.id.label_track_name)).setText(((ParcelableTrack) tracks.get(postition)).getTrack_name());
        Uri mCoverArt = Uri.parse(((ParcelableTrack) tracks.get(postition)).getAlbum_artwork());
        ImageView mImage = (ImageView)getView().findViewById(R.id.track_cover_image);
        Picasso.with(getActivity()).load(mCoverArt).into(mImage);
    }


    @Override
    public void onPause() {
        PlayerService.callback = null;
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //if(!PlayerService.isPlaying()) {
            updateTrack();
        //}
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        tracks = getArguments().getParcelableArrayList("tracks");


        Integer zPreviousPosition = getArguments().getInt("CurrentlyPlayingPosition");

        if(zPreviousPosition > 0) {
            postition = zPreviousPosition;
        } else {
            postition = getArguments().getInt("position");
        }

        final String mPreviewURL = ((ParcelableTrack)tracks.get(postition)).getPreview_url();

        // Inflate the layout to use as dialog or embedded fragment
        final View mView =inflater.inflate(R.layout.fragment_player, container, false);
        final SeekBar mSeelBar = (SeekBar)mView.findViewById(R.id.scrub_bar);
        mSeelBar.setMax(30000);




        mSeelBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                PlayerService.pause();
                isUsingManualSeek = true;
                //player.pause();
                //GlobalRefs.myService.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Integer mProgress = seekBar.getProgress();
                PlayerService.seekTo(mProgress);
                PlayerService.resume();
                isUsingManualSeek = false;

            }
        });

        PlayerService.callback = new MyCallback() {
            @Override
            public void playStarted() {
                setButtonState_pausable();
                isPaused = false;
            }

            @Override
            public void playStopped() {
                setButtonState_playable();
            }

            @Override
            public String playError(String error) {
                CharSequence text = error;
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(getActivity().getApplicationContext(), text, duration);
                toast.show();

                return error;
            }

            @Override
            public Integer playPositionChanged(final Integer position) {

                if(!isUsingManualSeek) {
                    //Make sure you update Seekbar on UI thread
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            mSeelBar.setProgress(position);
                            String zPos = "0";
                            if(position > 1000) {
                                if(position < 10000) {
                                    zPos = "0"+ new DecimalFormat("#.##").format(position/1000);
                                } else {
                                    zPos =  new DecimalFormat("#.##").format(position/1000);
                                }
                            } else {
                                zPos = "00";
                            }

                            TextView zLabel = (TextView) getView().findViewById(R.id.label_track_position);
                            if(zLabel != null) {
                                zLabel.setText("0:" + zPos);
                            }

                        }
                    });

                }

                return position;
            }


        };


        mPlayButton = (ImageButton)mView.findViewById(R.id.playButton);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPausable) {
                    PlayerService.pause();
                    isPaused = true;
                } else {
                    if(isPaused) {
                        PlayerService.resume();
                    } else {
                        updateTrack();
                    }
                }
            }
        });

        // Handle the previous button
        mView.findViewById(R.id.previousTrackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postition > 0) {
                    postition = postition - 1;
                    //updateLables();
                    updateTrack();
                    //setButtonState_pausable();
                }
            }
        });

        // Handle the next button
        mView.findViewById(R.id.nextTrackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postition < tracks.size() - 1) {
                    postition = postition + 1;
                    //updateLables();
                    updateTrack();
                    //setButtonState_pausable();
                    //Log.i("NEXT", "Set button to pausable");
                }
            }
        });

        return mView;


    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}