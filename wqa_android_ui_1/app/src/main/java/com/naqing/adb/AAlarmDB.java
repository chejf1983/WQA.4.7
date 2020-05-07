package com.naqing.adb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import nahon.comm.faultsystem.LogCenter;
import wqa.control.DB.AlarmRecord;
import wqa.control.DB.IAlarmHelper;
import wqa.control.common.SDisplayData;
import wqa.control.data.DevID;
import wqa.control.data.IMainProcess;

public class AAlarmDB implements IAlarmHelper {
    private final SQLiteHelper db_instance;

    public static String AlarmTable = AAlarmDB.class.getSimpleName().toUpperCase() + "AlarmTable";

    public static String DevInfo_Key = "dev_info";
    public static String Time_Key = "time";
    public static String Alarm_Key = "alarm";
    public static String AlarmInfo_Key = "alarm_info";
    private boolean is_inited = false;

    public AAlarmDB(SQLiteHelper helper) {
        this.db_instance = helper;
    }

    // <editor-fold defaultstate="collapsed" desc="例举报警信息">
    @Override
    public DevID[] ListAllDevice() {
        db_instance.dbLock.lock();
        try (SQLiteDatabase db = db_instance.getReadableDatabase()) {
            ArrayList<DevID> devices = new ArrayList();
            String sql = "select distinct(" + DevInfo_Key + ")" + DevInfo_Key + " from " + AlarmTable;

            Cursor ret_set = db.rawQuery(sql, null);
            //检查是否为空集
            while (ret_set.moveToNext()) {
                String devinfo = ret_set.getString(0);
                devices.add(new DevID(devinfo));
            }
            return devices.toArray(new DevID[0]);
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "获取设备列表失败", ex);
            return new DevID[0];
        } finally {
            db_instance.dbLock.unlock();
        }
    }

    public void CreateTable() {
        String CREATE_TABLE_SQL = "create table if not exists " + AlarmTable
                + "(" + DevInfo_Key + " varchar(50),"
                + Alarm_Key + " Int,"
                + AlarmInfo_Key + " varchar(50),"
                + Time_Key + " long primary key not null)";
        /* Creat Device Table is not exist */
        db_instance.dbLock.lock();
        try (SQLiteDatabase db = db_instance.getWritableDatabase()) {
            db.execSQL(CREATE_TABLE_SQL);
            is_inited = true;
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "创建异常", ex);
        } finally {
            db_instance.dbLock.unlock();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="删除数据">
    @Override
    public void DeleteAlarm(DevID devID) {
        if (!is_inited) {
            return;
        }

        db_instance.dbLock.lock();
        try (SQLiteDatabase db = db_instance.getWritableDatabase()) {
            String DEL_DATA_SQL = "delete from " + AlarmTable
                    + " where " + DevInfo_Key + " = " + devID.toString();
            db.execSQL(DEL_DATA_SQL);
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "删除异常", ex);
        } finally {
            db_instance.dbLock.unlock();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="保存数据">
    @Override
    public void SaveAlarmInfo(SDisplayData data) {
        if (!is_inited) {
            return;
        }
        String INSERT_TABLE_SQL = "insert into " + AlarmTable + " values(?, ?, ?, ?)";

        db_instance.dbLock.lock();
        try (SQLiteDatabase db = db_instance.getWritableDatabase()) {
            db.execSQL(INSERT_TABLE_SQL, new Object[]{data.dev_id.toString(), data.alarm, data.alram_info, data.time.getTime()});
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "报警信息保存错误", ex);
        } finally {
            db_instance.dbLock.unlock();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="搜索">
    @Override
    public void SearchAlarmInfo(DevID dev_key, Date startTime, Date stopTime, IMainProcess<AlarmRecord[]> process) {
        if (stopTime == null) {
            stopTime = new Date();
        }

        String sql = "select * from "
                + AlarmTable + " where " + DevInfo_Key + " = " + dev_key.toString() //数据类型
                + " and " + Time_Key + " <= ?";             //截至时间，为空就是当前时间

        if (startTime != null) {
            sql += " and " + Time_Key + " >= ?";            //其始时间
        }


        db_instance.dbLock.lock();
        //搜索报警信息
        try (SQLiteDatabase db = db_instance.getReadableDatabase())  {
            String[] pars;
            if (startTime != null) {
                pars = new String[]{stopTime.getTime() + "", startTime.getTime() + ""};
            } else {
                pars = new String[]{stopTime.getTime() + ""};
            }
            Cursor ret_set = db.rawQuery(sql, pars);

            //检查是否为空集
            if (!ret_set.moveToFirst()) {
                process.Finish(new AlarmRecord[0]);
            }
            //获取记录条数
            //统计记录个数
            ret_set.moveToLast();
            long data_count = ret_set.getCount();
            long row = 0;
            ret_set.moveToFirst();

            ArrayList<AlarmRecord> infolist = new ArrayList();
            //转换记录
            while (ret_set.moveToNext()) {
                infolist.add(BuildRecord(ret_set));
                row++;
                if (row % 10 == 0) {
                    process.SetValue(100 * row / data_count);
                }
            }
            process.Finish(infolist.toArray(new AlarmRecord[0]));
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "搜索失败", ex);
            process.Finish(new AlarmRecord[0]);
        } finally {
            db_instance.dbLock.unlock();
        }
    }

    private AlarmRecord BuildRecord(Cursor ret_set){
        if (ret_set != null) {
            AlarmRecord ret = new AlarmRecord();
            ret.alarm =  ret_set.getInt(1);
            ret.alarm_info = ret_set.getString(2);
            ret.time = new Date();
            ret.time.setTime(ret_set.getLong(3));
//            ret.alarm = ret_set.getInt(ret_set.getColumnIndex(Alarm_Key));
//            ret.alarm_info = ret_set.getString(AlarmInfo_Key);
//        ainfo.dev_name = ret.getString(DevInfo_Key);
           // ret.time = ret_set.getTimestamp(Time_Key);
            return ret;
        }
        return null;
    }
    // </editor-fold>

    @Override
    public void ExportToExcel(String s, DevID devID, Date date, Date date1, IMainProcess iMainProcess) {

    }
}
