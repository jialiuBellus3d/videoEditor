package com.itu.videoeditor;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManager;

import java.util.ArrayList;

public class MainActivity extends Activity {

    static final String TAG = "MainActivity";
    static final int PICK_IMAGE_MULTIPLE = 1;
    public ArrayList<Uri> mArrayUri;
    public int mScreenWidth;
    public int mScreenHeight;
    NavigationManager mNavigationManager;
    ArrayList<Pair<Integer, Float>> totalList;
    ArrayList<DataItem> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mArrayUri = new ArrayList<Uri>();
        totalList = new ArrayList<>();
        mDataList = new ArrayList<>();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;

        mNavigationManager = new NavigationManager(getFragmentManager());
        mNavigationManager.startStartScreen();
    }

    DataItem getCurrentDataItem(int index){
        Log.e(TAG, "totalList.get(index).first: "+totalList.get(index).first);
        return mDataList.get(totalList.get(index).first);
    }
}
