/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.naqing.mockdev;

import java.util.Random;
import java.util.logging.Level;

import modebus.register.FREG;
import modebus.register.IREG;
import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import nahon.comm.faultsystem.LogCenter;

/**
 *
 * @author chejf
 */
public class OSADevMock extends DevMock {

    // <editor-fold defaultstate="collapsed" desc="OSA寄存器"> 
    //---------------------------------------------------------------------------------------
    public final IREG ALARM = new IREG(0x00, 1, "报警码"); //R
    public final FREG MDATA = new FREG(0x01, 2, "主参数数据(OSA-Turb:浊度、OSA-TS：悬浮物/浊度、OSA-ChlA：叶绿素、OSA-Cyano：蓝绿藻、OSA-Oil: 水中油)");      //R
    public final FREG TEMPER = new FREG(0x03, 2, "当前温度");//R
    public final IREG ODATA = new IREG(0x05, 1, "原始光强数据");//R

    public final IREG RANGE = new IREG(0x06, 1, "量程");//R/W
    public final IREG AVR = new IREG(0x07, 1, "平均次数", 1, 100);//R/W
    public final IREG CMODE = new IREG(0x08, 1, "清扫模式", 0, 2);//R/W
    public final IREG CTIME = new IREG(0x09, 1, "清扫次数", 1, 100);//R/W
    public final IREG CINTVAL = new IREG(0x0A, 1, "清扫间隔", 10, 60 * 24);//R/W

    public final IREG CLRANGE = new IREG(0x30, 1, "定标量程"); //R/W
    public final IREG[] CLODATA = new IREG[]{new IREG(0x31, 1, "原始光强1"), new IREG(0x34, 1, "原始光强2"), new IREG(0x37, 1, "原始光强3")}; //R/W
    public final FREG[] CLTDATA = new FREG[]{new FREG(0x32, 2, "定标数据1"), new FREG(0x35, 2, "定标数据2"), new FREG(0x38, 2, "定标数据3")}; //R/W
    public final IREG CLSTART = new IREG(0x3A, 1, "启动定标", 1, 3); //R/W
    public final FREG CLTEMPER = new FREG(0x3B, 2, "温度定标参数");    //R/W
    public final IREG CLTEMPERSTART = new IREG(0x3D, 1, "温度启动定标");//R/W

    public final IREG RANGNUM = new IREG(0x50, 1, "量程个数"); //R
    public final FREG[] RANGN = new FREG[]{new FREG(0x51, 2, "量程1"), new FREG(0x53, 2, "量程2"), new FREG(0x55, 2, "量程3"), new FREG(0x57, 2, "量程4")}; //R
    // </editor-fold> 

    public OSADevMock() {
        super();
        client.RegisterREGS(ALARM,
                MDATA,
                TEMPER,
                ODATA,
                RANGE,
                AVR,
                CMODE,
                CTIME,
                CINTVAL,
                CLRANGE, RANGNUM,
                RANGN[0], RANGN[1], RANGN[2], RANGN[3],
                CLODATA[0],
                CLODATA[1],
                CLODATA[2],
                CLTDATA[0],
                CLTDATA[1],
                CLTDATA[2],
                CLSTART,
                CLTEMPER,
                CLTEMPERSTART
        );

        this.client.ReadDataEvent.RegeditListener(new EventListener() {
            @Override
            public void recevieEvent(Event event) {
                try {
//                    LogCenter.Instance().PrintLog(Level.INFO, randomdata.nextFloat() * 100 + "");
//                    LogCenter.Instance().PrintLog(Level.INFO, randomdata.nextFloat() * 100 + "");
//                    LogCenter.Instance().PrintLog(Level.INFO, randomdata.nextInt() + "");
                    MDATA.SetValue(randomdata.nextFloat() * 10);
                    TEMPER.SetValue(randomdata.nextFloat() * 100);
                    ODATA.SetValue((int)randomdata.nextFloat() * 1000);

                    client.SetREG(MDATA, TEMPER, ODATA);
                }catch (Exception ex){
                    LogCenter.Instance().PrintLog(Level.INFO, ex.getMessage());
                }
            }
        });
    }

    Random randomdata = new Random();
    
    @Override
    public void ResetREGS() throws Exception {
        super.ResetREGS();
        DEVTYPE.SetValue(0x0104);
        ///////////////////////////////////////////////////////////
        ALARM.SetValue(0);
        MDATA.SetValue((float) MDATA.RegAddr());
        TEMPER.SetValue((float) TEMPER.RegAddr());
        ODATA.SetValue(ODATA.RegAddr());
        RANGE.SetValue(0);
        AVR.SetValue(1);
        CMODE.SetValue(CMODE.min);
        CTIME.SetValue(CTIME.min);
        CINTVAL.SetValue(CINTVAL.min);
        RANGNUM.SetValue(2);
        for (int i = 0; i < RANGN.length; i++) {
            RANGN[i].SetValue((float) ((i+1) * 100));
        }
        WriteREGS();
    }


}
