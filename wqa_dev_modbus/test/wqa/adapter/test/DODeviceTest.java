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
import wqa.adapter.factory.AbsDevice;
import wqa.adapter.factory.ModBusDevFactory;
import wqa.adapter.io.ShareIO;
import wqa.adapter.model.MOCKIO;
import wqa.adapter.model.PrintLog;
import wqa.control.common.CDevDataTable;
import wqa.control.common.IDevice;
import wqa.control.config.SConfigItem;
import wqa.control.dev.collect.SDataElement;
import wqa.control.dev.collect.SDisplayData;

/**
 *
 * @author chejf
 */
public class DODeviceTest extends ABS_Test {

    public DODeviceTest() throws Exception {
        super();
        if (instance == null) {
            this.InitDevice();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    public static DODevice instance;
    public static DODevMock dev_mock;

    private void InitDevice() throws Exception {
        dev_mock = new DODevMock();
        dev_mock.ResetREGS();
        MOCKIO io = new MOCKIO(dev_mock.client);
        io.Open();
        IDevice devs = new ModBusDevFactory().SearchOneDev(new ShareIO(io), (byte) 02);
        if (devs != null) {
            instance = (DODevice) devs;
            instance.InitDevice();
        }    
    }
    // </editor-fold>         
    
    // <editor-fold defaultstate="collapsed" desc="读取info测试">
    @Test
    public void testInfoConfigList() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("ReadInfoList");
        super.testreadinfo(instance.GetInfoList(), dev_mock);        
    }
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="读取config测试">
    @Test
    public void testReadConfigList() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("ReadConfigList");
        
        ArrayList<SConfigItem> config = instance.GetConfigList();
        super.testreadconfig(config, dev_mock);
        this.check_item(config, dev_mock.PASCA, dev_mock.PASCA.GetValue().toString());
        this.check_item(config, dev_mock.SALT, dev_mock.SALT.GetValue().toString());
        this.check_item(config, dev_mock.AVR, dev_mock.AVR.GetValue().toString());  
    }
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="设置config测试">
    /**
     * Test of SetConfigList method, of class DODevice.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetConfigList() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("SetConfigList");
        
        ArrayList<SConfigItem> config = instance.GetConfigList();
        super.testsetconfig(config, dev_mock);
        this.set_item(config, dev_mock.PASCA, "12.0");
        this.set_item(config, dev_mock.SALT, "112.1");
        this.set_item(config, dev_mock.AVR, "3");
        
        instance.SetConfigList(config);
        dev_mock.ReadREGS();
        config = instance.GetConfigList();
        
        this.testcheckconfig(config, dev_mock);
        this.check_item(config, dev_mock.PASCA, "12.0");
        this.check_item(config, dev_mock.SALT, "112.1");
        this.check_item(config, dev_mock.AVR, "3");
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

        SDisplayData result = instance.CollectData();
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
