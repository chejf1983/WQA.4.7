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
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
//搜索结果
public class SDataRecordResult {

    public long search_num;
    public ArrayList<DataRecord> data;

    public SDataRecordResult() {
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

            this.names = SDataRecordResult.GetSupportData(dev_info);
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
    
    public static String[] GetSupportData(DevID dev_id) {
        CDevDataTable.DevInfo d_infos = CDevDataTable.GetInstance().namemap.get(dev_id.dev_type);
        ArrayList<String> list = new ArrayList();
        if (d_infos != null) {
            for (CDevDataTable.DataInfo info : d_infos.data_list) {
                if (info.internal_only) {
                    if (WQAPlatform.GetInstance().is_internal) {
                        list.add(info.data_name);
                    }
                } else {
                    list.add(info.data_name);
                }
            }
        }
        return list.toArray(new String[0]);
    }

    public static String[] GetAllData(DevID dev_id) {
        CDevDataTable.DevInfo d_infos = CDevDataTable.GetInstance().namemap.get(dev_id.dev_type);
        ArrayList<String> list = new ArrayList();
        if (d_infos != null) {
            for (CDevDataTable.DataInfo info : d_infos.data_list) {
                list.add(info.data_name);
            }
        }
        return list.toArray(new String[0]);
    }
    
    public static int GetDataToDBIndex(DevID dev_id, String data_name){
        String[] all_datas = GetAllData(dev_id);
        for(int i = 0; i < all_datas.length; i++){
            if(all_datas[i].contentEquals(data_name)){
                return i;
            }
        }
        return -1;
    }
}
