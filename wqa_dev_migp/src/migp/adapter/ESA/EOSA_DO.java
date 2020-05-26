/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ESA;

import base.migp.mem.*;
import base.migp.reg.*;
import base.pro.convert.NahonConvert;
import java.util.ArrayList;
import migp.adapter.factory.TemperCalibrateCalculate;
import wqa.dev.data.*;
import wqa.adapter.factory.CErrorTable;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class EOSA_DO extends ESADEV {

    public EOSA_DO(IMAbstractIO io, byte addr) {
        super(io, addr);
    }
    
    // <editor-fold defaultstate="collapsed" desc="VPA"> 
    IMEG VVATOKEN = new IMEG(new VPA(0x14, 2), "内部版本标志");
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    FMEG NA = new FMEG(new NVPA(16, 4), "定标参数A");
    FMEG NB = new FMEG(new NVPA(20, 4), "定标参数B");
    FMEG NCLTEMPER = new FMEG(new NVPA(24, 4), "定标点温度");
    FMEG NPASCA = new FMEG(new NVPA(28, 4), "大气压力");
    FMEG NSALT = new FMEG(new NVPA(32, 4), "盐度");
    FMEG NTEMPER_COM = new FMEG(new NVPA(36, 4), "温度补偿系数");

    DMEG NPA = new DMEG(new NVPA(40, 8), "溶氧系数A");
    DMEG NPB = new DMEG(new NVPA(48, 8), "溶氧系数B");
    DMEG NPC = new DMEG(new NVPA(56, 8), "溶氧系数C");
    DMEG NPD = new DMEG(new NVPA(64, 8), "溶氧系数D");
    DMEG NPE = new DMEG(new NVPA(72, 8), "溶氧系数E");
    DMEG NPF = new DMEG(new NVPA(80, 8), "溶氧系数F");
    DMEG NPG = new DMEG(new NVPA(88, 8), "溶氧系数G");

//    FREG c_2A = new MIGP_MEM(new NVPA(80, 4), "二次系数A");
//    FREG c_2B = new MIGP_MEM(new NVPA(84, 4), "二次系数B");
    FMEG NPTEMPER = new FMEG(new NVPA(96, 4), "温度修正系数");
    IMEG NAVR = new IMEG(new NVPA(100, 2), "平均次数");

    FMEG NDO100 = new FMEG(new NVPA(128, 4), "饱和相位");
    FMEG NDO0 = new FMEG(new NVPA(132, 4), "无氧相位");

    // </editor-fold>
    
    @Override
    public void InitDevice() throws Exception {
        super.InitDevice(); //To change body of generated methods, choose Tools | Templates.
        this.ReadMEG(VVATOKEN);
        if (this.VVATOKEN.GetValue() > 0) {
            this.VDEVTYPE.SetValue(this.VDEVTYPE.GetValue() + 0xA000);
        }
        this.ReadMEG(NA, NB, NCLTEMPER, NPASCA, NSALT, NTEMPER_COM, NPA, NPB, NPC, NPD, NPE, NPF, NPG, NPTEMPER, NAVR, NDO100, NDO0);
    }

    // <editor-fold defaultstate="collapsed" desc="配置接口"> 
    @Override
    public ArrayList<SConfigItem> GetConfigList() {
        ArrayList<SConfigItem> item = super.GetConfigList(); //To change body of generated methods, choose Tools | Templates.
        item.add(SConfigItem.CreateRWItem(NPASCA.toString(), NPASCA.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NSALT.toString(), NSALT.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NTEMPER_COM.toString(), NTEMPER_COM.GetValue().toString(), ""));

        if (this.VVATOKEN.GetValue() > 0) {
            item.add(SConfigItem.CreateRWItem(NAVR.toString(), NAVR.GetValue().toString(), ""));
        }
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

        if (this.VVATOKEN.GetValue() > 0) {
            item.add(SConfigItem.CreateInfoItem(""));
            item.add(SConfigItem.CreateInfoItem("溶解氧定标系数"));
            item.add(SConfigItem.CreateRWItem(NPA.toString(), NPA.GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(NPB.toString(), NPB.GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(NPC.toString(), NPC.GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(NPD.toString(), NPD.GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(NPE.toString(), NPE.GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(NPF.toString(), NPF.GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(NPG.toString(), NPG.GetValue().toString(), ""));

//            item.add(SConfigItem.CreateRWItem(c_2A.toString(), c_2A.GetValue().toString(), ""));
//            item.add(SConfigItem.CreateRWItem(c_2B.toString(), c_2B.GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(NDO100.toString(), NDO100.GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(NDO0.toString(), NDO0.GetValue().toString(), ""));
        }
        return item;
    }

    @Override
    public void SetCalParList(ArrayList<SConfigItem> list) throws Exception {
        super.SetCalParList(list);
        MEG[] reglist = new MEG[]{NA, NB, NCLTEMPER, NPA, NPB, NPC, NPD, NPE, NPF, NPG, NPTEMPER, NDO100, NDO0};
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
        if (this.VVATOKEN.GetValue() > 0) {
            //原始数据
            this.ReadMEG(SR1, SR2, SR3, SR4, SR5, SR6, SR7);
        } else {
            //原始数据
            this.ReadMEG(SR1, SR2);
        }
        disdata.datas[0].mainData = NahonConvert.TimData(MPAR1.GetValue(), 3);   //DO值
        disdata.datas[0].range_info = this.GetMainRangeString(); //量程
        disdata.datas[1].mainData = NahonConvert.TimData(MPAR2.GetValue(), 2);
        disdata.datas[2].mainData = NahonConvert.TimData(SR1.GetValue(), 3);     //DO原始值

        disdata.datas[3].mainData = NahonConvert.TimData(MPAR3.GetValue(), 2);   //温度值
        disdata.datas[3].range_info = this.GetTemperRangeString(); //量程
        disdata.datas[4].mainData = NahonConvert.TimData(SR2.GetValue(), 2);     //温度原始值

        if (this.VVATOKEN.GetValue() > 0) {
            disdata.datas[5].mainData = NahonConvert.TimData(SR3.GetValue(), 4); //相位差
            disdata.datas[6].mainData = NahonConvert.TimData(SR4.GetValue(), 4); //蓝光相位
            disdata.datas[7].mainData = NahonConvert.TimData(SR5.GetValue(), 4); //参考蓝光相位
            disdata.datas[8].mainData = NahonConvert.TimData(SR6.GetValue(), 4); //红光相位
            disdata.datas[9].mainData = NahonConvert.TimData(SR7.GetValue(), 4); //参考红光相位
        }

        disdata.alarm = MALARM.GetValue(); //报警信息
        String info = CErrorTable.GetInstance().GetErrorString(((this.VDEVTYPE.GetValue() & DMask) << 8) | disdata.alarm);
        disdata.alram_info = info == null ? "" : info;
        return disdata;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标接口"> 
    // <editor-fold defaultstate="collapsed" desc="静态数据"> 
    private static double g_cfkPa[] = new double[]{
        50.5, 55.5, 60.5, 65.5, 70.5, 75.5, 80.5, 85.5, 90.5, 95.5, 100.5, 105.5, 110.5
    };

    private static double g_cfTempkPa[][] = new double[][]{ //[41][13]
        {7.24, 7.97, 8.69, 9.42, 10.15, 10.87, 11.60, 12.32, 13.05, 13.77, 14.50, 15.23, 15.95},
        {7.04, 7.75, 8.45, 9.16, 9.87, 10.57, 11.28, 11.98, 12.69, 13.40, 14.10, 14.81, 15.52},
        {6.84, 7.53, 8.22, 8.91, 9.59, 10.28, 10.97, 11.65, 12.34, 13.03, 13.72, 14.40, 15.09},
        {6.66, 7.33, 8.00, 8.67, 9.33, 10.00, 10.67, 11.34, 12.01, 12.68, 13.35, 14.02, 14.69},
        {6.48, 7.13, 7.79, 8.44, 9.09, 9.74, 10.39, 11.05, 11.70, 12.35, 13.00, 13.65, 14.31},
        {6.31, 6.94, 7.58, 8.22, 8.85, 9.49, 10.12, 10.76, 11.39, 12.03, 12.67, 13.30, 13.94},
        {6.15, 6.77, 7.39, 8.01, 8.63, 9.25, 9.87, 10.49, 11.11, 11.73, 12.35, 12.97, 13.59},
        {5.99, 6.59, 7.20, 7.80, 8.41, 9.02, 9.62, 10.23, 10.83, 11.44, 12.04, 12.65, 13.25},
        {5.84, 6.43, 7.02, 7.61, 8.20, 8.79, 9.38, 9.97, 10.56, 11.15, 11.74, 12.33, 12.92},
        {5.69, 6.27, 6.85, 7.43, 8.00, 8.58, 9.16, 9.73, 10.31, 10.89, 11.46, 12.04, 12.62},
        {5.56, 6.12, 6.69, 7.25, 7.81, 8.38, 8.94, 9.51, 10.07, 10.63, 11.20, 11.76, 12.32},
        {5.42, 5.98, 6.53, 7.08, 7.63, 8.18, 8.73, 9.28, 9.84, 10.39, 10.94, 11.49, 12.04},
        {5.30, 5.84, 6.38, 6.92, 7.45, 7.99, 8.53, 9.07, 9.61, 10.15, 10.69, 11.23, 11.77},
        {5.17, 5.70, 6.23, 6.76, 7.29, 7.81, 8.34, 8.87, 9.40, 9.93, 10.45, 10.98, 11.51},
        {5.06, 5.57, 6.09, 6.61, 7.12, 7.64, 8.16, 8.67, 9.19, 9.71, 10.22, 10.74, 11.26},
        {4.94, 5.44, 5.95, 6.45, 6.96, 7.47, 7.97, 8.48, 8.98, 9.49, 10.00, 10.50, 11.01},
        {4.83, 5.33, 5.82, 6.32, 6.81, 7.31, 7.80, 8.30, 8.80, 9.29, 9.79, 10.28, 10.78},
        {4.72, 5.21, 5.69, 6.18, 6.66, 7.15, 7.64, 8.12, 8.61, 9.09, 9.58, 10.07, 10.55},
        {4.62, 5.10, 5.57, 6.05, 6.53, 7.01, 7.48, 7.96, 8.44, 8.91, 9.39, 9.87, 10.35},
        {4.52, 4.99, 5.46, 5.93, 6.39, 6.86, 7.33, 7.80, 8.27, 8.73, 9.20, 9.67, 10.14},
        {4.42, 4.88, 5.34, 5.80, 6.26, 6.72, 7.18, 7.64, 8.10, 8.56, 9.01, 9.47, 9.93},
        {4.33, 4.78, 5.23, 5.68, 6.13, 6.58, 7.03, 7.48, 7.93, 8.38, 8.84, 9.29, 9.74},
        {4.24, 4.68, 5.12, 5.57, 6.01, 6.45, 6.90, 7.34, 7.78, 8.22, 8.67, 9.11, 9.55},
        {4.15, 4.59, 5.02, 5.46, 5.90, 6.33, 6.77, 7.20, 7.64, 8.07, 8.51, 8.94, 9.38},
        {4.07, 4.50, 4.92, 5.35, 5.78, 6.21, 6.64, 7.06, 7.49, 7.92, 8.35, 8.78, 9.21},
        {3.98, 4.40, 4.82, 5.25, 5.67, 6.09, 6.51, 6.93, 7.35, 7.77, 8.19, 8.61, 9.03},
        {3.90, 4.32, 4.73, 5.14, 5.56, 5.97, 6.39, 6.80, 7.21, 7.63, 8.04, 8.46, 8.87},
        {3.83, 4.23, 4.64, 5.05, 5.46, 5.86, 6.27, 6.68, 7.09, 7.50, 7.90, 8.31, 8.72},
        {3.75, 4.15, 4.55, 4.95, 5.36, 5.76, 6.16, 6.56, 6.96, 7.36, 7.76, 8.17, 8.57},
        {3.67, 4.07, 4.46, 4.86, 5.25, 5.65, 6.04, 6.44, 6.83, 7.23, 7.62, 8.02, 8.41},
        {3.60, 3.99, 4.38, 4.77, 5.16, 5.55, 5.94, 6.33, 6.72, 7.11, 7.50, 7.89, 8.27},
        {3.53, 3.91, 4.30, 4.68, 5.06, 5.45, 5.83, 6.22, 6.60, 6.98, 7.37, 7.75, 8.13},
        {3.46, 3.84, 4.21, 4.59, 4.97, 5.35, 5.73, 6.10, 6.48, 6.86, 7.24, 7.62, 7.99},
        {3.39, 3.76, 4.14, 4.51, 4.88, 5.25, 5.63, 6.00, 6.37, 6.75, 7.12, 7.49, 7.86},
        {3.33, 3.70, 4.06, 4.43, 4.80, 5.17, 5.54, 5.90, 6.27, 6.64, 7.01, 7.38, 7.75},
        {3.26, 3.62, 3.99, 4.35, 4.71, 5.07, 5.44, 5.80, 6.16, 6.53, 6.89, 7.25, 7.62},
        {3.20, 3.55, 3.91, 4.27, 4.63, 4.99, 5.35, 5.71, 6.06, 6.42, 6.78, 7.14, 7.50},
        {3.13, 3.49, 3.84, 4.19, 4.55, 4.90, 5.26, 5.61, 5.96, 6.32, 6.67, 7.03, 7.38},
        {3.07, 3.42, 3.77, 4.12, 4.47, 4.82, 5.17, 5.52, 5.87, 6.22, 6.57, 6.92, 7.27},
        {3.01, 3.36, 3.70, 4.05, 4.40, 4.74, 5.09, 5.43, 5.78, 6.13, 6.47, 6.82, 7.17},
        {2.95, 3.29, 3.64, 3.98, 4.32, 4.66, 5.00, 5.35, 5.69, 6.03, 6.37, 6.72, 7.06}
    };

    private static double g_cfTemp[] = new double[]{
        0,
        1, 2, 3, 4, 5, 6, 7, 8, 9,
        10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
        20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
        30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40
    };

    private static double g_cfPw[] = new double[]{
        0.61,
        0.66, 0.71, 0.76, 0.81, 0.87, 0.93, 1.00, 1.07, 1.15,
        1.23, 1.31, 1.40, 1.49, 1.60, 1.71, 1.81, 1.93, 2.07, 2.20,
        2.81, 2.99, 3.17, 3.36, 3.56, 3.77, 4.00, 4.24, 4.49, 4.76,
        5.02, 5.32, 5.62, 5.94, 6.28, 6.62, 6.98, 2.81, 2.99, 3.17,
        7.37
    };
    // </editor-fold> 

    private double calculate_newton(double[] pfXData, double[] pfYData, int Length, double NewX) {
        int i = 0;
        double fTemp = 0;

        for (i = 0; i < Length; i++) {
            if (NewX <= pfXData[i]) {
                break;
            }
        }

        if (i == 0) {
            fTemp = pfYData[0];
        } else if (i >= Length) {
            fTemp = pfYData[Length - 1];
        } else {
            fTemp = (NewX - pfXData[i - 1]) / (pfXData[i] - pfXData[i - 1]);
            fTemp = (fTemp * (pfYData[i] - pfYData[i - 1])) + pfYData[i - 1];
        }

        return fTemp;
    }

    private void do_single_cal(float fOriginal, float fTemp) throws Exception {
//        double dPw;
//        double dPaCoff;
        float fX1, fX2, fY1, fY2;
        int ulTemp = 0;
        double dTemp[] = new double[2];

        if (fTemp < 0) {
            ulTemp = 0;
        } else if (fTemp > 40) {
            ulTemp = 40;
        } else {
            ulTemp = (int) fTemp;
        }

        dTemp[0] = calculate_newton(g_cfkPa, g_cfTempkPa[ulTemp], 13, (float) this.NPASCA.GetValue());
        if (ulTemp < 40) {
            dTemp[1] = calculate_newton(g_cfkPa, g_cfTempkPa[ulTemp + 1], 13, (float) this.NPASCA.GetValue());
            dTemp[0] = (fTemp - (float) ulTemp) * (dTemp[1] - dTemp[0]) + dTemp[0];
        }

        // dPw = calculate_newton(g_cfTemp, g_cfPw, 41, fTemp);
        // dPaCoff = ((double) this.Psca - dPw) / (101.325 - dPw);
        fX1 = -((float) this.NB.GetValue() / (float) this.NA.GetValue());
        fX2 = fOriginal;
        fY1 = 0;
        fY2 = (float) (dTemp[0]);// / dPaCoff);

        float newA = (fY2 - fY1) / (fX2 - fX1);
        float newB = fY2 - (newA * fX2);
        this.SetConfigREG(NA, newA + "");
        this.SetConfigREG(NB, newB + "");
        this.SetConfigREG(NCLTEMPER, fTemp + "");
    }

    private void do_double_cal(float fOriginalZero, float fOriginalSaturated, float fTemp) throws Exception {

//        double dPw;
//        double dPaCoff;
        float fX1, fX2, fY1, fY2;
        int ulTemp = 0;
        double dTemp[] = new double[2];

        if (fTemp < 0) {
            ulTemp = 0;
        } else if (fTemp > 40) {
            ulTemp = 40;
        } else {
            ulTemp = (int) fTemp;
        }

        dTemp[0] = calculate_newton(g_cfkPa, g_cfTempkPa[ulTemp], 13, (float) this.NPASCA.GetValue());
        if (ulTemp < 40) {
            dTemp[1] = calculate_newton(g_cfkPa, g_cfTempkPa[ulTemp + 1], 13, (float) this.NPASCA.GetValue());
            dTemp[0] = (fTemp - (float) ulTemp) * (dTemp[1] - dTemp[0]) + dTemp[0];
        }

//        dPw = calculate_newton(g_cfTemp, g_cfPw, 41, fTemp);
//        dPaCoff = ((double) this.Psca - dPw) / (101.325 - dPw);
        fX1 = fOriginalZero;
        fX2 = fOriginalSaturated;
        fY1 = 0;
        fY2 = (float) (dTemp[0]);// / dPaCoff);

        float newA = (fY2 - fY1) / (fX2 - fX1);
        float newB = fY2 - (newA * fX2);
        this.SetConfigREG(NA, newA + "");
        this.SetConfigREG(NB, newB + "");
        this.SetConfigREG(NCLTEMPER, fTemp + "");
    }

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
            this.ReadMEG(MPAR3);
            float temp = NahonConvert.TimData(MPAR3.GetValue(), 2);   //温度值
//            if (this.isVersionA) {
//                if (oradata.length == 1) {
//                    this.ESADOOnePointCalib((float) this.c_0.value, (float) this.c_100.value, oradata[0]);
//                } else {
//                    //界面输入是 {饱和氧,无氧}的顺序,需要交换顺序
//                    this.ESADOTwoPointCalib((float) this.c_0.value, (float) this.c_100.value, oradata[1], oradata[0]);
////                    this.do_double_cal(oradata[1], oradata[0], temp);
//                }
//
//                this.c_time_T.value = temp;
//                this.UpdateMem(c_time_T);
//            } else {
            if (oradata.length == 1) {
                this.do_single_cal(oradata[0], temp);
            } else {
                //界面输入是 {饱和氧,无氧}的顺序,需要交换顺序
                this.do_double_cal(oradata[1], oradata[0], temp);
            }
//            }
            ret.children.add(new LogNode(this.NCLTEMPER.toString(), this.NCLTEMPER.GetValue()));
            ret.children.add(new LogNode(this.NA.toString(), this.NA.GetValue()));
            ret.children.add(new LogNode(this.NB.toString(), this.NB.GetValue()));
        }
        return ret;
    }
    // </editor-fold> 

    public static void main(String... args) throws Exception{
        float ByteArrayToFloat = NahonConvert.ByteArrayToFloat(new byte[]{(byte)0x00, (byte)0xBE, (byte)0x70, (byte)0xF0}, 0);
    }
}
