package com.zhd.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.zhd.AppHelper;


public class SharedPreferencesUtils {

    private static String DATA_NAME = "CanData";

    public static int getIntData(String key) {
        SharedPreferences sharedPreferences = AppHelper.getContext().getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, -1);
    }

    public static void setIntData(String key, int value) {
        SharedPreferences sharedPreferences = AppHelper.getContext().getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}
