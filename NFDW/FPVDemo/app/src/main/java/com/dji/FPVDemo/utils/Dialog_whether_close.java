package com.dji.FPVDemo.utils;

import android.content.DialogInterface;

import java.lang.reflect.Field;

/**
 * Created by lenovo on 2017/2/17.
 */

public class Dialog_whether_close {
    public void keepDialog(DialogInterface dialog) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void distoryDialog(DialogInterface dialog){
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
