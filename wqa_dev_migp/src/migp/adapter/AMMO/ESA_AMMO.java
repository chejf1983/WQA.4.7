/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.AMMO;

import base.migp.mem.NVPA;
import base.migp.reg.DMEG;
import base.migp.reg.FMEG;
import base.pro.convert.NahonConvert;
import java.util.ArrayList;
import migp.adapter.ESA.ESADEV;
import migp.adapter.factory.TemperCalibrateCalculate;
import wqa.dev.data.*;
import wqa.adapter.factory.CErrorTable;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class ESA_AMMO extends ESADEV {

    public ESA_AMMO(SDevInfo devinfo) {
        super(devinfo);
    }

    // <editor-fold defaultstate="collapsed" desc="内存表"> 
    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    DMEG NA = new DMEG(new NVPA(0, 8), "氨氮系数A");
    DMEG NE = new DMEG(new NVPA(8, 8), "氨氮系数E");
    DMEG NF = new DMEG(new NVPA(16, 8), "氨氮系数F");
    FMEG NPH = new FMEG(new NVPA(24, 4), "PH补偿值");
    private FMEG NTEMP_CAL = new FMEG(new NVPA(96, 4), "温度系数");
    // </editor-fold> 
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="初始化"> 
    @Override
    public void InitDevice() throws Exception {
        super.InitDevice(); //To change body of generated methods, choose Tools | Templates.

        this.ReadMEG(NA, NE, NF, NPH);
        this.ReadMEG(NTEMP_CAL);
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="读取calpar"> 
    @Override
    public ArrayList<SConfigItem> GetCalParList() {
        ArrayList<SConfigItem> item = super.GetCalParList(); //To change body of generated methods, choose Tools | Templates.
        item.add(SConfigItem.CreateRWItem(NTEMP_CAL.toString(), this.NTEMP_CAL.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(NA.toString(), NA.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(NE.toString(), NE.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(NF.toString(), NF.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(NPH.toString(), NPH.GetValue() + "", ""));
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
            if (item.IsKey(NE.toString())) {
                this.SetConfigREG(NE, item.GetValue());
            }
            if (item.IsKey(NF.toString())) {
                this.SetConfigREG(NF, item.GetValue());
            }
            if (item.IsKey(NPH.toString())) {
                this.SetConfigREG(NPH, item.GetValue());
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
        disdata.datas[0].mainData = NahonConvert.TimData(MPAR1.GetValue(), 2);   //主值
        disdata.datas[0].range_info = this.GetMainRangeString(); //量程
        disdata.datas[1].mainData = NahonConvert.TimData(SR1.GetValue(), 2); //原始值

        disdata.datas[2].mainData = NahonConvert.TimData(MPAR3.GetValue(), 2);   //温度值
        disdata.datas[2].range_info = this.GetTemperRangeString(); //量程
        disdata.datas[3].mainData = NahonConvert.TimData(SR2.GetValue(), 2); //温度原始值

        disdata.alarm = MALARM.GetValue(); //报警信息
        String info = CErrorTable.GetInstance().GetErrorString(wqa.adapter.factory.CErrorTable.ESA_E | disdata.alarm);
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
            this.calph(oradata, testdata);
            ret.children.add(new LogNode(NA.toString(), this.NA.GetValue()));
            ret.children.add(new LogNode(NE.toString(), this.NE.GetValue()));
            ret.children.add(new LogNode(NF.toString(), this.NF.GetValue()));
            ret.children.add(new LogNode(NPH.toString(), this.NPH.GetValue()));
        }
        return ret;
    }

    private void calph(float[] oradata, float[] testdata) throws Exception {
//        float temper = MDA.GetData().datas[2];
        float temper = NahonConvert.TimData(MPAR3.GetValue(), 2);   //温度值
        if (oradata.length == 1) {
            this.cal_single(oradata[0], testdata[0], temper);
        } else if (oradata.length == 2) {
            this.cal_double(oradata, testdata, temper);
        }
    }

    private void cal_single(float oradata, float testdata, float temper) throws Exception {
        double templog = this.NPH.GetValue() - (0.090387 + 2729.33 / (273.15 + temper));
        double temp1 = testdata / ((14.01 / 18.04) * (1 + Math.pow(10, templog)));
        temp1 = Math.log10(temp1);

        double F = oradata - NA.GetValue() * (273.15 + temper) * temp1 - NE.GetValue() * temper;

        this.SetConfigREG(NF, F + "");
    }

    private void cal_double(float[] oradata, float[] testdata, float temper) throws Exception {
        double templog = this.NPH.GetValue() - (0.090387 + 2729.33 / (273.15 + temper));

        double temp1 = testdata[0] / ((14.01 / 18.04) * (1 + Math.pow(10, templog)));
        temp1 = Math.log10(temp1);
        double temp2 = testdata[1] / ((14.01 / 18.04) * (1 + Math.pow(10, templog)));
        temp2 = Math.log10(temp2);

        double A = ((double) (oradata[1] - oradata[0])) / ((temp2 - temp1) * (273.15 + temper));
        double t = oradata[0] * temp2 - oradata[1] * temp1;
        double F = t/(temp2 - temp1) - NE.GetValue() * temper;
        
        this.SetConfigREG(NA, A + "");
        this.SetConfigREG(NF, F + "");
    }
    // </editor-fold> 
}
