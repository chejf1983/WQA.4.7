/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.mock;

import base.migp.mem.MDA;
import base.migp.mem.NVPA;
import base.migp.mem.SRA;
import base.migp.mem.VPA;
import base.migp.reg.DMEG;
import base.migp.reg.FMEG;
import base.migp.reg.IMEG;
import base.migp.reg.MEG;

/**
 *
 * @author chejf
 */
public class OSAMock extends DevMock {

    // <editor-fold defaultstate="collapsed" desc="VPA"> 
    public FMEG[] VDRANGE_MIN = new FMEG[]{new FMEG(new VPA(0x02, 4), "主参数1量程下限"), new FMEG(new VPA(0x0A, 4), "主参数2量程下限"),
        new FMEG(new VPA(0x12, 4), "主参数3量程下限"), new FMEG(new VPA(0x1A, 4), "主参数4量程下限")};
    FMEG[] VDRANGE_MAX = new FMEG[]{new FMEG(new VPA(0x06, 4), "主参数1量程上限"), new FMEG(new VPA(0x0E, 4), "主参数2量程上限"),
        new FMEG(new VPA(0x16, 4), "主参数3量程上限"), new FMEG(new VPA(0x1E, 4), "主参数4量程上限")};
    FMEG VTRANGE_MIN = new FMEG(new VPA(0x22, 4), "温度参数量程下限");
    FMEG VTRANGE_MAX = new FMEG(new VPA(0x26, 4), "温度参数量程上限");
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="MDA"> 
    public IMEG MALARM = new IMEG(new MDA(0x00, 2), "报警码");  // OSA_TURB | OSA_TSS | OSA_SS | OSA_CHLA | OSA_CYANO_I | OSA_OIL_I | OSA_MLSS | OSA_FDO
    public FMEG MPAR1 = new FMEG(new MDA(0x02, 4), "参数1");    //   浊度      悬浮物    悬浮物     叶绿素      蓝绿藻        水中油     污泥浓度   溶解氧mg/L
    public FMEG MPAR2 = new FMEG(new MDA(0x06, 4), "参数2");    //   温度       温度      温度      温度         温度         温度        温度     溶解氧%
    public FMEG MPAR3 = new FMEG(new MDA(0x0A, 4), "参数3");    //    --         --        --       --           --           --          --     温度
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="SRA"> 
    public FMEG SR1 = new FMEG(new SRA(0x00, 4), "2.5V基准电压");
    public FMEG SR2 = new FMEG(new SRA(0x04, 4), "4.096V基准电压");
    public IMEG SR3 = new IMEG(new SRA(0x0C, 2), "原始光强信号(高电平)");
    public IMEG SR4 = new IMEG(new SRA(0x0E, 2), "原始光强信号(低电平)");
    public FMEG SR5 = new FMEG(new SRA(0x10, 4), "温度原始信号");
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    public IMEG NRANGE = new IMEG(new NVPA(0, 2), "量程档位", 0, 3);
    public IMEG NAVR = new IMEG(new NVPA(2, 2), "平均次数", 1, 100);

    public IMEG NCMODE = new IMEG(new NVPA(4, 2), "清扫模式", 0, 2);
    public IMEG NCTIME = new IMEG(new NVPA(6, 2), "清扫次数", 1, 100);
    public IMEG NCINTERVAL = new IMEG(new NVPA(8, 2), "清扫间隔(分钟)", 1, 24 * 60);
    public IMEG NCBRUSH = new IMEG(new NVPA(10, 2), "清扫刷偏移量", 0, 1000);

    public FMEG[] NCLTEMPER = new FMEG[]{new FMEG(new NVPA(12, 4), "定标温度1"), new FMEG(new NVPA(40, 4), "定标温度2"), new FMEG(new NVPA(68, 4), "定标温度3"), new FMEG(new NVPA(96, 4), "定标温度4")};
    public DMEG[] NCLPARA = new DMEG[]{new DMEG(new NVPA(16, 8), "定标系数A1"), new DMEG(new NVPA(44, 8), "定标系数A2"), new DMEG(new NVPA(72, 8), "定标系数A3"), new DMEG(new NVPA(100, 8), "定标系数A4")};
    public DMEG[] NCLPARB = new DMEG[]{new DMEG(new NVPA(24, 8), "定标系数B1"), new DMEG(new NVPA(52, 8), "定标系数B2"), new DMEG(new NVPA(80, 8), "定标系数B3"), new DMEG(new NVPA(108, 8), "定标系数B4")};
    public DMEG[] NCLPARC = new DMEG[]{new DMEG(new NVPA(32, 8), "定标系数C1"), new DMEG(new NVPA(60, 8), "定标系数C2"), new DMEG(new NVPA(88, 8), "定标系数C3"), new DMEG(new NVPA(116, 8), "定标系数C4")};

    public FMEG NTEMPER_COMP = new FMEG(new NVPA(124, 4), "温度补偿系数");
    public FMEG NTEMPER_PAR = new FMEG(new NVPA(128, 4), "温度定标系数");

    public IMEG[] NAMPLIFY = new IMEG[]{new IMEG(new NVPA(133, 2), "放大倍数1"), new IMEG(new NVPA(135, 2), "放大倍数2"),
        new IMEG(new NVPA(137, 2), "放大倍数3"), new IMEG(new NVPA(139, 2), "放大倍数4")};

    public IMEG NRANGE_NUM = new IMEG(new NVPA(132, 1), "量程数量", 1, 4);
    public FMEG[] NRANGE_MAX = new FMEG[]{new FMEG(new NVPA(141, 4), "主参数1量程上限"), new FMEG(new NVPA(145, 4), "主参数2量程上限"),
        new FMEG(new NVPA(149, 4), "主参数3量程上限"), new FMEG(new NVPA(153, 4), "主参数4量程上限")};
    // </editor-fold> 

    public OSAMock() {
        super();
        this.client.RegisterREGS(VDRANGE_MIN[0], VDRANGE_MIN[1], VDRANGE_MIN[2], VDRANGE_MIN[3],
                VDRANGE_MAX[0], VDRANGE_MAX[1], VDRANGE_MAX[2], VDRANGE_MAX[3], VTRANGE_MIN, VTRANGE_MAX,
        MALARM, MPAR1, MPAR2, MPAR3,
        SR1, SR2, SR3, SR4, SR5,
        NRANGE, NAVR, NCMODE, NCTIME, NCINTERVAL, NCBRUSH,
        NCLTEMPER[0], NCLTEMPER[1], NCLTEMPER[2], NCLTEMPER[3],
        NCLPARA[0], NCLPARA[1], NCLPARA[2], NCLPARA[3],
        NCLPARB[0], NCLPARB[1], NCLPARB[2], NCLPARB[3],
        NCLPARC[0], NCLPARC[1], NCLPARC[2], NCLPARC[3],
        NAMPLIFY[0], NAMPLIFY[1], NAMPLIFY[2], NAMPLIFY[3],
        NRANGE_MAX[0], NRANGE_MAX[1], NRANGE_MAX[2], NRANGE_MAX[3],
        NTEMPER_COMP, NTEMPER_PAR, NRANGE_NUM);
    }

    public MEG[] list = new MEG[]{VDRANGE_MIN[0], VDRANGE_MIN[1], VDRANGE_MIN[2], VDRANGE_MIN[3], 
        VDRANGE_MAX[0], VDRANGE_MAX[1], VDRANGE_MAX[2], VDRANGE_MAX[3], VTRANGE_MIN, VTRANGE_MAX,
        MALARM, MPAR1, MPAR2, MPAR3,
        SR1, SR2, SR3, SR4, SR5,
        /*NRANGE,*/ NAVR, /*NCMODE,*/ NCTIME, NCINTERVAL, NCBRUSH,
        NCLTEMPER[0], NCLTEMPER[1], NCLTEMPER[2], NCLTEMPER[3],
        NCLPARA[0], NCLPARA[1], NCLPARA[2], NCLPARA[3],
        NCLPARB[0], NCLPARB[1], NCLPARB[2], NCLPARB[3],
        NCLPARC[0], NCLPARC[1], NCLPARC[2], NCLPARC[3],
        NAMPLIFY[0], NAMPLIFY[1], NAMPLIFY[2], NAMPLIFY[3],
        NRANGE_MAX[0], NRANGE_MAX[1], NRANGE_MAX[2], NRANGE_MAX[3],
        NTEMPER_COMP, NTEMPER_PAR//, NRANGE_NUM
    };

    @Override
    public void ResetREGS() throws Exception {
        super.ResetREGS();

        for (int i = 0; i < list.length; i++) {
            list[i].SetValue(list[i].Convert(i + ""));
        }

        VDEVTYPE.SetValue(0x010E);
        NRANGE.SetValue(1);
        NCMODE.SetValue(1);
        NRANGE_NUM.SetValue(3);
        byte[] mem = new byte[]{0x00,0x68,(byte)0xC7,(byte)0xEE};
        SR3.LoadBytes(mem, 0);
        SR4.LoadBytes(mem, 2);
        ///////////////////////////////////////////////////////////        
        MALARM.SetValue(0);
        WriteREGS();
    }

}
