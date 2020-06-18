/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ISA;

import base.migp.mem.*;
import base.migp.reg.*;
import migp.adapter.factory.AbsDevice;
import base.pro.convert.NahonConvert;
import java.util.ArrayList;
import java.util.HashMap;
import static migp.adapter.factory.AbsDevice.DMask;
import wqa.dev.data.*;
import wqa.adapter.factory.CErrorTable;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class ISA_X extends AbsDevice {

    public ISA_X(SDevInfo devinfo) {
        super(devinfo);

        this.enablelist.put(0x0300, new String[]{"K+对氨离子"});
        this.enablelist.put(0x0301, new String[]{"K+对氨离子", "氨离子对K+"});
        this.enablelist.put(0x0308, new String[]{"Cl-对硝酸根离子"});
        this.enablelist.put(0x0309, new String[]{"Cl-对硝酸根离子", "硝酸根离子对Cl-"});
        this.enablelist.put(0x0310, new String[]{"K+对氨离子", "Cl-对硝酸根离子"});
        this.enablelist.put(0x0311, new String[]{"K+对氨离子", "Cl-对硝酸根离子", "氨离子对K+"});
        this.enablelist.put(0x0312, new String[]{"K+对氨离子", "Cl-对硝酸根离子", "硝酸根离子对Cl-"});
        this.enablelist.put(0x0320, new String[]{});

        this.com_list.put(0x0300, new String[]{"K+对氨离子系数", "氨离子对K+系数"});
        this.com_list.put(0x0301, new String[]{"K+对氨离子系数", "氨离子对K+系数"});
        this.com_list.put(0x0308, new String[]{"Cl-对硝酸根离子系数", "硝酸根离子对Cl-系数"});
        this.com_list.put(0x0309, new String[]{"Cl-对硝酸根离子系数", "硝酸根离子对Cl-系数"});
        this.com_list.put(0x0310, new String[]{"K+对氨离子系数", "氨离子对K+系数", "Cl-对硝酸根离子系数", "硝酸根离子对Cl-系数"});
        this.com_list.put(0x0311, new String[]{"K+对氨离子系数", "氨离子对K+系数", "Cl-对硝酸根离子系数", "硝酸根离子对Cl-系数"});
        this.com_list.put(0x0312, new String[]{"K+对氨离子系数", "氨离子对K+系数", "Cl-对硝酸根离子系数", "硝酸根离子对Cl-系数"});
        this.com_list.put(0x0320, new String[]{});
    }

    // <editor-fold defaultstate="collapsed" desc="VPA"> 
    FMEG[] VDRANGE_MIN = new FMEG[]{new FMEG(new VPA(0x02, 4), "主参数1量程下限"), new FMEG(new VPA(0x0A, 4), "主参数2量程下限"),
        new FMEG(new VPA(0x12, 4), "主参数3量程下限"), new FMEG(new VPA(0x1A, 4), "主参数4量程下限")};
    FMEG[] VDRANGE_MAX = new FMEG[]{new FMEG(new VPA(0x06, 4), "主参数1量程上限"), new FMEG(new VPA(0x0E, 4), "主参数2量程上限"),
        new FMEG(new VPA(0x16, 4), "主参数3量程上限"), new FMEG(new VPA(0x1E, 4), "主参数4量程上限")};
    FMEG VTRANGE_MIN = new FMEG(new VPA(0x22, 4), "温度参数量程下限");
    FMEG VTRANGE_MAX = new FMEG(new VPA(0x26, 4), "温度参数量程上限");
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="MDA"> 
    IMEG MALARM = new IMEG(new MDA(0x00, 2), "报警码");  // AMMO_I | AMMO_II | NITRA_I | NITRA_II | AMMO_NITRA_I | AMMO_NITRA_II | AMMO_NITRA_III | CHLORINE
    FMEG MPAR1 = new FMEG(new MDA(0x02, 4), "参数1");    //   pH       pH        pH         pH           pH              pH               pH          pH
    FMEG MPAR2 = new FMEG(new MDA(0x06, 4), "参数2");    //  氨氮      氨氮      硝氮       硝氮         氨氮             氨氮             氨氮       氯离子
    FMEG MPAR3 = new FMEG(new MDA(0x0A, 4), "参数3");    //   --      钾离子     --       氯离子        硝氮             硝氮             硝氮         --     
    FMEG MPAR4 = new FMEG(new MDA(0x0E, 4), "参数4");    //   --       --        --         --           --             钾离子           氯离子        --     
    FMEG MPAR5 = new FMEG(new MDA(0x12, 4), "温度参数");
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="SRA"> 
    FMEG SR1 = new FMEG(new SRA(0x00, 4), "参数1原始信号");
    FMEG SR2 = new FMEG(new SRA(0x04, 4), "参数2原始信号");
    IMEG SR3 = new IMEG(new SRA(0x0C, 2), "参数3原始信号");
    IMEG SR4 = new IMEG(new SRA(0x0E, 2), "参数4原始信号");
    FMEG SR5 = new FMEG(new SRA(0x10, 4), "温度原始信号");

    IMEG SCLNUM = new IMEG(new SRA(0x18, 2), "温度原始信号", 1, 4);//参数1，2，3，4
    FMEG[] SCLODATA = new FMEG[]{new FMEG(new SRA(0x1A, 4), "第1点原始信号"), new FMEG(new SRA(0x22, 4), "第2点原始信号")}; //R/W
    FMEG[] SCLTDATA = new FMEG[]{new FMEG(new SRA(0x1E, 4), "第1点定标数据"), new FMEG(new SRA(0x26, 4), "第2点定标数据")}; //R/W
    IMEG CLSTART = new IMEG(new SRA(0x2A, 2), "启动定标", 1, 2); //R/W
    FMEG CLTEMPER = new FMEG(new SRA(0x2C, 4), "温度定标参数");    //R/W
    IMEG CLTEMPERSTART = new IMEG(new SRA(0x30, 2), "温度启动定标");//R/W
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    DMEG[] NAS = new DMEG[]{new DMEG(new NVPA(0, 8), "系数A"), new DMEG(new NVPA(24, 8), "系数A"), new DMEG(new NVPA(48, 8), "系数A"), new DMEG(new NVPA(72, 8), "系数A")};
    DMEG[] NES = new DMEG[]{new DMEG(new NVPA(8, 8), "系数E"), new DMEG(new NVPA(32, 8), "系数E"), new DMEG(new NVPA(56, 8), "系数E"), new DMEG(new NVPA(80, 8), "系数E")};
    DMEG[] NFS = new DMEG[]{new DMEG(new NVPA(16, 8), "系数F"), new DMEG(new NVPA(40, 8), "系数F"), new DMEG(new NVPA(64, 8), "系数F"), new DMEG(new NVPA(88, 8), "系数F")};
    FMEG NTEMP_CAL = new FMEG(new NVPA(96, 4), "温度定标系数");
    IMEG[] NPAR_COM_ENABLE = new IMEG[]{new IMEG(new NVPA(100, 1), "补偿1使能"), new IMEG(new NVPA(101, 1), "补偿2使能"),
        new IMEG(new NVPA(102, 1), "补偿3使能"), new IMEG(new NVPA(103, 1), "补偿4使能")};
    FMEG[] NPAR_COM = new FMEG[]{new FMEG(new NVPA(104, 4), "补偿1"), new FMEG(new NVPA(108, 4), "补偿2"),
        new FMEG(new NVPA(112, 4), "补偿3"), new FMEG(new NVPA(116, 4), "补偿4")};
    FMEG NK_COM = new FMEG(new NVPA(120, 4), "K离子补偿参数");
    FMEG NCL_COM = new FMEG(new NVPA(124, 4), "CL离子补偿参数");
    // </editor-fold> 

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice(); //To change body of generated methods, choose Tools | Templates.

        //VPA初始化
        this.ReadMEG(VDRANGE_MIN[0], VDRANGE_MIN[1], VDRANGE_MIN[2], VDRANGE_MIN[3],
                VDRANGE_MAX[0], VDRANGE_MAX[1], VDRANGE_MAX[2], VDRANGE_MAX[3], VTRANGE_MIN, VTRANGE_MAX);

        this.ReadMEG(NAS[0], NAS[1], NAS[2], NAS[3], NES[0], NES[1], NES[2], NES[3],
                NFS[0], NFS[1], NFS[2], NFS[3], NTEMP_CAL, NPAR_COM_ENABLE[0], NPAR_COM_ENABLE[1], NPAR_COM_ENABLE[2], NPAR_COM_ENABLE[3],
                NPAR_COM[0], NPAR_COM[1], NPAR_COM[2], NPAR_COM[3], NK_COM, NCL_COM);
    }

    // <editor-fold defaultstate="collapsed" desc="量程数据">
    //获取量程字符串描述（量程档位）
    private String get_range_string(int index) {
        if (index < 0 || index >= VDRANGE_MIN.length) {
            return "未知量程" + index;
        }
        return "(" + VDRANGE_MIN[index].GetValue() + "-" + VDRANGE_MAX[index].GetValue() + ")";
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="配置">
    private HashMap<Integer, String[]> enablelist = new HashMap();
    private HashMap<Integer, String[]> com_list = new HashMap();

    @Override
    public ArrayList<SConfigItem> GetConfigList() {
        ArrayList<SConfigItem> item = super.GetConfigList();
        item.add(SConfigItem.CreateRWItem(NK_COM.toString(), this.NK_COM.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(NCL_COM.toString(), this.NCL_COM.GetValue() + "", ""));
        return item;
    }

    @Override
    public void SetConfigList(ArrayList<SConfigItem> list) throws Exception {
        super.SetConfigList(list);

        for (SConfigItem item : list) {
            if (item.IsKey(NK_COM.toString())) {
                this.SetConfigREG(NK_COM, item.GetValue());
            }
            if (item.IsKey(NCL_COM.toString())) {
                this.SetConfigREG(NCL_COM, item.GetValue());
            }
        }

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="校准系数">   
    @Override
    public ArrayList<SConfigItem> GetCalParList() {
        ArrayList<SConfigItem> item = super.GetCalParList(); //To change body of generated methods, choose Tools | Templates.        
        String[] datanames = this.GetDataNames();
        item.add(SConfigItem.CreateRWItem(NTEMP_CAL.toString(), this.NTEMP_CAL.GetValue() + "", ""));
        item.add(SConfigItem.CreateInfoItem(""));
        for (int i = 0; i < datanames.length - 1; i++) {
            item.add(SConfigItem.CreateInfoItem(datanames[i]));
            item.add(SConfigItem.CreateRWItem(datanames[i] + NAS[i].toString(), this.NAS[i].GetValue() + "", ""));
            item.add(SConfigItem.CreateRWItem(datanames[i] + NES[i].toString(), this.NES[i].GetValue() + "", ""));
            item.add(SConfigItem.CreateRWItem(datanames[i] + NFS[i].toString(), this.NFS[i].GetValue() + "", ""));
            item.add(SConfigItem.CreateInfoItem(""));
        }

        String[] enable_list = enablelist.get(this.GetDevInfo().dev_type);
        if (enable_list != null) {
            for (int i = 0; i < enable_list.length; i++) {
                item.add(SConfigItem.CreateRWItem(enable_list[i], this.NPAR_COM_ENABLE[i].GetValue().toString(), ""));
            }
        }
        String[] scom_list = com_list.get(this.GetDevInfo().dev_type);
        if (scom_list != null) {
            for (int i = 0; i < scom_list.length; i++) {
                item.add(SConfigItem.CreateRWItem(scom_list[i], this.NPAR_COM[i].GetValue().toString(), ""));
            }
        }
        return item;
    }

    @Override
    public void SetCalParList(ArrayList<SConfigItem> list) throws Exception {
        super.SetCalParList(list); //To change body of generated methods, choose Tools | Templates.

        for (SConfigItem item : list) {
            String[] datanames = this.GetDataNames();
            for (int i = 0; i < datanames.length - 1; i++) {
                if (item.IsKey(datanames[i] + NAS[i].toString())) {
                    this.SetConfigREG(NAS[i], item.GetValue());
                }
                if (item.IsKey(datanames[i] + NES[i].toString())) {
                    this.SetConfigREG(NES[i], item.GetValue());
                }
                if (item.IsKey(datanames[i] + NFS[i].toString())) {
                    this.SetConfigREG(NFS[i], item.GetValue());
                }
            }
            String[] enable_list = enablelist.get(this.GetDevInfo().dev_type);
            if (enable_list != null) {
                for (int i = 0; i < enable_list.length; i++) {
                    if (item.IsKey(enable_list[i])) {
                        this.SetConfigREG(NPAR_COM_ENABLE[i], item.GetValue());
                    }
                }
            }
            String[] scom_list = com_list.get(this.GetDevInfo().dev_type);
            if (scom_list != null) {
                for (int i = 0; i < scom_list.length; i++) {
                    if (item.IsKey(scom_list[i])) {
                        this.SetConfigREG(NPAR_COM[i], item.GetValue());
                    }
                }
            }
            if (item.IsKey(NTEMP_CAL.toString())) {
                this.SetConfigREG(NTEMP_CAL, item.GetValue());
            }
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="采集接口"> 
    @Override
    public CollectData CollectData() throws Exception {
        CollectData disdata = this.BuildDisplayData();
        //读取数据
        this.ReadMEG(MALARM, MPAR1, MPAR2, MPAR3, MPAR4, MPAR5);
        //原始数据
        this.ReadMEG(SR1, SR2, SR3, SR4, SR5);

        disdata.datas[0].mainData = NahonConvert.TimData(MPAR1.GetValue(), 2);   //ph测量值
        disdata.datas[0].range_info = this.get_range_string(0);
        disdata.datas[1].mainData = NahonConvert.TimData(SR1.GetValue(), 2);     //ph_ora测量值

        disdata.datas[2].mainData = NahonConvert.TimData(MPAR2.GetValue(), 2);   //氨氮测量值
        disdata.datas[2].range_info = this.get_range_string(1);
        disdata.datas[3].mainData = NahonConvert.TimData(SR2.GetValue(), 2);     //氨氮_ora测量值

        if (disdata.datas.length > 6) {
            disdata.datas[4].mainData = NahonConvert.TimData(MPAR3.GetValue(), 2);   //
            disdata.datas[4].range_info = this.get_range_string(2);
            disdata.datas[5].mainData = NahonConvert.TimData(SR3.GetValue(), 2);     //
        }

        if (disdata.datas.length > 8) {
            disdata.datas[6].mainData = NahonConvert.TimData(MPAR4.GetValue(), 2);   //
            disdata.datas[6].range_info = this.get_range_string(3);
            disdata.datas[7].mainData = NahonConvert.TimData(SR4.GetValue(), 2);     //
        }

        //最后一位是温度，重新赋值
        disdata.datas[disdata.datas.length - 2].mainData = NahonConvert.TimData(MPAR5.GetValue(), 2);   //温度值
        disdata.datas[disdata.datas.length - 1].mainData = NahonConvert.TimData(SR5.GetValue(), 2); //温度原始值
        disdata.datas[disdata.datas.length - 2].range_info = "(" + this.VTRANGE_MIN.GetValue() + "-" + this.VTRANGE_MAX.GetValue() + ")"; //量程

        disdata.alarm = MALARM.GetValue(); //报警信息
        String info = CErrorTable.GetInstance().GetErrorString(((this.GetDevInfo().dev_type & DMask) << 8) | disdata.alarm);
        disdata.alram_info = info == null ? "" : info;
        return disdata;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标接口"> 
    @Override
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception {
        LogNode ret = LogNode.CALOK();
        if (type.contentEquals("温度")) {
            //温度定标
            this.cal_temp(testdata[0]);
            this.ReadMEG(NTEMP_CAL);
            ret.children.add(new LogNode(this.NTEMP_CAL.toString(), this.NTEMP_CAL.GetValue()));
        } else {
            int i = 0;
            for (; i < this.GetDataNames().length; i++) {
                if (type.contentEquals(this.GetDataNames()[i])) {
                    this.cal_data(i, oradata, testdata, oradata.length);
                    String[] datanames = this.GetDataNames();
                    this.ReadMEG(NAS[i], NES[i], NFS[i]);
                    ret.children.add(new LogNode(datanames[i] + NAS[i].toString(), this.NAS[i].GetValue()));
                    ret.children.add(new LogNode(datanames[i] + NES[i].toString(), this.NES[i].GetValue()));
                    ret.children.add(new LogNode(datanames[i] + NFS[i].toString(), this.NFS[i].GetValue()));
                }
            }
        }
        return ret;
    }

    private void cal_data(int index, float[] oradata, float[] testdata, int len) throws Exception {
        SCLNUM.SetValue(index + 1);
        SCLODATA[0].SetValue(oradata[0]);
        SCLTDATA[0].SetValue(testdata[0]);
        SCLODATA[1].SetValue(len > 1 ? oradata[1] : 0);
        SCLTDATA[1].SetValue(len > 1 ? testdata[1] : 0);
        CLSTART.SetValue(len);
        this.SetMEG(SCLNUM, SCLODATA[0], SCLODATA[1], SCLTDATA[0], SCLTDATA[1], CLSTART);
    }

    private void cal_temp(float testdata) throws Exception {
        CLTEMPER.SetValue(testdata);
        CLTEMPERSTART.SetValue(1);
        this.SetMEG(CLTEMPER, CLTEMPERSTART);
    }
    // </editor-fold> 

}
