package com.lyn.face_doorkeeper.utils;

import android.text.TextUtils;

public class StringUtils {


    public static String realNameDisplay(String src) {
        if (TextUtils.isEmpty(src)) {
            return "";
        }

        return src.substring(0,1).concat("***");
    }
}
