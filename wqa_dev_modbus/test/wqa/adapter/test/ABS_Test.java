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
import wqa.adapter.factory.*;
import wqa.dev.data.*;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class ABS_Test {

public ABS_Test(AbsDevice instance, DevMock dev_mock) throws Exception {
        PrintLog.SetPrintlevel(PrintLog.PRINTLOG);
        this.instance = instance;
        this.dev_mock = dev_mock;
    }

    public AbsDevice instance;
    public DevMock dev_mock;

    // <editor-fold defaultstate="collapsed" desc="读取info测试">
    public void check_infolist() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("检查info配置");
        ArrayList<SConfigItem> config = instance.GetInfoList();
        dev_mock.ReadREGS();
        this.check_item(config, dev_mock.DEVNAME, CDevDataTable.GetInstance().namemap.get(dev_mock.DEVTYPE.GetValue()).dev_name_ch);
        this.check_item(config, dev_mock.SERIANUM);
        this.check_item(config, dev_mock.SWVER);
        this.check_item(config, dev_mock.HWVER);
        this.check_item(config, dev_mock.DEVTYPE, String.format("0X%04X", dev_mock.DEVTYPE.GetValue()));        
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="读取config测试">
    public void check_configlist() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("设置config配置");
        ArrayList<SConfigItem> config = instance.GetConfigList();
        dev_mock.ReadREGS();          
        this.check_item(config, dev_mock.DEVADDR, dev_mock.DEVADDR.GetValue().toString());
        this.check_item(config, dev_mock.BANDRANGEI, AbsDevice.SBandRate[dev_mock.BANDRANGEI.GetValue()]);
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置config测试">
    public void setconfiglist_setup() throws Exception {
        ArrayList<SConfigItem> config = instance.GetConfigList();
        this.set_item(config, dev_mock.DEVADDR, "2");
        this.set_item(config, dev_mock.BANDRANGEI, AbsDevice.SBandRate[5]);
        instance.SetConfigList(config);
    }

    public void setconfiglist_check() throws Exception {
        dev_mock.ReadREGS();
        assertEquals(dev_mock.DEVADDR.GetValue().toString(), "2");
        assertEquals(dev_mock.BANDRANGEI.GetValue().toString(), "5");
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="配置设置和检查">
    public void set_item(ArrayList<SConfigItem> list, REG reg, String testvalue) throws Exception {
        set_item(list, reg.toString(), testvalue);
    }

    public void set_item(ArrayList<SConfigItem> list, String itemname, String testvalue) throws Exception {
        for (SConfigItem item : list) {
            if (item.IsKey(itemname)) {
                item.SetValue(testvalue);
                return;
            }
        }
        fail("没有找到配置项" + itemname.toString());
    }

    public void check_item(ArrayList<SConfigItem> list, REG reg) throws Exception {
        this.check_item(list, reg, reg.GetValue().toString());
    }

    public void check_item(ArrayList<SConfigItem> list, REG reg, String testvalue) throws Exception {
        check_item(list, reg.toString(), testvalue);
    }
        
    public void check_item(ArrayList<SConfigItem> list, String name, String testvalue) throws Exception {
        for (SConfigItem item : list) {
            if (item.IsKey(name)) {
                if (!item.GetValue().contentEquals(testvalue)) {
                    fail(item.data_name + "检查失败![读取结果]:" + item.GetValue() + "[期待值]:" + testvalue);
                } else {
                    PrintLog.println(item.inputtype + "-" + item.data_name + "[读取结果]:" + item.GetValue() + "[期待值]:" + testvalue);
                    return;
                }
            }
        }
        fail("没有找到配置项" + name.toString());
    }
    // </editor-fold>
      
    // <editor-fold defaultstate="collapsed" desc="打印寄存器值">
    protected void printREG(REG reg) throws Exception {
        PrintLog.println("寄存器[" + reg.toString() + "]:" + reg.GetValue().toString());
    }
    // </editor-fold> 

}
