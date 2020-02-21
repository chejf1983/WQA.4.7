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
public class PHDevMock extends DevMock{

    // <editor-fold defaultstate="collapsed" desc="EC寄存器"> 
    //---------------------------------------------------------------------------------------
    public final IREG ALARM = new IREG(0x00, 1, "报警码"); //R
    public final FREG PH = new FREG(0x01, 2, "PH/ORP");       //R
    public final FREG TEMPER = new FREG(0x05, 2, "温度数据");   //R
    public final FREG OPH = new FREG(0x07, 2, "PH原始信号(mv)");   //R(mv)

    public final FREG[] CLODATA = new FREG[]{new FREG(0x31, 2, "原始数据1"), new FREG(0x35, 2, "原始数据2")};  //R/W
    public final FREG[] CLTDATA = new FREG[]{new FREG(0x33, 2, "定标数据1"), new FREG(0x37, 2, "定标数据2")};  //R/W
    public final IREG CLSTART = new IREG(0x39, 1, ""); //R/W
    public final FREG CLTEMPER = new FREG(0x3A, 2, "");    //R/W
    public final IREG CLTEMPERSTART = new IREG(0x3C, 1, "");//R/W
    // </editor-fold> 
    
    public PHDevMock(){
        super();
        client.RegisterREGS(
                ALARM,
                PH,
                TEMPER,
                OPH,
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
        DEVTYPE.SetValue(0x0208);
        ///////////////////////////////////////////////////////////
        ALARM.SetValue(0);
        PH.SetValue((float) PH.RegAddr());
        TEMPER.SetValue((float) TEMPER.RegAddr());
        OPH.SetValue((float) OPH.RegAddr());
        WriteREGS();
    }
    
}
