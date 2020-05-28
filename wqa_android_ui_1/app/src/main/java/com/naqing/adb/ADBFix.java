package com.naqing.adb;

import java.util.Date;
import java.util.logging.Level;

import nahon.comm.faultsystem.LogCenter;
import wqa.control.DB.IDBFix;
import wqa.control.DB.IJDBHelper;
import wqa.control.data.DevID;
import wqa.control.data.IMainProcess;

public class ADBFix implements IDBFix {
    private final SQLiteHelper db_instance;
    private final IJDBHelper parent;

    public ADBFix(SQLiteHelper helper, IJDBHelper parent) {
        this.db_instance = helper;
        this.parent = parent;
    }

//    public void DeleteData(DevID id) {
//        try {
//            this.parent.GetDataDB().DeleteTable(id);
//        } catch (Exception e) {
//            LogCenter.Instance().SendFaultReport(Level.SEVERE, "数据删除失败" + e.getMessage());
//        }
//
//        try {
//            if (this.parent.GetAlarmDB() != null) {
//                this.parent.GetAlarmDB().DeleteAlarm(id);
//            }
//        } catch (Exception e) {
//            LogCenter.Instance().SendFaultReport(Level.SEVERE, "报警信息删除失败" + e.getMessage());
//        }
//    }

    @Override
    public void DeleteData(Date date, IMainProcess iMainProcess) {
//


    }

    @Override
    public String GetDBSize() {
        long fileSize = ((ADBHelper) this.parent).GetDBFileSize();
        if (fileSize < 1048576) {
            return fileSize / 1024 + "K";
        } else {
            return fileSize / 1048576 + "M";
        }
    }
}
