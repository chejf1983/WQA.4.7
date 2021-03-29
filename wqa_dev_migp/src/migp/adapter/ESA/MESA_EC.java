/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ESA;

import base.pro.convert.NahonConvert;
import migp.adapter.factory.TemperCalibrateCalculate;
import wqa.dev.data.LogNode;
import wqa.dev.data.SDevInfo;

/**
 *
 * @author chejf
 */
public class MESA_EC extends ESA_EC{
    
    public MESA_EC(SDevInfo devinfo) {
        super(devinfo);
    }
    // <editor-fold defaultstate="collapsed" desc="定标接口"> 
    @Override
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception {
        LogNode ret = LogNode.CALOK();
        if (type.contentEquals("温度")) {
            //温度定标
            this.calTemperNew(oradata, testdata);
            this.ReadMEG(NTEMP_CAL);
            ret.children.add(new LogNode(NTEMP_CAL.toString(), this.NTEMP_CAL.GetValue()));
        } else {
            //参数定标
            this.calDataNew(oradata, testdata);
            this.ReadMEG(NA);
            ret.children.add(new LogNode(NA.toString(), this.NA.GetValue()));
        }
        return ret;
    }
    // </editor-fold> 

}
