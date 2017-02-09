package org.common.lib.analytics;

import android.support.v4.app.Fragment;


public class FragmentLifecycleAction {

    AnalyticCallback analyticCallback;

    public FragmentLifecycleAction(AnalyticCallback callback) {
        analyticCallback = callback;
    }

    public void onCreate(Fragment fragment) {
    }

    public void onStart(Fragment fragment) {
    }

    public void onResume(Fragment fragment) {
    }

    public void onPause(Fragment fragment) {
    }

    public void onStop(Fragment fragment) {
    }

    public void onDestroy(Fragment fragment) {
    }
}