package com.tummosoft;

import android.content.Context;
import android.content.SharedPreferences;
import anywheresoftware.b4a.BA;
import java.io.File;
import net.sqlcipher.database.SQLiteDatabase;

@BA.ShortName("TokenManager")
public class TokenManager {
    private Context context;
                
    public void initialize(final BA ba) {        
        this.context = ba.context;        
    }

    public void saveTokenToPreferences(String keyName, String ValueToken) {
       PreferencesHelper.saveTokenToPreferences(context, keyName, ValueToken);
    }

    public String getTokenFromPreferences(String keyName) {        
        return PreferencesHelper.getTokenFromPreferences(context, keyName);
    }
    
      public static String getDatabaseState(File dbPath) {
    if (dbPath.exists()) {
      SQLiteDatabase dbtemp=null;

      try {
        dbtemp = SQLiteDatabase.openDatabase(dbPath.getAbsolutePath(), "", null, SQLiteDatabase.OPEN_READONLY);
        dbtemp.getVersion();

        return("UNENCRYPTED");
      }
      catch (Exception e) {
        return("ENCRYPTED");
      }
      finally {
        if (dbtemp != null) {
          dbtemp.close();
        }
      }
    }

    return("DOES_NOT_EXIST");
  }
     
}
