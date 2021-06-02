/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.factory;

import java.util.HashMap;

/**
 *
 * @author chejf
 */
public class CErrorTable {

    private static CErrorTable instance;

    public static CErrorTable GetInstance() {
        if (instance == null) {
            instance = new CErrorTable();
            instance.init_error_table();
        }
        return instance;
    }

    private final HashMap<Integer, String> ErrorCode = new HashMap<>();

    public static int OSA_E = 0x010000;
    public static int ESA_E = 0x020000;
    public static int ISA_E = 0x030000;
    public static int FDO_E = 0x040000;
    public static int MPA_E = 0x140000;

    //获取出错信息
    public String GetErrorString(int error_code) {
        String error_info = "";
        int error_h = error_code & 0xFF0000;
        int error_l = ((error_code & 0x00FF) << 8) | ((error_code & 0xFF00) >> 8); //error_code 在modebus中反下高低位

        error_info += String.format("0X%X:", error_l);
        for (int key : this.ErrorCode.keySet()) {
            int table_h = key & 0xFF0000;
            int table_l = key & 0xFFFF;
            if ((error_h == table_h) && ((error_l & table_l) > 0)) {
                error_info += this.ErrorCode.get(key) + " ";
            }
        }
        return error_info;
    }

    public int TranslateErrorCode(int error_code) {
        return ((error_code & 0x00FF) << 8) | ((error_code & 0xFF00) >> 8); //error_code 在modebus中反下高低位
    }
    
    private void init_error_table(){
        this.ErrorCode.put(OSA_E, "");
        this.ErrorCode.put(OSA_E | 0x0001, "主参数超量程");
        this.ErrorCode.put(OSA_E | 0x0002, "");
        this.ErrorCode.put(OSA_E | 0x0004, "辅助参数超量程(温度)");
        this.ErrorCode.put(OSA_E | 0x0008, "光强信号超范围");
        this.ErrorCode.put(OSA_E | 0x0010, "");
        this.ErrorCode.put(OSA_E | 0x0020, "");
        this.ErrorCode.put(OSA_E | 0x0040, "清扫中");
        this.ErrorCode.put(OSA_E | 0x0080, "");
        this.ErrorCode.put(OSA_E | 0x0100, "温度传感器故障");
        this.ErrorCode.put(OSA_E | 0x0200, "电机故障");
        this.ErrorCode.put(OSA_E | 0x0400, "内部电压基准故障");
        this.ErrorCode.put(OSA_E | 0x0800, "");
        this.ErrorCode.put(OSA_E | 0x1000, "");
        this.ErrorCode.put(OSA_E | 0x2000, "");
        this.ErrorCode.put(OSA_E | 0x4000, "");
        this.ErrorCode.put(OSA_E | 0x8000, "");

        this.ErrorCode.put(ESA_E, "");
        this.ErrorCode.put(ESA_E | 0x0001, "温度超出测量范围");
        this.ErrorCode.put(ESA_E | 0x0002, "主测量参数超出测量范围");
        this.ErrorCode.put(ESA_E | 0x0004, "");
        this.ErrorCode.put(ESA_E | 0x0008, "");
        this.ErrorCode.put(ESA_E | 0x0010, "");
        this.ErrorCode.put(ESA_E | 0x0020, "");
        this.ErrorCode.put(ESA_E | 0x0040, "");
        this.ErrorCode.put(ESA_E | 0x0080, "");
        this.ErrorCode.put(ESA_E | 0x0100, "温度传感器故障");
        this.ErrorCode.put(ESA_E | 0x0200, "内部电压基准故障");
        this.ErrorCode.put(ESA_E | 0x0400, "");
        this.ErrorCode.put(ESA_E | 0x0800, "");
        this.ErrorCode.put(ESA_E | 0x1000, "");
        this.ErrorCode.put(ESA_E | 0x2000, "");
        this.ErrorCode.put(ESA_E | 0x4000, "");
        this.ErrorCode.put(ESA_E | 0x8000, "");

        this.ErrorCode.put(ISA_E, "");
        this.ErrorCode.put(ISA_E | 0x0001, "温度超出测量范围");
        this.ErrorCode.put(ISA_E | 0x0002, "pH超出测量范围");
        this.ErrorCode.put(ISA_E | 0x0004, "K离子超出测量范围");
        this.ErrorCode.put(ISA_E | 0x0008, "NH4离子超出测量范围");
        this.ErrorCode.put(ISA_E | 0x0010, "");
        this.ErrorCode.put(ISA_E | 0x0020, "");
        this.ErrorCode.put(ISA_E | 0x0040, "");
        this.ErrorCode.put(ISA_E | 0x0080, "");
        this.ErrorCode.put(ISA_E | 0x0100, "温度传感器故障");
        this.ErrorCode.put(ISA_E | 0x0200, "内部电压基准故障");
        this.ErrorCode.put(ISA_E | 0x0400, "");
        this.ErrorCode.put(ISA_E | 0x0800, "");
        this.ErrorCode.put(ISA_E | 0x1000, "");
        this.ErrorCode.put(ISA_E | 0x2000, "");
        this.ErrorCode.put(ISA_E | 0x4000, "");
        this.ErrorCode.put(ISA_E | 0x8000, "");
                
        this.ErrorCode.put(FDO_E, "");
        this.ErrorCode.put(FDO_E | 0x0001, "温度超出测量范围");
        this.ErrorCode.put(FDO_E | 0x0002, "溶解氧参数超出测量范围");
        this.ErrorCode.put(FDO_E | 0x0004, "荧光信号超出测量范围");
        this.ErrorCode.put(FDO_E | 0x0008, "");
        this.ErrorCode.put(FDO_E | 0x0010, "");
        this.ErrorCode.put(FDO_E | 0x0020, "");
        this.ErrorCode.put(FDO_E | 0x0040, "");
        this.ErrorCode.put(FDO_E | 0x0080, "");
        this.ErrorCode.put(FDO_E | 0x0100, "温度传感器故障");
        this.ErrorCode.put(FDO_E | 0x0200, "内部电压基准故障");
        this.ErrorCode.put(FDO_E | 0x0400, "膜帽异常");
        this.ErrorCode.put(FDO_E | 0x0800, "");
        this.ErrorCode.put(FDO_E | 0x1000, "");
        this.ErrorCode.put(FDO_E | 0x2000, "");
        this.ErrorCode.put(FDO_E | 0x4000, "");
        this.ErrorCode.put(FDO_E | 0x8000, "");
        
        this.ErrorCode.put(MPA_E | 0x0020, "状态提示");
        this.ErrorCode.put(MPA_E | 0x0100, "故障报警");
    }
}
