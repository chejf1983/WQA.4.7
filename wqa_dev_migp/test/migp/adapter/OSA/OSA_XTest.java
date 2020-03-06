/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.OSA;

import base.migp.mem.VPA;
import base.migp.reg.FMEG;
import base.migp.reg.MEG;
import java.util.ArrayList;
import static migp.adapter.OSA.OSA_X.AMPPAR;
import migp.adapter.factory.MIGPDevFactory;
import migp.adapter.mock.OSAMock;
import org.junit.Test;
import static org.junit.Assert.*;
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
public class OSA_XTest {

    public OSA_XTest() throws Exception {
        if (instance == null) {
            PrintLog.SetPrintlevel(PrintLog.PRINTLOG);
            this.InitDevice();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    public static OSA_X instance;
    public static OSAMock dev_mock;

    private void InitDevice() throws Exception {
        dev_mock = new OSAMock();
        dev_mock.ResetREGS();
        MOCKIO io = new MOCKIO(dev_mock.client);
        io.Open();
        IDevice devs = new MIGPDevFactory().SearchOneDev(new ShareIO(io), (byte) 02);
        if (devs != null) {
            instance = (OSA_X) devs;
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

    private String get_range_string(int index) {
        return "(" + dev_mock.VDRANGE_MIN[index].GetValue() + "-" + dev_mock.NRANGE_MAX[index].GetValue() + ")";
    }

    /**
     * Test of SetConfigList method, of class OSA_X.
     */
    @Test
    public void testSetConfigList() throws Exception {
        PrintLog.println("*****************************************");
        PrintLog.println("SetConfigList");
        check_config_item(dev_mock.NRANGE, get_range_string(2));
        check_config_item(dev_mock.NAVR, "3");
        check_config_item(dev_mock.NTEMPER_COMP, "24.6");
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

    /**
     * Test of SetCalParList method, of class OSA_X.
     */
    @Test
    public void testSetCalParList() throws Exception {
        PrintLog.println("*****************************************");
        PrintLog.println("SetCalParList");
        check_cal_item(dev_mock.NRANGE_NUM, "3");
        check_cal_item(dev_mock.NTEMPER_PAR, "3.0");
        for (int i = 0; i < dev_mock.NAMPLIFY.length; i++) {
            check_cal_item(dev_mock.NRANGE_MAX[i], "3.0");
            check_cal_item(dev_mock.NCLTEMPER[i], "22.0");
            check_cal_item(dev_mock.NCLPARA[i], "12.0");
            check_cal_item(dev_mock.NCLPARB[i], "13.0");
            check_cal_item(dev_mock.NCLPARC[i], "14.0");
            check_cal_item(dev_mock.NAMPLIFY[i], "2");
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="电机测试">
    private void check_moto_par(SMotorParameter result) throws Exception {
        dev_mock.ReadREGS();
        assertEquals(dev_mock.NCMODE.GetValue() + "", result.mode == SMotorParameter.CleanMode.Auto ? "0" : "1");
        PrintLog.println("当前模式" + result.mode.toString());

        if (result.mode == SMotorParameter.CleanMode.Auto) {
            for (SConfigItem item : result.auto_config) {
                if (item.IsKey(dev_mock.NCTIME.toString())) {
                    assertEquals(item.value, dev_mock.NCTIME.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.NCTIME.GetValue());
                }
                if (item.IsKey(dev_mock.NCINTERVAL.toString())) {
                    assertEquals(item.value, dev_mock.NCINTERVAL.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.NCTIME.GetValue());
                }
                if (item.IsKey(dev_mock.NCBRUSH.toString())) {
                    assertEquals(item.value, dev_mock.NCBRUSH.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.NCTIME.GetValue());
                }
            }
        } else {
            for (SConfigItem item : result.manu_config) {
                if (item.IsKey(dev_mock.NCTIME.toString())) {
                    assertEquals(item.value, dev_mock.NCTIME.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.NCTIME.GetValue());
                }
                if (item.IsKey(dev_mock.NCINTERVAL.toString())) {
                    assertEquals(item.value, dev_mock.NCINTERVAL.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.NCTIME.GetValue());
                }
                if (item.IsKey(dev_mock.NCBRUSH.toString())) {
                    assertEquals(item.value, dev_mock.NCBRUSH.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.NCTIME.GetValue());
                }
            }
        }
    }

    /**
     * Test of SetMotoPara method, of class OSA_X.
     */
    @Test
    public void testSetMotoPara() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("SetMotoPara");
        SMotorParameter result = instance.GetMotoPara();

        for (SConfigItem item : result.manu_config) {
            if (item.IsKey(dev_mock.NCTIME.toString())) {
                item.value = "100";
            }
            if (item.IsKey(dev_mock.NCINTERVAL.toString())) {
                item.value = "60";
            }
            if (item.IsKey(dev_mock.NCBRUSH.toString())) {
                item.value = "3";
            }
        }
        for (SConfigItem item : result.auto_config) {
            if (item.IsKey(dev_mock.NCTIME.toString())) {
                item.value = "99";
            }
            if (item.IsKey(dev_mock.NCINTERVAL.toString())) {
                item.value = "61";
            }
            if (item.IsKey(dev_mock.NCBRUSH.toString())) {
                item.value = "4";
            }
        }
        result.mode = SMotorParameter.CleanMode.Auto;
        instance.SetMotoPara(result);
        result.mode = SMotorParameter.CleanMode.Manu;
        instance.SetMotoPara(result);
        check_moto_par(result);
    }

    /**
     * Test of StartManual method, of class OSA_X.
     */
    @Test
    public void testStartManual() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("StartManual");
        instance.StartManual();
        assertEquals(dev_mock.NCMODE.GetValue() + "", instance.GetMotoPara().mode == SMotorParameter.CleanMode.Auto ? "0" : "1");
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="采集测试">
    /**
     * Test of CollectData method, of class OSA_X.
     */
    @Test
    public void testCollectData() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("CollectData");

        SDisplayData result = instance.CollectData();
        FMEG TMP = new FMEG(new VPA(0, 4), "");
        TMP.SetValue((float) (dev_mock.SR3.GetValue() - dev_mock.SR4.GetValue()));
        MEG[] regs = new MEG[]{dev_mock.MPAR1, TMP, dev_mock.MPAR2, dev_mock.SR5};
        for (int i = 0; i < result.datas.length; i++) {
            SDataElement data = result.datas[i];
            PrintLog.println(data.name + ":" + data.mainData + ":" + data.unit + ":" + data.range_info
                    + "-------" + regs[i].toString() + ":" + regs[i].GetValue());
            assertEquals(String.format("%.2f", regs[i].GetValue()), String.format("%.2f", data.mainData));
        }
        PrintLog.println("报警码" + result.alarm + "-------" + dev_mock.MALARM.toString() + dev_mock.MALARM.GetValue());
        assertEquals(dev_mock.MALARM.GetValue() + "", result.alarm + "");
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标测试">
    private void printREG(MEG reg) throws Exception {
        dev_mock.ReadREGS();
        PrintLog.println("寄存器[" + reg.toString() + "]:" + reg.GetValue().toString());
    }

    /**
     * Test of CalParameter method, of class OSA_X.
     */
    @Test
    public void testCalParameter() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("CalParameter");
        CDevDataTable.DataInfo[] cal_infos = instance.GetCalDataList();
        for (CDevDataTable.DataInfo info : cal_infos) {
            PrintLog.println("");
            PrintLog.println(info.data_name);
            if ("温度".equals(info.data_name)) {
                instance.CalParameter(info.data_name, new float[]{134f}, new float[]{132f});
                printREG(dev_mock.NTEMPER_PAR);
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
                    printREG(dev_mock.NCLTEMPER[dev_mock.NRANGE.GetValue()]);
                    printREG(dev_mock.NCLPARA[dev_mock.NRANGE.GetValue()]);
                    printREG(dev_mock.NCLPARB[dev_mock.NRANGE.GetValue()]);
                    printREG(dev_mock.NCLPARC[dev_mock.NRANGE.GetValue()]);
                }
            }
        }
    }
    // </editor-fold> 
}
