package com.dji.FPVDemo.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;

/**
 * Created by 沉默的奔跑者--zhou on 2017/4/7.
 */

public class ChangeOrientationHandler extends Handler {

    private Activity activity;

    public ChangeOrientationHandler(Activity ac) {
        super();
        activity = ac;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == 888) {
            int orientation = msg.arg1;
            if (orientation > 45 && orientation < 135) {
                activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
//                Log.d(MainActivity.TAG, "横屏翻转: ");
            }
//                else if (orientation > 135 && orientation < 225) {
//                    activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_PORTRAIT);
////                Log.d(MainActivity.TAG, "竖屏翻转: ");
//                }
            else if (orientation > 225 && orientation < 315) {
                activity.setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
//                Log.d(MainActivity.TAG, "横屏: ");
            }
//                else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
//                    activity.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
////                Log.d(MainActivity.TAG, "竖屏: ");
//                }
        }
        super.handleMessage(msg);
    }
}
