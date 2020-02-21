/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.devmock;

import modebus.register.FREG;
import modebus.register.IREG;

/**
 *
 * @author chejf
 */
public class AMMODevMock extends DevMock{
     
    // <editor-fold defaultstate="collapsed" desc="DO寄存器"> 
    //---------------------------------------------------------------------------------------
    public final IREG ALARM = new IREG(0x00, 1, "报警码");   //R
    public final FREG PH = new FREG(0x01, 2, "PH数据");   //R
    public final FREG NH4 = new FREG(0x03, 2, "NH4+浓度数据");   //R
    public final FREG K = new FREG(0x05, 2, "K+浓度数据");   //R
    public final FREG TEMPER = new FREG(0x09, 2, "温度数据");   //R
    public final FREG OPH = new FREG(0x0B, 2, "PH原始信号");   //R
    public final FREG ONH4 = new FREG(0x0D, 2, "NH4+原始信号");   //R
    public final FREG OK = new FREG(0x0F, 2, "K+原始信号");   //R

    public final IREG CLTYPE = new IREG(0x30, 1, "定标类型", 1, 3);   //R/W  (PH:01, NH:02, K:03)    
    public final FREG[] CLODATA = new FREG[]{new FREG(0x31, 2, "原始数据1"), new FREG(0x35, 2, "原始数据2")};  //R/W
    public final FREG[] CLTDATA = new FREG[]{new FREG(0x33, 2, "定标数据1"), new FREG(0x37, 2, "定标数据2")};  //R/W
    public final IREG CLSTART = new IREG(0x39, 1, "启动定标"); //R/W (一点：01， 两点02）
    public final FREG CLTEMPER = new FREG(0x3A, 2, "温度定标参数");    //R/W
    public final IREG CLTEMPERSTART = new IREG(0x3C, 1, "启动温度定标");//R/W
    // </editor-fold> 
       
    public AMMODevMock(){
        super();
        client.RegisterREGS(
                ALARM,
                PH,
                NH4,
                K,
                TEMPER,
                OPH,
                ONH4,
                OK,
                CLTYPE,
                CLODATA[0],
                CLODATA[1],
                CLTDATA[0],
                CLTDATA[1],
                CLSTART,
                CLTEMPER,
                CLTEMPERSTART
        );
    }
        
    @Override
    public void ResetREGS() throws Exception {
        super.ResetREGS();
        DEVTYPE.SetValue(0x0301);
        ///////////////////////////////////////////////////////////
        ALARM.SetValue(0);
        PH.SetValue((float) PH.RegAddr());
        NH4.SetValue((float) NH4.RegAddr());
        K.SetValue((float) K.RegAddr());
        TEMPER.SetValue((float) TEMPER.RegAddr());
        OPH.SetValue((float) OPH.RegAddr());
        ONH4.SetValue((float) ONH4.RegAddr());
        OK.SetValue((float) OK.RegAddr());
        WriteREGS();
    }
    
}
