package com.naqing.adb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import nahon.comm.faultsystem.LogCenter;
import wqa.control.DB.DataRecord;
import wqa.control.DB.IDataHelper;
import wqa.control.DB.SDataRecordResult;
import wqa.control.common.SDisplayData;
import wqa.control.data.DevID;
import wqa.control.data.IMainProcess;

public class ADataDB implements IDataHelper {
    private final SQLiteHelper db_instance;
    public static String Time_Key = "time";
    public static String DataIndexKey = "Data";
    public static String UnitIndexKey = "Unit";

    public ADataDB(SQLiteHelper helper) {
        this.db_instance = helper;
    }

    // <editor-fold defaultstate="collapsed" desc="表信息">
    private String ConvertTableName(DevID key) {
        return ADataDB.class.getSimpleName().toUpperCase() + key.toString();
    }

    @Override
    public DevID[] ListAllDevice() {
        db_instance.dbLock.lock();
        try {
            ArrayList<DevID> devices = new ArrayList();
            for (String tables : db_instance.GetAllTables()) {
                if (tables.startsWith(ADataDB.class.getSimpleName().toUpperCase())) {
                    String replace = tables.replace(ADataDB.class.getSimpleName().toUpperCase(), "");
                    devices.add(new DevID(replace));
                }
            }

            return devices.toArray(new DevID[0]);
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "获取设备列表失败", ex);
            return new DevID[0];
        } finally {
            db_instance.dbLock.unlock();
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="搜索">
    //搜索数据
    private DataRecord BuildRecord(DevID id, Cursor set) throws Exception {
        DataRecord record = new DataRecord(id);
        //读取时间
        record.time.setTime(set.getLong(0));

        //获取静态数据表
        for (int i = 0; i < record.values.length; i++) {
            int index = DataRecord.GetDataToDBIndex(id, record.names[i]);
            //根据显示数据内容查找静态数据表的序号，对应到数据库中的位置（0号是id, 1号是time）
            record.values[i] = set.getFloat(index * 2 + 1);
            record.value_strings[i] = set.getString(index * 2 + 2);
        }

        return record;
    }

    @Override
    public void SearchLimitData(DevID devID, Date startTime, Date stopTime, int limit_num, IMainProcess<SDataRecordResult> process) {
        String table_name = ConvertTableName(devID);

        if (stopTime == null) {
            stopTime = new Date();
        }

        String SEACH_DATA = "select * from "
                + table_name + " where " + Time_Key + " <= ?"; //截至时间，为空就是当前时间

        if (startTime != null) {
            SEACH_DATA += " and " + Time_Key + " >= ?";            //其始时间
        }

        db_instance.dbLock.lock();
        try (SQLiteDatabase db = db_instance.getReadableDatabase()) {
            SDataRecordResult ret = new SDataRecordResult();
            String[] pars;
            if (startTime != null) {
                pars = new String[]{stopTime.getTime() + "", startTime.getTime() + ""};
            } else {
                pars = new String[]{stopTime.getTime() + ""};
            }
            Cursor ret_set = db.rawQuery(SEACH_DATA, pars);
            //检查是否为空集
            if (!ret_set.moveToFirst()) {
                process.Finish(ret);
            }

            //统计记录个数
            ret_set.moveToLast();
            long data_count = ret_set.getCount();
            ret.search_num = data_count;
            ret_set.moveToFirst();

            //计算跳跃次数
            double data_to_jump = ((double) data_count / limit_num);
            if (data_to_jump < 1) {
                data_to_jump = 1;//int count = 0;
            }

            int row = 1;
            //跳跃搜索数据
            while (ret_set.moveToPosition(row)) {
                //保存结果
                ret.data.add(BuildRecord(devID, ret_set));
                row += data_to_jump;
                //count = 0;
                process.SetValue(100 * row / data_count);
            }

            //通知完成
            process.Finish(ret);
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "搜索失败", ex);
            process.Finish(new SDataRecordResult());
        } finally {
            db_instance.dbLock.unlock();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="保存数据">
    public void CreateTableIfNotExist(DevID id) throws Exception {
        String table_name = ConvertTableName(id);

        String CREATE_TABLE_SQL = "create table if not exists " + table_name
                + "(" + Time_Key + " long primary key";
        for (int i = 0; i < DataRecord.GetAllData(id).length; i++) {
            CREATE_TABLE_SQL += ", " + DataIndexKey + i + " float";
            CREATE_TABLE_SQL += ", " + UnitIndexKey + i + " varchar(50)";
        }

        CREATE_TABLE_SQL += ")";
        db_instance.dbLock.lock();
        try (SQLiteDatabase db = db_instance.getWritableDatabase()) {
            db.execSQL(CREATE_TABLE_SQL);
        } finally {
            db_instance.dbLock.unlock();
        }
    }

    public void AddData(SDisplayData data) throws Exception {
//        获取表名称
        String table_name = ConvertTableName(data.dev_id);
        //初始化插入SQL语句
        String INSERT_TABLE_SQL = "insert into " + table_name + " values(?";
        for (int i = 0; i < data.datas.length; i++) {
            INSERT_TABLE_SQL += ", ?, ?";
        }
        INSERT_TABLE_SQL += ")";

        if(data.datas.length != DataRecord.GetAllData(data.dev_id).length){
            throw new Exception("数据长度不完整");
        }

        db_instance.dbLock.lock();
        try (SQLiteDatabase db = db_instance.getWritableDatabase()) {
            Object[] tmp = new Object[data.datas.length * 2 + 1];

            //设置时间
            tmp[0] = data.time.getTime();
            //赋值有效数据
            for (int i = 0; i < data.datas.length; i++) {
                int index = DataRecord.GetDataToDBIndex(data.dev_id, data.datas[i].name);
                tmp[index * 2 + 1] = data.datas[i].mainData;
                tmp[index * 2 + 2] = data.datas[i].range_info + data.datas[i].unit;
            }
            db.execSQL(INSERT_TABLE_SQL, tmp);
        } finally {
            db_instance.dbLock.unlock();
        }
    }

    @Override
    public void SaveData(SDisplayData collectData) throws Exception {
        CreateTableIfNotExist(collectData.dev_id);

        AddData(collectData);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="删除数据">
    @Override
    public void DeleteTable(DevID devID) throws Exception {
        db_instance.dbLock.lock();
        try {
            db_instance.DropTable(ConvertTableName(devID));
        } finally {
            db_instance.dbLock.unlock();
        }
    }

    @Override
    public void DeleteTable(DevID key, Date befortime) throws Exception {
        String DEL_DATA = "delete from " +  ConvertTableName(key)
                + " where " + Time_Key + " <= ?";

        db_instance.dbLock.lock();
        try(SQLiteDatabase db = db_instance.getWritableDatabase()){
            db.execSQL(DEL_DATA, new String[]{befortime.getTime() + ""});
        }finally {
            db_instance.dbLock.unlock();
        }
    }
    // </editor-fold>

    @Override
    public void ExportToFile(String s, DevID devID, Date date, Date date1, IMainProcess iMainProcess) {

    }
}
