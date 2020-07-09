/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.model;

import base.migp.reg.MEG;
import java.util.ArrayList;
import migp.adapter.factory.AbsDevice;
import migp.adapter.mock.DevMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import wqa.dev.intf.SConfigItem;

/**
 *
 * @author chejf
 */
public class ABS_Test {

    public AbsDevice absinstance;
    public DevMock absdev_mock;

    public void SetPar(AbsDevice instance, DevMock dev_mock) throws Exception {
        PrintLog.SetPrintlevel(PrintLog.IOLOG | PrintLog.PRINTLOG);
        absinstance = instance;
        absdev_mock = dev_mock;
    }
    
    public void printItemlist(ArrayList<SConfigItem> list) {
        for (SConfigItem item : list) {
            PrintLog.println(item.inputtype + "-" + item.data_name + "[读取结果]:" + item.GetValue() + item.range + item.unit);
        }
    }

    @Test
    public void test_mock() throws Exception {
        PrintLog.println("测试ABSTest");
    }

    // <editor-fold defaultstate="collapsed" desc="读取info测试">
    public void check_infolist() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("检查info配置");
        ArrayList<SConfigItem> config = absinstance.GetInfoList();
        absdev_mock.ReadREGS();
        this.check_item(config, absdev_mock.EDEVNAME);
        this.check_item(config, absdev_mock.EBUILDSER);
        this.check_item(config, absdev_mock.EBUILDDATE);
        this.check_item(config, absdev_mock.ESWVER);
        this.check_item(config, absdev_mock.EHWVER);
        this.check_item(config, absdev_mock.VDEVTYPE, String.format("0X%04X", absdev_mock.VDEVTYPE.GetValue()));
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置info测试">
    public void setinfolist_setup() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("设置info配置");
        ArrayList<SConfigItem> config = absinstance.GetInfoList();
        this.set_item(config, absdev_mock.EDEVNAME, "TESTM01");
        this.set_item(config, absdev_mock.EBUILDSER, "20200310");
        this.set_item(config, absdev_mock.EBUILDDATE, "02010022");
        absinstance.SetInfoList(config);
    }

    public void setinfolist_check() throws Exception {
        absdev_mock.ReadREGS();
        assertEquals(absdev_mock.EDEVNAME.GetValue(), "TESTM01");
        assertEquals(absdev_mock.EBUILDSER.GetValue(), "20200310");
        assertEquals(absdev_mock.EBUILDDATE.GetValue(), "02010022");
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="读取config测试">
    public void check_configlist() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("检查config配置");
        ArrayList<SConfigItem> config = absinstance.GetConfigList();
        absdev_mock.ReadREGS();
        config.forEach(item -> {
            if (item.IsKey(AbsDevice.SBandRate)) {
                assertEquals(AbsDevice.BANDRATE_STRING[absdev_mock.client.bandrate], item.GetValue());
                PrintLog.println(item.inputtype + "-" + item.data_name + "[设置值]:" + AbsDevice.BANDRATE_STRING[absdev_mock.client.bandrate] + "[读取结果]:" + item.GetValue() + "[设备值]:" + absdev_mock.client.bandrate);
            }
            if (item.IsKey(AbsDevice.SDevAddr)) {
                assertEquals(absdev_mock.client.addr + "", item.GetValue());
                PrintLog.println(item.inputtype + "-" + item.data_name + "[设置值]:" + absdev_mock.client.addr + "[读取结果]:" + item.GetValue() + "[设备值]:" + absdev_mock.client.addr);
            }
        });
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置config测试">
    public void setconfiglist_setup() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("设置config配置");
        ArrayList<SConfigItem> config = absinstance.GetConfigList();
        set_item(config, AbsDevice.SBandRate, AbsDevice.BANDRATE_STRING[2]);
        set_item(config, AbsDevice.SDevAddr, "5");
        absinstance.SetConfigList(config);
    }

    public void setconfiglist_check() throws Exception {
        absdev_mock.ReadREGS();
        assertEquals(absdev_mock.client.bandrate + "", "2");
        assertEquals(absdev_mock.client.addr + "", "5");
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="配置设置和检查">
    public void set_item(ArrayList<SConfigItem> list, MEG reg, String testvalue) throws Exception {
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

    public void check_item(ArrayList<SConfigItem> list, MEG reg) throws Exception {
        this.check_item(list, reg, reg.GetValue().toString());
    }

    public void check_item(ArrayList<SConfigItem> list, MEG reg, String testvalue) throws Exception {
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
    public void printREG(MEG reg) throws Exception {
        PrintLog.println("寄存器[" + reg.toString() + "]:" + reg.GetValue().toString());
    }
    // </editor-fold> 
}
