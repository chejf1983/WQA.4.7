/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.ISA;

import modebus.pro.NahonConvert;
import modebus.register.*;
import wqa.adapter.factory.AbsDevice;
import static wqa.adapter.factory.AbsDevice.DEF_TIMEOUT;
import wqa.adapter.factory.*;
import wqa.dev.data.*;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class AMMODevice extends AbsDevice {

    public final IREG ALARM = new IREG(0x00, 1, "报警码");   //R
    public final FREG PH = new FREG(0x01, 2, "PH数据");   //R
    public final FREG NH4 = new FREG(0x03, 2, "NH4+浓度数据");   //R
    public final FREG K = new FREG(0x05, 2, "K+浓度数据");   //R
    public final FREG TEMPER = new FREG(0x09, 2, "温度数据");   //R
    public final FREG OPH = new FREG(0x0B, 2, "PH原始信号");   //R
    public final FREG ONH4 = new FREG(0x0D, 2, "NH4+原始信号");   //R
    public final FREG OK = new FREG(0x0F, 2, "K+原始信号");   //R

    public final IREG CLTYPE = new IREG(0x30, 1, "定标类型", 1, 3);   //R/W  (PH:01, NH:02, K:03)    
    public final FREG[] CLODATA = new FREG[]{new FREG(0x31, 2, "原始数据1"), new FREG(0x35, 2, "原始数据2")};  //R/W
    public final FREG[] CLTDATA = new FREG[]{new FREG(0x33, 2, "定标数据1"), new FREG(0x37, 2, "定标数据2")};  //R/W
    public final IREG CLSTART = new IREG(0x39, 1, "启动定标"); //R/W (一点：01， 两点02）
    public final FREG CLTEMP = new FREG(0x3A, 2, "温度定标参数");    //R/W
    public final IREG CLTEMPSTART = new IREG(0x3C, 1, "启动温度定标");//R/W

    public AMMODevice(SDevInfo info) {
        super(info);
    }

    // <editor-fold defaultstate="collapsed" desc="采集控制"> 
    @Override
    public CollectData CollectData() throws Exception {
        CollectData disdata = this.BuildDisplayData();
//        AmmoPacket ammo_data = this.ammo_drv.GetData();
        this.base_drv.ReadREG(RETRY_TIME, DEF_TIMEOUT, ALARM, PH, NH4, K, TEMPER, OPH, ONH4, OK);
        this.base_drv.ReadREG(RETRY_TIME, DEF_TIMEOUT, OTEMPER);

        disdata.datas[0].mainData = NahonConvert.TimData(PH.GetValue(), 2);
        disdata.datas[1].mainData = NahonConvert.TimData(OPH.GetValue(), 2);

        disdata.datas[2].mainData = NahonConvert.TimData(NH4.GetValue(), 2);
        disdata.datas[3].mainData = NahonConvert.TimData(ONH4.GetValue(), 2);

        if (this.GetDevInfo().dev_type == 0x0301) {
            disdata.datas[4].mainData = NahonConvert.TimData(K.GetValue(), 2);
            disdata.datas[5].mainData = NahonConvert.TimData(OK.GetValue(), 2);
        }

        disdata.datas[disdata.datas.length - 2].mainData = NahonConvert.TimData(TEMPER.GetValue(), 2);
        disdata.datas[disdata.datas.length - 1].mainData = NahonConvert.TimData(OTEMPER.GetValue(), 2);

        disdata.alarm = ALARM.GetValue();
        String info = CErrorTable.GetInstance().GetErrorString(((this.GetDevInfo().dev_type & 0xFF00) << 8) | disdata.alarm);
        disdata.alram_info = info == null ? "" : info;
        return disdata;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标"> 
    @Override
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception {
        for (int i = 0; i < this.GetDataNames().length; i++) {
            if (type.contentEquals("温度")) {
                this.CalTemer(testdata[0]);
                return LogNode.CALOK();
            } else if (type.contentEquals(this.GetDataNames()[i])) {
                //(PH:01, NH:02, K:03)    
                this.CalDevice(i + 1, oradata, testdata);
                return LogNode.CALOK();
            }
        }
        throw new Exception("没有找到数据类型");
    }

    private void CalDevice(int caltype, float[] oradata, float[] caldata) throws Exception {
        if (CLODATA.length < oradata.length) {
            throw new Exception("定标个数异常");
        }
        for (int i = 0; i < oradata.length; i++) {
            CLODATA[i].SetValue(oradata[i]);
            CLTDATA[i].SetValue(caldata[i]);
        }

        this.CLTYPE.SetValue(caltype);
        this.CLSTART.SetValue(oradata.length);
        this.base_drv.SetREG(RETRY_TIME, DEF_TIMEOUT, CLTYPE, CLODATA[0], CLODATA[1], CLTDATA[0], CLTDATA[1], CLSTART);
    }

    private void CalTemer(float caltemper) throws Exception {
        this.CLTEMP.SetValue(caltemper);
        this.CLTEMPSTART.SetValue(0x01);
        this.base_drv.SetREG(RETRY_TIME, DEF_TIMEOUT, CLTEMP, CLTEMPSTART);
    }
    // </editor-fold> 
}
