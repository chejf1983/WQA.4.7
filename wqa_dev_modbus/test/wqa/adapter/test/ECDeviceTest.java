/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.test;

import wqa.adapter.devmock.ECDevMock;
import java.util.ArrayList;
import modebus.pro.NahonConvert;
import modebus.register.FREG;
import org.junit.Test;
import wqa.adapter.ESA.ECDevice;
import static org.junit.Assert.*;
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
public class ECDeviceTest {

    public ECDeviceTest() throws Exception {
        if (instance == null) {
            this.InitDevice();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    public static ECDevice instance;
    public static ECDevMock dev_mock;

    private void InitDevice() throws Exception {
        dev_mock = new ECDevMock();
        dev_mock.ResetREGS();
        MOCKIO io = new MOCKIO(dev_mock.client);
        io.Open();
        IDevice devs = new ModBusDevFactory().SearchOneDev(new ShareIO(io), (byte) 02);
        if (devs != null) {
            instance = (ECDevice) devs;
            instance.InitDevice();
        }
    }
    // </editor-fold> 

    /**
     * Test of CollectData method, of class ECDevice.
     */
    @Test
    public void testCollectData() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("CollectData");

        SDisplayData result = instance.CollectData();
        FREG[] regs = new FREG[]{dev_mock.EC, dev_mock.OEC, dev_mock.TEMPER, dev_mock.OTEMPER, dev_mock.SALT};
        for (int i = 0; i < result.datas.length; i++) {
            SDataElement data = result.datas[i];
            PrintLog.println(data.name + ":" + data.mainData + ":" + data.unit + ":" + data.range_info
                    + "-------" + regs[i].toString() + regs[i].GetValue());
            assertEquals(String.format("%.2f", regs[i].GetValue()), String.format("%.2f", data.mainData));
        }
        PrintLog.println("报警码" + result.alarm + "-------" + dev_mock.ALARM.toString() + dev_mock.ALARM.GetValue());
        assertEquals(dev_mock.ALARM.GetValue() + "", result.alarm + "");
    }

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
            if (item.IsKey(dev_mock.ECRANG.toString())) {
                assertEquals(ECDevice.EC_UNIT_STRING[dev_mock.ECRANG.GetValue()], item.value);
            }
            if (item.IsKey(dev_mock.PAREC.toString())) {
                assertEquals(dev_mock.PAREC.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.SALTRANGE.toString())) {
                assertEquals(ECDevice.SALT_UNIT_STRING[dev_mock.SALTRANGE.GetValue()], item.value);
            }
            if (item.IsKey(dev_mock.CMPTDS.toString())) {
                assertEquals(NahonConvert.TimData(dev_mock.CMPTDS.GetValue(), 2) + "", item.value);
            }
            if (item.IsKey(dev_mock.CMPTEMP.toString())) {
                assertEquals(NahonConvert.TimData(dev_mock.CMPTEMP.GetValue(), 2) + "", item.value);
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
            
            if (item.IsKey(dev_mock.ECRANG.toString())) {
                item.value = dev_mock.ECRANG.GetValue() + 1 + "";
            }
            if (item.IsKey(dev_mock.PAREC.toString())) {
                item.value = dev_mock.PAREC.GetValue() + 1 + "";
            }
            if (item.IsKey(dev_mock.SALTRANGE.toString())) {
                item.value = dev_mock.SALTRANGE.GetValue() + 1 + "";
            }
            if (item.IsKey(dev_mock.CMPTDS.toString())) {
                item.value = dev_mock.CMPTDS.GetValue() + 1 + "";
            }
            if (item.IsKey(dev_mock.CMPTEMP.toString())) {
                item.value = dev_mock.CMPTEMP.GetValue() + 1 + "";
            }
        });
        instance.SetConfigList(result);
        this.CheckConfigInfo();
    }
    // </editor-fold> 

    /**
     * Test of CalParameter method, of class ECDevice.
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
                        assertEquals(String.format("%.2f", dev_mock.CLODATA[j].GetValue()), String.format("%.2f", 32f + j));
                        assertEquals(String.format("%.2f", dev_mock.CLTDATA[j].GetValue()), String.format("%.2f", 30f - j));
                    }
                    assertEquals(dev_mock.CLSTART.GetValue() + "", i + "");
                }
            }
        }
    }
}
