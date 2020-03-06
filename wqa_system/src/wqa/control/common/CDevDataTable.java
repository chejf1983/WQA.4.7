/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.common;

import java.util.ArrayList;
import java.util.HashMap;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class CDevDataTable {

    private static CDevDataTable instance;

    public static CDevDataTable GetInstance() {
        if (instance == null) {
            instance = new CDevDataTable();
        }
        return instance;
    }

    private CDevDataTable() {
        this.initInfoTable();
    }

    //获取支持的数据
    public DataInfo[] GetSupportData(int dev_type) {
        ArrayList<DataInfo> info = new ArrayList();
        DevInfo dev_info = this.namemap.get(dev_type);
        for (int i = 0; i < dev_info.data_list.length; i++) {
            DataInfo tinfo = dev_info.data_list[i];
            if (WQAPlatform.GetInstance().is_internal) {
                info.add(tinfo);
            } else if (!tinfo.internal_only) {
                info.add(tinfo);
            }
        }
        return info.toArray(new DataInfo[0]);
    }

    // <editor-fold defaultstate="collapsed" desc="设备静态信息">
    public class DevInfo {
        public int dev_type;
        public String dev_name;
        public String dev_name_ch;
        public DataInfo[] data_list;

        public DevInfo(
                int devtype,
                String devtype_string,
                String devtype_cstring,
                DataInfo... datas) {
            this.dev_type = devtype;
            this.dev_name = devtype_string;
            this.dev_name_ch = devtype_cstring;
            this.data_list = datas;
            for(DataInfo dinfo : this.data_list){
                dinfo.parent = this;
            }
        }
    }

    public class DataInfo {
        public DevInfo parent; 
        public String data_name;
        public String data_unit;
        public int cal_num;
        public String[] data_range;
        public boolean internal_only;

        public DataInfo(String data_name, String data_unit, int cal_num, String... data_range) {
            this(data_name, data_unit, cal_num, false, data_range);
        }

        public DataInfo(String data_name, String data_unit, int cal_num, boolean internal, String... data_range) {
            this.data_name = data_name;
            this.data_unit = data_unit;
            this.cal_num = cal_num;
            this.internal_only = internal;
            this.data_range = data_range;
        }
    }

    public HashMap<Integer, DevInfo> namemap = new HashMap();

    public static String ORA_Flag = "信号";

    public int GetDataIndex(int dev_type, String data_name) {
        for (int i = 0; i < this.namemap.get(dev_type).data_list.length; i++) {
            if (data_name.contentEquals(this.namemap.get(dev_type).data_list[i].data_name)) {
                return i;
            }
        }

        return -1;
    }

    private void initInfoTable() {
        //ESA     
        DevInfo[] d_list = new DevInfo[]{new DevInfo(0x0200, "ESA_PH", "PH",
                new DataInfo("pH", "", 2, "(0-14)"),
                new DataInfo("pH" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
            new DevInfo(0x0220, "ESA_PH", "PH",
                new DataInfo("pH", "", 2, "(0-14)"),
                new DataInfo("pH" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0201, "ESA_DO", "溶解氧",
                new DataInfo("溶解氧", "mg/L", 0x02, "(0-20)"), //0x102,高位1表示测量值内容没有，就是饱和氧和无氧
                new DataInfo("溶解氧百分比", "%", 0, ""),
                new DataInfo("溶解氧" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0202, "ESA_EC_I", "电导率",
                new DataInfo("电导率", "us/cm", 1, "(0-500000)"),
                new DataInfo("电导率" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, ""),
                new DataInfo("盐度", "ppt", 0, "(0-75)")),
        new DevInfo(0x0221, "ESA_EC_I", "电导率",
                new DataInfo("电导率", "us/cm", 1, "(0-500000)"),
                new DataInfo("电导率" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, ""),
                new DataInfo("盐度", "ppt", 0, "(0-75)")),
        new DevInfo(0x0203, "ESA_EC_II", "电导率",
                new DataInfo("电导率", "us/cm", 1, "(0-500000)"),
                new DataInfo("电导率" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, ""),
                new DataInfo("盐度", "ppt", 0, "(0-75)")),
        new DevInfo(0x0208, "ESA_ORP", "ORP",
                new DataInfo("ORP", "mV", 2, "(-2000-2000)"),
                new DataInfo("ORP" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0210, "OSA_FDO", "溶解氧FDO",
                new DataInfo("溶解氧", "mg/L", 0x02, "(0-20)"), //0x102,高位1表示测量值内容没有，就是饱和氧和无氧
                new DataInfo("溶解氧百分比", "%", 0, ""),
                new DataInfo("溶解氧" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0xA210, "OSA_FDO", "溶解氧FDO",
                new DataInfo("溶解氧", "mg/L", 0x02, "(0-20)"), //0x102,高位1表示测量值内容没有，就是饱和氧和无氧
                new DataInfo("溶解氧百分比", "%", 0, ""),
                new DataInfo("溶解氧" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, ""),
                new DataInfo("相位差", "", 0, true, "度"),
                new DataInfo("蓝光幅值", "", 0, true, ""),
                new DataInfo("参考蓝光幅值", "", 0, true, ""),
                new DataInfo("红光幅值", "", 0, true, ""),
                new DataInfo("信号偏置", "", 0, true, "")),

        //OSA
        new DevInfo(0x0100, "OSA_TURB", "浊度",
                new DataInfo("浊度", "NTU", 3, "(0-100)", "(0-500)", "(0-2000)", "(0-4000)"),
                new DataInfo("浊度" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0102, "OSA_TS", "悬浮物",
                new DataInfo("悬浮物", "mg/L", 3, "(0-20000)"),
                new DataInfo("悬浮物" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0104, "OSA_SS", "悬浮物",
                new DataInfo("悬浮物", "mg/L", 3, "(0-20000)"),
                new DataInfo("悬浮物" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0106, "OSA_CHLA", "叶绿素",
                new DataInfo("叶绿素", "ug/L", 3, "(0-500)","(0-50)"),
                new DataInfo("叶绿素" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0108, "OSA_CYANO", "蓝绿藻",
                new DataInfo("蓝绿藻", "细胞/ml", 3, "(0-2000000)", "(0-200000)"),
                new DataInfo("蓝绿藻" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x010A, "OSA_OIL", "水中油",
                new DataInfo("水中油", "ppm", 3, "(0-500)"),
                new DataInfo("水中油" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x010E, "OSA_MLSS", "悬浮物",
                new DataInfo("悬浮物", "mg/L", 3, "(0-10000)", "(0-20000)"),
                new DataInfo("悬浮物" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0110, "OSA_FDO", "溶解氧FDO",
                new DataInfo("溶解氧", "mg/L", 0x02, "(0-20)"), //0x102,高位1表示测量值内容没有，就是饱和氧和无氧
                new DataInfo("溶解氧百分比", "%", 0, ""),
                new DataInfo("溶解氧" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0xA110, "OSA_FDO", "溶解氧FDO",
                new DataInfo("溶解氧", "mg/L", 0x02, "(0-20)"), //0x102,高位1表示测量值内容没有，就是饱和氧和无氧
                new DataInfo("溶解氧百分比", "%", 0, ""),
                new DataInfo("溶解氧" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, ""),
                new DataInfo("相位差", "", 0, true, "度"),
                new DataInfo("蓝光幅值", "", 0, true, ""),
                new DataInfo("参考蓝光幅值", "", 0, true, ""),
                new DataInfo("红光幅值", "", 0, true, ""),
                new DataInfo("信号偏置", "", 0, true, "")),

        //ISA
        new DevInfo(0x0300, "ISA_AMMO_I", "氨氮I",
                new DataInfo("pH", "", 2, "(0-14)"),
                new DataInfo("pH" + ORA_Flag, "", 0, true, ""),
                new DataInfo("NH4", "mg/L", 2, "(0-1000)"),
                new DataInfo("NH4" + ORA_Flag, "", 0, true, ""),
                //                new ElementInfo("K", "mg/L", 2),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0301, "ISA_AMMO_II", "氨氮II",
                new DataInfo("pH", "", 2, "(0-14)"),
                new DataInfo("pH" + ORA_Flag, "", 0, true, ""),
                new DataInfo("NH4", "mg/L", 2, "(0-1000)"),
                new DataInfo("NH4" + ORA_Flag, "", 0, true, ""),
                new DataInfo("K", "mg/L", 2, "(0-1000)"),
                new DataInfo("K" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0308, "ISA_NITRA_I", "硝氮I",
                new DataInfo("pH", "", 2, "(0-14)"),
                new DataInfo("pH" + ORA_Flag, "", 0, true, ""),
                new DataInfo("CN3", "mg/L", 2),
                new DataInfo("CN3" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0309, "ISA_NITRA_II", "硝氮II",
                new DataInfo("pH", "", 2, "(0-14)"),
                new DataInfo("pH" + ORA_Flag, "", 0, true, ""),
                new DataInfo("CN3", "mg/L", 2, ""),
                new DataInfo("CN3" + ORA_Flag, "", 0, true, ""),
                new DataInfo("Cl", "mg/L", 2, ""),
                new DataInfo("Cl" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0310, "ISA_AMMO_NITRA_I", "",
                new DataInfo("pH", "", 2, "(0-14)"),
                new DataInfo("pH" + ORA_Flag, "", 0, true, ""),
                new DataInfo("NH4", "mg/L", 2, ""),
                new DataInfo("NH4" + ORA_Flag, "", 0, true, ""),
                new DataInfo("CN3", "mg/L", 2, ""),
                new DataInfo("CN3" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0311, "ISA_AMMO_NITRA_II", "",
                new DataInfo("pH", "", 2, "(0-14)"),
                new DataInfo("pH" + ORA_Flag, "", 0, true, ""),
                new DataInfo("NH4", "mg/L", 2, ""),
                new DataInfo("NH4" + ORA_Flag, "", 0, true, ""),
                new DataInfo("CN3", "mg/L", 2, ""),
                new DataInfo("CN3" + ORA_Flag, "", 0, true, ""),
                new DataInfo("K", "mg/L", 2, ""),
                new DataInfo("K" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0312, "ISA_AMMO_NITRA_III", "",
                new DataInfo("pH", "", 2, "(0-14)"),
                new DataInfo("pH" + ORA_Flag, "", 0, true, ""),
                new DataInfo("NH4", "mg/L", 2, ""),
                new DataInfo("NH4" + ORA_Flag, "", 0, true, ""),
                new DataInfo("CN3", "mg/L", 2, ""),
                new DataInfo("CN3" + ORA_Flag, "", 0, true, ""),
                new DataInfo("Cl", "mg/L", 2, ""),
                new DataInfo("Cl" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, "")),
        new DevInfo(0x0320, "ISA_CHLORINE", "",
                new DataInfo("Cl", "mg/L", 2, ""),
                new DataInfo("Cl" + ORA_Flag, "", 0, true, ""),
                new DataInfo("温度", "℃", 1, "(0-60)"),
                new DataInfo("温度" + ORA_Flag, "", 0, true, ""))};
        
        for(DevInfo info : d_list){
            this.namemap.put(info.dev_type, info);
        }
    }
    // </editor-fold> 
}
