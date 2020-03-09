/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.test;

import java.util.ArrayList;
import modebus.register.REG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import wqa.adapter.devmock.DevMock;
import wqa.adapter.factory.AbsDevice;
import wqa.adapter.model.PrintLog;
import wqa.control.common.CDevDataTable;
import wqa.control.config.SConfigItem;

/**
 *
 * @author chejf
 */
public class ABS_Test {

    public ABS_Test() throws Exception {
        PrintLog.SetPrintlevel(PrintLog.PRINTLOG);
    }
    
    // <editor-fold defaultstate="collapsed" desc="读取config测试">
    protected void testreadconfig(ArrayList<SConfigItem> config, DevMock dev_mock) throws Exception{        
        this.check_item(config, dev_mock.DEVADDR, dev_mock.DEVADDR.GetValue().toString());
        this.check_item(config, dev_mock.BANDRANGEI, AbsDevice.SBandRate[dev_mock.BANDRANGEI.GetValue()]);
    }
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="设置config测试">
    protected void testsetconfig(ArrayList<SConfigItem> config, DevMock dev_mock) throws Exception{        
        this.set_item(config, dev_mock.DEVADDR, "2");
        this.set_item(config, dev_mock.BANDRANGEI, AbsDevice.SBandRate[2]);
//        this.set_item(config, dev_mock.SDTEMPSWT, !dev_mock.SDTEMPSWT.GetValue() + "");
//        this.set_item(config, dev_mock.SDTEMP, "22.0");
    }
    
    protected void testcheckconfig(ArrayList<SConfigItem> config, DevMock dev_mock) throws Exception{    
        this.check_item(config, dev_mock.DEVADDR, "2");
        this.check_item(config, dev_mock.BANDRANGEI, AbsDevice.SBandRate[2]);
//        this.set_item(config, dev_mock.SDTEMPSWT, !dev_mock.SDTEMPSWT.GetValue() + "");
//        this.set_item(config, dev_mock.SDTEMP, "22.0");
    }
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="读取info测试">
    protected void testreadinfo(ArrayList<SConfigItem> config, DevMock dev_mock)throws Exception{       
        this.check_item(config, dev_mock.DEVNAME, CDevDataTable.GetInstance().namemap.get(dev_mock.DEVTYPE.GetValue()).dev_name_ch);
        this.check_item(config, dev_mock.SERIANUM, dev_mock.SERIANUM.GetValue());
        this.check_item(config, dev_mock.SWVER, dev_mock.SWVER.GetValue());
        this.check_item(config, dev_mock.HWVER, dev_mock.HWVER.GetValue());
        this.check_item(config, dev_mock.DEVTYPE, String.format("0X%04X", dev_mock.DEVTYPE.GetValue()));
    }
    // </editor-fold> 
        
    // <editor-fold defaultstate="collapsed" desc="设置寄存器接口">
    protected void set_item(ArrayList<SConfigItem> list, REG reg, String testvalue) throws Exception {
        for (SConfigItem item : list) {
            if (item.IsKey(reg.toString())) {
                item.SetValue(testvalue);
                break;
            }
        }
    }

    protected void check_item(ArrayList<SConfigItem> list, REG reg, String testvalue) throws Exception {
        for (SConfigItem item : list) {
            if (item.IsKey(reg.toString())) {
                assertEquals(item.value, testvalue);
                PrintLog.println(item.inputtype + "-" + item.data_name + "[设置值]:" + testvalue + "[读取结果]:" + item.value + "[设备值]:" + reg.GetValue().toString());
                return;
            }
        }
        fail("没有找到配置项" + reg.toString());
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="打印寄存器值">
    protected void printREG(REG reg) throws Exception {
        PrintLog.println("寄存器[" + reg.toString() + "]:" + reg.GetValue().toString());
    }
    // </editor-fold> 

}
