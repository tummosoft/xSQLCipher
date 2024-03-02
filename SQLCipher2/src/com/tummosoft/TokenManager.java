package com.tummosoft;

import android.content.Context;
import android.content.SharedPreferences;
import anywheresoftware.b4a.BA;

@BA.ShortName("TokenManager")
public class TokenManager {
    private Context context;
        
    public void initialize(final BA ba) {        
        this.context = ba.context;        
    }

    public void saveTokenToPreferences(String keyName, String ValueToken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(keyName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyName, ValueToken);
        editor.apply();
    }

    public String getTokenFromPreferences(String keyName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(keyName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(keyName, null);
    }
     
}
