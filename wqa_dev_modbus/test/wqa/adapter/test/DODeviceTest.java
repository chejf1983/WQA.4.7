/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.test;

import wqa.adapter.devmock.DODevMock;
import java.util.ArrayList;
import modebus.pro.NahonConvert;
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
public class DODeviceTest {

    public DODeviceTest() throws Exception {
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
    
    // <editor-fold defaultstate="collapsed" desc="配置测试（包含AbsDev的公共配置选项）">
    private void PrintConfigItem(ArrayList<SConfigItem> result) {
        result.forEach(item -> {
            PrintLog.println(item.inputtype + "-" + item.data_name + ":" + item.value);
        });
    }

    private void CheckConfigInfo() throws Exception {
        PrintLog.println("InfoList:");
        ArrayList<SConfigItem> result = instance.GetInfoList();
        PrintConfigItem(result);
        dev_mock.ReadREGS();
        //设备读取应该与本地寄存器相同
        result.forEach(item -> {
            if (item.IsKey(dev_mock.SERIANUM.toString())) {
                assertEquals(dev_mock.SERIANUM.GetValue(), item.value);
            }
            if (item.IsKey(dev_mock.HWVER.toString())) {
                assertEquals(dev_mock.HWVER.GetValue(), item.value);
            }
            if (item.IsKey(dev_mock.SWVER.toString())) {
                assertEquals(dev_mock.SWVER.GetValue(), item.value);
            }
            if (item.IsKey(dev_mock.DEVTYPE.toString())) {
                assertEquals(String.format("0X%04X", dev_mock.DEVTYPE.GetValue()), item.value);
            }
        });
        PrintLog.println("ConfigList:");
        result = instance.GetConfigList();
        PrintConfigItem(result);
        //设备读取应该与本地寄存器相同
        result.forEach(item -> {
            if (item.IsKey(dev_mock.DEVADDR.toString())) {
                assertEquals(dev_mock.DEVADDR.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.BANDRANGEI.toString())) {
                assertEquals(AbsDevice.SBandRate[dev_mock.BANDRANGEI.GetValue()], item.value);
            }
            if (item.IsKey(dev_mock.SDTEMPSWT.toString())) {
                assertEquals(dev_mock.SDTEMPSWT.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.SDTEMP.toString())) {
                assertEquals(dev_mock.SDTEMP.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.PASCA.toString())) {
                assertEquals(NahonConvert.TimData(dev_mock.PASCA.GetValue(), 2) + "", item.value);
            }
            if (item.IsKey(dev_mock.SALT.toString())) {
                assertEquals(NahonConvert.TimData(dev_mock.SALT.GetValue(), 2) + "", item.value);
            }
            if (item.IsKey(dev_mock.AVR.toString())) {
                assertEquals(dev_mock.AVR.GetValue().toString(), item.value);
            }
        });
    }

    /**
     * Test of GetConfigList method, of class DODevice.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetConfigList() throws Exception {
        PrintLog.println("***********************************");
        this.CheckConfigInfo();
    }

    /**
     * Test of SetConfigList method, of class DODevice.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetConfigList() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("SetConfigList");
        ArrayList<SConfigItem> result = instance.GetConfigList();
        //修改值都增加1
        result.forEach(item -> {
            if (item.IsKey(dev_mock.DEVADDR.toString())) {
                item.value = dev_mock.DEVADDR.GetValue() + 1 + "";
            }
            if (item.IsKey(dev_mock.BANDRANGEI.toString())) {
                item.value = AbsDevice.SBandRate[dev_mock.BANDRANGEI.GetValue() + 1] + "";
            }
            if (item.IsKey(dev_mock.SDTEMPSWT.toString())) {
                item.value = !dev_mock.SDTEMPSWT.GetValue() + "";
            }
            if (item.IsKey(dev_mock.SDTEMP.toString())) {
                item.value = dev_mock.SDTEMP.GetValue() + 1 + "";
            }
            if (item.IsKey(dev_mock.PASCA.toString())) {
                item.value = dev_mock.PASCA.GetValue() + 1 + "";
            }
            if (item.IsKey(dev_mock.SALT.toString())) {
                item.value = dev_mock.SALT.GetValue() + 1 + "";
            }
            if (item.IsKey(dev_mock.AVR.toString())) {
                item.value = dev_mock.AVR.GetValue() + 1 + "";
            }
        });
        instance.SetConfigList(result);
        this.CheckConfigInfo();
    }
    // </editor-fold> 

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
}
