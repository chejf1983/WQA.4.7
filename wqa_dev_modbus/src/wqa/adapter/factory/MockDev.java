/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.factory;

import modebus.register.REG;
import wqa.dev.data.LogNode;
import wqa.dev.data.SDevInfo;

/**
 *
 * @author chejf
 */
public class MockDev extends AbsDevice{

    public MockDev(byte devaddr) {
        super(new SDevInfo(new TestIO(), 0x00, devaddr, "", SDevInfo.ProType.MODEBUS));
    }

    public void GETMEG(REG... megs){
        try {
            this.ReadREG(megs);
        } catch (Exception ex) {
        }
    }
   
    public void SETMEG(REG... megs){
        try {
            this.SetREG(megs);
        } catch (Exception ex) {
        }
    }
    
    @Override
    public wqa.dev.data.CollectData CollectData() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
