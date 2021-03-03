package com.example.kiddylock;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by lbennett on 8/04/17.
 */

/**
 * Foreground service. Creates a head view.
 * The pending intent allows to go back to the settings activity.
 */
public class UnlockedFabService extends Service {

    private final static int FOREGROUND_ID = 999;

    private UnlockedFabLayer mFabLayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logServiceStarted();
        initFabLayer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        destroyHeadLayer();
        stopForeground(true);
        logServiceEnded();
    }

    private void initFabLayer() {
        mFabLayer = new UnlockedFabLayer(this);
    }

    private void destroyHeadLayer() {
        mFabLayer.destroy();
        mFabLayer = null;
    }

    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    private void logServiceStarted() {
        //Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    }

    private void logServiceEnded() {
        //Toast.makeText(this, "Service ended", Toast.LENGTH_SHORT).show();
    }
}
