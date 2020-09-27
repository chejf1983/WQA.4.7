/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ESA;

import base.migp.mem.NVPA;
import base.migp.reg.FMEG;
import base.pro.convert.NahonConvert;
import migp.adapter.factory.TemperCalibrateCalculate;
import wqa.dev.data.*;
import wqa.adapter.factory.CErrorTable;

/**
 *
 * @author chejf
 */
public class ESA_CHLII extends ESA_CHL {

    public ESA_CHLII(SDevInfo devinfo) {
        super(devinfo);

        this.CalList.add(NORPA);
        this.CalList.add(NORPB);
    }

    // <editor-fold defaultstate="collapsed" desc="内存表"> 
    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    FMEG NORPA = new FMEG(new NVPA(28, 4), "ORP参数A");
    FMEG NORPB = new FMEG(new NVPA(32, 4), "ORP参数B");
    // </editor-fold> 
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="采集接口">     
    @Override
    public CollectData CollectData() throws Exception {
        CollectData disdata = this.BuildDisplayData();
        //读取数据
        this.ReadMEG(MALARM, MPAR1, MPAR2, MPAR3, MPAR4);
        //原始数据
        this.ReadMEG(SR1, SR2, SR3, SR4);
        disdata.datas[0].mainData = NahonConvert.TimData(MPAR1.GetValue(), 2);   //余氯
        disdata.datas[0].range_info = this.GetMainRangeString(); //余氯量程
        disdata.datas[1].mainData = NahonConvert.TimData(SR1.GetValue(), 2); //余氯原始值

        disdata.datas[2].mainData = NahonConvert.TimData(MPAR2.GetValue(), 2);   //PH值
        disdata.datas[3].mainData = NahonConvert.TimData(SR3.GetValue(), 2); //PH原始值

        disdata.datas[4].mainData = NahonConvert.TimData(MPAR4.GetValue(), 2);   //ORP值
        disdata.datas[5].mainData = NahonConvert.TimData(SR4.GetValue(), 2); //ORP原始值

        disdata.datas[6].mainData = NahonConvert.TimData(MPAR3.GetValue(), 2);   //温度值
        disdata.datas[6].range_info = this.GetTemperRangeString(); //量程
        disdata.datas[7].mainData = NahonConvert.TimData(SR2.GetValue(), 2); //温度原始值

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
        } else if (type.contentEquals(this.GetCalDataList()[0].data_name)) {
            ////余氯参数定标
            this.calchl(oradata, testdata);
            ret.children.add(new LogNode(N0.toString(), this.N0.GetValue()));
            ret.children.add(new LogNode(NS.toString(), this.NS.GetValue()));
            ret.children.add(new LogNode(NT.toString(), this.NT.GetValue()));
        } else if (type.contentEquals(this.GetCalDataList()[1].data_name)) {
            //参数定标
            this.calph(oradata, testdata);
            ret.children.add(new LogNode(NPH0.toString(), this.NPH0.GetValue()));
            ret.children.add(new LogNode(NPHA.toString(), this.NPHA.GetValue()));
        } else {
            //参数定标
            this.cal_orp(oradata, testdata);
            ret.children.add(new LogNode(NPH0.toString(), this.NPH0.GetValue()));
            ret.children.add(new LogNode(NPHA.toString(), this.NPHA.GetValue()));
        }
        return ret;
    }

    // <editor-fold defaultstate="collapsed" desc="定标orp"> 
    private void cal_orp(float[] oradata, float[] testdata) throws Exception {
        if (oradata.length == 1) {
            this.cal_single(oradata[0], testdata[0]);
        } else if (oradata.length == 2) {
            this.cal_double(oradata, testdata);
        }
    }

    private void cal_single(float oradata, float testdata) throws Exception {
        float newB = testdata - oradata * this.NORPA.GetValue();
        this.SetConfigREG(NORPB, newB + "");
    }

    private void cal_double(float[] oradata, float[] testdata) throws Exception {
        float newA = 1;
        if (oradata[0] - oradata[1] == 0) {
            newA = (testdata[0] - testdata[1]) / (oradata[0] - oradata[1]);
        }
        float newB = testdata[0] - newA * oradata[0];
        this.SetConfigREG(NORPA, newA + "");
        this.SetConfigREG(NORPB, newB + "");
    }
    // </editor-fold>   
    // </editor-fold> 
}
