package com.airtf.geslib;

import android.app.Activity;
import android.util.Log;

import com.airtf.util.AppUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * 手势识别过滤器，实现过滤规则，判断某些页面或者场景不用显示手势密码
 * Author: freyrao
 * Time: 18/5/11 16:27
 * Desc:
 */

public class GestureFilter implements IGestureFilter {

    // 不显示activity的列表
    private List<String> activityList = new ArrayList<>();

    {
        activityList.add("com.airtf.gesdemo.MainActivity");
    }

    @Override
    public boolean isShowGesture(Activity activity) {
        // 白名单列表不用显示
        if(activityList != null) {
            for(String act : activityList) {
                if (activity.getClass().getName().equals(act)) {
                    return false;
                }
            }
        }

        // 从后台回到前台时间>30s显示
        long time = AppUtil.toForegroundTime() / 1000;
        if(time > 30) {
            return true;
        }

        // 已经解锁，没有退出，不用显示
        if(GestureController.isUnlock) {
            return false;
        }

        return true;
    }

}
