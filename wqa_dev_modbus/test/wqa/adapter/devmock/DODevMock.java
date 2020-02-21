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
public final class DODevMock extends DevMock{
     
    // <editor-fold defaultstate="collapsed" desc="DO寄存器"> 
    //---------------------------------------------------------------------------------------
    public final IREG ALARM = new IREG(0x00, 1, "报警码"); //R
    public final FREG DO = new FREG(0x01, 2, "溶解氧数据(ppm)");       //R
    public final FREG DOPEC = new FREG(0x03, 2, "溶解氧数据(%)");       //R
    public final FREG TEMPER = new FREG(0x05, 2, "温度数据");   //R
    public final FREG ODO = new FREG(0x07, 2, "溶解氧原始信号(nA)");   //R(nA)

//    public final Register Avr = new Register(0x0c, 1); //R/W
    public final FREG PASCA = new FREG(0x09, 2, "大气压力");   //R/W
//    public final Register TempCompen = new Register(0x0A, 2);   //R/W
    public final FREG SALT = new FREG(0x0B, 2, "盐度");   //R/W
    public final IREG AVR = new IREG(0x0C, 1, "平均次数");   //R/W

    public final FREG[] CLODATA = new FREG[]{new FREG(0x31, 2, "饱和氧原始数据"), new FREG(0x35, 2, "无氧原始数据")};  //R/W
    public final IREG CLSTART = new IREG(0x39, 1, "启动定标"); //R/W
    public final FREG CLTEMPER = new FREG(0x3A, 2, "温度定标参数");    //R/W
    public final IREG CLTEMPERSTART = new IREG(0x3C, 1, "启动温度定标");//R/W
    // </editor-fold> 
       
    public DODevMock(){
        super();
        client.RegisterREGS(
                ALARM,
                DO,
                DOPEC,
                TEMPER,
                ODO,
                PASCA,
                SALT,
                AVR,
                CLODATA[0],
                CLODATA[1],
                CLSTART,
                CLTEMPER,
                CLTEMPERSTART
        );
    }
        
    @Override
    public void ResetREGS() throws Exception {
        super.ResetREGS();
        DEVTYPE.SetValue(0x0110);
        ///////////////////////////////////////////////////////////
        ALARM.SetValue(0);
        DO.SetValue((float) DO.RegAddr());
        DOPEC.SetValue((float) DOPEC.RegAddr());
        TEMPER.SetValue((float) TEMPER.RegAddr());
        ODO.SetValue((float) ODO.RegAddr());
        PASCA.SetValue((float) PASCA.RegAddr());
        SALT.SetValue((float) SALT.RegAddr());
        AVR.SetValue(AVR.GetValue());
        WriteREGS();
    }
}
