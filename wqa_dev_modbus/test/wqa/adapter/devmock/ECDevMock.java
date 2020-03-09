/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.devmock;

import modebus.register.FREG;
import modebus.register.IREG;

/**
 *
 * @author chejf
 */
public class ECDevMock extends DevMock {

    // <editor-fold defaultstate="collapsed" desc="EC寄存器"> 
    //---------------------------------------------------------------------------------------
    public final IREG ALARM = new IREG(0x00, 1, "报警码"); //R
    public final FREG EC = new FREG(0x01, 2, "电导率数据 us/cm");       //R(us/cm)
    public final FREG SALT = new FREG(0x03, 2, "盐度PPT/盐度PSU/TDS");    //R
    public final FREG TEMPER = new FREG(0x05, 2, "温度数据");   //R
    public final FREG OEC = new FREG(0x07, 2, "电导率原始信号(电阻)");   //R(电阻)

    public final IREG ECRANG = new IREG(0x09, 1, "电导率单位"); //R/W 0x0000: uS/cm 0x0001: mS/cm 0x0002: mS/m
    public final FREG CMPTEMP = new FREG(0x0A, 2, "温度补偿系数");   //R(mv)
    public final FREG PAREC = new FREG(0x0C, 2, "电极系数"); //R/W
    public final IREG SALTRANGE = new IREG(0x0E, 1, "辅助参数选择"); //R/W 0x0000:盐度PPT 0x0001:盐度PSU 0x0002:TDS mg/L

    public final FREG CMPTDS = new FREG(0x26, 2, "TDS系数"); //R/W

    public final FREG[] CLODATA = new FREG[]{new FREG(0x31, 2, "原始数据1")};  //R/W
    public final FREG[] CLTDATA = new FREG[]{new FREG(0x33, 2, "定标数据1")};  //R/W
    public final IREG CLSTART = new IREG(0x39, 1, "启动定标"); //R/W
    public final FREG CLTEMPER = new FREG(0x3A, 2, "温度定标参数");    //R/W
    public final IREG CLTEMPERSTART = new IREG(0x3C, 1, "启动温度定标");//R/W
    // </editor-fold> 

    public ECDevMock() {
        super();
        client.RegisterREGS(
                ALARM,
                EC,
                SALT,
                TEMPER,
                OEC,
                ECRANG,
                CMPTEMP,
                PAREC,
                SALTRANGE,
                CMPTDS,
                CLODATA[0],
                CLTDATA[0],
                CLSTART,
                CLTEMPER,
                CLTEMPERSTART
        );
    }

    @Override
    public void ResetREGS() throws Exception {
        super.ResetREGS();
        DEVTYPE.SetValue(0x0202);
        
        ///////////////////////////////////////////////////////////
        ALARM.SetValue(0);
        EC.SetValue((float) EC.RegAddr());
        SALT.SetValue((float) SALT.RegAddr());
        TEMPER.SetValue((float) TEMPER.RegAddr());
        OEC.SetValue((float) OEC.RegAddr());
        ECRANG.SetValue(1);
        SALTRANGE.SetValue(1);
        CMPTEMP.SetValue((float) CMPTEMP.RegAddr());
        PAREC.SetValue((float) PAREC.RegAddr());
        CMPTDS.SetValue(33f);
//        CLODATA.SetValue((float) CLODATA.RegAddr());
//        CLTDATA.SetValue((float) CLTDATA.RegAddr());
//        CLSTART.SetValue(CLSTART.GetValue());
//        CLTEMPER.SetValue((float) CLTEMPER.RegAddr());
//        CLTEMPERSTART.SetValue(CLTEMPERSTART.GetValue());
        WriteREGS();
    }
}
