package org.common.lib.analytics;

import android.app.Activity;

public class ActivityLifecycleAction {

    AnalyticCallback analyticCallback;

    public ActivityLifecycleAction(AnalyticCallback callback) {
        analyticCallback = callback;
    }

    public void onCreate(Activity activity) {
        HomeUtils.onCreate(activity);
    }

    public void onStart(Activity activity) {
        HomeUtils.onStart(activity);
    }

    public void onResume(Activity activity) {
        HomeUtils.onResume(activity);
    }

    public void onPause(Activity activity) {
        HomeUtils.onPause(activity);
    }

    public void onStop(Activity activity) {
        HomeUtils.onStop(activity);
    }

    public void onDestory(Activity activity) {
        HomeUtils.onDestory(activity);
    }
}