/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.OSA;

import base.migp.mem.NVPA;
import base.migp.mem.VPA;
import base.migp.reg.DMEG;
import base.migp.reg.FMEG;
import base.migp.reg.IMEG;
import base.migp.reg.MEG;
import base.pro.convert.NahonConvert;
import java.util.ArrayList;
import migp.adapter.ESA.ESADEV;
import static migp.adapter.factory.AbsDevice.DMask;
import migp.adapter.factory.TemperCalibrateCalculate;
import wqa.adapter.factory.CErrorTable;
import wqa.dev.data.CollectData;
import wqa.dev.data.LogNode;
import wqa.dev.data.SDevInfo;
import wqa.dev.intf.SConfigItem;

/**
 *
 * @author chejf
 */
public class OSA_FDOII extends ESADEV {

    public OSA_FDOII(SDevInfo devinfo) {
        super(devinfo);
    }

    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    FMEG NA = new FMEG(new NVPA(16, 4), "校准参数A");
    FMEG NB = new FMEG(new NVPA(20, 4), "校准参数B");
    FMEG NCLTEMPER = new FMEG(new NVPA(24, 4), "校准点温度");
    FMEG NPASCA = new FMEG(new NVPA(28, 4), "大气压力");
    FMEG NSALT = new FMEG(new NVPA(32, 4), "盐度");
    FMEG NTEMPER_COM = new FMEG(new NVPA(36, 4), "温度补偿系数");

    DMEG NPA = new DMEG(new NVPA(40, 8), "溶氧系数A");
    DMEG NPB = new DMEG(new NVPA(48, 8), "溶氧系数B");
    DMEG NPC = new DMEG(new NVPA(56, 8), "溶氧系数C");
    DMEG NPD = new DMEG(new NVPA(64, 8), "溶氧系数D");
    DMEG NPE = new DMEG(new NVPA(72, 8), "溶氧系数E");

    FMEG NPA2 = new FMEG(new NVPA(80, 4), "二次校准系数A");
    FMEG NPB2 = new FMEG(new NVPA(84, 4), "二次校准系数B");
    FMEG NDO100 = new FMEG(new NVPA(88, 4), "饱和相位");
    FMEG NDO0 = new FMEG(new NVPA(92, 4), "无氧相位");

    FMEG NPTEMPER = new FMEG(new NVPA(96, 4), "温度校准系数");
    IMEG NAVR = new IMEG(new NVPA(100, 2), "平均次数");

    // </editor-fold>
    @Override
    public void InitDevice() throws Exception {
        super.InitDevice(); //To change body of generated methods, choose Tools | Templates.
//        this.ReadMEG(VVATOKEN);
        this.ReadMEG(NA, NB, NCLTEMPER, NPASCA, NSALT, NTEMPER_COM, NPA, NPB, NPC, NPD, NPE, NPA2, NPB2, NDO100, NDO0, NPTEMPER, NAVR);
    }

    //获取设备类型
    @Override
    public boolean ReTestType() {
        IMEG VDEVTYPE = new IMEG(new VPA(0x00, 2), "设备类型");
        IMEG VDOATOKEN = new IMEG(new VPA(0x14, 2), "溶氧A版本标志");
//        VPA VPA00 = new VPA(0x00, 2);//设备类型地址
//        VPA VPA20 = new VPA(0x14, 2);//溶氧A版本标志
        try {
            this.base_drv.ReadMEG(1, 200, VDEVTYPE);
            //创建一个基础协议包
            if (VDEVTYPE.GetValue() == 0x110 || VDEVTYPE.GetValue() == 0x210) {
                base_drv.ReadMEG(1, 200, VDOATOKEN);
                if (VDOATOKEN.GetValue() > 0) {
                    VDEVTYPE.SetValue(VDEVTYPE.GetValue() + 0xA000);
                }
            }
            return VDEVTYPE.GetValue() == this.GetDevInfo().dev_type;
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="配置接口"> 
    @Override
    public ArrayList<SConfigItem> GetConfigList() {
        ArrayList<SConfigItem> item = super.GetConfigList(); //To change body of generated methods, choose Tools | Templates.
        item.add(SConfigItem.CreateRWItem(NPASCA.toString(), NPASCA.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NSALT.toString(), NSALT.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NTEMPER_COM.toString(), NTEMPER_COM.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NAVR.toString(), NAVR.GetValue().toString(), ""));

        return item;
    }

    @Override
    public void SetConfigList(ArrayList<SConfigItem> list) throws Exception {
        super.SetConfigList(list);
        MEG[] reglist = new MEG[]{NPASCA, NSALT, NTEMPER_COM, NAVR};
        for (SConfigItem item : list) {
            for (MEG mem : reglist) {
                if (item.IsKey(mem.toString())) {
                    this.SetConfigREG(mem, item.GetValue());
                    break;
                }
            }
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="系数"> 
    @Override
    public ArrayList<SConfigItem> GetCalParList() {
        ArrayList<SConfigItem> item = super.GetCalParList(); //To change body of generated methods, choose Tools | Templates.
        item.add(SConfigItem.CreateRWItem(NPTEMPER.toString(), this.NPTEMPER.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NA.toString(), this.NA.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NB.toString(), this.NB.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NCLTEMPER.toString(), this.NCLTEMPER.GetValue().toString(), ""));

        item.add(SConfigItem.CreateInfoItem(""));
        item.add(SConfigItem.CreateInfoItem("溶解氧定标系数"));
        item.add(SConfigItem.CreateRWItem(NPA.toString(), NPA.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NPB.toString(), NPB.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NPC.toString(), NPC.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NPD.toString(), NPD.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NPE.toString(), NPE.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NPA2.toString(), NPA2.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NPB2.toString(), NPB2.GetValue().toString(), ""));

        item.add(SConfigItem.CreateRWItem(NDO100.toString(), NDO100.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NDO0.toString(), NDO0.GetValue().toString(), ""));

        return item;
    }

    @Override
    public void SetCalParList(ArrayList<SConfigItem> list) throws Exception {
        super.SetCalParList(list);
        MEG[] reglist = new MEG[]{NA, NB, NCLTEMPER, NPA, NPB, NPC, NPD, NPE, NPA2, NPB2, NPTEMPER, NDO100, NDO0};
        for (SConfigItem item : list) {
            for (MEG mem : reglist) {
                if (item.IsKey(mem.toString())) {
                    this.SetConfigREG(mem, item.GetValue());
                    break;
                }
            }
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="采集接口"> 
    @Override
    public CollectData CollectData() throws Exception {
        CollectData disdata = this.BuildDisplayData();
        this.ReadMEG(MALARM, MPAR1, MPAR2, MPAR3);
        //原始数据
        this.ReadMEG(SR1, SR2, SR3, SR4, SR5, SR6, SR7);

        disdata.datas[0].mainData = NahonConvert.TimData(MPAR1.GetValue(), 3);   //DO值
        disdata.datas[0].range_info = this.GetMainRangeString(); //量程
        disdata.datas[1].mainData = NahonConvert.TimData(MPAR2.GetValue(), 2);  //DO百分比
        disdata.datas[2].mainData = NahonConvert.TimData(SR1.GetValue(), 3);     //DO原始值

        disdata.datas[3].mainData = NahonConvert.TimData(MPAR3.GetValue(), 2);   //温度值
        disdata.datas[3].range_info = this.GetTemperRangeString(); //量程
        disdata.datas[4].mainData = NahonConvert.TimData(SR2.GetValue(), 2);     //温度原始值

        disdata.datas[5].mainData = NahonConvert.TimData(SR3.GetValue(), 4); //相位差
        disdata.datas[6].mainData = NahonConvert.TimData(SR4.GetValue(), 4); //蓝光相位
        disdata.datas[7].mainData = NahonConvert.TimData(SR5.GetValue(), 4); //参考蓝光相位
        disdata.datas[8].mainData = NahonConvert.TimData(SR6.GetValue(), 4); //红光相位
        disdata.datas[9].mainData = NahonConvert.TimData(SR7.GetValue(), 4); //参考红光相位

        disdata.alarm = MALARM.GetValue(); //报警信息
        String info = CErrorTable.GetInstance().GetErrorString(((this.GetDevInfo().dev_type & DMask) << 8) | disdata.alarm);
        disdata.alram_info = info == null ? "" : info;
        return disdata;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标接口"> 
//float fZeroOxygen:  零点相位标准信号
//float fSaturatedOxygen:  饱和氧相位标准信号
//float fOriginalSaturated:  饱和氧相位原始信号
    private void ESADOOnePointCalib(float fZeroOxygen, float fSaturatedOxygen,
            float fOriginalSaturated) throws Exception {
        float fX1, fX2, fY1, fY2;

        fX1 = fZeroOxygen;
        fX2 = fOriginalSaturated;
        fY1 = fZeroOxygen;
        fY2 = fSaturatedOxygen;

        float A = (fY2 - fY1) / (fX2 - fX1);
        float B = fY2 - (A * fX2);

        this.SetConfigREG(NA, A + "");
        this.SetConfigREG(NB, B + "");
    }

//    float fZeroOxygen:  零点相位标准信号
//float fSaturatedOxygen:  饱和氧相位标准信号
//float fOriginalZero:  零点相位原始信号
//float fOriginalSaturated:  饱和氧相位原始信号
    private void ESADOTwoPointCalib(
            float fZeroOxygen, float fSaturatedOxygen,
            float fOriginalZero, float fOriginalSaturated) throws Exception {

        float fX1, fX2, fY1, fY2;
        double dTemp = 0;

        if (fOriginalZero < fOriginalSaturated) {
            fX2 = fOriginalZero;
            fX1 = fOriginalSaturated;
        } else {
            fX2 = fOriginalSaturated;
            fX1 = fOriginalZero;
        }

        fY1 = fZeroOxygen;
        fY2 = fSaturatedOxygen;

        float A = (fY2 - fY1) / (fX2 - fX1);
        float B = fY2 - (A * fX2);

        this.SetConfigREG(NA, A + "");
        this.SetConfigREG(NB, B + "");
    }

    @Override
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception {
        LogNode ret = LogNode.CALOK();
        if (type.contentEquals("温度")) {
            //温度定标
            float cal_par = new TemperCalibrateCalculate().Calculate(testdata, oradata);
            this.SetConfigREG(NPTEMPER, cal_par + "");
            ret.children.add(new LogNode(NPTEMPER.toString(), this.NPTEMPER.GetValue()));
        } else {
            //参数定标
            if (oradata.length == 1) {
                this.ESADOOnePointCalib((float) this.NDO0.GetValue(), (float) this.NDO100.GetValue(), oradata[0]);
            } else {
                //界面输入是 {饱和氧,无氧}的顺序,需要交换顺序
                this.ESADOTwoPointCalib((float) this.NDO0.GetValue(), (float) this.NDO100.GetValue(), oradata[1], oradata[0]);
            }
            ret.children.add(new LogNode(this.NCLTEMPER.toString(), this.NCLTEMPER.GetValue()));
            ret.children.add(new LogNode(this.NA.toString(), this.NA.GetValue()));
            ret.children.add(new LogNode(this.NB.toString(), this.NB.GetValue()));
        }
        return ret;
    }
    // </editor-fold> 
}
