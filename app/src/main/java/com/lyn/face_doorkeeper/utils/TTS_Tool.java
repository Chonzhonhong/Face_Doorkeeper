package com.lyn.face_doorkeeper.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * tts语音引擎
 */
public class TTS_Tool {
    private static class TTS_ToolClassInstance {
        private static final TTS_Tool instance = new TTS_Tool();
    }

    public static TTS_Tool getInstance() {
        return TTS_ToolClassInstance.instance;
    }


    private List<String> submitList=new ArrayList<>();
    private Context mContext;
    public TextToSpeech textToSpeech;
    public int result;
    private final HashMap ttsOptions = new HashMap<>();

    public void init(Context context) {
        try {
            mContext = context;
            textToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == textToSpeech.SUCCESS) {
                        int speed = 2;
                        switch (speed) {
                            case 0:
                                textToSpeech.setSpeechRate(0.1f);
                                break;
                            case 1:
                                textToSpeech.setSpeechRate(0.5f);
                                break;
                            case 2:
                                textToSpeech.setSpeechRate(1f);
                                break;
                            case 3:
                                textToSpeech.setSpeechRate(1.5f);
                                break;
                            case 4:
                                textToSpeech.setSpeechRate(2f);
                                break;
                        }
                    }
                }
            });
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    LogUtils.i("onStart:"+utteranceId);
                    submitList.add(utteranceId);
                }

                @Override
                public void onDone(String utteranceId) {
                    LogUtils.i("onDone:"+utteranceId);
                    for (int i = 0; i < submitList.size(); i++) {
                        String str=submitList.get(i);
                        if (utteranceId.equals(str)){
                            submitList.remove(i);
                        }
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    LogUtils.i("onError:"+utteranceId);
                    for (int i = 0; i < submitList.size(); i++) {
                        String str=submitList.get(i);
                        if (utteranceId.equals(str)){
                            submitList.remove(i);
                        }
                    }
                }
            });
            LogUtils.i("语音引擎初始化完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置语速
    public void setSpeechRate(float speed) {
        if (textToSpeech != null) {
            textToSpeech.setSpeechRate(speed);
        }
    }

    public void setLanguage(final Locale loc) {
        try {
            textToSpeech.setLanguage(loc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置播报监听
     *
     * @param onUtteranceProgressListener
     */
    public void setOnUtteranceProgressListener(UtteranceProgressListener onUtteranceProgressListener) {
        try {
            textToSpeech.setOnUtteranceProgressListener(onUtteranceProgressListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void close() {
        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
                textToSpeech = null;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void Pronounce(String s) {
        try {
            if (textToSpeech==null){
                return;
            }
            ttsOptions.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, s);
            boolean isExist=false;
            for (String str:submitList){
                if (str.equals(s)){
                    isExist=true;
                    break;
                }
            }
            if (!isExist){
                textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, ttsOptions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void Pronounce(String s,String tag) {
        try {
            if (textToSpeech==null){
                return;
            }
            ttsOptions.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, tag);
            boolean isExist=false;
            for (String str:submitList){
                if (str.equals(tag)){
                    isExist=true;
                    break;
                }
            }
            if (!isExist){
                textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, ttsOptions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stop() {
        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
