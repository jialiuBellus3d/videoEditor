package com.itu.videoeditor;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    static final String TAG = "MainActivity";
    NavigationManager mNavigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationManager = new NavigationManager(getFragmentManager());
        mNavigationManager.startStartScreen();
    }
}
