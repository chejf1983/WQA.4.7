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

    /** 表是否存在*/
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

    /** 表是否为空*/
    public boolean IsTableEmpty(String table_name) throws Exception {
        try (SQLiteDatabase db = getReadableDatabase()) {
            String sql = "select count(*) from " + table_name;
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

    /** 删除表*/
    public void DropTable(String table_name) throws Exception {
        try (SQLiteDatabase db = getWritableDatabase()) {
            String DEL_TABLE = "drop table " + table_name;
            db.execSQL(DEL_TABLE);
        }
    }

    /** 列举所有表*/
    public String[] GetAllTables() throws Exception {
        ArrayList<String> tables = new ArrayList();
        try (SQLiteDatabase db = getReadableDatabase()) {
            Cursor cursor = db.rawQuery("select * from sqlite_master where type ='table'", null);
            while (cursor.moveToNext()) {
                tables.add(cursor.getString(1));
            }
        }
        return tables.toArray(new String[0]);
    }
}
