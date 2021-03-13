package com.leigh.kiddylock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class UnlockedFabLayer extends View {

    private Context mContext;
    private FrameLayout mFrameLayout;
    private WindowManager mWindowManager;

    public UnlockedFabLayer(Context context) {
        super(context);
        mContext = context;
        mFrameLayout = new FrameLayout(mContext);
        addToWindowManager();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addToWindowManager() {
        final WindowManager.LayoutParams params;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        }
        params.gravity = Gravity.LEFT;

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mFrameLayout, params);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Here is the place where you can inject whatever layout you want.
        layoutInflater.inflate(R.layout.unlockedfab, mFrameLayout);

        // Support dragging the image view
        final ImageView imageView = (ImageView) mFrameLayout.findViewById(R.id.unlockedBtn);
        imageView.setOnTouchListener(new OnTouchListener() {
            private int initX, initY;
            private int initTouchX, initTouchY;
            //variable for counting two successive up-down events
            private int clickCount = 0;
            //variable for storing the time of first click
            private long startTime;
            //variable for calculating the total time
            private long duration;
            //constant for defining the time duration between the click that can be considered as double-tap
            static final int MAX_DURATION = 300;

            @Override public boolean onTouch(View v, MotionEvent event) {
                int x = (int)event.getRawX();
                int y = (int)event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        clickCount++;
                        initX = params.x;
                        initY = params.y;
                        initTouchX = x;
                        initTouchY = y;
                        return true;

                    case MotionEvent.ACTION_UP:
                        long time = System.currentTimeMillis() - startTime;
                        duration=  duration + time;
                        if(clickCount == 2) {
                            if (duration <= MAX_DURATION) {
                                Context context = getContext();
                                context.stopService(new Intent(context, UnlockedFabService.class));
                                context.startService(new Intent(getContext(), LockedFabService.class));
                            }
                            clickCount = 0;
                            duration = 0;
                        }
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initX + (x - initTouchX);
                        params.y = initY + (y - initTouchY);

                        // Invalidate layout
                        mWindowManager.updateViewLayout(mFrameLayout, params);
                        return true;
                }
                return false;
            }
        });
    }
    public void destroy() {
        mWindowManager.removeView(mFrameLayout);
    }
}