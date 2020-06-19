/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.ESA;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import wqa.adapter.factory.AbsDevice;
import modebus.pro.NahonConvert;
import modebus.register.*;
import wqa.adapter.factory.*;
import wqa.dev.data.*;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class ECDevice extends AbsDevice {

    private final IREG ALARM = new IREG(0x00, 1, "报警码"); //R
    private final FREG EC = new FREG(0x01, 2, "电导率数据 us/cm");       //R(us/cm)
    private final FREG SALT = new FREG(0x03, 2, "盐度PPT/盐度PSU/TDS");    //R
    private final FREG TEMPER = new FREG(0x05, 2, "温度数据");   //R
    private final FREG OEC = new FREG(0x07, 2, "电导率原始信号(电阻)");   //R(电阻)

    private final IREG ECRANG = new IREG(0x09, 1, "电导率单位"); //R/W 0x0000: uS/cm 0x0001: mS/cm 0x0002: mS/m
    private final FREG CMPTEMP = new FREG(0x0A, 2, "温度补偿系数");   //R(mv)
    private final FREG PAREC = new FREG(0x0C, 2, "电极系数"); //R/W
    private final IREG SALTRANGE = new IREG(0x0E, 1, "辅助参数选择"); //R/W 0x0000:盐度PPT 0x0001:盐度PSU 0x0002:TDS mg/L

    private final FREG CMPTDS = new FREG(0x26, 2, "TDS系数"); //R/W

    private final FREG CLODATA = new FREG(0x31, 2, "原始数据1");  //R/W
    private final FREG CLTDATA = new FREG(0x33, 2, "定标数据1");  //R/W
    private final IREG CLSTART = new IREG(0x39, 1, "启动定标"); //R/W
    private final FREG CLTEMPER = new FREG(0x3A, 2, "温度定标参数");    //R/W
    private final IREG CLTEMPERSTART = new IREG(0x3C, 1, "启动温度定标");//R/W

    public ECDevice(SDevInfo info) {
        super(info);
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    @Override
    public void InitDevice() throws Exception {
        super.InitDevice(); //To change body of generated methods, choose Tools | Templates.
        this.ReadREG(ECRANG, CMPTEMP, PAREC, SALTRANGE, CMPTDS);
        if (this.IsOverVersion(104)) {
            if (this.ECRANG.GetValue() < 0 && this.ECRANG.GetValue() >= EC_UNIT_STRING.length) {
                Logger.getGlobal().log(Level.SEVERE, "ECRANG无效值{0}，修改为0", ECRANG.GetValue());
                this.ECRANG.SetValue(0);
            }
            if (this.SALTRANGE.GetValue() < 0 && this.SALTRANGE.GetValue() >= SALT_UNIT_STRING.length) {
                Logger.getGlobal().log(Level.SEVERE, "SALTRANGE无效值{0}，修改为0", ECRANG.GetValue());
                this.SALTRANGE.SetValue(0);
            }
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="采集控制">
    @Override
    public CollectData CollectData() throws Exception {
        CollectData disdata = this.BuildDisplayData();
        this.ReadREG(ALARM, EC, SALT, TEMPER, OEC);
        this.ReadREG(OTEMPER);
        disdata.datas[0].mainData = NahonConvert.TimData(EC.GetValue(), 2);
        disdata.datas[1].mainData = NahonConvert.TimData(OEC.GetValue(), 2);

        disdata.datas[2].mainData = NahonConvert.TimData(TEMPER.GetValue(), 2);
        disdata.datas[3].mainData = NahonConvert.TimData(OTEMPER.GetValue(), 2);
        //盐度
        disdata.datas[4].mainData = NahonConvert.TimData(SALT.GetValue(), 2);

        if (this.IsOverVersion(104)) {
            disdata.datas[0].unit = EC_UNIT_STRING[this.ECRANG.GetValue()];
            int max_range = 500;
            if (ECRANG.GetValue() == 0x00) {
                max_range = (max_range * 1000);
            }
            if (ECRANG.GetValue() == 0x02) {
                max_range = (max_range * 100);
            }
            disdata.datas[0].range_info = "(0-" + max_range + ")";
            disdata.datas[4].unit = SALT_UNIT_STRING[this.SALTRANGE.GetValue()];
        }

        disdata.alarm = ALARM.GetValue();
        String info = CErrorTable.GetInstance().GetErrorString(((this.GetDevInfo().dev_type & 0xFF00) << 8) | disdata.alarm);
        disdata.alram_info = info == null ? "" : info;
        return disdata;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置接口"> 
//    private ModeBus_EC.ECConfig ec_config;
    public static final String[] EC_UNIT_STRING = new String[]{"us/cm", "ms/cm", "ms/m"};
    public static final String[] SALT_UNIT_STRING = new String[]{"ppt", "psu", "mg/L"};
//    private static final String[] CONFIGSTRING = new String[]{
//        "电导率单位",
//        "温度补偿系数",
//        "电极系数",
//        "盐度单位",
//        "TDS系数"
//    };

    @Override
    public ArrayList<SConfigItem> GetConfigList() {
        ArrayList<SConfigItem> list = super.GetConfigList();
        if (this.IsOverVersion(104)) {
            list.add(SConfigItem.CreateSItem(ECRANG.toString(), EC_UNIT_STRING[this.ECRANG.GetValue()], "", EC_UNIT_STRING));
            list.add(SConfigItem.CreateRWItem(PAREC.toString(), PAREC.GetValue().toString(), ""));
            list.add(SConfigItem.CreateSItem(SALTRANGE.toString(), SALT_UNIT_STRING[SALTRANGE.GetValue()], "", SALT_UNIT_STRING));
            list.add(SConfigItem.CreateRWItem(CMPTDS.toString(), CMPTDS.GetValue().toString(), ""));
        }

        list.add(SConfigItem.CreateRWItem(CMPTEMP.toString(), CMPTEMP.GetValue().toString(), ""));
        return list;
    }

    @Override
    public void SetConfigList(ArrayList<SConfigItem> list) throws Exception {
        super.SetConfigList(list);
        for (SConfigItem item : list) {
            if (item.IsKey(ECRANG.toString())) {
                for (int i = 0; i < EC_UNIT_STRING.length; i++) {
                    if (item.GetValue().contentEquals(EC_UNIT_STRING[i])) {
                        this.SetConfigREG(ECRANG, String.valueOf(i));
                        break;
                    }
                }
            }

            if (item.IsKey(CMPTEMP.toString())) {
                this.SetConfigREG(CMPTEMP, item.GetValue());
            }

            if (item.IsKey(PAREC.toString())) {
                this.SetConfigREG(PAREC, item.GetValue());
            }

            if (item.IsKey(SALTRANGE.toString())) {
                for (int i = 0; i < SALT_UNIT_STRING.length; i++) {
                    if (item.GetValue().contentEquals(SALT_UNIT_STRING[i])) {
                        this.SetConfigREG(SALTRANGE, String.valueOf(i));
                        break;
                    }
                }
            }
            if (item.IsKey(CMPTDS.toString())) {
                this.SetConfigREG(CMPTDS, item.GetValue());
            }
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标接口"> 
    @Override
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception {
        if (type.contentEquals("温度")) {
            this.CalTemer(testdata[0]);
        } else {
            this.CalDevice(oradata, testdata);
        }
        return LogNode.CALOK();
    }

    private void CalDevice(float[] oradata, float[] caldata) throws Exception {
        if (1 < oradata.length) {
            throw new Exception("定标个数异常");
        }
        CLODATA.SetValue(oradata[0]);
        CLTDATA.SetValue(caldata[0]);
        this.CLSTART.SetValue(oradata.length);
        this.SetREG(CLODATA, CLTDATA, CLSTART);
    }

    private void CalTemer(float caltemper) throws Exception {
        this.CLTEMPER.SetValue(caltemper);
        this.CLTEMPERSTART.SetValue(0x01);
        this.SetREG(CLTEMPER, CLTEMPERSTART);
    }
    // </editor-fold> 

}
