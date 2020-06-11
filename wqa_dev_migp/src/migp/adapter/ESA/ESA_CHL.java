/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ESA;

import base.migp.mem.NVPA;
import base.migp.reg.DMEG;
import base.migp.reg.FMEG;
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
public class ESA_CHL extends ESADEV {

    public ESA_CHL(SDevInfo devinfo) {
        super(devinfo);
    }

    // <editor-fold defaultstate="collapsed" desc="内存表"> 
    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    FMEG N0 = new FMEG(new NVPA(0, 4), "校准参数ZERO");
    FMEG NS = new FMEG(new NVPA(4, 4), "校准参数S");
    FMEG NT = new FMEG(new NVPA(8, 4), "余氯定标点温度");
    DMEG NPH0 = new DMEG(new NVPA(12, 8), "PH参数E0");
    DMEG NPHA = new DMEG(new NVPA(20, 8), "PH参数A");
    FMEG NTEMP_CAL = new FMEG(new NVPA(96, 4), "温度系数");
    // </editor-fold> 
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="初始化"> 
    @Override
    public void InitDevice() throws Exception {
        super.InitDevice(); //To change body of generated methods, choose Tools | Templates.

        this.ReadMEG(N0, NS, NT, NPH0, NPHA, NTEMP_CAL);
        this.ReadMEG(NTEMP_CAL);
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="读取calpar"> 
    @Override
    public ArrayList<SConfigItem> GetCalParList() {
        ArrayList<SConfigItem> item = super.GetCalParList(); //To change body of generated methods, choose Tools | Templates.
        item.add(SConfigItem.CreateRWItem(NTEMP_CAL.toString(), this.NTEMP_CAL.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(N0.toString(), N0.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(NS.toString(), NS.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(NT.toString(), NT.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(NPH0.toString(), NPH0.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(NPHA.toString(), NPHA.GetValue() + "", ""));
        return item;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置calpar"> 
    @Override
    public void SetCalParList(ArrayList<SConfigItem> list) throws Exception {
        super.SetCalParList(list);
        for (SConfigItem item : list) {
            if (item.IsKey(N0.toString())) {
                this.SetConfigREG(N0, item.GetValue());
            }
            if (item.IsKey(NS.toString())) {
                this.SetConfigREG(NS, item.GetValue());
            }
            if (item.IsKey(NT.toString())) {
                this.SetConfigREG(NT, item.GetValue());
            }
            if (item.IsKey(NPH0.toString())) {
                this.SetConfigREG(NPH0, item.GetValue());
            }
            if (item.IsKey(NPHA.toString())) {
                this.SetConfigREG(NPHA, item.GetValue());
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
        this.ReadMEG(MALARM, MPAR1, MPAR2, MPAR3);
        //原始数据
        this.ReadMEG(SR1, SR2);
        disdata.datas[0].mainData = NahonConvert.TimData(MPAR1.GetValue(), 2);   //余氯
        disdata.datas[0].range_info = this.GetMainRangeString(); //余氯量程
        disdata.datas[1].mainData = NahonConvert.TimData(SR1.GetValue(), 2); //余氯原始值

        disdata.datas[2].mainData = NahonConvert.TimData(MPAR2.GetValue(), 2);   //PH值
        disdata.datas[3].mainData = NahonConvert.TimData(SR3.GetValue(), 2); //PH原始值

        disdata.datas[2].mainData = NahonConvert.TimData(MPAR3.GetValue(), 2);   //温度值
        disdata.datas[2].range_info = this.GetTemperRangeString(); //量程
        disdata.datas[3].mainData = NahonConvert.TimData(SR2.GetValue(), 2); //温度原始值

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
            float cal_par = new TemperCalibrateCalculate().Calculate(testdata, oradata);
            this.SetConfigREG(this.NTEMP_CAL, cal_par + "");
            ret.children.add(new LogNode(NTEMP_CAL.toString(), this.NTEMP_CAL.GetValue()));
        } else if (type.contentEquals(this.GetDataNames()[0])) {
            //参数定标
            this.calchl(oradata, testdata);
            ret.children.add(new LogNode(N0.toString(), this.N0.GetValue()));
            ret.children.add(new LogNode(NS.toString(), this.NS.GetValue()));
            ret.children.add(new LogNode(NT.toString(), this.NT.GetValue()));
        } else {
            //参数定标
            this.calph(oradata, testdata);
            ret.children.add(new LogNode(NPH0.toString(), this.NPH0.GetValue()));
            ret.children.add(new LogNode(NPHA.toString(), this.NPHA.GetValue()));
        }
        return ret;
    }

    // <editor-fold defaultstate="collapsed" desc="定标chl"> 
    private void calchl(float[] oradata, float[] testdata) throws Exception {
        float temper = NahonConvert.TimData(MPAR3.GetValue(), 2);   //温度值
        if (oradata.length == 1) {
            this.calchl_single(oradata[0], testdata[0], temper);
        } else if (oradata.length == 2) {
            this.calchl_double(oradata, testdata, temper);
        }
    }

    private void calchl_single(float oradata, float testdata, float temper) throws Exception {
        double fT = temper + 273.15;
        double temp = 1 + Math.pow(10, (MPAR2.GetValue() - ((3000.0 / fT) - 10.0686 + (0.0253 * fT))));
        if (testdata == 0) {
            testdata = 0.001f;
        }
        double dHOCL = (testdata / temp);
//        this.setE0(tE0);
        double TS = ((oradata - (N0.GetValue() * 1000))) / dHOCL;
        this.SetConfigREG(NS, TS + "");
        this.SetConfigREG(NT, temper + "");
    }

    private void calchl_double(float[] oradata, float[] testdata, float temper) throws Exception {
        double fT = temper + 273.15;
        double temp = 1 + Math.pow(10, (MPAR2.GetValue() - ((3000.0 / fT) - 10.0686 + (0.0253 * fT))));
        if (testdata[0] == 0) {
            testdata[0] = 0.001f;
        }
        if (testdata[1] == 0) {
            testdata[1] = 0.001f;
        }
        double dHOCL0 = (testdata[0] / temp);
        double dHOCL1 = (testdata[1] / temp);
        double temp2 = dHOCL1 / dHOCL0;
//        this.setA(tA);
        double tZ = (oradata[1] - (temp2 * oradata[0])) / (1 - temp2) / 1000.0;
        double ts = ((oradata[0] - (tZ * 1000))) / dHOCL0;

        this.SetConfigREG(N0, tZ + "");
        this.SetConfigREG(NS, ts + "");
        this.SetConfigREG(NT, temper + "");
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标ph"> 
    private void calph(float[] oradata, float[] testdata) throws Exception {
        float temper = NahonConvert.TimData(MPAR3.GetValue(), 2);   //温度值
        if (oradata.length == 1) {
            this.calph_single(oradata[0], testdata[0], temper);
        } else if (oradata.length == 2) {
            this.calph_double(oradata, testdata, temper);
        }
    }

    private void calph_single(float oradata, float testdata, float temper) throws Exception {
        double tE0 = (testdata - 7) * NPHA.GetValue() * (temper + 273.15) + oradata;
//        this.setE0(tE0);
        this.SetConfigREG(NPH0, tE0 + "");
    }

    private void calph_double(float[] oradata, float[] testdata, float temper) throws Exception {
        double tA = (oradata[0] - oradata[1]) / (testdata[1] - testdata[0]);
        tA = tA / (temper + 273.15);
        double tE0 = oradata[1] * (testdata[0] - 7) - oradata[0] * (testdata[1] - 7);
        tE0 = tE0 / (testdata[0] - testdata[1]);
//        this.setE0(tE0);
//        this.setA(tA);
        this.SetConfigREG(NPH0, tE0 + "");
        this.SetConfigREG(NPHA, tA + "");
    }
    // </editor-fold>   
    // </editor-fold> 
}
