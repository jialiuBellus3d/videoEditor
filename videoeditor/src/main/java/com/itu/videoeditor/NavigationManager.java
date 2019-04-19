package com.itu.videoeditor;

/**
 * Created by Jia Liu on 3/17/2019.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

/**
 * Navigate across different fragments.
 */

public class NavigationManager {

    private static final String LOGTAG = "NavigationManager";


    /**
     * Listener interface for navigation events.
     */
    public interface NavigationListener {

        /**
         * Callback on backstack changed.
         */
        void onBackstackChanged();
    }

    private FragmentManager mFragmentManager;

    private NavigationListener mNavigationListener;

//    private SingleVideoFragment singleFragment;
    private StartFragment startFragment;
//    private GalleryFragment galleryFragment;
    private OperationFragment operationFragment;
//    private MultipleVideoFragment multipleFragment;

    /**
     * Initialize the NavigationManager with a FragmentManager, which will be used at the
     * fragment transactions.
     *
     */
    public NavigationManager(FragmentManager fragmentManager) {

        mFragmentManager = fragmentManager;

        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (mNavigationListener != null) {
                    mNavigationListener.onBackstackChanged();
                }
            }
        });
    }

//    void startSingleScreen() {
//        if (singleFragment == null) {
//            singleFragment = new SingleVideoFragment();
//        }
//        open(singleFragment);
//    }
    void startStartScreen() {
        if (startFragment == null) {
            startFragment = new StartFragment();
        }
        open(startFragment);
    }
//    void startGalleryScreen() {
//        if (galleryFragment == null) {
//            galleryFragment = new GalleryFragment();
//        }
//        open(galleryFragment);
//    }
    void startOperationScreen(ArrayList<Uri> uriList) {
        if (operationFragment == null) {
            operationFragment = new OperationFragment();
            operationFragment.setUriList(uriList);
        }
        open(operationFragment);
    }

//    void startMultipleFragment() {
//        if (multipleFragment == null) {
//            multipleFragment = new MultipleVideoFragment();
//        }
//        open(multipleFragment);
//    }

    /**
     * Displays the next fragment
     *
     * @param fragment
     */
    private void open(Fragment fragment) {

        Log.d(LOGTAG, "open fragment called");
        if (mFragmentManager != null) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

}
