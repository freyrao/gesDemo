package com.airtf.geslib;

import android.app.Activity;

import com.airtf.geslib.inner.GestureController;

/**
 * @author zzy
 * @date 2018/5/16
 */

public class GesProxy implements GesContract.Req{
    public static GesProxy getInstance(){
        return LazyHolder.ourInstance;
    }
    private static class LazyHolder {
        private static final GesProxy ourInstance = new GesProxy();
    }
    private GesProxy() {}


    @Override
    public void resetPassword(Activity activity, GesContract.Callback callback) {
        GestureController.getInstance().setGesture(activity, callback);
    }

    @Override
    public void showPassword(Activity activity, GesContract.Callback callback) {
        GestureController.getInstance().showGesture(activity, callback);
    }

}
