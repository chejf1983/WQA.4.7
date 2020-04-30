package com.naqing.adb;

import android.app.Activity;

import wqa.control.DB.IAlarmHelper;
import wqa.control.DB.IDBFix;
import wqa.control.DB.IDataHelper;
import wqa.control.DB.IJDBHelper;

public class ADBHelper implements IJDBHelper {
    private Activity parent;
    private SQLiteHelper dbsaver;
    private ADataDB aDataDB;
    public ADBHelper(Activity parent){
        this.parent = parent;
    }

    @Override
    public void Init(String s) throws Exception {
        dbsaver = new SQLiteHelper(parent, "wqa.db", null, 1);
        aDataDB = new ADataDB(dbsaver);
    }

    @Override
    public void Close() {
        dbsaver.close();
        dbsaver = null;
        aDataDB = null;
    }

    @Override
    public IDBFix GetDBFix() {
        return null;
    }

    @Override
    public IAlarmHelper GetAlarmDB() {
        return null;
    }

    @Override
    public IDataHelper GetDataDB() {
        return aDataDB;
    }
}
