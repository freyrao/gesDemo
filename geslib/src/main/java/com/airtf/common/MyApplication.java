package com.airtf.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.airtf.geslib.GestureController;
import com.airtf.geslib.GestureFilter;
import com.airtf.util.AppUtil;

import java.util.List;

/**
 * Author: freyrao
 * Time: 18/5/11 11:31
 * Desc:
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        GestureController.getInstance().init(new GestureFilter());

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                Log.e(TAG, "---onActivityCreated--" + activity);
                ActivityManager.getInstance().addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.e(TAG, "---onActivityStarted--" + activity);
                GestureController.getInstance().showGesture(activity);
                count++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.e(TAG, "---onActivityResumed--" + activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.e(TAG, "---onActivityPaused--" + activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.e(TAG, "---onActivityStopped--" + activity);
                AppUtil.saveBackgroundTime(activity);
                count--;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                Log.e(TAG, "---onActivitySaveInstanceState--" + activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.e(TAG, "---onActivityDestroyed--" + activity);
                ActivityManager.getInstance().finishActivity(activity);
            }
        });
    }

}
