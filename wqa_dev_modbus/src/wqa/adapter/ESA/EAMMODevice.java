/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.ESA;

import java.util.ArrayList;
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
public class EAMMODevice extends AbsDevice {

    private final IREG ALARM = new IREG(0x00, 1, "报警码"); //R
    private final FREG PH = new FREG(0x01, 2, "NH3-N");       //R
    private final FREG TEMPER = new FREG(0x05, 2, "温度数据");   //R
    private final FREG OPH = new FREG(0x07, 2, "氨氮原始信号(mv)");   //R(mv)
    private final FREG CLPH = new FREG(0x09, 2, "PH补偿数据");   //R/W

    private final FREG[] CLODATA = new FREG[]{new FREG(0x31, 2, "原始数据1"), new FREG(0x35, 2, "原始数据2")};  //R/W
    private final FREG[] CLTDATA = new FREG[]{new FREG(0x33, 2, "定标数据1"), new FREG(0x37, 2, "定标数据2")};  //R/W
    private final IREG CLSTART = new IREG(0x39, 1, ""); //R/W
    private final FREG CLTEMPER = new FREG(0x3A, 2, "");    //R/W
    private final IREG CLTEMPERSTART = new IREG(0x3C, 1, "");//R/W

    public EAMMODevice(SDevInfo info) {
        super(info);
    }

    // <editor-fold defaultstate="collapsed" desc="设置接口"> 
    @Override
    public ArrayList<SConfigItem> GetConfigList() {
        ArrayList<SConfigItem> list = super.GetConfigList();
        list.add(SConfigItem.CreateRWItem(CLPH.toString(), CLPH.GetValue().toString(), ""));
        return list;
    }

    @Override
    public void SetConfigList(ArrayList<SConfigItem> list) throws Exception {
        super.SetConfigList(list);
        for (SConfigItem item : list) {
            if (item.IsKey(CLPH.toString())) {
                this.SetConfigREG(CLPH, item.GetValue());
            }
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="采集接口"> 
    @Override
    public CollectData CollectData() throws Exception {
        CollectData disdata = this.BuildDisplayData();
        this.ReadREG(ALARM, PH, TEMPER, OPH);
        this.ReadREG(OTEMPER);

        disdata.datas[0].mainData = NahonConvert.TimData(PH.GetValue(), 2);
        disdata.datas[1].mainData = NahonConvert.TimData(OPH.GetValue(), 2);

        disdata.datas[2].mainData = NahonConvert.TimData(TEMPER.GetValue(), 2);
        disdata.datas[3].mainData = NahonConvert.TimData(OTEMPER.GetValue(), 2);

        disdata.alarm = ALARM.GetValue();
        String info = CErrorTable.GetInstance().GetErrorString(((this.GetDevInfo().dev_type & 0xFF00) << 8) | disdata.alarm);
        disdata.alram_info = info == null ? "" : info;
        return disdata;
    }
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="定标接口"> 
    private void CalDevice(float[] oradata, float[] caldata) throws Exception {
        if (CLODATA.length < oradata.length) {
            throw new Exception("定标个数异常");
        }
        for (int i = 0; i < oradata.length; i++) {
            CLODATA[i].SetValue(oradata[i]);
            CLTDATA[i].SetValue(caldata[i]);
        }

        this.CLSTART.SetValue(oradata.length);
        this.SetREG(CLODATA[0], CLODATA[1], CLTDATA[0], CLTDATA[1], CLSTART);
    }

    private void CalTemer(float caltemper) throws Exception {
        this.CLTEMPER.SetValue(caltemper);
        this.CLTEMPERSTART.SetValue(0x01);
        this.SetREG(CLTEMPER, CLTEMPERSTART);
    }

    @Override
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception {
        if (type.contentEquals("温度")) {
            this.CalTemer(testdata[0]);
        } else {
            this.CalDevice(oradata, testdata);
        }
        return new LogNode("测试结果", "成功");
    }
    // </editor-fold> 
}
