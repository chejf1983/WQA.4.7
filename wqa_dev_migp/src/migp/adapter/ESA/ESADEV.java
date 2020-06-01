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
public abstract class ESADEV extends AbsDevice{
    
    // <editor-fold defaultstate="collapsed" desc="内存表"> 
    // <editor-fold defaultstate="collapsed" desc="VPA"> 
    FMEG VDRANGE_MIN = new FMEG(new VPA(0x02, 4), "主参数量程下限");
    FMEG VDRANGE_MAX = new FMEG(new VPA(0x06, 4), "主参数量程上限");
    FMEG VTRANGE_MIN = new FMEG(new VPA(0x0C, 4), "温度参数量程下限");
    FMEG VTRANGE_MAX = new FMEG(new VPA(0x10, 4), "温度参数量程上限");
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="MDA"> 
    IMEG MALARM = new IMEG(new MDA(0x00, 2), "报警码");  //  PH   |    DO   |     EC_I    |    EC_II    |  ORP 
    FMEG MPAR1 = new FMEG(new MDA(0x02, 4), "参数1");    //  PH     溶氧mg/L   电导率us/cm   电导率us/cm   ORPmv       
    FMEG MPAR2 = new FMEG(new MDA(0x06, 4), "参数2");    //  --      溶氧%       盐度ppt        盐度ppt     --         
    FMEG MPAR3 = new FMEG(new MDA(0x0A, 4), "参数3");    //  温度     温度         温度          温度       温度    
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="SRA"> 
    FMEG SR1 = new FMEG(new SRA(0x00, 4), "原始信号");
    FMEG SR2 = new FMEG(new SRA(0x04, 4), "温度原始信号");
    FMEG SR3 = new FMEG(new SRA(0x08, 4), "相位差");
    FMEG SR4 = new FMEG(new SRA(12, 4), "蓝光幅值");
    FMEG SR5 = new FMEG(new SRA(16, 4), "参考蓝光幅值");
    FMEG SR6 = new FMEG(new SRA(20, 4), "红光幅值");
    FMEG SR7 = new FMEG(new SRA(24, 4), "参考红光幅值");
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
    
}
