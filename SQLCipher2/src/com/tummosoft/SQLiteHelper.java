package com.tummosoft;

import android.content.Context;
import anywheresoftware.b4a.BA;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
     public enum State {
        DOES_NOT_EXIST, UNENCRYPTED, ENCRYPTED
    }
    
    private String PASSWORD;
    private String DB_NAME;
    private String token;
    
    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);        
    }

    @Override
    public void onCreate(SQLiteDatabase sqld) {        
        BA.Log("Database created" + sqld.getPath());                
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqld, int oldversion, int newversion) {
        BA.Log("Database has updated" + sqld.getPath());                
    }

    @Override
    public void onOpen(SQLiteDatabase sqld) {
        super.onOpen(sqld);                           
    }
}
