/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ESA;

import base.migp.mem.NVPA;
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
public class ESA_ORP extends ESADEV {

    public ESA_ORP(SDevInfo devinfo) {
        super(devinfo);
    }

    // <editor-fold defaultstate="collapsed" desc="寄存器列表"> 
    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    FMEG NA = new FMEG(new NVPA(0, 4), "ORP系数A");
    FMEG NB = new FMEG(new NVPA(4, 4), "ORP系数B");
    private FMEG NTEMP_CAL = new FMEG(new NVPA(96, 4), "温度系数");
    // </editor-fold> 
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="初始化"> 
    @Override
    public void InitDevice() throws Exception {
        super.InitDevice(); //To change body of generated methods, choose Tools | Templates.
        this.ReadMEG(NA, NB, NTEMP_CAL);
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="读取calpar"> 
    @Override
    public ArrayList<SConfigItem> GetCalParList() {
        ArrayList<SConfigItem> item = super.GetCalParList(); //To change body of generated methods, choose Tools | Templates.
        item.add(SConfigItem.CreateRWItem(NTEMP_CAL.toString(), this.NTEMP_CAL.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(NA.toString(), this.NA.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(NB.toString(), this.NB.GetValue() + "", ""));
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
            if (item.IsKey(NB.toString())) {
                this.SetConfigREG(NB, item.GetValue());
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

        disdata.datas[0].mainData = NahonConvert.TimData(MPAR1.GetValue(), 2);   //ph值
        disdata.datas[0].range_info = this.GetMainRangeString(); //量程
        disdata.datas[1].mainData = NahonConvert.TimData(SR1.GetValue(), 2); //ph原始值

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
//            this.setTemper_cal(cal_par);
            this.SetConfigREG(NTEMP_CAL, cal_par + "");
            ret.children.add(new LogNode(NTEMP_CAL.toString(), this.NTEMP_CAL.GetValue()));
        } else {
            //参数定标
            this.cal_orp(oradata, testdata);
            ret.children.add(new LogNode(NA.toString(), this.NA.GetValue()));
            ret.children.add(new LogNode(NB.toString(), this.NB.GetValue()));
        }
        return ret;
    }

    private void cal_orp(float[] oradata, float[] testdata) throws Exception {
        if (oradata.length == 1) {
            this.cal_single(oradata[0], testdata[0]);
        } else if (oradata.length == 2) {
            this.cal_double(oradata, testdata);
        }
    }

    private void cal_single(float oradata, float testdata) throws Exception {
        float newB = testdata - oradata * this.NA.GetValue();
        this.SetConfigREG(NB, newB + "");
    }

    private void cal_double(float[] oradata, float[] testdata) throws Exception {
        float newA = 1;
        if (oradata[0] - oradata[1] != 0) {
            newA = (testdata[0] - testdata[1]) / (oradata[0] - oradata[1]);
        }
        float newB = testdata[0] - newA * oradata[0];
        this.SetConfigREG(NA, newA + "");
        this.SetConfigREG(NB, newB + "");
    }
    // </editor-fold> 

}
