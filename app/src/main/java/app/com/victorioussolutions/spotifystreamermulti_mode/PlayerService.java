package app.com.victorioussolutions.spotifystreamermulti_mode;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


interface MyCallback {
    void playStarted();
    void playStopped();
    Integer playPositionChanged(Integer position);
}

public class PlayerService extends Service {
    private final IBinder myBinder = new MyLocalBinder();
    static MediaPlayer player = null;

    static MyCallback callback;

    public static void onPlayStarted() {
        if(callback != null)
            callback.playStarted();

    }

    public static void onPlayStopped() {
        if(callback != null)
            callback.playStopped();
    }

    public static void onplayPositionChanged(Integer position) {
        if(callback != null)
            callback.playPositionChanged(position);
    }

    public PlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        runner mRun = new runner();
        new Thread(mRun).start();
    }

    public static void seekTo(int aSeekPos) {
        if(player != null) {
            player.seekTo(aSeekPos);
        }
    }

    public static boolean isPlaying() {
        boolean mPlaying = false;
        if (player != null) {
            mPlaying = player.isPlaying();
        }
        return  mPlaying;
    }

    public static void playNew(String aPreviewURL) {
        // If we were using this instance in the past. Release it before we do anything else.
        if(player != null) {
            if(player.isPlaying()) {
                // TODO: There may be states that we can't do this in.
                player.stop();
            }
            player.release();
        }

        player = new MediaPlayer();

        // Setup an error listener so we know something went wrong.
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("MediaPlayerService", "Error occurred while playing audio.");
                mp.stop();
                mp.release();
                player = null;
                return true;
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                player.reset();
                player = null;
            }
        });
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(aPreviewURL);
        } catch (Exception e) {
            Log.e("MediaPlayerService", "Error occurred while setting up player.");
        }
        player.prepareAsync();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                player.start();
                onPlayStarted();
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onPlayStopped();
            }
        });

    }

    public static void pause() {
        if (player != null) {
            player.pause();
            onPlayStopped();
        }
    }

    public static void resume() {
        if (player != null) {
            player.start();
            onPlayStarted();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return myBinder;
    }

    public class MyLocalBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }



    public static boolean isRecursionEnable = false;

    public class runner implements Runnable {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            while (true) {
                try {

                    if(player !=null) {
                        try {
                            PlayerService.onplayPositionChanged(player.getCurrentPosition());
                        } catch (Exception ex) {
                            Log.e("PlayerError","Probably switching views .. not fatal");
                        }
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
