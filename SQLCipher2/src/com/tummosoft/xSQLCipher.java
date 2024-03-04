package com.tummosoft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.collections.List;
import anywheresoftware.b4a.sql.SQL;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sqlcipher.DatabaseUtils;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

@BA.ShortName("xSQLCipher")
@BA.Version(1.04f)
@BA.DependsOn(values = {"android-database-sqlcipher-4.5.4.aar"})
@BA.Events(values = {"CopyCompleted(succes as boolean)"})
public class xSQLCipher extends SQL {

    private static Context _baContext;
    private String _eventName;
    private BA ba;
    static private SQLiteDatabase db;
    private SQLiteHelper helper;

    public void initializeCipher(BA ba, String event, String database, String password, int version) {
        _baContext = ba.context;
        SQLiteDatabase.loadLibs(_baContext);
        this.ba = ba;
        _eventName = event;
        helper = new SQLiteHelper(_baContext, database, null, version);
        LoadDB(password, null);
    }

    public void initializeCopy(BA ba, String event, String DBSource, String password, int version) {
        _baContext = ba.context;
        SQLiteDatabase.loadLibs(_baContext);
        
        File dbtarget = new File(DBSource);        
        if (dbtarget.exists()) {
           ba.raiseEventFromUI(_baContext, event.toLowerCase() + "_copycompleted", true);           
           db = SQLiteDatabase.openOrCreateDatabase(dbtarget, password, null, null, null);
           db.setVersion(version);
        } else {
            ba.raiseEventFromUI(_baContext, event.toLowerCase() + "_copycompleted", false);     
        }
        //
        _eventName = event;
    }

    public void initializeCipher2(BA ba, String event, String database, byte[] password, int version) {
        _baContext = ba.context;
        SQLiteDatabase.loadLibs(_baContext);
        xEncrypter.backupDatabase(_baContext, database, database);
        this.ba = ba;
        _eventName = event;
        helper = new SQLiteHelper(_baContext, database, null, version);
        LoadDB("", password);
    }

    private void CreateDB(String dbname, String password1, byte[] password2) {

        if (password1.isEmpty() && (password2 == null)) {
            db = SQLiteDatabase.openOrCreateDatabase(dbname, "", null);
        } else if (!password1.isEmpty()) {
            db = SQLiteDatabase.openOrCreateDatabase(dbname, password1, null);
        } else if (password2 != null) {
            db = SQLiteDatabase.openOrCreateDatabase(dbname, password2, null);
        }
    }

    private void LoadDB(String password1, byte[] password2) {
        try {
            if (password1.isEmpty() && (password2 == null)) {
                db = (SQLiteDatabase) helper.getWritableDatabase("");
            } else if (!password1.isEmpty()) {
                db = (SQLiteDatabase) helper.getWritableDatabase(password1);
            } else if (password2 != null) {
                db = (SQLiteDatabase) helper.getWritableDatabase(password2);
            }
        } catch (xSQLiteException ex) {
            BA.LogError("Wrong password");
        } finally {
            BA.LogInfo("Load database success");
        }
    }

    public long getPageSize() {
        return db.getPageSize();
    }

    public SQLiteStatement compileStatement(String sql) {
        return db.compileStatement(sql);
    }

    public String getPath() {

        return db.getPath();
    }

    public void DeleteTable(String table, String whereClause, String[] whereArgs) {
        db.delete(table, whereClause, whereArgs);
    }

    public String isDBEncrypted(String DBPath) throws Exception {
        java.io.File path = new java.io.File(DBPath);
        FileInputStream in = new FileInputStream(path);

        byte[] buffer = new byte[6];
        in.read(buffer, 0, 6);

        String res = "encrypted";
        if (Arrays.equals(buffer, (new String("SQLite")).getBytes())) {
            res = "unencrypted";
        }
        return res;
    }

    private volatile ArrayList<Object[]> nonQueryStatementsList = new ArrayList<Object[]>();

    private static xSQLCipher cloneMe(xSQLCipher sql) {
        xSQLCipher ret = new xSQLCipher();
        ret.db = sql.db;
        ret.nonQueryStatementsList = sql.nonQueryStatementsList;
        return ret;
    }

    public static void encrypt(String RealPath, byte[] passphrase) {
        File dbname = new File(RealPath);
        try {
            encrypt2(_baContext, dbname, passphrase);
        } catch (IOException ex) {
            Logger.getLogger(xSQLCipher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void changePassword(String password) {        
        db.changePassword(password);
        
    }

    public void changePassword2(char[] password) {
        db.changePassword(password);

    }

    public String getDatabasePath() {
        return db.getPath();
    }

    public int getVersion() {
        return db.getVersion();
    }

    private static void encrypt2(Context ctxt, File originalFile, byte[] passphrase) throws IOException {
        SQLiteDatabase.loadLibs(ctxt);
        if (originalFile.exists()) {
            File newFile = File.createTempFile("sqlcipherutils", "tmp",
                    ctxt.getCacheDir());
            SQLiteDatabase tempdb = SQLiteDatabase.openDatabase(originalFile.getAbsolutePath(), "", null, SQLiteDatabase.OPEN_READWRITE);
            int version = tempdb.getVersion();
            tempdb.close();
            tempdb = SQLiteDatabase.openDatabase(newFile.getAbsolutePath(), passphrase,
                    null, SQLiteDatabase.OPEN_READWRITE, null, null);

            final SQLiteStatement st = tempdb.compileStatement("ATTACH DATABASE ? AS plaintext KEY ''");

            st.bindString(1, originalFile.getAbsolutePath());
            st.execute();

            tempdb.rawExecSQL("SELECT sqlcipher_export('main', 'plaintext')");
            tempdb.rawExecSQL("DETACH DATABASE plaintext");
            tempdb.setVersion(version);
            st.close();
            tempdb.close();

            originalFile.delete();
            newFile.renameTo(originalFile);
        } else {
            BA.LogError(originalFile.getAbsolutePath() + " not found");
        }
    }

    public static void decrypt(String RealPath, byte[] passphrase) {
        File dbname = new File(RealPath);
        try {
            decrypt2(_baContext, dbname, passphrase);
        } catch (IOException ex) {
            Logger.getLogger(xSQLCipher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void decrypt2(Context ctxt, File originalFile, byte[] passphrase) throws IOException {
        SQLiteDatabase.loadLibs(ctxt);
        if (originalFile.exists()) {
            File newFile
                    = File.createTempFile("sqlcipherutils", "tmp",
                            ctxt.getCacheDir());
            SQLiteDatabase tempdb
                    = SQLiteDatabase.openDatabase(originalFile.getAbsolutePath(),
                            passphrase, null, SQLiteDatabase.OPEN_READWRITE, null, null);

            final SQLiteStatement st = tempdb.compileStatement("ATTACH DATABASE ? AS plaintext KEY ''");

            st.bindString(1, newFile.getAbsolutePath());
            st.execute();

            tempdb.rawExecSQL("SELECT sqlcipher_export('plaintext')");
            tempdb.rawExecSQL("DETACH DATABASE plaintext");

            int version = tempdb.getVersion();

            st.close();
            tempdb.close();

            tempdb = SQLiteDatabase.openDatabase(newFile.getAbsolutePath(), "",
                    null, SQLiteDatabase.OPEN_READWRITE);
            tempdb.setVersion(version);
            tempdb.close();

            originalFile.delete();
            newFile.renameTo(originalFile);
        } else {
            BA.LogError(originalFile.getAbsolutePath() + " not found");
        }
    }

    private void checkNull() {
        if (db == null) {
            throw new RuntimeException("Object should first be initialized.");
        }
    }

    /**
     * Tests whether the database is initialized and opened.
     */
    @Override
    public boolean IsInitialized() {
        if (db == null) {
            return false;
        }
        return db.isOpen();
    }

    /**
     * Executes a single non query SQL statement. null null null     Example:<code>
	 *SQL1.ExecNonQuery("CREATE TABLE table1 (col1 TEXT , col2 INTEGER, col3 INTEGER)")</code>
     * If you plan to do many "writing" queries one after another, then you
     * should consider using BeginTransaction / EndTransaction. It will execute
     * significantly faster.
     */
    @Override
    public void ExecNonQuery(String Statement) {
        checkNull();
        db.execSQL(Statement);
    }

    /**
     * Executes a single non query SQL statement. The statement can include
     * question marks which will be replaced by the items in the given list.
     * Note that Basic4android converts arrays to lists implicitly. The values
     * in the list should be strings, numbers or bytes arrays. null null null     Example:<code>
	 *SQL1.ExecNonQuery2("INSERT INTO table1 VALUES (?, ?, 0)", Array As Object("some text", 2))</code>
     */
    @Override
    public void ExecNonQuery2(String Statement, List Args) {
        checkNull();
        SQLiteStatement s = db.compileStatement(Statement);
        try {
            int numArgs = Args.getSize();
            for (int i = 0; i < numArgs; i++) {
                DatabaseUtils.bindObjectToProgram(s, i + 1, Args.Get(i));
            }
            s.execute();
        } finally {
            s.close();
        }
    }

    /**
     * Adds a non-query statement to the batch of statements. The statements are
     * (asynchronously) executed when you call ExecNonQueryBatch. Args parameter
     * can be Null if it is not needed. null null null     Example:<code>
	 *For i = 1 To 1000
     *	sql.AddNonQueryToBatch("INSERT INTO table1 VALUES (?)", Array(Rnd(0, 100000)))
     *Next
     *Dim SenderFilter As Object = sql.ExecNonQueryBatch("SQL")
     *Wait For (SenderFilter) SQL_NonQueryComplete (Success As Boolean)
     *Log("NonQuery: " & Success)</code>
     */
    @Override
    public void AddNonQueryToBatch(String Statement, List Args) {
        nonQueryStatementsList.add(new Object[]{Statement, Args});
    }

    /**
     * Asynchronously executes a batch of non-query statements (such as INSERT).
     * The NonQueryComplete event is raised after the statements are completed.
     * You should call AddNonQueryToBatch one or more times before calling this
     * method to add statements to the batch. Note that this method internally
     * begins and ends a transaction. Returns an object that can be used as the
     * sender filter for Wait For calls. null null null     Example:<code>
	 *For i = 1 To 1000
     *	sql.AddNonQueryToBatch("INSERT INTO table1 VALUES (?)", Array(Rnd(0, 100000)))
     *Next
     *Dim SenderFilter As Object = sql.ExecNonQueryBatch("SQL")
     *Wait For (SenderFilter) SQL_NonQueryComplete (Success As Boolean)
     *Log("NonQuery: " & Success)</code>
     */
    @Override
    public Object ExecNonQueryBatch(final BA ba, final String EventName) {
        final ArrayList<Object[]> myList = nonQueryStatementsList;
        nonQueryStatementsList = new ArrayList<Object[]>();
        final xSQLCipher ret = xSQLCipher.cloneMe(this);
        BA.submitRunnable(new Runnable() {

            @Override
            public void run() {
                synchronized (db) {
                    try {
                        BeginTransaction();
                        for (Object[] o : myList) {
                            ExecNonQuery2((String) o[0], (List) o[1]);
                        }
                        TransactionSuccessful();
                        EndTransaction();
                        ba.raiseEventFromDifferentThread(ret, xSQLCipher.this, 0, EventName.toLowerCase(BA.cul) + "_nonquerycomplete", true, new Object[]{true});
                    } catch (Exception e) {
                        EndTransaction();
                        e.printStackTrace();
                        ba.setLastException(e);
                        ba.raiseEventFromDifferentThread(ret, xSQLCipher.this, 0, EventName.toLowerCase(BA.cul) + "_nonquerycomplete", true, new Object[]{false});
                    }
                }
            }

        }, this, 1);
        return ret;
    }

    /**
     * Asynchronously executes the given query. The QueryComplete event will be
     * raised when the results are ready. Note that ResultSet extends Cursor.
     * You can use Cursor if preferred. Returns an object that can be used as
     * the sender filter for Wait For calls. null null null     Example:<code>
	 *Dim SenderFilter As Object = sql.ExecQueryAsync("SQL", "SELECT * FROM table1", Null)
     *Wait For (SenderFilter) SQL_QueryComplete (Success As Boolean, rs As ResultSet)
     *If Success Then
     *	Do While rs.NextRow
     *		Log(rs.GetInt2(0))
     *	Loop
     *	rs.Close
     *Else
     *	Log(LastException)
     *End If</code>
     */
    @Override
    public Object ExecQueryAsync(final BA ba, final String EventName, final String Query, final List Args) {
        final xSQLCipher ret = xSQLCipher.cloneMe(this);
        ba.submitRunnable(new Runnable() {

            @Override
            public void run() {
                synchronized (db) {
                    try {
                        String[] s = null;
                        if (Args != null && Args.IsInitialized()) {
                            s = new String[Args.getSize()];
                            for (int i = 0; i < s.length; i++) {
                                Object o = Args.Get(i);
                                s[i] = o == null ? null : String.valueOf(o);
                            }
                        }
                        Cursor c = ExecQuery2(Query, s);
                        ba.raiseEventFromDifferentThread(ret, xSQLCipher.this, 0, EventName.toLowerCase(BA.cul) + "_querycomplete", true, new Object[]{true, AbsObjectWrapper.ConvertToWrapper(new ResultSetWrapper(), c)});
                    } catch (Exception e) {
                        e.printStackTrace();
                        ba.setLastException(e);
                        ba.raiseEventFromDifferentThread(ret, xSQLCipher.this, 0, EventName.toLowerCase(BA.cul) + "_querycomplete", true, new Object[]{false, AbsObjectWrapper.ConvertToWrapper(new ResultSetWrapper(), null)});
                    }
                }
            }

        }, this, 0);
        return ret;
    }

    /**
     * Executes the query and returns a cursor which is used to go over the
     * results. null null null     Example:<code>
	 *Dim Cursor As Cursor
     *Cursor = SQL1.ExecQuery("SELECT col1, col2 FROM table1")
     *For i = 0 To Cursor.RowCount - 1
     *	Cursor.Position = i
     *	Log(Cursor.GetString("col1"))
     *	Log(Cursor.GetInt("col2"))
     *Next</code>
     */
    @Override
    public Cursor ExecQuery(String Query) {
        checkNull();
        return ExecQuery2(Query, null);
    }

    /**
     * Executes the query and returns a cursor which is used to go over the
     * results. The query can include question marks which will be replaced with
     * the values in the array. null null null     Example:<code>
	 *Dim Cursor As Cursor
     *Cursor = sql1.ExecQuery2("SELECT col1 FROM table1 WHERE col3 = ?", Array As String(22))</code>
     * SQLite will try to convert the string values based on the columns types.
     */
    @Override
    public Cursor ExecQuery2(String Query, String[] StringArgs) {
        checkNull();
        return db.rawQuery(Query, StringArgs);
    }

    /**
     * Executes the query and returns the value in the first column and the
     * first row (in the result set). Returns Null if no results were found.
     * null null null     Example:<code>
	 *Dim NumberOfMatches As Int
     *NumberOfMatches = SQL1.ExecQuerySingleResult("SELECT count(*) FROM table1 WHERE col2 > 300")</code>
     */
    @Override
    public String ExecQuerySingleResult(String Query) {
        return ExecQuerySingleResult2(Query, null);
    }

    /**
     * Executes the query and returns the value in the first column and the
     * first row (in the result set). Returns Null if no results were found.
     * null null null     Example:<code>
	 *Dim NumberOfMatches As Int
     *NumberOfMatches = SQL1.ExecQuerySingleResult2("SELECT count(*) FROM table1 WHERE col2 > ?", Array As String(300))</code>
     */
    @Override
    public String ExecQuerySingleResult2(String Query, String[] StringArgs) {
        checkNull();
        Cursor cursor = db.rawQuery(Query, StringArgs);
        try {
            if (!cursor.moveToFirst()) {
                return null;
            }
            if (cursor.getColumnCount() == 0) {
                return null;
            }
            return cursor.getString(0);
        } finally {
            cursor.close();
        }
    }

    /**
     * Begins a transaction. A transaction is a set of multiple "writing"
     * statements that are atomically committed, hence all changes will be made
     * or no changes will be made. As a side effect those statements will be
     * executed significantly faster (in the default case a transaction is
     * implicitly created for each statement). It is very important to handle
     * transaction carefully and close them. The transaction is considered
     * successful only if TransactionSuccessful is called. Otherwise no changes
     * will be made. Typical usage:<code>
     *SQL1.BeginTransaction
     *Try
     *	'block of statements like:
     *	For i = 1 to 1000
     *		SQL1.ExecNonQuery("INSERT INTO table1 VALUES(...)
     *	Next
     *	SQL1.TransactionSuccessful
     *Catch
     *	Log(LastException.Message) 'no changes will be made
     *End Try
     *SQL1.EndTransaction</code>
     */
    @Override
    public void BeginTransaction() {
        checkNull();
        db.beginTransaction();
    }

    /**
     * Marks the transaction as a successful transaction. No further statements
     * should be executed till calling EndTransaction.
     */
    @Override
    public void TransactionSuccessful() {
        db.setTransactionSuccessful();
    }

    /**
     * Ends the transaction.
     */
    @Override
    public void EndTransaction() {
        db.endTransaction();
    }

    /**
     * Closes the database. Does not do anything if the database is not opened
     * or was closed before.
     */
    @Override
    public void Close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
// Nguyen cho phuoc duc nay hoa giai tat ca ta thuat cua ut vuon co, 6 chia, ut tron, 8 duc, vo chong thang gan dap ong ta,...
