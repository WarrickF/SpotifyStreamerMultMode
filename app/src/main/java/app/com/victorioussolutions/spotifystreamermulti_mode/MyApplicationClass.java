package app.com.victorioussolutions.spotifystreamermulti_mode;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by Warrick on 9/10/2015.
 */
public class MyApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // TODO Put your application initialization code here.
        // Bind to LocalService
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    PlayerService mService;
    boolean mBound = false;


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayerService.MyLocalBinder binder = (PlayerService.MyLocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


}