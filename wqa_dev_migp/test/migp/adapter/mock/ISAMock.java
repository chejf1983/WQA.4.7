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
public class ISAMock extends DevMock {
    
    // <editor-fold defaultstate="collapsed" desc="VPA"> 
    FMEG[] VDRANGE_MIN = new FMEG[]{new FMEG(new VPA(0x02, 4), "主参数1量程下限"), new FMEG(new VPA(0x0A, 4), "主参数2量程下限"),
        new FMEG(new VPA(0x12, 4), "主参数3量程下限"), new FMEG(new VPA(0x1A, 4), "主参数4量程下限")};
    FMEG[] VDRANGE_MAX = new FMEG[]{new FMEG(new VPA(0x06, 4), "主参数1量程上限"), new FMEG(new VPA(0x0E, 4), "主参数2量程上限"),
        new FMEG(new VPA(0x16, 4), "主参数3量程上限"), new FMEG(new VPA(0x1E, 4), "主参数4量程上限")};
    FMEG VTRANGE_MIN = new FMEG(new VPA(0x22, 4), "温度参数量程下限");
    FMEG VTRANGE_MAX = new FMEG(new VPA(0x26, 4), "温度参数量程上限");
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="MDA"> 
    public IMEG MALARM = new IMEG(new MDA(0x00, 2), "报警码");  // AMMO_I | AMMO_II | NITRA_I | NITRA_II | AMMO_NITRA_I | AMMO_NITRA_II | AMMO_NITRA_III | CHLORINE
    public FMEG MPAR1 = new FMEG(new MDA(0x02, 4), "参数1");    //   pH       pH        pH         pH           pH              pH               pH          pH
    public FMEG MPAR2 = new FMEG(new MDA(0x06, 4), "参数2");    //  氨氮      氨氮      硝氮       硝氮         氨氮             氨氮             氨氮       氯离子
    public FMEG MPAR3 = new FMEG(new MDA(0x0A, 4), "参数3");    //   --      钾离子     --       氯离子        硝氮             硝氮             硝氮         --     
    public FMEG MPAR4 = new FMEG(new MDA(0x0E, 4), "参数4");    //   --       --        --         --           --             钾离子           氯离子        --     
    public FMEG MPAR5 = new FMEG(new MDA(0x12, 4), "温度参数");
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="SRA"> 
    public FMEG SR1 = new FMEG(new SRA(0x00, 4), "参数1原始信号");
    public FMEG SR2 = new FMEG(new SRA(0x04, 4), "参数2原始信号");
    public IMEG SR3 = new IMEG(new SRA(0x0C, 2), "参数3原始信号");
    public IMEG SR4 = new IMEG(new SRA(0x0E, 2), "参数4原始信号");
    public FMEG SR5 = new FMEG(new SRA(0x10, 4), "温度原始信号");

    public IMEG SCLNUM = new IMEG(new SRA(0x18, 2), "参数顺序", 0, 3);//参数1，2，3，4
    public FMEG[] SCLODATA = new FMEG[]{new FMEG(new SRA(0x1A, 4), "第1点原始信号"), new FMEG(new SRA(0x22, 4), "第2点原始信号")}; //R/W
    public FMEG[] SCLTDATA = new FMEG[]{new FMEG(new SRA(0x1E, 4), "第1点定标数据"), new FMEG(new SRA(0x26, 4), "第2点定标数据")}; //R/W
    IMEG CLSTART = new IMEG(new SRA(0x2A, 2), "启动定标", 1, 2); //R/W
    FMEG CLTEMPER = new FMEG(new SRA(0x2C, 4), "温度定标参数");    //R/W
    IMEG CLTEMPERSTART = new IMEG(new SRA(0x30, 2), "温度启动定标");//R/W
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    public DMEG[] NAS = new DMEG[]{new DMEG(new NVPA(0, 8), "系数A"), new DMEG(new NVPA(24, 8), "系数A"), new DMEG(new NVPA(48, 8), "系数A"), new DMEG(new NVPA(72, 8), "系数A")};
    public DMEG[] NES = new DMEG[]{new DMEG(new NVPA(8, 8), "系数E"), new DMEG(new NVPA(32, 8), "系数E"), new DMEG(new NVPA(56, 8), "系数E"), new DMEG(new NVPA(80, 8), "系数E")};
    public DMEG[] NFS = new DMEG[]{new DMEG(new NVPA(16, 8), "系数F"), new DMEG(new NVPA(40, 8), "系数F"), new DMEG(new NVPA(64, 8), "系数F"), new DMEG(new NVPA(88, 8), "系数F")};
    public FMEG NTEMP_CAL = new FMEG(new NVPA(96, 4), "温度定标系数");
    IMEG[] NPAR_COM_ENABLE = new IMEG[]{new IMEG(new NVPA(100, 1), "补偿1使能"), new IMEG(new NVPA(101, 1), "补偿2使能"),
        new IMEG(new NVPA(102, 1), "补偿3使能"), new IMEG(new NVPA(103, 1), "补偿4使能")};
    FMEG[] NPAR_COM = new FMEG[]{new FMEG(new NVPA(104, 4), "补偿1"), new FMEG(new NVPA(108, 4), "补偿2"),
        new FMEG(new NVPA(112, 4), "补偿3"), new FMEG(new NVPA(116, 4), "补偿4")};
    public FMEG NK_COM = new FMEG(new NVPA(120, 4), "K离子补偿参数");
    public FMEG NCL_COM = new FMEG(new NVPA(124, 4), "CL离子补偿参数");
    // </editor-fold> 

    public ISAMock() {
        super();
        this.client.RegisterREGS(VDRANGE_MIN[0], VDRANGE_MIN[1], VDRANGE_MIN[2], VDRANGE_MIN[3], VDRANGE_MAX[0], VDRANGE_MAX[1], VDRANGE_MAX[2], VDRANGE_MAX[3], VTRANGE_MIN, VTRANGE_MAX,
        MALARM, MPAR1, MPAR2, MPAR3, MPAR4, MPAR5,
        SR1, SR2, SR3, SR4, SR5,
        SCLNUM, SCLODATA[0], SCLODATA[1], SCLTDATA[0], SCLTDATA[1], CLSTART, CLTEMPER, CLTEMPERSTART,
        NTEMP_CAL, NK_COM, NCL_COM, 
        NAS[0], NAS[1], NAS[2], NAS[3],
        NES[0], NES[1], NES[2], NES[3],
        NFS[0], NFS[1], NFS[2], NFS[3],
        NPAR_COM_ENABLE[0], NPAR_COM_ENABLE[1], NPAR_COM_ENABLE[2], NPAR_COM_ENABLE[3],
        NPAR_COM[0], NPAR_COM[1], NPAR_COM[2], NPAR_COM[3]);
    }

    public MEG[] list = new MEG[]{VDRANGE_MIN[0], VDRANGE_MIN[1], VDRANGE_MIN[2], VDRANGE_MIN[3], VDRANGE_MAX[0], VDRANGE_MAX[1], VDRANGE_MAX[2], VDRANGE_MAX[3], VTRANGE_MIN, VTRANGE_MAX,
        MALARM, MPAR1, MPAR2, MPAR3, MPAR4, MPAR5,
        SR1, SR2, SR3, SR4, SR5,
        SCLNUM, SCLODATA[0], SCLODATA[1], SCLTDATA[0], SCLTDATA[1], CLSTART, CLTEMPER, CLTEMPERSTART,
        NTEMP_CAL, NK_COM, NCL_COM, 
        NAS[0], NAS[1], NAS[2], NAS[3],
        NES[0], NES[1], NES[2], NES[3],
        NFS[0], NFS[1], NFS[2], NFS[3],
        NPAR_COM_ENABLE[0], NPAR_COM_ENABLE[1], NPAR_COM_ENABLE[2], NPAR_COM_ENABLE[3],
        NPAR_COM[0], NPAR_COM[1], NPAR_COM[2], NPAR_COM[3]};

    @Override
    public void ResetREGS() throws Exception {
        super.ResetREGS();

        for (int i = 0; i < list.length; i++) {
            list[i].SetValue(list[i].Convert(2 + ""));
        }

        VDEVTYPE.SetValue(0x0311);
        ///////////////////////////////////////////////////////////        
        MALARM.SetValue(0);
        WriteREGS();
    }

}
