package com.tummosoft;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import anywheresoftware.b4a.BA;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;
import net.sqlcipher.database.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
        
    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    
    @Override
    public void onCreate(SQLiteDatabase sqld) {        
        BA.Log("Database created" + sqld.getPath());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqld, int i, int i1) {
        BA.Log("Database has updated" + sqld.getPath());
    }

   
  
    
}
