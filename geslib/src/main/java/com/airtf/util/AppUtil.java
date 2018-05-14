package com.airtf.util;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Author: freyrao
 * Time: 18/5/11 17:47
 * Desc:
 */

public class AppUtil {

    private static long toBackgroundTime = 0;

    /**
     * 判断app是否在前台
     * @param context
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        android.app.ActivityManager activityManager = (android.app.ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if(appProcesses == null) {
            return false;
        }
        for(android.app.ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if(appProcess.processName.equals(packageName) &&
                    appProcess.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    /**
     * 保存退到后台时间
     * @param context
     */
    public static void saveBackgroundTime(Context context) {
        //if(!isAppOnForeground(context)) {
            toBackgroundTime = System.currentTimeMillis();
        //}
    }

    /**
     * 从后台切回到前台时间间隔
     * @return
     */
    public static long toForegroundTime() {
        return System.currentTimeMillis() - toBackgroundTime;
    }

}
