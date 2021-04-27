/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.OSA;

import wqa.adapter.model.ABS_Test;
import base.migp.mem.VPA;
import base.migp.reg.FMEG;
import base.migp.reg.MEG;
import java.util.ArrayList;
import static migp.adapter.factory.AbsDevice.AMPPAR;
import migp.adapter.factory.MIGPDevFactory;
import migp.adapter.mock.OSAMock;
import org.junit.Test;
import static org.junit.Assert.*;
import wqa.adapter.model.MOCKIO;
import wqa.adapter.model.PrintLog;
import wqa.dev.data.*;
import wqa.adapter.factory.*;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class OSA_XTest {

    public OSA_XTest() throws Exception {
        if (dev_mock == null) {
            this.InitDevice();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    public static OSA_X instance;
    public static OSAMock dev_mock;
    public static ABS_Test commontest;

    protected void InitDevice() throws Exception {
        dev_mock = new OSAMock();
        dev_mock.ResetREGS();
        MOCKIO io = new MOCKIO(dev_mock.client);
        io.Open();
        IDevice devs = new MIGPDevFactory().SearchOneDev((io), (byte) 02);
        if (devs != null) {
            instance = (OSA_X) devs;
            instance.InitDevice();
            commontest = new ABS_Test();
            commontest.SetPar(instance, dev_mock);
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="读取info测试">
    @Test
    public void test_readinfo() throws Exception {
        commontest.check_infolist();
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置info测试">
    @Test
    public void test_setinfo() throws Exception {
        //设置配置
        commontest.setinfolist_setup();
        //检查配置
        commontest.setinfolist_check();
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="读取config测试">
    @Test
    public void test_readconfig() throws Exception {
        commontest.check_configlist();
        ArrayList<SConfigItem> list = instance.GetConfigList();
        commontest.check_item(list, dev_mock.NRANGE, get_range_string(dev_mock.NRANGE.GetValue()));
        commontest.check_item(list, dev_mock.NAVR);
        commontest.check_item(list, dev_mock.NTEMPER_COMP);
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置config测试">
    private String get_range_string(int index) {
        return "(" + dev_mock.VDRANGE_MIN[index].GetValue() + "-" + dev_mock.NRANGE_MAX[index].GetValue() + ")";
    }

    @Test
    public void test_setconfig() throws Exception {
        //设置配置
        commontest.setconfiglist_setup();
        ArrayList<SConfigItem> config = instance.GetConfigList();
        commontest.set_item(config, dev_mock.NRANGE, get_range_string(2));
        commontest.set_item(config, dev_mock.NAVR, "3");
        commontest.set_item(config, dev_mock.NTEMPER_COMP, "24.6");

        //下发
        instance.SetConfigList(config);
        dev_mock.ReadREGS();

        //检查配置
        commontest.setconfiglist_check();
        assertEquals(dev_mock.NRANGE.GetValue().toString(), "2");
        assertEquals(dev_mock.NAVR.GetValue().toString(), "3");
        assertEquals(dev_mock.NTEMPER_COMP.GetValue().toString(), "24.6");
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="读取calpar测试">
    @Test
    public void test_readcalpar() throws Exception {
        commontest.check_configlist();
        ArrayList<SConfigItem> list = instance.GetCalParList();
        commontest.check_item(list, dev_mock.NRANGE_NUM, (dev_mock.NRANGE_NUM.GetValue() + 1) + "");
        commontest.check_item(list, dev_mock.NTEMPER_PAR);
        for (int i = 0; i < dev_mock.NAMPLIFY.length; i++) {
            commontest.check_item(list, dev_mock.NRANGE_MAX[i]);
            commontest.check_item(list, dev_mock.NCLTEMPER[i]);
            commontest.check_item(list, dev_mock.NCLPARA[i]);
            commontest.check_item(list, dev_mock.NCLPARB[i]);
            commontest.check_item(list, dev_mock.NCLPARC[i]);
            commontest.check_item(list, dev_mock.NAMPLIFY[i], (int) (AMPPAR / dev_mock.NAMPLIFY[i].GetValue()) + "");
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置calpar测试">
    @Test
    public void test_setcalpar() throws Exception {
        //设置配置
        ArrayList<SConfigItem> list = instance.GetCalParList();
        commontest.set_item(list, dev_mock.NRANGE_NUM, "3");
        commontest.set_item(list, dev_mock.NTEMPER_PAR, "2.0");
        for (int i = 0; i < dev_mock.NAMPLIFY.length; i++) {
            commontest.set_item(list, dev_mock.NRANGE_MAX[i], "3.0");
            commontest.set_item(list, dev_mock.NCLTEMPER[i], "22.0");
            commontest.set_item(list, dev_mock.NCLPARA[i], "12.0");
            commontest.set_item(list, dev_mock.NCLPARB[i], "13.0");
            commontest.set_item(list, dev_mock.NCLPARC[i], "13.0");
            commontest.set_item(list, dev_mock.NAMPLIFY[i], "2");
        }
        //下发
        instance.SetCalParList(list);
        dev_mock.ReadREGS();

        assertEquals(dev_mock.NRANGE_NUM.GetValue() + 1 + "", "3");
        assertEquals(dev_mock.NTEMPER_PAR.GetValue().toString(), "2.0");
        for (int i = 0; i < dev_mock.NAMPLIFY.length; i++) {
            assertEquals(dev_mock.NRANGE_MAX[i].GetValue().toString(), "3.0");
            assertEquals(dev_mock.NCLTEMPER[i].GetValue().toString(), "22.0");
            assertEquals(dev_mock.NCLPARA[i].GetValue().toString(), "12.0");
            assertEquals(dev_mock.NCLPARB[i].GetValue().toString(), "13.0");
            assertEquals(dev_mock.NCLPARC[i].GetValue().toString(), "13.0");
            assertEquals((int) (0.5 + (float)OSA_X.AMPPAR / dev_mock.NAMPLIFY[i].GetValue()) + "", "2");
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
                    assertEquals(item.GetValue(), dev_mock.NCTIME.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.NCTIME.GetValue());
                }
                if (item.IsKey(dev_mock.NCINTERVAL.toString())) {
                    assertEquals(item.GetValue(), dev_mock.NCINTERVAL.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.NCTIME.GetValue());
                }
                if (item.IsKey(dev_mock.NCBRUSH.toString())) {
                    assertEquals(item.GetValue(), dev_mock.NCBRUSH.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.NCTIME.GetValue());
                }
            }
        } else {
            for (SConfigItem item : result.manu_config) {
                if (item.IsKey(dev_mock.NCTIME.toString())) {
                    assertEquals(item.GetValue(), dev_mock.NCTIME.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.NCTIME.GetValue());
                }
                if (item.IsKey(dev_mock.NCINTERVAL.toString())) {
                    assertEquals(item.GetValue(), dev_mock.NCINTERVAL.GetValue() + "");
                    PrintLog.println(item.data_name + "[设置值]:" + item.toString() + "[寄存器值]:" + dev_mock.NCTIME.GetValue());
                }
                if (item.IsKey(dev_mock.NCBRUSH.toString())) {
                    assertEquals(item.GetValue(), dev_mock.NCBRUSH.GetValue() + "");
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
                item.SetValue("100");
            }
            if (item.IsKey(dev_mock.NCINTERVAL.toString())) {
                item.SetValue("60");
            }
            if (item.IsKey(dev_mock.NCBRUSH.toString())) {
                item.SetValue("3");
            }
        }
        for (SConfigItem item : result.auto_config) {
            if (item.IsKey(dev_mock.NCTIME.toString())) {
                item.SetValue("99");
            }
            if (item.IsKey(dev_mock.NCINTERVAL.toString())) {
                item.SetValue("61");
            }
            if (item.IsKey(dev_mock.NCBRUSH.toString())) {
                item.SetValue("4");
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

        CollectData result = instance.CollectData();
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
                commontest.printREG(dev_mock.NTEMPER_PAR);
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
                    commontest.printREG(dev_mock.NCLTEMPER[dev_mock.NRANGE.GetValue()]);
                    commontest.printREG(dev_mock.NCLPARA[dev_mock.NRANGE.GetValue()]);
                    commontest.printREG(dev_mock.NCLPARB[dev_mock.NRANGE.GetValue()]);
                    commontest.printREG(dev_mock.NCLPARC[dev_mock.NRANGE.GetValue()]);
                }
            }
        }
    }
    // </editor-fold> 
}
