package com.lyn.face_doorkeeper.utils;

import android.media.AudioManager;
import android.media.ToneGenerator;

/*
铃声播报
 */
public class ToneUtil {
    private static ToneGenerator mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    public static void playPromptTone() {
        if (mToneGenerator != null) {
            mToneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 100);
        }
    }

    public static void playPromptTone(int code) {
        if (mToneGenerator != null) {
            mToneGenerator.startTone(code, 100);
        }
    }
}
