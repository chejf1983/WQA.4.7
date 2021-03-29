/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ESA;

import base.migp.mem.*;
import base.migp.reg.*;
import migp.adapter.factory.AbsDevice;
import wqa.dev.data.SDevInfo;

/**
 *
 * @author chejf
 */
public abstract class ESADEV extends AbsDevice {

    // <editor-fold defaultstate="collapsed" desc="内存表"> 
    // <editor-fold defaultstate="collapsed" desc="VPA"> 
    public FMEG VDRANGE_MIN = new FMEG(new VPA(0x02, 4), "主参数量程下限");
    public FMEG VDRANGE_MAX = new FMEG(new VPA(0x06, 4), "主参数量程上限");
    public FMEG VTRANGE_MIN = new FMEG(new VPA(0x0C, 4), "温度参数量程下限");
    public FMEG VTRANGE_MAX = new FMEG(new VPA(0x10, 4), "温度参数量程上限");
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="MDA"> 
    public IMEG MALARM = new IMEG(new MDA(0x00, 2), "报警码");  //  PH   |    DO   |     EC_I    |    EC_II    |  ORP    |  AMMO   |  CHLI  |  CHLII
    public FMEG MPAR1 = new FMEG(new MDA(0x02, 4), "参数1");    //  PH     溶氧mg/L   电导率us/cm   电导率us/cm   ORPmv    氨氮mg/L   余氯ppm   余氯ppm
    public FMEG MPAR2 = new FMEG(new MDA(0x06, 4), "参数2");    //  --      溶氧%       盐度ppt        盐度ppt     --        --        PH        PH      
    public FMEG MPAR3 = new FMEG(new MDA(0x0A, 4), "参数3");    //  温度     温度         温度          温度       温度      温度      温度       温度     
    public FMEG MPAR4 = new FMEG(new MDA(0x0E, 4), "参数4");    //  __        __           __           __         __       --        --        ORP
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="SRA"> 
    public FMEG SR1 = new FMEG(new SRA(0x00, 4), "原始信号");
    public FMEG SR2 = new FMEG(new SRA(0x04, 4), "温度原始信号");

    public IMEG SR24 = new IMEG(new SRA(24, 2), "定标方式");
    public FMEG SR26 = new FMEG(new SRA(26, 4), "定标点1原始信号");
    public FMEG SR30 = new FMEG(new SRA(30, 4), "定标点1数据");
    public FMEG SR34 = new FMEG(new SRA(34, 4), "定标点2原始信号");
    public FMEG SR38 = new FMEG(new SRA(38, 4), "定标点2数据");
    public FMEG SR42 = new FMEG(new SRA(42, 4), "保留");
    public FMEG SR46 = new FMEG(new SRA(46, 4), "保留");
    public IMEG SR50 = new IMEG(new SRA(50, 2), "定标使能");
    public FMEG SR52 = new FMEG(new SRA(52, 4), "温度定标数据");
    public IMEG SR56 = new IMEG(new SRA(56, 2), "温度定标使能");
    // </editor-fold> 
    // </editor-fold> 

    public ESADEV(SDevInfo devinfo) {
        super(devinfo);
    }

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice(); //To change body of generated methods, choose Tools | Templates.

        this.ReadMEG(VDRANGE_MIN, VDRANGE_MAX, VTRANGE_MIN, VTRANGE_MAX);
    }

    public String GetMainRangeString() {
        return "(" + this.VDRANGE_MIN.GetValue() + "-" + this.VDRANGE_MAX.GetValue() + ")"; //量程
    }

    public String GetTemperRangeString() {
        return "(" + this.VTRANGE_MIN.GetValue() + "-" + this.VTRANGE_MAX.GetValue() + ")"; //量程;
    }

    public void calDataNew(float[] oradata, float[] testdata) throws Exception {
        if (oradata.length == 1) {
            SR26.SetValue(oradata[0]);
            SR30.SetValue(testdata[0]);
            SR24.SetValue(0x01);
            SR50.SetValue(0x01);
            this.SetMEG(SR24, SR26, SR30, SR50);
        } else {
            SR26.SetValue(oradata[0]);
            SR30.SetValue(testdata[0]);
            SR34.SetValue(oradata[1]);
            SR38.SetValue(testdata[1]);
            SR24.SetValue(0x02);
            SR50.SetValue(0x01);
            this.SetMEG(SR24, SR26, SR30, SR34, SR38, SR50);
        }
    }

    public void calTemperNew(float[] oradata, float[] testdata) throws Exception {
        SR52.SetValue(testdata[0]);
        SR56.SetValue(0x01);
        this.SetMEG(SR52, SR56);
    }
}
