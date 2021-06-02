/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.ESA;

import wqa.adapter.factory.AbsDevice;
import modebus.pro.NahonConvert;
import modebus.register.*;
import wqa.adapter.factory.*;
import wqa.dev.data.*;

/**
 *
 * @author chejf
 */
public class ECHLORI extends AbsDevice {

    protected final IREG ALARM = new IREG(0x00, 1, "报警码"); //R
    protected final FREG CHL = new FREG(0x01, 2, "余氯");       //R
    protected final FREG PH = new FREG(0x03, 2, "PH");       //R
    protected FREG TEMPER = new FREG(0x05, 2, "温度数据");   //R
    protected FREG OCHL = new FREG(0x07, 2, "余氯原始信号(mv)");   //R(mv)
    protected FREG OPH = new FREG(0x09, 2, "PH原始信号(mv)");   //R/W

    private final IREG CTYPE = new IREG(0x30, 1, "参数类型"); //R0x00余氯，0x01PH
    private final FREG[] CLODATA = new FREG[]{new FREG(0x31, 2, "原始数据1"), new FREG(0x35, 2, "原始数据2")};  //R/W
    private final FREG[] CLTDATA = new FREG[]{new FREG(0x33, 2, "定标数据1"), new FREG(0x37, 2, "定标数据2")};  //R/W
    private final IREG CLSTART = new IREG(0x39, 1, ""); //R/W
    private final FREG CLTEMPER = new FREG(0x3A, 2, "");    //R/W
    private final IREG CLTEMPERSTART = new IREG(0x3C, 1, "");//R/W

    public ECHLORI(SDevInfo info) {
        super(info);
        
    }

    // <editor-fold defaultstate="collapsed" desc="采集接口"> 
    @Override
    public CollectData CollectData() throws Exception {
        CollectData disdata = this.BuildDisplayData();
        this.ReadREG(ALARM, CHL, PH, TEMPER, OCHL, OPH);
        this.ReadREG(OTEMPER);

        disdata.datas[0].mainData = NahonConvert.TimData(CHL.GetValue(), 2);
        disdata.datas[1].mainData = NahonConvert.TimData(OCHL.GetValue(), 2);

        disdata.datas[2].mainData = NahonConvert.TimData(PH.GetValue(), 2);
        disdata.datas[3].mainData = NahonConvert.TimData(OPH.GetValue(), 2);

        disdata.datas[4].mainData = NahonConvert.TimData(TEMPER.GetValue(), 2);
        disdata.datas[5].mainData = NahonConvert.TimData(OTEMPER.GetValue(), 2);

        disdata.alarm = ALARM.GetValue();
        String info = CErrorTable.GetInstance().GetErrorString(CErrorTable.ESA_E | disdata.alarm);
        disdata.alram_info = info == null ? "" : info;
        return disdata;
    }
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="定标接口"> 
    private void CalDevice(String type, float[] oradata, float[] caldata) throws Exception {
        if (CLODATA.length < oradata.length) {
            throw new Exception("定标个数异常");
        }
        for (int i = 0; i < oradata.length; i++) {
            CLODATA[i].SetValue(oradata[i]);
            CLTDATA[i].SetValue(caldata[i]);
        }

        for (int i = 0; i < this.GetDataNames().length; i++) {
            if (type.contentEquals(this.GetCalDataList()[i].data_name)) {
                CTYPE.SetValue(i);
            }
        }
//        if (type.contentEquals(this.GetDataNames()[0])) {
//            CTYPE.SetValue(0);
//        } else {
//            CTYPE.SetValue(1);
//        }
        this.CLSTART.SetValue(oradata.length);
        this.SetREG(CTYPE, CLODATA[0], CLODATA[1], CLTDATA[0], CLTDATA[1], CLSTART);
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
            this.CalDevice(type, oradata, testdata);
        }
        return new LogNode("测试结果", "成功");
    }
    // </editor-fold> 
}
