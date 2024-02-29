package com.tummosoft.Err;

import android.content.Context;
import anywheresoftware.b4a.BA;
import java.io.File;
import net.zetetic.database.DatabaseErrorHandler;
import net.zetetic.database.sqlcipher.SQLiteDatabase;
import net.zetetic.database.sqlcipher.SQLiteOpenHelper;
import net.zetetic.database.DatabaseUtils;

public class xCiperErrorHandler implements DatabaseErrorHandler {
    private BA _ba;
    private String _event;
        
    public xCiperErrorHandler(String event, BA ba) {
        _ba = ba;
        _event = event.toLowerCase();
    }
        
    @Override
    public void onCorruption(SQLiteDatabase db) {
        BA.LogError("Database corruption detected.");        
        db.close();
        _ba.raiseEventFromUI(this, _event + "_oncorruption", true);
            
    }
    
}
