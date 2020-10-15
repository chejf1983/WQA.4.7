/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.factory;

import base.migp.reg.MEG;
import wqa.adapter.factory.TestIO;
import wqa.dev.data.LogNode;
import wqa.dev.data.SDevInfo;

/**
 *
 * @author chejf
 */
public class MockDev extends AbsDevice{

    public MockDev(byte devaddr) {
        super(new SDevInfo(new TestIO(), 0x00, devaddr, "", SDevInfo.ProType.MIGP));
    }

    public void GETMEG(MEG... megs){
        try {
            this.ReadMEG(megs);
        } catch (Exception ex) {
        }
    }
   
    public void SETMEG(MEG... megs){
        try {
            this.SetMEG(megs);
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
