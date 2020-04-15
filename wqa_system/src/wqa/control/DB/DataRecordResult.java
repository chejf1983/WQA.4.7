/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import wqa.adapter.factory.CDevDataTable;
import wqa.bill.db.JDBDataTable;
import wqa.dev.data.DevID;

/**
 *
 * @author chejf
 */
//搜索结果
public class DataRecordResult {

    public long search_num;
    public ArrayList<DataRecord> data;

    public DataRecordResult() {
        search_num = 0;
        data = new ArrayList();
    }

    public class DataRecord {

        //有效数据
        public Float[] values;
        //量程+单位
        public String[] value_strings;

        public String[] names;
        //时间
        public Date time;
        //DB数据信息
        public DevID dev_info;

        public DataRecord(DevID dev_info) {
            //获取DB显示数据
            this.dev_info = dev_info;

            this.names = JDBDataTable.GetSupportData(dev_info);
            //赋值数据值
            this.values = new Float[names.length];
            //赋值量程单位
            this.value_strings = new String[names.length];

        }

        public int GetIndex(String name) {
            for (int i = 0; i < names.length; i++) {
                if (names[i].contentEquals(name)) {
                    return i;
                }
            }
            return -1;
        }

        public void InitData(ResultSet set) throws SQLException {
            //读取时间
            this.time = set.getTimestamp(JDBDataTable.Time_Key);

            //获取静态数据表
            for (int i = 0; i < values.length; i++) {
                int index = CDevDataTable.GetInstance().GetDataIndex(dev_info.dev_type, names[i]);
                //根据显示数据内容查找静态数据表的序号，对应到数据库中的位置
                values[i] = set.getFloat(JDBDataTable.DataIndexKey + index);
                value_strings[i] = set.getString(JDBDataTable.UnitIndexKey + index);
            }
        }
    }
}
