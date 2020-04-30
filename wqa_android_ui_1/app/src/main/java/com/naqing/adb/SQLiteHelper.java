package com.naqing.adb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class SQLiteHelper extends SQLiteOpenHelper {

    public final Lock dbLock = new ReentrantLock();

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean IsTableExist(String table_name) throws Exception {
        try (SQLiteDatabase db = getReadableDatabase()) {
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
                    + table_name.trim() + "' ";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean IsTableEmpty(String table_name) throws Exception {
        String sql = "select * from " + table_name;
//        CallableStatement prepareCall = conn.prepareCall(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//        try (ResultSet rs = prepareCall.executeQuery()) {
//            return !rs.next();
//        }
        //ToDo add function code
        return false;
    }

    public void DropTable(String table_name) throws Exception {
        try (SQLiteDatabase db = getWritableDatabase()) {
            String DEL_TABLE = "drop table " + table_name;
            db.execSQL(DEL_TABLE);
        }
    }

    public String[] GetAllTables() throws Exception {
        ArrayList<String> tables = new ArrayList();
        try (SQLiteDatabase db = getReadableDatabase()) {
//            String sql = "select name from sqlite_master where type ='table'";
//            Cursor cursor = db.rawQuery(sql, null);
//            if (cursor.moveToNext()) {
//
//            }
            Cursor cursor = db.rawQuery("select * from sqlite_master where type ='table'", null);
            while (cursor.moveToNext()) {
                tables.add(cursor.getString(1));
            }
        }
        return tables.toArray(new String[0]);
    }
}
