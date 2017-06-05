package com.dji.FPVDemo.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.dji.FPVDemo.R;

/**
 * Created by lenovo on 2016/11/23.
 */

//播放警报BGM
public class SoundPlayUtils {
    // SoundPool对象
    public static SoundPool mSoundPlayer = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    public static SoundPlayUtils soundPlayUtils;
    // 上下文
    static Context mContext;

    public static SoundPlayUtils init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
        }

        // 初始化声音
        mContext = context;

        mSoundPlayer.load(mContext, R.raw.warning_bgm, 1);// 1

        return soundPlayUtils;
    }

    public static void play(int soundID) {
        mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
    }

    public static void releaseSource(int soundID){
        mSoundPlayer.unload(soundID);
    }
}
