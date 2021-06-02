/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.ESA;

import modebus.pro.NahonConvert;
import modebus.register.*;
import wqa.adapter.factory.*;
import wqa.dev.data.*;

/**
 *
 * @author chejf
 */
public class ECHLORII extends ECHLORI {

    private final FREG ORP = new FREG(0x05, 2, "ORP");   //R
    private final FREG OORP = new FREG(0x0D, 2, "ORP原始信号(mv)");   //R/W

    public ECHLORII(SDevInfo info) {
        super(info);

        TEMPER = new FREG(0x07, 2, "温度数据");   //R
        OCHL = new FREG(0x09, 2, "余氯原始信号(mv)");   //R(mv)
        OPH = new FREG(0x0B, 2, "PH原始信号(mv)");   //R/W
    }

    // <editor-fold defaultstate="collapsed" desc="采集接口"> 
    @Override
    public CollectData CollectData() throws Exception {
        CollectData disdata = this.BuildDisplayData();
        this.ReadREG(ALARM, CHL, PH, ORP, TEMPER, OCHL, OPH, OORP);
        this.ReadREG(OTEMPER);

        disdata.datas[0].mainData = NahonConvert.TimData(CHL.GetValue(), 2);
        disdata.datas[1].mainData = NahonConvert.TimData(OCHL.GetValue(), 2);

        disdata.datas[2].mainData = NahonConvert.TimData(PH.GetValue(), 2);
        disdata.datas[3].mainData = NahonConvert.TimData(OPH.GetValue(), 2);

        disdata.datas[4].mainData = NahonConvert.TimData(ORP.GetValue(), 2);
        disdata.datas[5].mainData = NahonConvert.TimData(OORP.GetValue(), 2);

        disdata.datas[6].mainData = NahonConvert.TimData(TEMPER.GetValue(), 2);
        disdata.datas[7].mainData = NahonConvert.TimData(OTEMPER.GetValue(), 2);

        disdata.alarm = ALARM.GetValue();
        String info = CErrorTable.GetInstance().GetErrorString(CErrorTable.ESA_E | disdata.alarm);
        disdata.alram_info = info == null ? "" : info;
        return disdata;
    }
    // </editor-fold>   
}
