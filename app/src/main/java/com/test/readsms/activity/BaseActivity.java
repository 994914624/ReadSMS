package com.test.readsms.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.test.readsms.R;

public class BaseActivity extends AppCompatActivity {

    private String TAG = "SSS.." + getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "----- onCreate  -----");
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "----- onStart  ----");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "----- onResume  ----");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "----- onPause  ----");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "-----  onDestroy  ----");
    }
}
