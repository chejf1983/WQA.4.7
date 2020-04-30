/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.naqing.mockdev;

import modebus.register.BREG;
import modebus.register.FREG;
import modebus.register.IREG;
import modebus.register.SREG;

/**
 *
 * @author chejf
 */
public abstract class DevMock {

    // <editor-fold defaultstate="collapsed" desc="DO寄存器"> 
    public final SREG DEVNAME = new SREG(0x10, 8, "设备名称");//R   
    public final SREG SERIANUM = new SREG(0x18, 8, "序列号");//R
    public final SREG HWVER = new SREG(0x20, 1, "硬件版本");//R
    public final SREG SWVER = new SREG(0x21, 2, "软件版本");//R
    public final IREG DEVADDR = new IREG(0x23, 1, "设备地址", 1, 32);//R/W
    public final IREG BANDRANGEI = new IREG(0x24, 1, "波特率", 0, 10);//R/W
    public final IREG DEVTYPE = new IREG(0x25, 1, "设备类型");//R

    public final FREG OTEMPER = new FREG(0x40, 2, "温度原始值");//R 
    public final BREG SDTEMPSWT = new BREG(0x42, 1, "手动温补开关"); //R/W
    public final FREG SDTEMP = new FREG(0x43, 2, "手动温补值", 0, 60);//R/W
    // </editor-fold> 

    public ModbusClient client = new ModbusClient();

    public DevMock(){

        client.RegisterREGS(
                DEVNAME,
                SERIANUM,
                HWVER,
                SWVER,
                DEVADDR,
                BANDRANGEI,
                DEVTYPE,
                OTEMPER,
                SDTEMPSWT,
                SDTEMP);
    }

    public void ResetREGS() throws Exception {
        DEVNAME.SetValue("TestDO");
        SERIANUM.SetValue("201912261415DO");
        HWVER.SetValue("H1");
        SWVER.SetValue("D105");
        DEVADDR.SetValue(02);
        BANDRANGEI.SetValue(1);
        DEVTYPE.SetValue(0x0110);

        ///////////////////////////////////////////////////////////
        OTEMPER.SetValue((float) OTEMPER.RegAddr());
        SDTEMPSWT.SetValue(Boolean.FALSE);
        SDTEMP.SetValue(23f);
        WriteREGS();
    }

    public void ReadREGS() throws Exception {
        this.client.Refresh();
    }

    public void WriteREGS() throws Exception {
        this.client.DowloadRegs();
    }
}
