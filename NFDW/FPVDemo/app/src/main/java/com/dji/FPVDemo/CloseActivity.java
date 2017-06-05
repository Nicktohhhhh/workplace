package com.dji.FPVDemo;

import android.app.Activity;

import java.util.LinkedList;

/**
 * Created by 沉默的奔跑者--zhou on 2017/1/15.
 */
public class CloseActivity {

    private static LinkedList<Activity> acys = new LinkedList<Activity>();

    public static Activity curActivity;

    public static void add(Activity acy)
    {
        acys.add(acy);
    }

    public static void remove(Activity acy) {
        acys.remove(acy);
    }

    public static void close()
    {
        Activity acy;
        while (acys.size() != 0)
        {
            acy = acys.poll();
            if (!acy.isFinishing())
            {
                acy.finish();
            }
        }
//        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
