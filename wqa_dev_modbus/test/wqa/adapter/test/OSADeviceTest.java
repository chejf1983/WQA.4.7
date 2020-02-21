/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.test;

import java.util.ArrayList;
import modebus.pro.NahonConvert;
import modebus.register.REG;
import org.junit.Test;
import wqa.adapter.OSA.OSADevice;
import static org.junit.Assert.*;
import wqa.adapter.devmock.OSADevMock;
import wqa.adapter.factory.AbsDevice;
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
//        PrintLog.PintSwitch = true;
        if (instance == null) {
            this.InitDevice();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    public static OSADevice instance;
    public static OSADevMock dev_mock;

    private void InitDevice() throws Exception {
        dev_mock = new OSADevMock();
        dev_mock.ResetREGS();
        MOCKIO io = new MOCKIO(dev_mock.client);
        io.Open();
        IDevice devs = new ModBusDevFactory().SearchOneDev(new ShareIO(io), (byte) 02);
        if (devs != null) {
            instance = (OSADevice) devs;
            instance.InitDevice();
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="配置测试">
    private void PrintConfigItem(ArrayList<SConfigItem> result) {
        result.forEach(item -> {
            PrintLog.println(item.inputtype + "-" + item.data_name + ":" + item.value);
        });
    }

    private void CheckConfigInfo() throws Exception {
        PrintLog.println("ConfigList:");
        ArrayList<SConfigItem> result = instance.GetConfigList();
        PrintConfigItem(result);

        dev_mock.ReadREGS();
        //设备读取应该与本地寄存器相同
        result.forEach(item -> {
            if (item.IsKey(dev_mock.RANGE.toString())) {
                assertEquals(item.range[dev_mock.RANGE.GetValue()], item.value);
            }
            if (item.IsKey(dev_mock.AVR.toString())) {
                assertEquals(dev_mock.AVR.GetValue().toString(), item.value);
            }
        });
    }

    /**
     * Test of GetConfigList method, of class OSADevice.
     */
    @Test
    public void testGetConfigList() throws Exception {
        PrintLog.println("***********************************");
        this.CheckConfigInfo();
    }

    /**
     * Test of SetConfigList method, of class OSADevice.
     */
    @Test
    public void testSetConfigList() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("SetConfigList");
        ArrayList<SConfigItem> result = instance.GetConfigList();
        //修改值都增加1
        result.forEach(item -> {
            if (item.IsKey(dev_mock.RANGE.toString())) {
                item.value = dev_mock.RANGE.GetValue() + 1 + "";
            }
            if (item.IsKey(dev_mock.AVR.toString())) {
                item.value = dev_mock.AVR.GetValue() + 1 + "";
            }
        });
        instance.SetConfigList(result);
        this.CheckConfigInfo();
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="电机刷测试">
    /**
     * Test of GetMotoPara method, of class OSADevice.
     */
    @Test
    public void testGetMotoPara() {
        PrintLog.println("***********************************");
        PrintLog.println("GetMotoPara");
        SMotorParameter result = instance.GetMotoPara();
        assertEquals(dev_mock.CMODE.GetValue() + "", result.mode == SMotorParameter.CleanMode.Auto ? "0" : "1");
        for (SConfigItem item : result.auto_config) {
            if (item.IsKey(dev_mock.CTIME.toString())) {
                assertEquals(item.value, dev_mock.CTIME.GetValue() + "");
            }
            if (item.IsKey(dev_mock.CINTVAL.toString())) {
                assertEquals(item.value, dev_mock.CINTVAL.GetValue() + "");
            }
        }

        assertEquals(result.manu_config.length, 0);
    }

    /**
     * Test of SetMotoPara method, of class OSADevice.
     */
    @Test
    public void testSetMotoPara() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("SetMotoPara");
        SMotorParameter result = instance.GetMotoPara();
        assertEquals(dev_mock.CMODE.GetValue() + "", result.mode == SMotorParameter.CleanMode.Auto ? "0" : "1");
        for (SConfigItem item : result.auto_config) {
            if (item.IsKey(dev_mock.CTIME.toString())) {
                item.value = dev_mock.CTIME.GetValue() + 1 + "";
            }
            if (item.IsKey(dev_mock.CINTVAL.toString())) {
                item.value = dev_mock.CINTVAL.GetValue() + 1 + "";
            }
        }

        instance.SetMotoPara(result);
        dev_mock.ReadREGS();
        
        result = instance.GetMotoPara();
        assertEquals(dev_mock.CMODE.GetValue() + "", result.mode == SMotorParameter.CleanMode.Auto ? "0" : "1");
        for (SConfigItem item : result.auto_config) {
            if (item.IsKey(dev_mock.CTIME.toString())) {
                assertEquals(item.value, dev_mock.CTIME.GetValue() + "");
            }
            if (item.IsKey(dev_mock.CINTVAL.toString())) {
                assertEquals(item.value, dev_mock.CINTVAL.GetValue() + "");
            }
        }

        assertEquals(result.manu_config.length, 0);
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
}
