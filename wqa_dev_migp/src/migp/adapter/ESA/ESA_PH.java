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
import wqa.adapter.io.ShareIO;
import wqa.bill.log.LogNode;
import wqa.control.dev.collect.SDisplayData;
import wqa.control.common.CErrorTable;
import wqa.control.config.SConfigItem;
import static migp.adapter.factory.AbsDevice.DEF_TIMEOUT;

/**
 *
 * @author chejf
 */
public class ESA_PH extends ESADEV {

    public ESA_PH(ShareIO io, byte addr) {
        super(io, addr);
    }

    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    DMEG NE0 = new DMEG(new NVPA(0, 8), "PH系数E0");
    DMEG NA = new DMEG(new NVPA(8, 8), "PH系数A");
    private FMEG NTEMP_CAL = new FMEG(new NVPA(96, 4), "温度系数");
    // </editor-fold> 

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice(); //To change body of generated methods, choose Tools | Templates.

        this.ReadMEG(NA, NE0);
        this.ReadMEG(NTEMP_CAL);
    }

    // <editor-fold defaultstate="collapsed" desc="配置接口"> 
    @Override
    public ArrayList<SConfigItem> GetCalParList() {
        ArrayList<SConfigItem> item = super.GetCalParList(); //To change body of generated methods, choose Tools | Templates.
        item.add(SConfigItem.CreateRWItem(NTEMP_CAL.toString(), this.NTEMP_CAL.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(NA.toString(), NA.GetValue() + "", ""));
        item.add(SConfigItem.CreateRWItem(NE0.toString(), NE0.GetValue() + "", ""));
        return item;
    }

    @Override
    public void SetCalParList(ArrayList<SConfigItem> list) throws Exception {
        super.SetCalParList(list);
        for (SConfigItem item : list) {
            if (item.IsKey(NA.toString())) {
                this.SetConfigREG(NA, item.value);
            }
            if (item.IsKey(NE0.toString())) {
                this.SetConfigREG(NE0, item.value);
            }
            if (item.IsKey(NTEMP_CAL.toString())) {
                this.SetConfigREG(NTEMP_CAL, item.value);
            }
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="采集接口">     
    @Override
    public SDisplayData CollectData() throws Exception {
        SDisplayData disdata = this.BuildDisplayData();
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
            this.calph(oradata, testdata);
            ret.children.add(new LogNode(NA.toString(), this.NA.GetValue()));
            ret.children.add(new LogNode(NE0.toString(), this.NE0.GetValue()));
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
        double tE0 = (testdata - 7) * NA.GetValue() * (temper + 273.15) + oradata;
//        this.setE0(tE0);
        this.SetConfigREG(NE0, tE0 + "");
    }

    private void cal_double(float[] oradata, float[] testdata, float temper) throws Exception {
        double tA = (oradata[0] - oradata[1]) / (testdata[1] - testdata[0]);
        tA = tA / (temper + 273.15);
        double tE0 = oradata[1] * (testdata[0] - 7) - oradata[0] * (testdata[1] - 7);
        tE0 = tE0 / (testdata[0] - testdata[1]);
//        this.setE0(tE0);
//        this.setA(tA);
        this.SetConfigREG(NE0, tE0 + "");
        this.SetConfigREG(NA, tA + "");
    }
    // </editor-fold> 
}
