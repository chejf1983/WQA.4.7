/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.bill.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import wqa.adapter.factory.CDevDataTable;
import wqa.control.DB.DataReadHelper;

/**
 *
 * @author chejf
 */
public class DataRecord {

        //有效数据
        public Float[] values;
        //量程+单位
        public String[] value_strings;
        //时间
        public Date time;
        //DB数据信息
        public DataReadHelper.DevTableInfo dev_info;

        private String[] names;

        public DataRecord(DataReadHelper.DevTableInfo dev_info) {
            //获取DB显示数据
            this.dev_info = dev_info;
            //赋值数据值
            this.values = new Float[dev_info.data_element.length];
            //赋值量程单位
            this.value_strings = new String[dev_info.data_element.length];

            names = new String[dev_info.data_element.length * 2 + 1];
            names[0] = "时间";
            for (int i = 0; i < dev_info.data_element.length; i++) {
                names[i * 2 + 1] = dev_info.data_element[i].data_name;
                names[i * 2 + 2] = "(量程)单位";
            }
        }

        public DataRecord(DataReadHelper.DevTableInfo dev_info, ResultSet set) throws SQLException {
            this(dev_info);
            this.InitData(set);
        }

        public void InitData(ResultSet set) throws SQLException {
            //读取时间
            this.time = set.getTimestamp(JDBDataTable.Time_Key);

            //获取静态数据表
            for (int i = 0; i < values.length; i++) {
                int index = CDevDataTable.GetInstance().GetDataIndex(dev_info.dev_id.dev_type, dev_info.data_element[i].data_name);
                //根据显示数据内容查找静态数据表的序号，对应到数据库中的位置
                values[i] = set.getFloat(JDBDataTable.DataIndexKey + index);
                value_strings[i] = set.getString(JDBDataTable.UnitIndexKey + index);
            }
        }

        public String[] GetNames() {
            return names;
        }
    }
