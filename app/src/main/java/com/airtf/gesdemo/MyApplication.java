package com.airtf.gesdemo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.airtf.geslib.inner.GestureController;
import com.airtf.geslib.inner.GestureFilter;
import com.airtf.util.AppUtil;

/**
 * Author: freyrao
 * Time: 18/5/11 11:31
 * Desc:
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();

//        GestureController.getInstance().init(new GestureFilter());

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

}
