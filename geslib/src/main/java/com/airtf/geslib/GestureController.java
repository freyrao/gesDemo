package com.airtf.geslib;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airtf.common.ActivityManager;

/**
 * 手势密码控制器，使用时需要初始化过滤器，让过滤器生效
 * Author: freyrao
 * Time: 18/5/11 11:07
 * Desc:
 */

public class GestureController {
    private static final String TAG = "GestureController";

    private IGestureFilter gestureFilter;

    private ViewGroup contentView;
    private View rootView;
    private GestureLockView glv;
    private TextView tv_pwd_tips;
    private GestureLockHelper helper;

    public static boolean isUnlock = false;

    private static GestureController gestureController;
    private GestureController() {

    }

    public static GestureController getInstance() {
        if(gestureController == null) {
            synchronized (GestureController.class) {
                if(gestureController == null) {
                    gestureController = new GestureController();
                }
            }
        }

        return gestureController;
    }

    public void init(IGestureFilter gestureFilter) {
        this.gestureFilter = gestureFilter;
    }

    public void showGesture(Activity activity) {
        if(gestureFilter != null && gestureFilter.isShowGesture(activity)) {
            addGesView(activity);
        }
    }

    /**
     * 添加手势view
     * @param activity
     */
    private void addGesView(Activity activity) {
        contentView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = LayoutInflater.from(activity).inflate(R.layout.layout_ges, null);
        contentView.addView(rootView);
        glv = (GestureLockView) rootView.findViewById(R.id.glv);
        tv_pwd_tips = (TextView) rootView.findViewById(R.id.tv_pwd_tips);
        helper = new GestureLockHelper(activity, glv);
        initGesPwd();
        helper.setGestureLockCallback(new GestureLockHelper.GestureLockCallback() {
            @Override
            public void onFinish(int resultCode, String resultDesc, String result) {
                tv_pwd_tips.setText(resultDesc);
                if (resultCode == GestureLockHelper.CODE_UNLOCK_OK) {
                    isUnlock = true;
                    contentView.removeView(rootView);
                } else if(resultCode == GestureLockHelper.CODE_SET_OK) {
                    helper.resetMode(GestureLockHelper.MODE_GES_UNLOCK);
                }
            }
        });
    }

    private void initGesPwd() {
        if (helper.isSetGes()) {
            helper.setMode(GestureLockHelper.MODE_GES_UNLOCK);
        } else {
            helper.setMode(GestureLockHelper.MODE_GES_SET);
        }
    }

}
