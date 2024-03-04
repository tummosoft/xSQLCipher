package com.tummosoft;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    
     static public void saveTokenToPreferences(Context context, String keyName, String ValueToken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(keyName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyName, ValueToken);
        editor.apply();
    }

    static public String getTokenFromPreferences(Context context, String keyName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(keyName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(keyName, null);
    }
}
