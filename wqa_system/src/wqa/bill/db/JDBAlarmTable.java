/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.bill.db;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import wqa.control.common.SDisplayData;
import wqa.control.data.DevID;

/**
 *
 * @author chejf
 */
public class JDBAlarmTable {

//    public static String TableMark = "A_";
    public static String AlarmTable = JDBAlarmTable.class.getSimpleName().toUpperCase() + "AlarmTable";

    public static String DevInfo_Key = "dev_info";
    public static String Time_Key = "time";
    public static String Alarm_Key = "alarm";
    public static String AlarmInfo_Key = "alarm_info";

    public final H2DBSaver db; //所属表名称

    public JDBAlarmTable(H2DBSaver db) {
        this.db = db;
    }

    public void AddAlarm(SDisplayData data) throws Exception {
        String INSERT_TABLE_SQL = "insert into " + AlarmTable + " values(null, ?, ?, ?, ?)";

        CallableStatement prepareCall = db.conn.prepareCall(INSERT_TABLE_SQL);
        prepareCall.setString(1, data.dev_id.toString());
        prepareCall.setInt(2, data.alarm);
        prepareCall.setString(3, data.alram_info);
        prepareCall.setTimestamp(4, new java.sql.Timestamp(data.time.getTime()));
        prepareCall.execute();
    }

    public void CreateTable() throws Exception {
        String CREATE_TABLE_SQL = "create table if not exists " + AlarmTable
                + "(id int auto_increment primary key not null, "
                + DevInfo_Key + " varchar(50),"
                + Alarm_Key + " Int,"
                + AlarmInfo_Key + " varchar(50),"
                + Time_Key + " datetime(2))";
        /* Creat Device Table is not exist */
        db.conn.createStatement().executeUpdate(CREATE_TABLE_SQL);
    }

    //返回"设备类型_设备地址"
    public DevID[] ListAllDevice() throws Exception {
        String sql = "select distinct(" + DevInfo_Key + ")" + DevInfo_Key + " from " + AlarmTable;
        CallableStatement pcall = db.conn.prepareCall(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ArrayList<DevID> tmp = new ArrayList();
        try (ResultSet ret = pcall.executeQuery()) {
            while (ret.next()) {
                String devinfo = ret.getString(DevInfo_Key);
                tmp.add(new DevID(devinfo));
            }
        }
        return tmp.toArray(new DevID[0]);
    }

    public ResultSet SearchAlarm(DevID dev_key, Date startTime, Date stopTime) throws Exception {
        if (stopTime == null) {
            stopTime = new Date();
        }

        String sql = "select * from "
                + AlarmTable + " where " + DevInfo_Key + " = ?" //数据类型
                + " and " + Time_Key + " <= ?";             //截至时间，为空就是当前时间

        if (startTime != null) {
            sql += " and " + Time_Key + " >= ?";            //其始时间
        }

        sql += "order by id asc";                           //按序号增加排列

        CallableStatement prepareCall = db.conn.prepareCall(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        prepareCall.setString(1, dev_key.toString());
        prepareCall.setTimestamp(2, new java.sql.Timestamp(stopTime.getTime()));
        if (startTime != null) {
            prepareCall.setTimestamp(3, new java.sql.Timestamp(startTime.getTime()));
        }

        return prepareCall.executeQuery();
    }

    public void DeleteData(Date befortime) throws Exception {
        String sql = "delete from " + AlarmTable
                + " where ";

        sql += Time_Key + " <= ?";

        CallableStatement prepareCall = db.conn.prepareCall(sql);

        prepareCall.setTimestamp(1, new java.sql.Timestamp(befortime.getTime()));

        prepareCall.executeUpdate();
    }

    public void DeleteData(DevID dev_key) throws Exception {
        String sql = "delete from " + AlarmTable
                + " where ";

        sql += DevInfo_Key + " = ?";

        CallableStatement prepareCall = db.conn.prepareCall(sql);

        prepareCall.setString(1, dev_key.toString());

        prepareCall.executeUpdate();
    }
}
