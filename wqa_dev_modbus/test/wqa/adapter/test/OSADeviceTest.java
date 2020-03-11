/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.test;

import java.util.ArrayList;
import modebus.register.REG;
import org.junit.Test;
import wqa.adapter.OSA.OSADevice;
import static org.junit.Assert.*;
import wqa.adapter.devmock.OSADevMock;
import wqa.adapter.factory.ModBusDevFactory;
import wqa.adapter.io.ShareIO;
import wqa.adapter.model.MOCKIO;
import wqa.adapter.model.PrintLog;
import wqa.control.common.CDevDataTable;
import wqa.control.common.IDevice;
import wqa.control.config.SConfigItem;
import wqa.control.data.SMotorParameter;
import wqa.control.dev.collect.SDataElement;
import wqa.control.dev.collect.SDisplayData;

/**
 *
 * @author chejf
 */
public class OSADeviceTest {

    public OSADeviceTest() throws Exception {
        if (instance == null) {
            this.InitDevice();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    public static OSADevice instance;
    public static OSADevMock dev_mock;
    public static ABS_Test commontest;

    private void InitDevice() throws Exception {
        dev_mock = new OSADevMock();
        dev_mock.ResetREGS();
        MOCKIO io = new MOCKIO(dev_mock.client);
        io.Open();
        IDevice devs = new ModBusDevFactory().SearchOneDev(new ShareIO(io), (byte) 02);
        if (devs != null) {
            instance = (OSADevice) devs;
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
        commontest.check_item(config, dev_mock.RANGE, instance.range_info[dev_mock.RANGE.GetValue()]);
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
        commontest.set_item(config, dev_mock.RANGE, instance.range_info[1]);
        commontest.set_item(config, dev_mock.AVR, "3");
        
        instance.SetConfigList(config);
        dev_mock.ReadREGS();
        
        commontest.setconfiglist_check();
        assertEquals(dev_mock.RANGE.GetValue().toString(), "1");
        assertEquals(dev_mock.AVR.GetValue().toString(), "3");
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="电机刷测试">
    
    private void check_moto_par(SMotorParameter result) throws Exception {
        dev_mock.ReadREGS();
        assertEquals(dev_mock.CMODE.GetValue() + "", result.mode == SMotorParameter.CleanMode.Auto ? "0" : "1");
        PrintLog.println("当前模式" + result.mode.toString());

        if (result.mode == SMotorParameter.CleanMode.Auto) {
            for (SConfigItem item : result.auto_config) {
                if (item.IsKey(dev_mock.CTIME.toString())) {
                    assertEquals(item.value, dev_mock.CTIME.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.CTIME.GetValue());
                }
                if (item.IsKey(dev_mock.CINTVAL.toString())) {
                    assertEquals(item.value, dev_mock.CINTVAL.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.CINTVAL.GetValue());
                }
            }
        } else {
            for (SConfigItem item : result.manu_config) {
                if (item.IsKey(dev_mock.CTIME.toString())) {
                    assertEquals(item.value, dev_mock.CTIME.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.CTIME.GetValue());
                }
                if (item.IsKey(dev_mock.CINTVAL.toString())) {
                    assertEquals(item.value, dev_mock.CINTVAL.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.CINTVAL.GetValue());
                }
            }
        }
    }
    /**
     * Test of GetMotoPara method, of class OSADevice.
     */
    @Test
    public void testGetMotoPara() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("GetMotoPara");
        SMotorParameter result = instance.GetMotoPara();

        for (SConfigItem item : result.manu_config) {
            if (item.IsKey(dev_mock.CTIME.toString())) {
                item.value = "100";
            }
            if (item.IsKey(dev_mock.CINTVAL.toString())) {
                item.value = "60";
            }
        }
        for (SConfigItem item : result.auto_config) {
            if (item.IsKey(dev_mock.CTIME.toString())) {
                item.value = "99";
            }
            if (item.IsKey(dev_mock.CINTVAL.toString())) {
                item.value = "61";
            }
        }
        result.mode = SMotorParameter.CleanMode.Auto;
        instance.SetMotoPara(result);
        result.mode = SMotorParameter.CleanMode.Manu;
        instance.SetMotoPara(result);
        check_moto_par(result);
    }

    /**
     * Test of StartManual method, of class OSADevice.
     */
    @Test
    public void testStartManual() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("StartManual");
        instance.StartManual();
        assertEquals(dev_mock.CMODE.GetValue() + "", instance.GetMotoPara().mode == SMotorParameter.CleanMode.Auto ? "0" : "1");
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="采集测试">
    /**
     * Test of CollectData method, of class OSADevice.
     */
    @Test
    public void testCollectData() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("CollectData");

        SDisplayData result = instance.CollectData();
        REG[] regs = new REG[]{dev_mock.MDATA, dev_mock.ODATA, dev_mock.TEMPER, dev_mock.OTEMPER};
        for (int i = 0; i < result.datas.length; i++) {
            SDataElement data = result.datas[i];
            PrintLog.println(data.name + ":" + data.mainData + ":" + data.unit + ":" + data.range_info
                    + "-------" + regs[i].toString() + regs[i].GetValue());
            assertEquals(String.format("%.2f", Float.valueOf(regs[i].GetValue().toString())), String.format("%.2f", data.mainData));
        }
        PrintLog.println("报警码" + result.alarm + "-------" + dev_mock.ALARM.toString() + dev_mock.ALARM.GetValue());
        assertEquals(dev_mock.ALARM.GetValue() + "", result.alarm + "");
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标测试">
    /**
     * Test of CalParameter method, of class OSADevice.
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
                    instance.CalParameter(info.data_name, oradata, testdata);
                    dev_mock.ReadREGS();
                    for (int j = 0; j < i; j++) {
                        assertEquals(String.format("%.2f", (float) dev_mock.CLODATA[j].GetValue()), String.format("%.2f", 32f + j));
                        assertEquals(String.format("%.2f", dev_mock.CLTDATA[j].GetValue()), String.format("%.2f", 30f - j));
                    }
                    assertEquals(dev_mock.CLSTART.GetValue() + "", i + "");
                }
            }
        }
    }
    // </editor-fold> 
}
