/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ISA;

import base.migp.mem.VPA;
import base.migp.reg.FMEG;
import base.migp.reg.MEG;
import java.util.ArrayList;
import static migp.adapter.OSA.OSA_XTest.dev_mock;
import static migp.adapter.OSA.OSA_XTest.instance;
import migp.adapter.factory.MIGPDevFactory;
import migp.adapter.mock.ISAMock;
import org.junit.Test;
import static org.junit.Assert.*;
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
public class ISA_XTest {
    
    public ISA_XTest() throws Exception {
        if (instance == null) {
//            PrintLog.PintSwitch = true;
            PrintLog.SetPrintlevel(PrintLog.PRINTLOG);
            this.InitDevice();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    public static ISA_X instance;
    public static ISAMock dev_mock;
    
    private void InitDevice() throws Exception {
        dev_mock = new ISAMock();
        dev_mock.ResetREGS();
        MOCKIO io = new MOCKIO(dev_mock.client);
        io.Open();
        IDevice devs = new MIGPDevFactory().SearchOneDev(new ShareIO(io), (byte) 02);
        if (devs != null) {
            instance = (ISA_X) devs;
            instance.InitDevice();
        }
        
        PrintLog.println("\r\n打印设置列表");
        PrintConfigItem(instance.GetConfigList());
        PrintLog.println("\r\n打印系数列表");
        PrintConfigItem(instance.GetCalParList());
        PrintLog.println("\r\n打印信息列表");
        PrintConfigItem(instance.GetInfoList());
    }
    
    private void PrintConfigItem(ArrayList<SConfigItem> result) {
        result.forEach(item -> {
            PrintLog.println(item.inputtype + "-" + item.data_name + ":" + item.value);
        });
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="测试设置功能">
    private void check_config_item(MEG reg, String testvalue) throws Exception {
        //设置寄存器
        ArrayList<SConfigItem> list = instance.GetConfigList();
        for (SConfigItem item : list) {
            if (item.IsKey(reg.toString())) {
                item.SetValue(testvalue);
                instance.SetConfigList(list);
                break;
            }
        }
        //比较测试结果
        list = instance.GetConfigList();
        for (SConfigItem item : list) {
            if (item.IsKey(reg.toString())) {
                assertEquals(item.value, testvalue);
                dev_mock.ReadREGS();
                PrintLog.println(item.inputtype + "-" + item.data_name + "[设置值]:" + testvalue + "[读取结果]:" + item.value + "[设备值]:" + reg.GetValue().toString());
                return;
            }
        }
        fail("没有找到配置项" + reg.toString());
    }

    /**
     * Test of SetConfigList method, of class OSA_X.
     */
    @Test
    public void testSetConfigList() throws Exception {
        PrintLog.println("*****************************************");
        PrintLog.println("SetConfigList");
        check_config_item(dev_mock.NK_COM, "2.0");
        check_config_item(dev_mock.NCL_COM, "3.0");
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="检查定标系数">
    private void check_cal_item(MEG reg, String testvalue) throws Exception {
        //设置寄存器
        ArrayList<SConfigItem> list = instance.GetCalParList();
        for (SConfigItem item : list) {
            if (item.IsKey(reg.toString())) {
                item.SetValue(testvalue);
                instance.SetCalParList(list);
            }
        }
        //比较测试结果
        list = instance.GetCalParList();
        for (SConfigItem item : list) {
            if (item.IsKey(reg.toString())) {
                assertEquals(item.value, testvalue);
                dev_mock.ReadREGS();
                PrintLog.println(item.inputtype + "-" + item.data_name + "设置值:" + testvalue + "读取结果:" + item.value + "设备值:" + reg.GetValue().toString());
                return;
            }
        }
        fail("没有找到配置项" + reg.toString());
    }
    
    private void check_cal_item(MEG reg, String name, String testvalue) throws Exception {
        //设置寄存器
        ArrayList<SConfigItem> list = instance.GetCalParList();
        for (SConfigItem item : list) {
            if (item.IsKey(name)) {
                item.SetValue(testvalue);
                instance.SetCalParList(list);
            }
        }
        //比较测试结果
        list = instance.GetCalParList();
        for (SConfigItem item : list) {
            if (item.IsKey(name)) {
                assertEquals(item.value, testvalue);
                dev_mock.ReadREGS();
                PrintLog.println(item.inputtype + "-" + item.data_name + "设置值:" + testvalue + "读取结果:" + item.value + "设备值:" + reg.GetValue().toString());
                return;
            }
        }
        fail("没有找到配置项" + reg.toString());
    }

    /**
     * Test of SetCalParList method, of class OSA_X.
     */
    @Test
    public void testSetCalParList() throws Exception {
        PrintLog.println("*****************************************");
        PrintLog.println("SetCalParList");
        check_cal_item(dev_mock.NTEMP_CAL, "3.0");
        for (int i = 0; i < instance.GetCalDataList().length - 1; i++) {
            check_cal_item(dev_mock.NAS[i], instance.GetCalDataList()[i].data_name + dev_mock.NAS[i].toString(), "3.0");
            check_cal_item(dev_mock.NES[i], instance.GetCalDataList()[i].data_name + dev_mock.NAS[i].toString(),"22.0");
            check_cal_item(dev_mock.NFS[i], instance.GetCalDataList()[i].data_name + dev_mock.NAS[i].toString(),"12.0");
        }
    }
    // </editor-fold> 

    /**
     * Test of CollectData method, of class ISA_X.
     */
    @Test
    public void testCollectData() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("CollectData");
        
        SDisplayData result = instance.CollectData();
//        FMEG TMP = new FMEG(new VPA(0, 4), "");
//        TMP.SetValue((float) (dev_mock.SR3.GetValue() - dev_mock.SR4.GetValue()));
        MEG[] regs = new MEG[]{dev_mock.MPAR1, dev_mock.SR1, dev_mock.MPAR2, dev_mock.SR2,
            dev_mock.MPAR3, dev_mock.SR3, dev_mock.MPAR4, dev_mock.SR4, dev_mock.MPAR5, dev_mock.SR5};
        for (int i = 0; i < result.datas.length; i++) {
            SDataElement data = result.datas[i];
            PrintLog.println(data.name + ":" + data.mainData + ":" + data.unit + ":" + data.range_info
                    + "-------" + regs[i].toString() + ":" + regs[i].GetValue());
            assertEquals(String.format("%.2f", Float.valueOf(regs[i].GetValue().toString())), String.format("%.2f", (float) data.mainData));
        }
        PrintLog.println("报警码" + result.alarm + "-------" + dev_mock.MALARM.toString() + dev_mock.MALARM.GetValue());
        assertEquals(dev_mock.MALARM.GetValue() + "", result.alarm + "");
    }

    // <editor-fold defaultstate="collapsed" desc="定标测试">
    private void printREG(MEG reg) throws Exception {
        dev_mock.ReadREGS();
        PrintLog.println("寄存器[" + reg.toString() + "]:" + reg.GetValue().toString());
    }

    /**
     * Test of CalParameter method, of class ISA_X.
     */
    @Test
    public void testCalParameter() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("CalParameter");
        CDevDataTable.DataInfo[] cal_infos = instance.GetCalDataList();
        for (CDevDataTable.DataInfo info : cal_infos) {
            PrintLog.println(info.data_name);
            if ("温度".equals(info.data_name)) {
                instance.CalParameter(info.data_name, new float[]{134f}, new float[]{132f});
                printREG(dev_mock.NTEMP_CAL);
            } else {
                for (int i = 1; i <= info.cal_num; i++) {
                    float[] oradata = new float[i];
                    float[] testdata = new float[i];
                    for (int j = 0; j < oradata.length; j++) {
                        oradata[j] = 132f + (float) Math.random() * j;
                        testdata[j] = 130f - (float) Math.random() * j;
                    }
                    PrintLog.println(i + "点定标");
                    instance.CalParameter(info.data_name, oradata, testdata);
                    printREG(dev_mock.SCLNUM);
                    printREG(dev_mock.SCLODATA[0]);
                    printREG(dev_mock.SCLODATA[1]);
                    printREG(dev_mock.SCLTDATA[0]);
                    printREG(dev_mock.SCLTDATA[1]);
                }
            }
        }
    }
    // </editor-fold> 

}
