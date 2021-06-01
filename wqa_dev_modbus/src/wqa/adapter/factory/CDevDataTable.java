/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.factory;

import java.util.ArrayList;
import java.util.HashMap;

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

    //获取采集数据，所以不是内部版本显示的数据
    public DataInfo[] GetStanderDatas(int dev_type, boolean need_internal, boolean need_ora) {
        DataInfo[] data_list = CDevDataTable.GetInstance().namemap.get(dev_type).data_list;
        ArrayList<DataInfo> data_names = new ArrayList();
        for (int i = 0; i < data_list.length; i++) {
            if (!need_internal) {
                if (!data_list[i].internal_only) {
                    data_names.add(data_list[i]);
                }
            } else if (!need_ora) {
                if (!data_list[i].data_name.endsWith(ORA_Flag)) {
                    data_names.add(data_list[i]);
                }
            } else {
                data_names.add(data_list[i]);
            }
        }
        return data_names.toArray(new DataInfo[0]);
    }

    public int GetDataIndex(int dev_type, String data_name) {
        for (int i = 0; i < this.namemap.get(dev_type).data_list.length; i++) {
            if (data_name.contentEquals(this.namemap.get(dev_type).data_list[i].data_name)) {
                return i;
            }
        }

        return -1;
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
            for (DataInfo dinfo : this.data_list) {
                dinfo.parent = this;
            }
        }
    }

    public class DataInfo {

        public DevInfo parent;  //设备信息
        public int team;        //组队编号
        public String data_name; //参数名称
        public String data_unit; //参数单位
        public int cal_num;      //定标个数
        public String[] data_range; //量程范围
        public boolean internal_only; //是否只有内部版本显示

        public DataInfo(String data_name, int team, String data_unit, int cal_num, String... data_range) {
            this(data_name, team, data_unit, cal_num, false, data_range);
        }

        public DataInfo(String data_name, int team, String data_unit, int cal_num, boolean internal, String... data_range) {
            this.data_name = data_name;
            this.team = team;
            this.data_unit = data_unit;
            this.cal_num = cal_num;
            this.internal_only = internal;
            this.data_range = data_range;
        }
    }

    public HashMap<Integer, DevInfo> namemap = new HashMap();

    public static String ORA_Flag = "信号";

    private void initInfoTable() {
        DevInfo[] d_list = new DevInfo[]{
            //ESA0x02系列
            new DevInfo(0x0200, "ESA_PH", "PH", new DataInfo("pH", 1, "", 2, "(0-14)"), new DataInfo("pH" + ORA_Flag, 1, "mV", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0220, "ESA_PH", "PH", new DataInfo("pH", 1, "", 2, "(0-14)"), new DataInfo("pH" + ORA_Flag, 1, "mV", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0201, "ESA_DO", "溶解氧", new DataInfo("溶解氧", 1, "mg/L", 0x02, "(0-20)"), new DataInfo("溶解氧百分比", 1, "%", 0, ""), new DataInfo("溶解氧" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0202, "ESA_EC_I", "电导率", new DataInfo("电导率", 1, "uS/cm", 1, "(0-500000)"), new DataInfo("电导率" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, ""), new DataInfo("盐度", 1, "ppt", 0, "(0-75)")),
            new DevInfo(0x0221, "ESA_EC_I", "电导率", new DataInfo("电导率", 1, "uS/cm", 1, "(0-500000)"), new DataInfo("电导率" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, ""), new DataInfo("盐度", 1, "ppt", 0, "(0-75)")),
            new DevInfo(0x0203, "ESA_EC_II", "电导率", new DataInfo("电导率", 1, "uS/cm", 1, "(0-500000)"), new DataInfo("电导率" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, ""), new DataInfo("盐度", 1, "ppt", 0, "(0-75)")),
            new DevInfo(0x0204, "ESA_CHLORI", "余氯", new DataInfo("余氯", 2, "ppm", 2, "(0-5)"), new DataInfo("余氯" + ORA_Flag, 2, "mV", 0, true, ""), new DataInfo("pH", 1, "", 2, "(0-14)"), new DataInfo("pH" + ORA_Flag, 1, "mV", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0205, "ESA_CHLORI_II", "余氯", new DataInfo("余氯", 3, "ppm", 2, "(0-20)"), new DataInfo("余氯" + ORA_Flag, 3, "uA", 0, true, ""), new DataInfo("pH", 2, "", 2, "(0-14)"), new DataInfo("pH" + ORA_Flag, 2, "mV", 0, true, ""), new DataInfo("ORP", 1, "", 2, "(-2000-2000)"), new DataInfo("ORP" + ORA_Flag, 1, "mV", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0208, "ESA_ORP", "ORP", new DataInfo("ORP", 1, "mV", 2, "(-2000-2000)"), new DataInfo("ORP" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0209, "ESA_Ammo", "氨氮", new DataInfo("NH3-N", 1, "mg/L", 2, "(0-1000)"), new DataInfo("NH3-N" + ORA_Flag, 1, "mv", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0210, "ESA_FDO", "溶解氧FDO", new DataInfo("溶解氧", 1, "mg/L", 0x02, "(0-20)"), new DataInfo("溶解氧百分比", 1, "%", 0, ""), new DataInfo("溶解氧" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0xA210, "OSA_FDOII", "溶解氧FDO", new DataInfo("溶解氧", 2, "mg/L", 0x02, "(0-20)"), new DataInfo("溶解氧百分比", 2, "%", 0, ""), new DataInfo("溶解氧" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, ""), new DataInfo("相位差", 1, "度", 0, true, ""), new DataInfo("蓝光峰值", 1, "", 0, true, ""), new DataInfo("激发信号", 1, "", 0, true, ""), new DataInfo("红光峰值", 1, "", 0, true, ""), new DataInfo("信号偏置", 1, "", 0, true, ""), new DataInfo("蓝光幅值", 1, "", 0, true, ""), new DataInfo("红光幅值", 1, "", 0, true, "")),
            //MPA_ESA0x12系列
            new DevInfo(0x1200, "MESA_PH", "PH", new DataInfo("pH", 1, "", 2, "(0-14)"), new DataInfo("pH" + ORA_Flag, 1, "mV", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x1201, "MESA_ORP", "ORP", new DataInfo("ORP", 1, "mV", 2, "(-2000-2000)"), new DataInfo("ORP" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x1202, "MESA_EC", "电导率", new DataInfo("电导率", 1, "uS/cm", 1, "(0-500000)"), new DataInfo("电导率" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, ""), new DataInfo("盐度", 1, "ppt", 0, "(0-75)")),
            new DevInfo(0x1203, "MESA_DO", "溶解氧", new DataInfo("溶解氧", 1, "mg/L", 0x02, "(0-20)"), new DataInfo("溶解氧百分比", 1, "%", 0, ""), new DataInfo("溶解氧" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            //OSA0x01系列
            new DevInfo(0x0100, "OSA_TURB", "浊度", new DataInfo("浊度", 1, "NTU", 3, "(0-100)", "(0-500)", "(0-2000)", "(0-4000)"), new DataInfo("浊度" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0101, "OSA_TURB", "浊度", new DataInfo("浊度", 1, "NTU", 3, "(0-100)", "(0-500)", "(0-2000)", "(0-4000)"), new DataInfo("浊度" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0102, "OSA_TS", "悬浮物", new DataInfo("悬浮物", 1, "mg/L", 3, "(0-20000)"), new DataInfo("悬浮物" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0104, "OSA_SS", "悬浮物", new DataInfo("悬浮物", 1, "mg/L", 3, "(0-20000)"), new DataInfo("悬浮物" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0106, "OSA_CHLA", "叶绿素", new DataInfo("叶绿素", 1, "ug/L", 3, "(0-500)", "(0-50)"), new DataInfo("叶绿素" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0108, "OSA_CYANO", "蓝绿藻", new DataInfo("蓝绿藻", 1, "细胞/ml", 3, "(0-2000000)", "(0-200000)"), new DataInfo("蓝绿藻" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x010A, "OSA_OIL", "水中油", new DataInfo("水中油", 1, "ppm", 3, "(0-500)"), new DataInfo("水中油" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x010E, "OSA_MLSS", "悬浮物", new DataInfo("悬浮物", 1, "mg/L", 3, "(0-10000)", "(0-20000)"), new DataInfo("悬浮物" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0110, "OSA_FDOI", "溶解氧FDO", new DataInfo("溶解氧", 1, "mg/L", 0x02, "(0-20)"), new DataInfo("溶解氧百分比", 1, "%", 0, ""), new DataInfo("溶解氧" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0xA110, "OSA_FDOII", "溶解氧FDO", new DataInfo("溶解氧", 2, "mg/L", 0x02, "(0-20)"), new DataInfo("溶解氧百分比", 2, "%", 0, ""), new DataInfo("溶解氧" + ORA_Flag, 2, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, ""), new DataInfo("相位差", 1, "度", 0, true, ""), new DataInfo("蓝光峰值", 1, "", 0, true, ""), new DataInfo("激发信号", 1, "", 0, true, ""), new DataInfo("红光峰值", 1, "", 0, true, ""), new DataInfo("信号偏置", 1, "", 0, true, ""), new DataInfo("蓝光幅值", 1, "", 0, true, ""), new DataInfo("红光幅值", 1, "", 0, true, "")),
            //MPA_OSA0x11系列
            new DevInfo(0x1100, "MOSA_TURB", "浊度", new DataInfo("浊度", 1, "NTU", 3, "(0-100)", "(0-500)", "(0-2000)", "(0-4000)"), new DataInfo("浊度" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x1101, "MOSA_FDO", "溶解氧FDO", new DataInfo("溶解氧", 2, "mg/L", 0x02, "(0-20)"), new DataInfo("溶解氧百分比", 2, "%", 0, ""), new DataInfo("溶解氧" + ORA_Flag, 2, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, ""), new DataInfo("相位差", 1, "度", 0, true, ""), new DataInfo("蓝光峰值", 1, "", 0, true, ""), new DataInfo("激发信号", 1, "", 0, true, ""), new DataInfo("红光峰值", 1, "", 0, true, ""), new DataInfo("信号偏置", 1, "", 0, true, ""), new DataInfo("蓝光幅值", 1, "", 0, true, ""), new DataInfo("红光幅值", 1, "", 0, true, "")),
            new DevInfo(0x1102, "MOSA_CHLA", "叶绿素", new DataInfo("叶绿素", 1, "ug/L", 3, "(0-500)", "(0-50)"), new DataInfo("叶绿素" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x1103, "MOSA_CYANO_I", "蓝绿藻", new DataInfo("蓝绿藻", 1, "细胞/ml", 3, "(0-2000000)", "(0-200000)"), new DataInfo("蓝绿藻" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x1104, "MOSA_CYANO_II", "蓝绿藻", new DataInfo("蓝绿藻", 1, "细胞/ml", 3, "(0-2000000)", "(0-200000)"), new DataInfo("蓝绿藻" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x1105, "MOSA_OIL", "水中油", new DataInfo("水中油", 1, "ppm", 3, "(0-500)"), new DataInfo("水中油" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x1110, "MOSA_CHLA Pro", "微型叶绿素A传感器", new DataInfo("叶绿素", 2, "ug/L", 3, "(0-500)", "(0-50)"), new DataInfo("叶绿素" + ORA_Flag, 2, "", 0, true, ""), new DataInfo("浊度", 1, "NTU", 3, "(0-100)", "(0-500)", "(0-2000)", "(0-4000)"), new DataInfo("浊度" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x1111, "MOSA_CYANO_I Pro", "微型藻密度传感器", new DataInfo("蓝绿藻(淡)", 2, "细胞/ml", 3, "(0-2000000)", "(0-200000)"), new DataInfo("蓝绿藻(淡)" + ORA_Flag, 2, "", 0, true, ""), new DataInfo("浊度", 1, "NTU", 3, "(0-100)", "(0-500)", "(0-2000)", "(0-4000)"), new DataInfo("浊度" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x1112, "MOSA_CYANO_II Pro", "微型藻密度传感器", new DataInfo("蓝绿藻(海)", 2, "细胞/ml", 3, "(0-2000000)", "(0-200000)"), new DataInfo("蓝绿藻(海)" + ORA_Flag, 2, "", 0, true, ""), new DataInfo("浊度", 1, "NTU", 3, "(0-100)", "(0-500)", "(0-2000)", "(0-4000)"), new DataInfo("浊度" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x1113, "MOSA_TA_I", "微型总藻传感器", new DataInfo("叶绿素", 2, "ug/L", 3, "(0-500)", "(0-50)"), new DataInfo("叶绿素" + ORA_Flag, 2, "", 0, true, ""), new DataInfo("蓝绿藻(淡)", 1, "细胞/ml", 3, "(0-2000000)", "(0-200000)"), new DataInfo("蓝绿藻(淡)" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x1114, "MOSA_TA_II", "微型总藻传感器", new DataInfo("叶绿素", 2, "ug/L", 3, "(0-500)", "(0-50)"), new DataInfo("叶绿素" + ORA_Flag, 2, "", 0, true, ""), new DataInfo("蓝绿藻(海)", 1, "细胞/ml", 3, "(0-2000000)", "(0-200000)"), new DataInfo("蓝绿藻(海)" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            //ISA0x03系列
            new DevInfo(0x0300, "ISA_AMMO_I", "氨氮I", new DataInfo("pH", 2, "", 2, "(0-14)"), new DataInfo("pH" + ORA_Flag, 2, "mV", 0, true, ""), new DataInfo("NH4", 1, "mg/L", 2, "(0-1000)"), new DataInfo("NH4" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0301, "ISA_AMMO_II", "氨氮II", new DataInfo("pH", 3, "", 2, "(0-14)"), new DataInfo("pH" + ORA_Flag, 3, "mV", 0, true, ""), new DataInfo("NH4", 2, "mg/L", 2, "(0-1000)"), new DataInfo("NH4" + ORA_Flag, 2, "", 0, true, ""), new DataInfo("K", 1, "mg/L", 2, "(0-1000)"), new DataInfo("K" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0308, "ISA_NITRA_I", "硝氮I", new DataInfo("pH", 2, "", 2, "(0-14)"), new DataInfo("pH" + ORA_Flag, 2, "mV", 0, true, ""), new DataInfo("CN3", 1, "mg/L", 2), new DataInfo("CN3" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0309, "ISA_NITRA_II", "硝氮II", new DataInfo("pH", 3, "", 2, "(0-14)"), new DataInfo("pH" + ORA_Flag, 3, "mV", 0, true, ""), new DataInfo("CN3", 2, "mg/L", 2, ""), new DataInfo("CN3" + ORA_Flag, 2, "", 0, true, ""), new DataInfo("Cl", 1, "mg/L", 2, ""), new DataInfo("Cl" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0310, "ISA_AMMO_NITRA_I", "", new DataInfo("pH", 3, "", 2, "(0-14)"), new DataInfo("pH" + ORA_Flag, 3, "mV", 0, true, ""), new DataInfo("NH4", 2, "mg/L", 2, ""), new DataInfo("NH4" + ORA_Flag, 2, "", 0, true, ""), new DataInfo("CN3", 1, "mg/L", 2, ""), new DataInfo("CN3" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0311, "ISA_AMMO_NITRA_II", "", new DataInfo("pH", 4, "", 2, "(0-14)"), new DataInfo("pH" + ORA_Flag, 4, "mV", 0, true, ""), new DataInfo("NH4", 3, "mg/L", 2, ""), new DataInfo("NH4" + ORA_Flag, 3, "", 0, true, ""), new DataInfo("CN3", 2, "mg/L", 2, ""), new DataInfo("CN3" + ORA_Flag, 2, "", 0, true, ""), new DataInfo("K", 1, "mg/L", 2, ""), new DataInfo("K" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0312, "ISA_AMMO_NITRA_III", "", new DataInfo("pH", 4, "", 2, "(0-14)"), new DataInfo("pH" + ORA_Flag, 4, "mV", 0, true, ""), new DataInfo("NH4", 3, "mg/L", 2, ""), new DataInfo("NH4" + ORA_Flag, 3, "", 0, true, ""), new DataInfo("CN3", 2, "mg/L", 2, ""), new DataInfo("CN3" + ORA_Flag, 2, "", 0, true, ""), new DataInfo("Cl", 1, "mg/L", 2, ""), new DataInfo("Cl" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x0320, "ISA_CHLORINE", "", new DataInfo("pH", 2, "", 2, "(0-14)"), new DataInfo("pH" + ORA_Flag, 2, "mV", 0, true, ""), new DataInfo("Cl", 1, "mg/L", 2, ""), new DataInfo("Cl" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            //ECA经济型系列  
            new DevInfo(0x2100, "ECA_PH", "PH", new DataInfo("pH", 1, "", 2, "(0-14)"), new DataInfo("pH" + ORA_Flag, 1, "mV", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x2101, "ECA_ORP", "ORP", new DataInfo("ORP", 1, "mV", 2, "(-2000-2000)"), new DataInfo("ORP" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x2102, "ECA_EC", "电导率", new DataInfo("电导率", 1, "uS/cm", 1, "(0-500000)"), new DataInfo("电导率" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, ""), new DataInfo("盐度", 1, "ppt", 0, "(0-75)")),
            new DevInfo(0x2103, "ECA_DO", "溶解氧", new DataInfo("溶解氧", 2, "mg/L", 0x02, "(0-20)"), new DataInfo("溶解氧百分比", 2, "%", 0, ""), new DataInfo("溶解氧" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, ""), new DataInfo("相位差", 1, "度", 0, true, ""), new DataInfo("蓝光峰值", 1, "", 0, true, ""), new DataInfo("激发信号", 1, "", 0, true, ""), new DataInfo("红光峰值", 1, "", 0, true, ""), new DataInfo("信号偏置", 1, "", 0, true, ""), new DataInfo("蓝光幅值", 1, "", 0, true, ""), new DataInfo("红光幅值", 1, "", 0, true, "")),
            new DevInfo(0x2104, "ECA_TURB", "浊度", new DataInfo("浊度", 1, "NTU", 3, "(0-100)", "(0-500)", "(0-2000)", "(0-4000)"), new DataInfo("浊度" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),
            new DevInfo(0x2105, "ECA_SS", "悬浮物", new DataInfo("悬浮物", 1, "mg/L", 3, "(0-20000)"), new DataInfo("悬浮物" + ORA_Flag, 1, "", 0, true, ""), new DataInfo("温度", 0, "℃", 1, "(0-60)"), new DataInfo("温度" + ORA_Flag, 0, "", 0, true, "")),};
        //MISA0x13系列

        for (DevInfo info : d_list) {
            this.namemap.put(info.dev_type, info);
        }
    }

    public static void main(String... args) {

        for (Integer key : CDevDataTable.GetInstance().namemap.keySet()) {
            DevInfo dev = CDevDataTable.instance.namemap.get(key);
            System.out.println("设备:" + dev.dev_name);

            int maxnum = 0;
            for (int i = 0; i < dev.data_list.length; i++) {
                if (dev.data_list[i].team > maxnum) {
                    maxnum = dev.data_list[i].team;
                }
            }
            ArrayList<DataInfo>[] infos = new ArrayList[maxnum + 1];
            for (int i = 0; i < dev.data_list.length; i++) {
                if (infos[dev.data_list[i].team] == null) {
                    infos[dev.data_list[i].team] = new ArrayList();
                }
                infos[dev.data_list[i].team].add(dev.data_list[i]);
            }

            for (int i = 0; i < infos.length; i++) {
                System.out.print("组" + i + ": ");
                for (int j = 0; j < infos[i].size(); j++) {
                    System.out.print(infos[i].get(j).data_name + " ");
                }
                System.out.println();
            }
        }
    }
    // </editor-fold> 
}
