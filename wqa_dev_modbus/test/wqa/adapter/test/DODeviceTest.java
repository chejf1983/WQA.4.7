/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.test;

import java.util.ArrayList;
import wqa.adapter.devmock.DODevMock;
import modebus.register.FREG;
import static org.junit.Assert.*;
import org.junit.Test;
import wqa.adapter.ESA.DODevice;
import wqa.adapter.factory.ModBusDevFactory;
import wqa.adapter.model.MOCKIO;
import wqa.adapter.model.PrintLog;
import wqa.adapter.factory.*;
import wqa.dev.data.*;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class DODeviceTest {

    public DODeviceTest() throws Exception {
        if (instance == null) {
            this.InitDevice();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    public static DODevice instance;
    public static DODevMock dev_mock;
    public static ABS_Test commontest;

    private void InitDevice() throws Exception {
        dev_mock = new DODevMock();
        dev_mock.ResetREGS();
        MOCKIO io = new MOCKIO(dev_mock.client);
        io.Open();
        IDevice devs = new ModBusDevFactory().SearchOneDev((io), (byte) 02);
        if (devs != null) {
            instance = (DODevice) devs;
            instance.InitDevice();
            commontest = new ABS_Test(instance, dev_mock);
        }    
    }
    // </editor-fold>         
        
    // <editor-fold defaultstate="collapsed" desc="读取info测试">
    @Test
    public void test_readinfo() throws Exception {
        commontest.check_infolist();
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="读取config测试">
    @Test
    public void test_readconfig() throws Exception {        
        commontest.check_configlist();
        ArrayList<SConfigItem> config = instance.GetConfigList();
        dev_mock.ReadREGS();
        commontest.check_item(config, dev_mock.PASCA);
        commontest.check_item(config, dev_mock.SALT);
        commontest.check_item(config, dev_mock.AVR);
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置config测试">
    /**
     * Test of SetConfigList method, of class DODevice.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void test_setconfig() throws Exception {        
        commontest.setconfiglist_setup();
        ArrayList<SConfigItem> config = instance.GetConfigList();
        commontest.set_item(config, dev_mock.PASCA, "12.0");
        commontest.set_item(config, dev_mock.SALT, "112.1");
        commontest.set_item(config, dev_mock.AVR, "3");
        
        instance.SetConfigList(config);
        dev_mock.ReadREGS();
        
        commontest.setconfiglist_check();
        assertEquals(dev_mock.PASCA.GetValue().toString(), "12.0");
        assertEquals(dev_mock.SALT.GetValue().toString(), "112.1");
        assertEquals(dev_mock.AVR.GetValue().toString(), "3");
    }
    // </editor-fold> 
        
    // <editor-fold defaultstate="collapsed" desc="采集测试）">
    /**
     * Test of CollectData method, of class DODevice.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCollectData() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("CollectData");

        CollectData result = instance.CollectData();
        FREG[] regs = new FREG[]{dev_mock.DO, dev_mock.DOPEC, dev_mock.ODO, dev_mock.TEMPER, dev_mock.OTEMPER};
        for (int i = 0; i < result.datas.length; i++) {
            SDataElement data = result.datas[i];
            PrintLog.println(data.name + ":" + data.mainData + ":" + data.unit + ":" + data.range_info
                    + "-------" + regs[i].toString() + regs[i].GetValue());
            assertEquals(String.format("%.2f", regs[i].GetValue()), String.format("%.2f", data.mainData));
        }
        PrintLog.println("报警码" + result.alarm + "-------" + dev_mock.ALARM.toString() + dev_mock.ALARM.GetValue());
        assertEquals(dev_mock.ALARM.GetValue() + "", result.alarm + "");
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标测试）">
    /**
     * Test of CalParameter method, of class DODevice.
     */
    @Test
    public void testCalParameter() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("CalParameter");
        CDevDataTable.DataInfo[] cal_infos = instance.GetCalDataList();
        for (CDevDataTable.DataInfo info : cal_infos) {
            PrintLog.println(info.data_name);
            if ("温度".equals(info.data_name)) {
                instance.CalParameter(info.data_name, new float[]{34f}, new float[]{32f});
                dev_mock.ReadREGS();
                assertEquals(String.format("%.2f", dev_mock.CLTEMPER.GetValue()), String.format("%.2f", 32f));
                assertEquals(dev_mock.CLTEMPERSTART.GetValue() + "", 1 + "");
            } else {
                for (int i = 1; i <= info.cal_num; i++) {
                    float[] oradata = new float[i];
                    float[] testdata = new float[i];
                    for (int j = 0; j < oradata.length; j++) {
                        oradata[j] = 32f + j;
                        testdata[j] = 30f - j;
                    }
//                    System.out.println(info.data_name + ":" + info.cal_num);
                    instance.CalParameter(info.data_name, oradata, testdata);
                    dev_mock.ReadREGS();
                    for (int j = 0; j < i; j++) {
                        assertEquals(String.format("%.2f", dev_mock.CLODATA[j].GetValue()), String.format("%.2f", 32f + j));
//                        assertEquals(String.format("%.2f", dev_mock.CLTDATA[0].GetValue()), String.format("%.2f", 34f));
                    }
                    assertEquals(dev_mock.CLSTART.GetValue() + "", i + "");
                }
            }
        }
    }
    // </editor-fold> 
}
