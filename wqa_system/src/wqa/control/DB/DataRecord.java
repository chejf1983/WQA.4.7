/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import wqa.adapter.factory.CDevDataTable;
import wqa.control.data.DevID;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
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

        this.names = GetSupportData(dev_info);
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

    public Object[] GetValue() throws Exception {
        Object[] ret = new Object[names.length * 2 + 1];
        ret[0] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
        for (int i = 0; i < names.length; i++) {
            ret[i * 2 + 1] = values[i];
            String info = value_strings[i];
            ret[i * 2 + 2] = info.contentEquals("") ? "--" : info;
        }
        return ret;
    }

    public String[] GetNames() {
        String[] tnames = new String[names.length * 2 + 1];
        tnames[0] = "时间";
        for (int i = 0; i < names.length; i++) {
            tnames[i * 2 + 1] = names[i];
            tnames[i * 2 + 2] = "(量程)单位";
        }
        return tnames;
    }
    
    public static String[] GetNames(DevID dev_info) {
        String[] tnames = GetSupportData(dev_info);
        tnames[0] = "时间";
        for (int i = 0; i < tnames.length; i++) {
            tnames[i * 2 + 1] = tnames[i];
            tnames[i * 2 + 2] = "(量程)单位";
        }
        return tnames;
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

    public static int GetDataToDBIndex(DevID dev_id, String data_name) {
        String[] all_datas = GetAllData(dev_id);
        for (int i = 0; i < all_datas.length; i++) {
            if (all_datas[i].contentEquals(data_name)) {
                return i;
            }
        }
        return -1;
    }

}
