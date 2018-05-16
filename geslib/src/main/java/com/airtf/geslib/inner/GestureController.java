package com.airtf.geslib.inner;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airtf.geslib.GesContract;
import com.airtf.geslib.R;

/**
 * 手势密码控制器，使用时需要初始化过滤器，让过滤器生效
 * Author: freyrao
 * Time: 18/5/11 11:07
 * Desc:
 */

public class GestureController {
    private static final String TAG = "GestureController";

    private ViewGroup contentView;
    private View rootView;
    private GestureLockView glv;
    private TextView tv_pwd_tips;
    private GestureLockHelper helper;
    private GesContract.Callback callback;

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

    public void showGesture(Activity activity, GesContract.Callback callback) {
        this.callback = callback;
        addGesView(activity, GestureLockHelper.MODE_GES_UNLOCK);
    }

    public void setGesture(Activity activity, GesContract.Callback callback) {
        this.callback = callback;
        addGesView(activity, GestureLockHelper.MODE_GES_SET);
    }

    /**
     * 添加手势view
     * @param activity
     */
    private void addGesView(Activity activity, int mode) {
        contentView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = LayoutInflater.from(activity).inflate(R.layout.layout_ges, null);
        contentView.addView(rootView);
        glv = (GestureLockView) rootView.findViewById(R.id.glv);
        tv_pwd_tips = (TextView) rootView.findViewById(R.id.tv_pwd_tips);
        helper = new GestureLockHelper(activity, glv);
        helper.setMode(mode);
        helper.setGestureLockCallback(new GestureLockHelper.GestureLockCallback() {
            @Override
            public void onFinish(int resultCode, String resultDesc, String result) {
                tv_pwd_tips.setText(resultDesc);
                if (resultCode == GestureLockHelper.CODE_UNLOCK_OK) {
                    contentView.removeView(rootView);
                    if(callback != null) {
                        callback.onCallback(true, resultDesc);
                    }
                } else if(resultCode == GestureLockHelper.CODE_SET_OK) {
                    //helper.setMode(GestureLockHelper.MODE_GES_UNLOCK);
                    if(callback != null) {
                        callback.onCallback(true, resultDesc);
                    }
                } else {
                    if(callback != null) {
                        callback.onCallback(false, resultDesc);
                    }
                }
            }
        });
    }
}
