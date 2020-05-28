package com.naqing.adb;

import android.app.Activity;

import java.io.File;

import wqa.control.DB.IAlarmHelper;
import wqa.control.DB.IDBFix;
import wqa.control.DB.IDataHelper;
import wqa.control.DB.IJDBHelper;

public class ADBHelper implements IJDBHelper {
    private Activity parent;
    private SQLiteHelper dbsaver;
    private ADataDB aDataDB;
    private AAlarmDB aAlarmDB;
    private ADBFix aDBFix;
    public static String dbName = "wqa.db";

    public ADBHelper(Activity parent) {
        this.parent = parent;
    }

    @Override
    public void Init(String s) throws Exception {
        dbsaver = new SQLiteHelper(parent, dbName, null, 1);
        aDataDB = new ADataDB(dbsaver);
        aAlarmDB = new AAlarmDB(dbsaver);
        aAlarmDB.CreateTable();
        aDBFix = new ADBFix(dbsaver, this);
    }

    @Override
    public void Close() {
        dbsaver.close();
        dbsaver = null;
        aDataDB = null;
        aAlarmDB = null;
    }

    @Override
    public IDBFix GetDBFix() {
        return aDBFix;
    }

    @Override
    public IAlarmHelper GetAlarmDB() { return aAlarmDB; }

    @Override
    public IDataHelper GetDataDB() {
        return aDataDB;
    }

    public long GetDBFileSize(){
        return parent.getDatabasePath(dbName).length();
    }
}
