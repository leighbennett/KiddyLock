package com.example.kiddylock;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private PermissionChecker mPermissionChecker;
    private SharedPreferences mPrefs;
    private NumberPicker code_one;
    private NumberPicker code_two;
    private NumberPicker code_three;
    private NumberPicker code_four;
    private Switch lockscreen_switch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        mPermissionChecker = new PermissionChecker(this);
        if (!mPermissionChecker.isRequiredPermissionGranted()) {
            Intent intent = mPermissionChecker.createRequiredPermissionIntent();
            startActivityForResult(intent, PermissionChecker.REQUIRED_PERMISSION_REQUEST_CODE);
        }
        lockscreen_switch = (Switch) findViewById(R.id.lock_screen_switch);
        lockscreen_switch.setChecked(false);
        lockscreen_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               activate_unlocked_fab();
           }
        });
        mPrefs = getSharedPreferences("kiddy_lock_prefs", Context.MODE_PRIVATE);
        int val_one = mPrefs.getInt("code_one",0);
        int val_two = mPrefs.getInt("code_two",0);
        int val_three = mPrefs.getInt("code_three",0);
        int val_four = mPrefs.getInt("code_four",0);
        code_one = findViewById(R.id.code_set_one);
        code_one.setMaxValue(9);
        code_one.setMinValue(0);
        code_one.setValue(val_one);
        code_one.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setLock();
            }
        });
        code_two = findViewById(R.id.code_set_two);
        code_two.setMaxValue(9);
        code_two.setMinValue(0);
        code_two.setValue(val_two);
        code_two.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setLock();
            }
        });
        code_three = findViewById(R.id.code_set_three);
        code_three.setMaxValue(9);
        code_three.setMinValue(0);
        code_three.setValue(val_three);
        code_three.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setLock();
            }
        });
        code_four = findViewById(R.id.code_set_four);
        code_four.setMaxValue(9);
        code_four.setMinValue(0);
        code_four.setValue(val_four);
        code_four.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setLock();
            }
        });
    }

    private void setLock(){
        int lock_value_one = code_one.getValue();
        int lock_value_two = code_two.getValue();
        int lock_value_three = code_three.getValue();
        int lock_value_four = code_four.getValue();
        mPrefs = getSharedPreferences("kiddy_lock_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt("code_one", lock_value_one);
        editor.putInt("code_two", lock_value_two);
        editor.putInt("code_three", lock_value_three);
        editor.putInt("code_four", lock_value_four);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopHeadService();
    }

    private void activate_unlocked_fab() {
        int val_one = mPrefs.getInt("code_one",0);
        int val_two = mPrefs.getInt("code_two",0);
        int val_three = mPrefs.getInt("code_three",0);
        int val_four = mPrefs.getInt("code_four",0);
        int total = val_one + val_two + val_three + val_four;
        if (total > 0) {
            Boolean switchState = lockscreen_switch.isChecked();
            mPermissionChecker = new PermissionChecker(this);
            if (!mPermissionChecker.isRequiredPermissionGranted()) {
                Intent intent = mPermissionChecker.createRequiredPermissionIntent();
                startActivityForResult(intent, PermissionChecker.REQUIRED_PERMISSION_REQUEST_CODE);
            } else {
                if (switchState) {
                    startHeadService();
                } else {
                    stopHeadService();
                }
            }
        }else {
            lockscreen_switch.setChecked(false);
            Toast.makeText(this,"Lock code cannot be 0000", Toast.LENGTH_LONG).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startHeadService() {
        Context context = this;
        context.startService(new Intent(context, UnlockedFabService.class));
    }

    private void stopHeadService() {
        Context context = this;
        context.stopService(new Intent(context, UnlockedFabService.class));

    }

    @Override
    public void onClick(View v) {

    }
}