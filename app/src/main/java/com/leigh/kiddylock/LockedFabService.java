package com.leigh.kiddylock;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LockedFabService extends Service {

    private final static int FOREGROUND_ID = 1000;

    private LockedFabLayer mFabLayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initFabLayer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        destroyHeadLayer();
        stopForeground(true);
    }

    private void initFabLayer() {
        mFabLayer = new LockedFabLayer(this);
    }

    private void destroyHeadLayer() {
        mFabLayer.destroy();
        mFabLayer = null;
    }

    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

}