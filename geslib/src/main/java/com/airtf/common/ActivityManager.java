package com.airtf.common;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.Iterator;
import java.util.Stack;

/**
 * Author: freyrao
 * Time: 18/5/10 16:18
 * Desc:
 */

public class ActivityManager {
    private static Stack<Activity> activityStack;

    public static ActivityManager getInstance() {
        return ActivityManager.ViewManagerHolder.sInstance;
    }

    private ActivityManager() {
    }

    public void addActivity(Activity activity) {
        if(activityStack == null) {
            activityStack = new Stack();
        }

        activityStack.add(activity);
    }

    public Activity currentActivity() {
        Activity activity = (Activity)activityStack.lastElement();
        return activity;
    }

    public void finishActivity() {
        Activity activity = (Activity)activityStack.lastElement();
        this.finishActivity(activity);
    }

    public void finishActivity(Activity activity) {
        if(activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }

    }

    public void finishActivity(Class<?> cls) {
        Iterator var2 = activityStack.iterator();

        Activity activity;
        do {
            if(!var2.hasNext()) {
                return;
            }

            activity = (Activity)var2.next();
        } while(!activity.getClass().equals(cls));

        this.finishActivity(activity);
    }

    public void finishAllActivity() {
        int i = 0;

        for(int size = activityStack.size(); i < size; ++i) {
            if(null != activityStack.get(i)) {
                ((Activity)activityStack.get(i)).finish();
            }
        }

        activityStack.clear();
    }

    public void exitApp(Context context) {
        Log.e("ActivityManager", "app exit");

        try {
            this.finishAllActivity();
            Runtime.getRuntime().exit(0);
        } catch (Exception var3) {
            Log.e("ActivityManager", "app exit" + var3.getMessage());
        }

    }

    private static class ViewManagerHolder {
        private static final ActivityManager sInstance = new ActivityManager();

        private ViewManagerHolder() {
        }
    }
}
