/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.mock;

import base.migp.mem.*;
import base.migp.reg.*;

/**
 *
 * @author chejf
 */
public class ORPMock extends DevMock {

    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    public FMEG NA = new FMEG(new NVPA(0, 4), "ORP系数A");
    public FMEG NB = new FMEG(new NVPA(4, 4), "ORP系数B");
    public FMEG NTEMP_CAL = new FMEG(new NVPA(96, 4), "温度系数");
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="VPA"> 
    public FMEG VDRANGE_MIN = new FMEG(new VPA(0x02, 4), "主参数量程下限");
    public FMEG VDRANGE_MAX = new FMEG(new VPA(0x06, 4), "主参数量程上限");
    public FMEG VTRANGE_MIN = new FMEG(new VPA(0x22, 4), "温度参数量程下限");
    public FMEG VTRANGE_MAX = new FMEG(new VPA(0x26, 4), "温度参数量程上限");
    public IMEG VVATOKEN = new IMEG(new VPA(0x14, 2), "内部版本标志");
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="MDA"> 
    public IMEG MALARM = new IMEG(new MDA(0x00, 2), "报警码");  //  PH   |    DO   |     EC_I    |    EC_II    |  ORP 
    public FMEG MPAR1 = new FMEG(new MDA(0x02, 4), "参数1");    //  PH     溶氧mg/L   电导率us/cm   电导率us/cm   ORPmv       
    public FMEG MPAR2 = new FMEG(new MDA(0x06, 4), "参数2");    //  --      溶氧%       盐度ppt        盐度ppt     --         
    public FMEG MPAR3 = new FMEG(new MDA(0x0A, 4), "参数3");    //  温度     温度         温度          温度       温度    
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="SRA"> 
    public FMEG SR1 = new FMEG(new SRA(0x00, 4), "原始信号");
    public FMEG SR2 = new FMEG(new SRA(0x04, 4), "温度原始信号");
    public FMEG SR3 = new FMEG(new SRA(0x08, 4), "相位差");
    public FMEG SR4 = new FMEG(new SRA(12, 4), "蓝光幅值");
    public FMEG SR5 = new FMEG(new SRA(16, 4), "参考蓝光幅值");
    public FMEG SR6 = new FMEG(new SRA(20, 4), "红光幅值");
    public FMEG SR7 = new FMEG(new SRA(24, 4), "参考红光幅值");
    // </editor-fold> 

    public ORPMock() {
        super();
        this.client.RegisterREGS(NB, NA, NTEMP_CAL,
                VDRANGE_MIN, VDRANGE_MAX, VTRANGE_MIN, VTRANGE_MAX, VVATOKEN,
                MALARM, MPAR1, MPAR2, MPAR3,
                SR1, SR2, SR3, SR4, SR5, SR6, SR7);
    }

    @Override
    public void ResetREGS() throws Exception {
        super.ResetREGS();
        MEG[] list = new MEG[]{NB, NA, NTEMP_CAL,
            VDRANGE_MIN, VDRANGE_MAX, VTRANGE_MIN, VTRANGE_MAX, VVATOKEN,
            MALARM, MPAR1, MPAR2, MPAR3,
            SR1, SR2, SR3, SR4, SR5, SR6, SR7};
        for (int i = 0; i < list.length; i++) {
            
            list[i].SetValue(list[i].Convert(i + ""));
        }
        VDEVTYPE.SetValue(0x0208);
        ///////////////////////////////////////////////////////////
        MALARM.SetValue(0);
        WriteREGS();
    }

}
