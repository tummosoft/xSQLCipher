package com.tummosoft;

import android.content.Context;
import androidx.annotation.NonNull;
import anywheresoftware.b4a.BA;
import java.io.File;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static String DB_NAME;
    private Context context;    
    private String sourceDB;
    private String targetDB;
    private int rule;
    private int version;
    private byte[] password1 = null;
    private String password2 = "";
    private BA ba;
    private String Event;
  
    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
        DB_NAME = PreferencesHelper.getTokenFromPreferences(context, "R_DB_NAME");
    }
        
    private SQLiteHelper createDelegate(Context context, String name, int version) {
        return (new SQLiteHelper(context, name, null, version));
    }

    @Override
    public void onCreate(SQLiteDatabase sqld) {        
        BA.Log("Database created" + sqld.getPath());
    }

    public static boolean databaseFileExists(@NonNull Context context) {
        return context.getDatabasePath(DB_NAME).exists();
    }

    public static java.io.File getDatabaseFile(@NonNull Context context) {
        return context.getDatabasePath(DB_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqld, int oldversion, int newversion) {
        BA.Log("Database has updated" + sqld.getPath());
    }

    @Override
    public void onOpen(SQLiteDatabase sqld) {
        super.onOpen(sqld);
    }

    @Override
    public void close() {
        super.close();
    }
}
