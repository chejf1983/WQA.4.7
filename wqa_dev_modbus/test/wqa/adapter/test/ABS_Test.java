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
import wqa.control.config.SConfigItem;

/**
 *
 * @author chejf
 */
public class ABS_Test {

    public ABS_Test() throws Exception {
        PrintLog.SetPrintlevel(PrintLog.PRINTLOG);
    }

    public static AbsDevice absinstance;
    public static DevMock absdev_mock;

    protected void InitDevice(AbsDevice instance, DevMock dev_mock) throws Exception {
        absinstance = instance;
        absdev_mock = dev_mock;

        PrintLog.println("\r\n打印设置列表");
        PrintConfigItem(absinstance.GetConfigList());
//        PrintLog.println("\r\n打印系数列表");
//        PrintConfigItem(absinstance.GetCalParList());
        PrintLog.println("\r\n打印信息列表");
        PrintConfigItem(absinstance.GetInfoList());
    }

    protected void PrintConfigItem(ArrayList<SConfigItem> result) {
        result.forEach(item -> {
            PrintLog.println(item.inputtype + "-" + item.data_name + ":" + item.value);
        });
    }
    
    
    protected void check_info_item(REG reg, String testvalue) throws Exception {
        //设置寄存器
        ArrayList<SConfigItem> list = absinstance.GetInfoList();
        for (SConfigItem item : list) {
            if (item.IsKey(reg.toString())) {
                item.SetValue(testvalue);
                absinstance.SetInfoList(list);
                break;
            }
        }
        //比较测试结果
        list = absinstance.GetInfoList();
        for (SConfigItem item : list) {
            if (item.IsKey(reg.toString())) {
                assertEquals(item.value, testvalue);
                absdev_mock.ReadREGS();
                PrintLog.println(item.inputtype + "-" + item.data_name + "[设置值]:" + testvalue + "[读取结果]:" + item.value + "[设备值]:" + reg.GetValue().toString());
                return;
            }
        }
        fail("没有找到配置项" + reg.toString());
    }

    protected void check_config_item(REG reg, String testvalue) throws Exception {
        //设置寄存器
        ArrayList<SConfigItem> list = absinstance.GetConfigList();
        for (SConfigItem item : list) {
            if (item.IsKey(reg.toString())) {
                item.SetValue(testvalue);
                absinstance.SetConfigList(list);
                break;
            }
        }
        //比较测试结果
        list = absinstance.GetConfigList();
        for (SConfigItem item : list) {
            if (item.IsKey(reg.toString())) {
                assertEquals(item.value, testvalue);
                absdev_mock.ReadREGS();
                PrintLog.println(item.inputtype + "-" + item.data_name + "[设置值]:" + testvalue + "[读取结果]:" + item.value + "[设备值]:" + reg.GetValue().toString());
                return;
            }
        }
        fail("没有找到配置项" + reg.toString());
    }

    protected void printREG(REG reg) throws Exception {
        absdev_mock.ReadREGS();
        PrintLog.println("寄存器[" + reg.toString() + "]:" + reg.GetValue().toString());
    }

}
