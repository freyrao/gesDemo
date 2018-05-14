package com.airtf.common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.airtf.geslib.R;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    protected boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "---onCreate--");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

    }

    @Override
    protected void onStart() {
        Log.e(TAG, "---onStart--");
        super.onStart();

    }

    @Override
    protected void onRestart() {
        Log.e(TAG, "---onRestart--");
        super.onRestart();

    }

    @Override
    protected void onResume() {
        Log.e(TAG, "---onResume--");
        super.onResume();
        isForeground = true;

    }

    @Override
    protected void onPause() {
        Log.e(TAG, "---onPause--");
        super.onPause();
        isForeground = false;

    }

    @Override
    protected void onStop() {
        Log.e(TAG, "---onStop--");
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "---onDestroy--");
        super.onDestroy();

    }
}
