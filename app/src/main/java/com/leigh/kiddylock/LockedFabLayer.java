package com.leigh.kiddylock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public class LockedFabLayer extends View {

    private Context mContext;
    private FrameLayout mFrameLayout;
    private WindowManager mWindowManager;
    private WindowManager bWindowManager;
    private SharedPreferences mPrefs;
    private NumberPicker check1 ;
    private NumberPicker check2 ;
    private NumberPicker check3 ;
    private NumberPicker check4 ;

    public LockedFabLayer(Context context) {
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
        params.flags = FLAG_KEEP_SCREEN_ON;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mFrameLayout, params);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Here is the place where you can inject whatever layout you want.
        layoutInflater.inflate(R.layout.lockedfab, mFrameLayout);

        // Support dragging the image view
        final ImageView imageView = (ImageView) mFrameLayout.findViewById(R.id.lockedBtn);
        imageView.setOnTouchListener(new OnTouchListener() {
            private int initX, initY;
            private int initTouchX, initTouchY;
            private boolean show_code_layout = true;
            //variable for storing the time of first click
            private long startTime;
            //variable for calculating the total time
            private long duration;
            //constant for defining the time duration between the click that can be considered as double-tap
            static final int MAX_DURATION = 200;

            @Override public boolean onTouch(View v, MotionEvent event) {
                int x = (int)event.getRawX();
                int y = (int)event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        initX = params.x;
                        initY = params.y;
                        initTouchX = x;
                        initTouchY = y;
                        return true;

                    case MotionEvent.ACTION_UP:
                        long time = System.currentTimeMillis() - startTime;
                        duration=  duration + time;
                        if (duration <= MAX_DURATION) {
                            LinearLayout code_check_layout = (LinearLayout) mFrameLayout.findViewById(R.id.code_check);
                            if (show_code_layout) {
                                code_check_layout.setVisibility(ImageButton.VISIBLE);
                                show_code_layout = false;
                            } else {
                                code_check_layout.setVisibility(ImageButton.GONE);
                                show_code_layout = true;
                            }
                        }
                        duration=0;
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
        check1 = mFrameLayout.findViewById(R.id.code_check_one);
        check1.setMaxValue(9);
        check1.setMinValue(0);
        check1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                checkLock();
            }
        });
        check2 = mFrameLayout.findViewById(R.id.code_check_two);
        check2.setMaxValue(9);
        check2.setMinValue(0);
        check2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                checkLock();
            }
        });
        check3 = mFrameLayout.findViewById(R.id.code_check_three);
        check3.setMaxValue(9);
        check3.setMinValue(0);
        check3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                checkLock();
            }
        });
        check4 = mFrameLayout.findViewById(R.id.code_check_four);
        check4.setMaxValue(9);
        check4.setMinValue(0);
        check4.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                checkLock();
            }
        });
    }
    private void checkLock(){
        Context context = mFrameLayout.getContext();
        mPrefs = context.getSharedPreferences("kiddy_lock_prefs", Context.MODE_PRIVATE);
        int val_one = mPrefs.getInt("code_one", 0);
        int val_two = mPrefs.getInt("code_two", 0);
        int val_three = mPrefs.getInt("code_three", 0);
        int val_four = mPrefs.getInt("code_four", 0);
        int valueCheck1 = check1.getValue();
        int valueCheck2 = check2.getValue();
        int valueCheck3 = check3.getValue();
        int valueCheck4 = check4.getValue();
        if (val_one == valueCheck1 && val_two == valueCheck2 && val_three == valueCheck3 && val_four == valueCheck4) {
            context.stopService(new Intent(context, LockedFabService.class));
            context.stopService(new Intent(context, UnlockedFabService.class));
            context.startService(new Intent(context, UnlockedFabService.class));
        }
    }

    /**
     * Removes the view from window manager.
     */
    public void destroy() {
        mWindowManager.removeView(mFrameLayout);
    }
}