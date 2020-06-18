/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.OSA;

import base.migp.mem.*;
import base.migp.reg.*;
import migp.adapter.factory.AbsDevice;
import wqa.dev.data.LogNode;
import wqa.dev.data.SDevInfo;

/**
 *
 * @author chejf
 */
public class MOSAII_X extends AbsDevice {

    public MOSAII_X(SDevInfo devinfo) {
        super(devinfo);
    }

    // <editor-fold defaultstate="collapsed" desc="寄存器"> 
    // <editor-fold defaultstate="collapsed" desc="VPA"> 
    FMEG[] VDRANGE_MIN = new FMEG[]{new FMEG(new VPA(0x02, 4), "主参数1量程下限"), new FMEG(new VPA(0x0A, 4), "主参数2量程下限"),
        new FMEG(new VPA(0x12, 4), "主参数3量程下限"), new FMEG(new VPA(0x1A, 4), "主参数4量程下限")};
    FMEG[] VDRANGE_MAX = new FMEG[]{new FMEG(new VPA(0x06, 4), "主参数1量程上限"), new FMEG(new VPA(0x0E, 4), "主参数2量程上限"),
        new FMEG(new VPA(0x16, 4), "主参数3量程上限"), new FMEG(new VPA(0x1E, 4), "主参数4量程上限")};
    FMEG[] VDRANGE_MIN2 = new FMEG[]{new FMEG(new VPA(0x22, 4), "附参数1量程下限"), new FMEG(new VPA(0x2A, 4), "附参数2量程下限"),
        new FMEG(new VPA(0x32, 4), "附参数3量程下限"), new FMEG(new VPA(0x3A, 4), "附参数4量程下限")};
    FMEG[] VDRANGE_MAX2 = new FMEG[]{new FMEG(new VPA(0x26, 4), "附参数1量程上限"), new FMEG(new VPA(0x2E, 4), "附参数2量程上限"),
        new FMEG(new VPA(0x36, 4), "附参数3量程上限"), new FMEG(new VPA(0x3E, 4), "附参数4量程上限")};
    FMEG VTRANGE_MIN = new FMEG(new VPA(0x42, 4), "温度参数量程下限");
    FMEG VTRANGE_MAX = new FMEG(new VPA(0x46, 4), "温度参数量程上限");
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="MDA"> 
    IMEG MALARM = new IMEG(new MDA(0x00, 2), "报警码");  // OSA_TURB | OSA_TSS | OSA_SS | OSA_CHLA | OSA_CYANO_I | OSA_OIL_I | OSA_MLSS | OSA_FDO    |MOSA_CHlA pro | **-Cyano /I/II | **-TA /I/II|
    FMEG MPAR1 = new FMEG(new MDA(0x02, 4), "参数1");    //   浊度      悬浮物    悬浮物     叶绿素      蓝绿藻        水中油     污泥浓度   溶解氧mg/L     叶绿素           蓝绿藻          叶绿素
    FMEG MPAR2 = new FMEG(new MDA(0x06, 4), "参数2");    //   温度       温度      温度      温度         温度         温度        温度     溶解氧%         浊度             浊度          蓝绿藻     
    FMEG MPAR3 = new FMEG(new MDA(0x0A, 4), "参数3");    //    --         --        --       --           --           --          --     温度            温度             温度           温度
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    IMEG NRANGE = new IMEG(new NVPA(0, 2), "参数1量程档位1", 0, 3);
    IMEG NAVR = new IMEG(new NVPA(2, 2), "参数1平均次数1", 1, 100);

    FMEG[] NCLTEMPER = new FMEG[]{new FMEG(new NVPA(12, 4), "定标温度1"), new FMEG(new NVPA(40, 4), "定标温度2"), new FMEG(new NVPA(68, 4), "定标温度3"), new FMEG(new NVPA(96, 4), "定标温度4")};
    DMEG[] NCLPARA = new DMEG[]{new DMEG(new NVPA(16, 8), "定标系数A1"), new DMEG(new NVPA(44, 8), "定标系数A2"), new DMEG(new NVPA(72, 8), "定标系数A3"), new DMEG(new NVPA(100, 8), "定标系数A4")};
    DMEG[] NCLPARB = new DMEG[]{new DMEG(new NVPA(24, 8), "定标系数B1"), new DMEG(new NVPA(52, 8), "定标系数B2"), new DMEG(new NVPA(80, 8), "定标系数B3"), new DMEG(new NVPA(108, 8), "定标系数B4")};
    DMEG[] NCLPARC = new DMEG[]{new DMEG(new NVPA(32, 8), "定标系数C1"), new DMEG(new NVPA(60, 8), "定标系数C2"), new DMEG(new NVPA(88, 8), "定标系数C3"), new DMEG(new NVPA(116, 8), "定标系数C4")};

    FMEG NTEMPER_COMP = new FMEG(new NVPA(124, 4), "参数1温度补偿系数");
    
    FMEG NTEMPER_PAR = new FMEG(new NVPA(128, 4), "温度定标系数");
    public static int AMPPAR = 4096;
    
    IMEG[] NAMPLIFY = new IMEG[]{new IMEG(new NVPA(133, 2), "放大倍数1"), new IMEG(new NVPA(135, 2), "放大倍数2"),
        new IMEG(new NVPA(137, 2), "放大倍数3"), new IMEG(new NVPA(139, 2), "放大倍数4")};

    IMEG NRANGE_NUM = new IMEG(new NVPA(132, 1), "量程数量", 0, 3);
    FMEG[] NRANGE_MAX = new FMEG[]{new FMEG(new NVPA(141, 4), "主参数1量程上限"), new FMEG(new NVPA(145, 4), "主参数2量程上限"),
        new FMEG(new NVPA(149, 4), "主参数3量程上限"), new FMEG(new NVPA(153, 4), "主参数4量程上限")};
    
   
    
    IMEG NRANGE2 = new IMEG(new NVPA(4, 2), "参数2量程档位", 0, 3);
    IMEG NAVR2 = new IMEG(new NVPA(6, 2), "参数2平均次数", 1, 100);

    FMEG[] NCLTEMPER2 = new FMEG[]{new FMEG(new NVPA(160, 4), "定标温度1"), new FMEG(new NVPA(188, 4), "定标温度2"), new FMEG(new NVPA(216, 4), "定标温度3"), new FMEG(new NVPA(244, 4), "定标温度4")};
    DMEG[] NCLPARA2 = new DMEG[]{new DMEG(new NVPA(164, 8), "定标系数A1"), new DMEG(new NVPA(192, 8), "定标系数A2"), new DMEG(new NVPA(220, 8), "定标系数A3"), new DMEG(new NVPA(248, 8), "定标系数A4")};
    DMEG[] NCLPARB2 = new DMEG[]{new DMEG(new NVPA(172, 8), "定标系数B1"), new DMEG(new NVPA(200, 8), "定标系数B2"), new DMEG(new NVPA(228, 8), "定标系数B3"), new DMEG(new NVPA(256, 8), "定标系数B4")};
    DMEG[] NCLPARC2 = new DMEG[]{new DMEG(new NVPA(180, 8), "定标系数C1"), new DMEG(new NVPA(208, 8), "定标系数C2"), new DMEG(new NVPA(236, 8), "定标系数C3"), new DMEG(new NVPA(264, 8), "定标系数C4")};

    FMEG NTEMPER_COMP2 = new FMEG(new NVPA(272, 4), "参数2温度补偿系数");

    IMEG[] NAMPLIFY2 = new IMEG[]{new IMEG(new NVPA(277, 2), "放大倍数1"), new IMEG(new NVPA(279, 2), "放大倍数2"),
        new IMEG(new NVPA(281, 2), "放大倍数3"), new IMEG(new NVPA(283, 2), "放大倍数4")};

    IMEG NRANGE_NUM2 = new IMEG(new NVPA(276, 1), "量程数量", 0, 3);
    FMEG[] NRANGE_MAX2 = new FMEG[]{new FMEG(new NVPA(285, 4), "主参数1量程上限"), new FMEG(new NVPA(289, 4), "主参数2量程上限"),
        new FMEG(new NVPA(293, 4), "主参数3量程上限"), new FMEG(new NVPA(297, 4), "主参数4量程上限")};
    // </editor-fold> 
    
    @Override
    public wqa.dev.data.CollectData CollectData() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
