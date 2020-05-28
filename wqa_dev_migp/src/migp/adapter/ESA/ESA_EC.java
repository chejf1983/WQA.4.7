/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ESA;

import base.migp.mem.NVPA;
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
public class ESA_EC extends ESADEV {

    public ESA_EC(SDevInfo devinfo) {
        super(devinfo);
    }

    // <editor-fold defaultstate="collapsed" desc="NVPA">   
    private FMEG NA = new FMEG(new NVPA(36, 4), "电导率参数A");
    private FMEG NTEMP_COM = new FMEG(new NVPA(40, 4), "温度补偿系数");
    private FMEG NTEMP_CAL = new FMEG(new NVPA(96, 4), "温度系数");
    // </editor-fold> 

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice(); //To change body of generated methods, choose Tools | Templates.

        this.ReadMEG(NA, NTEMP_COM);
        this.ReadMEG(NTEMP_CAL);
    }

    // <editor-fold defaultstate="collapsed" desc="读取config"> 
    @Override
    public ArrayList<SConfigItem> GetConfigList() {
        ArrayList<SConfigItem> item = super.GetConfigList(); //To change body of generated methods, choose Tools | Templates.
        item.add(SConfigItem.CreateRWItem(NTEMP_COM.toString(), NTEMP_COM.GetValue() + "", ""));
        return item;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置config"> 
    @Override
    public void SetConfigList(ArrayList<SConfigItem> list) throws Exception {
        super.SetConfigList(list);
        for (SConfigItem item : list) {
            if (item.IsKey(NTEMP_COM.toString())) {
                this.SetConfigREG(NTEMP_COM, item.GetValue());
            }
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="读取calpar"> 
    @Override
    public ArrayList<SConfigItem> GetCalParList() {
        ArrayList<SConfigItem> item = super.GetCalParList(); //To change body of generated methods, choose Tools | Templates.
        item.add(SConfigItem.CreateRWItem(NA.toString(), this.NA.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(NTEMP_CAL.toString(), this.NTEMP_CAL.GetValue() + "", ""));
        return item;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置calpar"> 
    @Override
    public void SetCalParList(ArrayList<SConfigItem> list) throws Exception {
        super.SetCalParList(list);
        for (SConfigItem item : list) {
            if (item.IsKey(NA.toString())) {
                this.SetConfigREG(NA, item.GetValue());
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
        disdata.datas[0].mainData = NahonConvert.TimData(MPAR1.GetValue(), 2);   //EC值
        disdata.datas[0].range_info = this.GetMainRangeString(); //量程
        disdata.datas[1].mainData = NahonConvert.TimData(SR1.GetValue(), 2); //EC原始值

        disdata.datas[2].mainData = NahonConvert.TimData(MPAR3.GetValue(), 2);   //温度值
        disdata.datas[2].range_info = this.GetTemperRangeString(); //量程
        disdata.datas[3].mainData = NahonConvert.TimData(SR2.GetValue(), 2); //温度原始值

        disdata.datas[4].mainData = NahonConvert.TimData(MPAR2.GetValue(), 2);   //盐度值

        disdata.alarm = MALARM.GetValue(); //报警信息
        String info = CErrorTable.GetInstance().GetErrorString(((VDEVTYPE.GetValue() & DMask) << 8) | disdata.alarm);
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
        } else {
            //参数定标
            this.cal_EC(oradata, testdata);
            ret.children.add(new LogNode(NA.toString(), this.NA.GetValue()));
        }
        return ret;
    }

    private void cal_EC(float[] oradata, float[] testdata) throws Exception {
//        double temp = MDA.GetData().datas[2];
        double temp = NahonConvert.TimData(MPAR3.GetValue(), 2);   //温度值
        double ectemp = oradata[0] * (1 + this.NTEMP_COM.GetValue() * (temp - 25));
        double ec = (double) 1000000.0 / testdata[0];
        double tA = ectemp / ec;
//        this.setA((float) tA);
        this.SetConfigREG(NA, tA + "");
    }
    // </editor-fold> 

}
