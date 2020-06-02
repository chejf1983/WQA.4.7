/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.bill.db;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import wqa.control.common.DataHelper;
import wqa.control.common.SDisplayData;
import wqa.control.data.DevID;

/**
 *
 * @author chejf
 */
public class JDBDataTable {

    public static String Time_Key = "time";
    public static String DataIndexKey = "Data";
    public static String UnitIndexKey = "Unit";
//    public static int Max_Data_num = 20;

    public final H2DBSaver db; //所属表名称

    public JDBDataTable(H2DBSaver db) {
        this.db = db;
    }

    private String ConvertTableName(DevID key) {
        return JDBDataTable.class.getSimpleName().toUpperCase() + key.toString();
    }

    //返回"设备类型_设备地址"
    public DevID[] ListAllDevice() throws Exception {
        ArrayList<DevID> devices = new ArrayList();
        for (String tables : db.GetAllTables()) {
            if (tables.startsWith(JDBDataTable.class.getSimpleName().toUpperCase())) {
                String replace = tables.replace(JDBDataTable.class.getSimpleName().toUpperCase(), "");
                devices.add(new DevID(replace));
            }
        }

        return devices.toArray(new DevID[0]);
    }

    public void AddData(SDisplayData data) throws Exception {
//        获取表名称
        String table_name = ConvertTableName(data.dev_id);
        //初始化插入SQL语句
        String INSERT_TABLE_SQL = "insert into " + table_name + " values(null, ?";
        for (int i = 0; i < data.datas.length; i++) {
            INSERT_TABLE_SQL += ", ?, ?";
        }
        INSERT_TABLE_SQL += ")";

        //创建Statement
        CallableStatement prepareCall = db.conn.prepareCall(INSERT_TABLE_SQL);

        //填写时间
        prepareCall.setTimestamp(1, new java.sql.Timestamp(data.time.getTime()));

        //赋值有效数据
        for (int i = 0; i < data.datas.length; i++) {
            //遍历静态表位置
            prepareCall.setFloat(i * 2 + 2, data.datas[i].mainData);
            prepareCall.setString(i * 2 + 3, data.datas[i].range_info + data.datas[i].unit);
        }

        prepareCall.execute();
    }

    public void CreateTableIfNotExist(DevID id) throws Exception {
        String table_name = ConvertTableName(id);

        if (db.IsTableExist(table_name)) {
            return;
        }

        String CREATE_TABLE_SQL = "create table if not exists " + table_name
                + "(id int auto_increment primary key not null, " + Time_Key + " datetime(2)";
        for (int i = 0; i < DataHelper.GetAllData(id.dev_type).length; i++) {
            CREATE_TABLE_SQL += ", " + DataIndexKey + i + " varchar(50)";
            CREATE_TABLE_SQL += ", " + UnitIndexKey + i + " varchar(50)";
        }

        CREATE_TABLE_SQL += ")";
        /* Creat Device Table is not exist */
        db.conn.createStatement().executeUpdate(CREATE_TABLE_SQL);
    }

    public int GetCount(DevID id, Date startTime, Date stopTime) throws Exception {

        String table_name = ConvertTableName(id);

        //截至时间为空，默认为当前时间
        if (stopTime == null) {
            stopTime = new Date();
        }

        //初始化查找命令
        String sql = "select count(*) from "
                + table_name + " where " + Time_Key + " <= ?";             //截至时间，为空就是当前时间

        //有开始时间，增加开始时间选项
        if (startTime != null) {
            sql += " and " + Time_Key + " >= ?";            //其始时间
        }

        //装备statement
        PreparedStatement prepareCall = db.conn.prepareStatement(sql);
        //设置时间
        prepareCall.setTimestamp(1, new java.sql.Timestamp(stopTime.getTime()));
        if (startTime != null) {
            prepareCall.setTimestamp(2, new java.sql.Timestamp(startTime.getTime()));
        }

        try (ResultSet ret = prepareCall.executeQuery()) {
            if (ret.next()) {
                return ret.getInt(1);
            } else {
                return 0;
            }
        }
    }

    //搜索数据
    public ResultSet SearchRecords(DevID id, Date startTime, Date stopTime) throws Exception {

        String table_name = ConvertTableName(id);

        if (stopTime == null) {
            stopTime = new Date();
        }

        String sql = "select * from "
                + table_name + " where " + Time_Key + " <= ?"; //截至时间，为空就是当前时间

        if (startTime != null) {
            sql += " and " + Time_Key + " >= ?";            //其始时间
        }

//        sql += "order by " + Time_Key + " asc";             //按序号增加排列
        CallableStatement prepareCall = db.conn.prepareCall(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        prepareCall.setTimestamp(1, new java.sql.Timestamp(stopTime.getTime()));
        if (startTime != null) {
            prepareCall.setTimestamp(2, new java.sql.Timestamp(startTime.getTime()));
        }

        return prepareCall.executeQuery();
    }

    public void DeleteData(DevID key, Date befortime) throws Exception {
        int index = 0;
        try (ResultSet result = SearchRecords(key, null, befortime)) {
            result.last();
            index = result.getInt("id");
        }

        DeleteData(key, index);
    }

    public void DeleteData(DevID key, int index) throws Exception {
        String table_name = ConvertTableName(key);

        String sql = "delete from " + table_name
                + " where ";

        sql += "id <= ?";

        CallableStatement prepareCall = db.conn.prepareCall(sql);

        prepareCall.setInt(1, index);

        prepareCall.executeUpdate();
    }

    public boolean IsTableEmpty(DevID key) throws SQLException {
        String table_name = ConvertTableName(key);
        return this.db.IsTableEmpty(table_name);
    }

    public void DropTable(DevID key) throws SQLException {
        String table_name = ConvertTableName(key);
        this.db.DropTable(table_name);
    }
}
