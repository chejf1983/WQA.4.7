/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB;

import java.util.Date;
import wqa.control.data.DevID;

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

//    public DataRecord(DevID dev_info) {
//        //获取DB显示数据
//        this.dev_info = dev_info;
//
//        this.names = DataHelper.GetSupportDataName(dev_info.dev_type);
//        //赋值数据值
//        this.values = new Float[names.length];
//        //赋值量程单位
//        this.value_strings = new String[names.length];
//
//        this.time = new Date();
//    }
//
    public int GetIndex(String name) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].contentEquals(name)) {
                return i;
            }
        }
        return -1;
    }
//
//    public Object[] GetValue() throws Exception {
//        Object[] ret = new Object[names.length * 2 + 1];
//        ret[0] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
//        for (int i = 0; i < names.length; i++) {
//            ret[i * 2 + 1] = values[i];
//            String info = value_strings[i];
//            ret[i * 2 + 2] = info.contentEquals("") ? "--" : info;
//        }
//        return ret;
//    }
//    
//    public static String[] GetNames(DevID dev_info) {
//        String[] tnames = DataHelper.GetSupportDataName(dev_info.dev_type);
//        String[] names = new String[tnames.length * 2 + 1];
//        names[0] = "时间";
//        for (int i = 0; i < tnames.length; i++) {
//            names[i * 2 + 1] = tnames[i];
//            names[i * 2 + 2] = "(量程)单位";
//        }
//        return names;
//    }
}
