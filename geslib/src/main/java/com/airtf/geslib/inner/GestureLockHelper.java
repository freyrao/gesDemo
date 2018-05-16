package com.airtf.geslib.inner;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 手势密码辅助类，可设置手势回调
 * 使用方法：
 * helper = new GestureLockHelper(activity, glv);
 * helper.setMode(GestureLockHelper.MODE_GES_UNLOCK);
 * helper.setGestureLockCallback(new GestureLockHelper.GestureLockCallback() {});
 *
 * Author: freyrao
 * Time: 18/5/10 10:48
 * Desc:
 */

public class GestureLockHelper {
    private static final String TAG = "GestureLockHelper";

    /**解锁成功**/
    public static final int CODE_UNLOCK_OK = 0;
    /**解锁失败，与原有密码不一致**/
    public static final int CODE_UNLOCK_ERROR = 1;
    /**设置手势成功**/
    public static final int CODE_SET_OK = 2;
    /**设置手势：两次输入不一致**/
    public static final int CODE_SET_DIFF = 3;
    /**设置手势：输入第一次成功**/
    public static final int CODE_SET_FIRST_OK = 4;
    /**手势少于4个点**/
    public static final int CODE_GES_SHORT = 5;
    /**其他错误**/
    public static final int CODE_GES_ERROR = 6;
    /**保存的手势密码异常：可能被清除等**/
    public static final int CODE_GES_SAVE_ERROR = 7;
    /**清除手势密码**/
    public static final int CODE_GES_CLEAR = 8;


    /**SharedPreferences存储**/
    private static final String PREF_NAME = "gesture_lock";
    private static final String GESTURE_PWD = "gesture_pwd";
    /**设置手势**/
    public static final int MODE_GES_SET = 0;
    /**手势解锁**/
    public static final int MODE_GES_UNLOCK = 1;

    private Context context;
    private GestureLockView glv;
    private SharedPreferences pref;
    /**保存的值**/
    private String savePwd;
    /**手势的值**/
    private String glvPwd;
    private int mode = MODE_GES_UNLOCK;

    private GestureLockCallback callback;

    public GestureLockHelper(Context context, GestureLockView glv) {
        this(context, glv, MODE_GES_UNLOCK);
    }

    public GestureLockHelper(Context context, GestureLockView glv, int mode) {
        this.context = context;
        this.glv = glv;
        setMode(mode);
        glv.setGesEnable(true);
        init();
    }

    public void setMode(int mode) {
        this.mode = mode;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if(mode != MODE_GES_SET) {
            savePwd = pref.getString(GESTURE_PWD, "");
        }
        glvPwd = "";
        glv.setGesEnable(true);
    }

//    public void resetMode(int mode) {
//        this.setMode(mode);
//    }

    private void init() {
        glv.setOnGestureLockListener(new GestureLockView.OnGestureLockListener() {
            @Override
            public void onGesture(int status, int resultCode, String result) {
                if(status == GestureLockView.STATUS_OVER) {
                    if(mode == MODE_GES_SET) {
                        checkGesSet(resultCode, result);
                    } else {
                        checkGesUnlock(resultCode, result);
                    }
                }
            }
        });
    }

    /**
     * 设置手势校验
     * @param resultCode
     * @param result
     */
    private void checkGesSet(int resultCode, String result) {
        if(resultCode == GestureLockView.RESULT_OK) {
            // 绘制正确，校验两次是否一致
            if(TextUtils.isEmpty(glvPwd)) {
                // 第一次滑动
                glvPwd = result;
                callback.onFinish(CODE_SET_FIRST_OK, "第一次设置成功", result);
                glv.reset();
            } else {
                // 第二次滑动
                if(glvPwd.equals(result)) {
                    pref.edit().putString(GESTURE_PWD, result).commit();
                    glv.setGesEnable(false);
                    glv.reset();
                    callback.onFinish(CODE_SET_OK, "设置成功", result);
                } else {
                    callback.onFinish(CODE_SET_DIFF, "两次不一致，请重新绘制", "");
                }
            }
        } else if(resultCode == GestureLockView.RESULT_ERROR_POINT_SHORT){
            // 绘制太短
            if(TextUtils.isEmpty(glvPwd)) {
                // 第一次滑动
                callback.onFinish(CODE_GES_SHORT, "需要至少4个点", "");
            } else {
                // 第二次滑动
                callback.onFinish(CODE_SET_DIFF, "两次不一致，请重新绘制", "");
            }
        } else {
            callback.onFinish(CODE_GES_ERROR, "设置异常","");
        }
    }

    /**
     * 校验解锁
     * @param resultCode
     * @param result
     */
    private void checkGesUnlock(int resultCode, String result) {
        if(TextUtils.isEmpty(savePwd)) {
            callback.onFinish(CODE_GES_SAVE_ERROR, "手势密码被清除", "");
            return;
        }

        if(resultCode == GestureLockView.RESULT_OK) {
            if(savePwd.equals(result)) {
                glv.setGesEnable(false);
                glv.reset();
                callback.onFinish(CODE_UNLOCK_OK, "解锁成功", result);
            } else {
                callback.onFinish(CODE_UNLOCK_ERROR, "解锁失败", "");
            }
        } else if(resultCode == GestureLockView.RESULT_ERROR_POINT_SHORT) {
            callback.onFinish(CODE_GES_SHORT, "需要至少4个点", "");
        } else {
            callback.onFinish(CODE_GES_ERROR, "解锁异常","");
        }
    }

    /**
     * 清除密码
     */
    public void clearPwd() {
        pref.edit().clear();
        callback.onFinish(CODE_GES_CLEAR, "清除手势密码成功", "");
    }

    /**
     * 设置回调
     * @param callback
     */
    public void setGestureLockCallback(GestureLockCallback callback) {
        this.callback = callback;
    }

    /**
     * 获取手势密码
     */
    public String getGesPwd() {
        return pref.getString(GESTURE_PWD, "");
    }

    /**
     * 判断是否设置密码
     * @return
     */
    public boolean isSetGes() {
        String gesPwd = getGesPwd();
        return !TextUtils.isEmpty(gesPwd);
    }

    public interface GestureLockCallback {
        public void onFinish(int resultCode, String resultDesc, String result);
    }

}
