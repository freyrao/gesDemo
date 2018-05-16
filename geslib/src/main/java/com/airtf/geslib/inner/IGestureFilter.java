package com.airtf.geslib.inner;

import android.app.Activity;

/**
 * Author: freyrao
 * Time: 18/5/14 11:48
 * Desc:
 */

public interface IGestureFilter {
    /**
     * 判断是否显示手势
     * @param activity
     * @return
     */
    public boolean isShowGesture(Activity activity);
}
