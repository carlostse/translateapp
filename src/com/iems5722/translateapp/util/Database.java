package com.iems5722.translateapp.util;

import java.util.ArrayList;
import java.util.List;
import com.iems5722.translateapp.object.History;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

    private static final String TAG = "Database";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "translation";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "create tables");
        db.execSQL("CREATE TABLE history (message TEXT, type INTEGER)");
        db.execSQL("CREATE TABLE cache (source TEXT, result TEXT, PRIMARY KEY(source))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "upgrade from " + oldVersion + " to " + newVersion);
    }

    /** -----------
     *  | history |
     *  -----------
     */

    public List<History> getHistory() {
        List<History> list = new ArrayList<History>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("history", new String[]{"ROWID", "message", "type"},
                null, null, null, null, "ROWID");

        if (c == null || !c.moveToFirst())
            return list;

        do {
            list.add(new History(c.getInt(0), c.getString(1), c.getInt(2)));
        } while (c.moveToNext());

        db.close();
        return list;
    }

    public void saveHistory(History obj) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("message", obj.getText());
        values.put("type", obj.getType().ordinal());

        db.insert("history", null, values);
        db.close();
    }

    public void deleteHistory(int rowId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("history", "ROWID=?", new String[]{Integer.toString(rowId)});
        db.close();
    }

    /** ---------
     *  | cache |
     *  ---------
     */
    public String getCache(String source) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query("cache", new String[]{"result"},
                "source=?", new String[]{source}, null, null, null);

        if (c == null || !c.moveToFirst())
            return null;

        return c.getString(0);
    }

    public void saveCache(String source, String result) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("source", source);
        values.put("result", result);

        db.insert("cache", null, values);
        db.close();
    }
}
