/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.ESA;

import wqa.adapter.factory.AbsDevice;
import java.util.ArrayList;
import modebus.pro.NahonConvert;
import modebus.register.*;
import static wqa.adapter.factory.AbsDevice.DEF_TIMEOUT;
import static wqa.adapter.factory.AbsDevice.RETRY_TIME;
import wqa.adapter.factory.*;
import wqa.dev.data.*;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class DODevice extends AbsDevice {

    private final IREG ALARM = new IREG(0x00, 1, "报警码"); //R
    private final FREG DO = new FREG(0x01, 2, "溶解氧数据(ppm)");       //R
    private final FREG DOPEC = new FREG(0x03, 2, "溶解氧数据(%)");       //R
    private final FREG TEMPER = new FREG(0x05, 2, "温度数据");   //R
    private final FREG ODO = new FREG(0x07, 2, "溶解氧原始信号(nA)");   //R(nA)

//    private final Register Avr = new Register(0x0c, 1); //R/W
    private final FREG PASCA = new FREG(0x09, 2, "大气压力");   //R/W
//    private final Register TempCompen = new Register(0x0A, 2);   //R/W
    private final FREG SALT = new FREG(0x0B, 2, "盐度");   //R/W
    private final IREG AVR = new IREG(0x0D, 1, "平均次数");   //R/W

    private final FREG[] CLODATA = new FREG[]{new FREG(0x31, 2, "饱和氧原始数据"), new FREG(0x35, 2, "无氧原始数据")};  //R/W
    private final IREG CLSTART = new IREG(0x39, 1, "启动定标"); //R/W
    private final FREG CLTEMPER = new FREG(0x3A, 2, "温度定标参数");    //R/W
    private final IREG CLTEMPERSTART = new IREG(0x3C, 1, "启动温度定标");//R/W

    public DODevice(SDevInfo info) {
        super(info);
    }

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice();
        //获取盐度//获取大气压//平均次数
        this.base_drv.ReadREG(RETRY_TIME, DEF_TIMEOUT, PASCA, SALT, AVR);
    }

    // <editor-fold defaultstate="collapsed" desc="溶氧额外设置"> 
//    private static String[] CONFIGSTRING = new String[]{
//        "大气压力",
//        "盐度",
//        "平均次数"
//    };
//    private float salt;  //盐度
//    private float pasca; //大气压
//    private int avr;  //温度补偿
    @Override
    public ArrayList<SConfigItem> GetConfigList() {
        ArrayList<SConfigItem> list = super.GetConfigList();
        list.add(SConfigItem.CreateRWItem(PASCA.toString(), PASCA.GetValue().toString(), ""));
        list.add(SConfigItem.CreateRWItem(SALT.toString(), SALT.GetValue().toString(), ""));
        list.add(SConfigItem.CreateRWItem(AVR.toString(), AVR.GetValue().toString(), ""));
        return list;
    }

    @Override
    public void SetConfigList(ArrayList<SConfigItem> list) throws Exception {
        super.SetConfigList(list);
        for (SConfigItem item : list) {
            if (item.IsKey(PASCA.toString())) {
                this.SetConfigREG(PASCA, item.GetValue());
            }

            if (item.IsKey(SALT.toString())) {
                this.SetConfigREG(SALT, item.GetValue());
            }

            if (item.IsKey(AVR.toString())) {
                this.SetConfigREG(AVR, item.GetValue());
            }
        }
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="采集控制"> 
    @Override
    public CollectData CollectData() throws Exception {
        CollectData disdata = this.BuildDisplayData();
        this.base_drv.ReadREG(RETRY_TIME, DEF_TIMEOUT, ALARM, DO, DOPEC, TEMPER, ODO);
        this.base_drv.ReadREG(RETRY_TIME, DEF_TIMEOUT, OTEMPER);

        disdata.datas[0].mainData = NahonConvert.TimData(DO.GetValue(), 3);
        disdata.datas[1].mainData = NahonConvert.TimData(DOPEC.GetValue(), 2);
        disdata.datas[2].mainData = NahonConvert.TimData(ODO.GetValue(), 3);

        disdata.datas[3].mainData = NahonConvert.TimData(TEMPER.GetValue(), 2);
        disdata.datas[4].mainData = NahonConvert.TimData(OTEMPER.GetValue(), 2);

        disdata.alarm = ALARM.GetValue();
        int tmptype = 0;
        if ((this.GetDevInfo().dev_type & 0xFF) == 0x10) {
            tmptype = CErrorTable.OSA_E;
        } else {
            tmptype = CErrorTable.ESA_E;
        }
        String info = CErrorTable.GetInstance().GetErrorString(tmptype | disdata.alarm);
        disdata.alram_info = info == null ? "" : info;
        return disdata;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标设置"> 
    @Override
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception {
        if (type.contentEquals("温度")) {
            this.CalTemer(testdata[0]);
        } else {
            //界面输入是 {饱和氧,无氧}的顺序,需要交换顺序
//            if (oradata.length == 2) {
//                float temp = oradata[1];
//                oradata[1] = oradata[0];
//                oradata[0] = temp;
//            }
            this.CalDevice(oradata);
        }
        return LogNode.CALOK();
    }

    private void CalDevice(float[] oradata) throws Exception {
        if (CLODATA.length < oradata.length) {
            throw new Exception("定标个数异常" + oradata.length);
        }
        for (int i = 0; i < oradata.length; i++) {
            CLODATA[i].SetValue(oradata[i]);
        }

        this.CLSTART.SetValue(oradata.length);
        this.base_drv.SetREG(RETRY_TIME, DEF_TIMEOUT, CLODATA[0], CLODATA[1], CLSTART);
//        byte[] data = new byte[oradata.length * 8];
//        for (int i = 0; i < oradata.length; i++) {
//            System.arraycopy(NahonConvert.FloatToByteArray(oradata[i]), 0, data, i * 8, 4);
//            System.arraycopy(NahonConvert.FloatToByteArray(0), 0, data, i * 8 + 4, 4);
//        }
//
//        this.WriterMemory(Do1.reg_add, (Do1.reg_num + Do1.reg_num) * oradata.length, data, def_timeout);
//        this.WriterMemory(DataCalStart.reg_add, DataCalStart.reg_num, NahonConvert.UShortToByteArray(oradata.length), def_timeout);
    }

    private void CalTemer(float caltemper) throws Exception {
        this.CLTEMPER.SetValue(caltemper);
        this.CLTEMPERSTART.SetValue(0x01);
        this.base_drv.SetREG(RETRY_TIME, DEF_TIMEOUT, CLTEMPER, CLTEMPERSTART);
    }
    // </editor-fold>     
}
