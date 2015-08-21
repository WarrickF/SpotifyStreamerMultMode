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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Warrick on 8/16/2015.
 */
public class PlayerDialogFragment extends DialogFragment {
    MediaPlayer player = null;
    String mPlayerState = "new";
    ImageButton mPlayButton = null;
    ArrayList<ParcelableTrack> tracks;
    Integer postition;

    @Override
    public void onStart() {
        super.onStart();
        player = new MediaPlayer();
        //getActivity().findViewById(R.id.playButton).
        updateLables();
        mPlayButton.performClick();
    }

    @Override
    public void onStop() {
        super.onStop();
        player.release();
        player = null;
    }

    void setButtonState_pausable() {
        ((ImageButton)getView().findViewById(R.id.playButton)).setImageResource(android.R.drawable.ic_media_pause);
    }

    void setButtonState_playable() {
        ((ImageButton)getView().findViewById(R.id.playButton)).setImageResource(android.R.drawable.ic_media_play);
    }


    void firstPlay() {

    }

    void updateTrack() {

        String mPreviewURL = ((ParcelableTrack) tracks.get(postition)).getPreview_url();
        player.stop();
        player.reset();
        try {
            player.setDataSource(mPreviewURL);
            player.prepareAsync();
        } catch (Exception e) {
            //TODO: handle exception
        }
        //player.start();
    }


    void updateLables() {
        ((TextView)getView().findViewById(R.id.label_artist_name)).setText(((ParcelableTrack) tracks.get(postition)).getArtist_name());
        ((TextView)getView().findViewById(R.id.label_album_name)).setText(((ParcelableTrack) tracks.get(postition)).getAlbum_name());
        ((TextView)getView().findViewById(R.id.label_track_name)).setText(((ParcelableTrack) tracks.get(postition)).getTrack_name());
        Uri mCoverArt = Uri.parse(((ParcelableTrack) tracks.get(postition)).getAlbum_artwork());
        ImageView mImage = (ImageView)getView().findViewById(R.id.track_cover_image);
        Picasso.with(getActivity()).load(mCoverArt).into(mImage);
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        tracks = getArguments().getParcelableArrayList("tracks");
        postition = getArguments().getInt("position");

        // Inflate the layout to use as dialog or embedded fragment
        final View mView =inflater.inflate(R.layout.fragment_player, container, false);
        mPlayButton = (ImageButton)mView.findViewById(R.id.playButton);
        TextView mlabel_artist_name = (TextView)mView.findViewById(R.id.label_artist_name);
        //final String mPreviewURL = getArguments().getString("PreviewURL");
        final String mPreviewURL = ((ParcelableTrack)tracks.get(postition)).getPreview_url();
        mlabel_artist_name.setText(mPreviewURL);


        final SeekBar mSeelBar = (SeekBar)mView.findViewById(R.id.scrub_bar);
        mSeelBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //player.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Integer mProgress = seekBar.getProgress();
                player.seekTo(mProgress * 1000);
                //player.start();
            }
        });

        mView.findViewById(R.id.previousTrackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postition > 0) {
                    postition = postition - 1;
                    updateLables();
                    updateTrack();
                }
            }
        });

        mView.findViewById(R.id.nextTrackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postition < tracks.size() - 1) {
                    postition = postition + 1;
                    updateLables();
                    updateTrack();
                }
            }
        });

        mView.findViewById(R.id.playButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    switch (mPlayerState) {
                        // we've
                        case "new" :
                            mPlayerState = "setting up";
                            try {
                                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                player.setDataSource(mPreviewURL);
                            } catch (Exception e) {
                                // TODO: handle exception
                                mPlayerState = "stopped";
                            }
                            //player.prepare();
                            player.prepareAsync();

                            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    setButtonState_playable();
                                    mPlayerState = "new";
                                    player.reset();
                                }
                            });

                            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mPlayerState = "playing";
                                    player.start();
                                    setButtonState_pausable();
                                    int duration = player.getDuration() / 1000;
                                    mSeelBar.setMax(duration);
                                }
                            });

                            final Handler mHandler = new Handler();
                            //Make sure you update Seekbar on UI thread
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if (player != null) {
                                        int mCurrentPosition = player.getCurrentPosition() / 1000;
                                        mSeelBar.setProgress(mCurrentPosition);
                                    }
                                    mHandler.postDelayed(this, 1000);
                                }
                            });

                            break;
                        case "paused":
                            player.start();
                            mPlayerState = "playing";
                            setButtonState_pausable();
                            break;
                        case "playing":
                            player.pause();
                            mPlayerState = "paused";
                            setButtonState_playable();
                            break;
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