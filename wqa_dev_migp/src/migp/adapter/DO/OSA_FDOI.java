/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.DO;

import migp.adapter.ESA.*;
import base.migp.mem.*;
import base.migp.reg.*;
import java.util.ArrayList;
import wqa.dev.data.*;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class OSA_FDOI extends ESA_DO {

    public OSA_FDOI(SDevInfo devinfo) {
        super(devinfo);
    }

    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    FMEG NTEMPER_COM = new FMEG(new NVPA(36, 4), "温度补偿系数"); //ESA-DO特有
    // </editor-fold>

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice(); //To change body of generated methods, choose Tools | Templates.
        this.ReadMEG(NTEMPER_COM);
    }

    // <editor-fold defaultstate="collapsed" desc="配置接口"> 
    @Override
    public ArrayList<SConfigItem> GetConfigList() {
        ArrayList<SConfigItem> item = super.GetConfigList(); //To change body of generated methods, choose Tools | Templates.
        item.add(SConfigItem.CreateRWItem(NTEMPER_COM.toString(), NTEMPER_COM.GetValue().toString(), ""));
        return item;
    }

    @Override
    public void SetConfigList(ArrayList<SConfigItem> list) throws Exception {
        super.SetConfigList(list);
        for (SConfigItem item : list) {
            if (item.IsKey(NTEMPER_COM.toString())) {
                this.SetConfigREG(NTEMPER_COM, item.GetValue());
                break;
            }
        }

    }
    // </editor-fold> 
}
