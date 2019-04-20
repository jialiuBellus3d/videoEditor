package com.itu.videoeditor;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends Activity {

    static final String TAG = "MainActivity";
    static final int PICK_IMAGE_MULTIPLE = 1;
    public ArrayList<Uri> mArrayUri;

    NavigationManager mNavigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mArrayUri = new ArrayList<Uri>();

        mNavigationManager = new NavigationManager(getFragmentManager());
        mNavigationManager.startStartScreen();
    }
}
