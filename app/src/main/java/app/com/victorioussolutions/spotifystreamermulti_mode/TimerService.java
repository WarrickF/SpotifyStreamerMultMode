package app.com.victorioussolutions.spotifystreamermulti_mode;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

/**
 * Created by Warrick on 9/10/2015.
 */
public class TimerService extends Service {
    private final IBinder myBinder = new MyLocalBinder();
    public static int counter = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public void run() {
        runner mRun = new runner();
        //mRun.run();
        new Thread(mRun).start();

    }


    public class MyLocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    public static boolean isRecursionEnable = false;

    public class runner implements Runnable {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            while (true) {
                counter++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }


}
