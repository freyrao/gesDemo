package com.airtf.geslib;
import android.app.Activity;

/**
 * @author zzy
 * @date 2018/5/16
 */

public interface GesContract {
    interface Req {
        /**
         * 重置手势密码
         */
        void resetPassword(Activity activity, Callback callback);

        /**
         * 显示手势密码浮层
         */
        void showPassword(Activity activity, Callback callback);
    }
    interface Callback{
        void onCallback(boolean bResult, String msg);
    }
}
