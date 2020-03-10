/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ISA;

import base.migp.reg.MEG;
import java.util.ArrayList;
import static migp.adapter.OSA.OSA_XTest.dev_mock;
import static migp.adapter.OSA.OSA_XTest.instance;
import migp.adapter.factory.MIGPDevFactory;
import migp.adapter.mock.ISAMock;
import org.junit.Test;
import static org.junit.Assert.*;
import wqa.adapter.io.ShareIO;
import wqa.adapter.model.ABS_Test;
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

    public ISA_XTest() throws Exception{
        if(dev_mock == null){
            this.InitDevice();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    public static ISA_X instance;
    public static ISAMock dev_mock;
    public static ABS_Test commontest;

    private void InitDevice() throws Exception {
        dev_mock = new ISAMock();
        dev_mock.ResetREGS();
        MOCKIO io = new MOCKIO(dev_mock.client);
        io.Open();
        IDevice devs = new MIGPDevFactory().SearchOneDev(new ShareIO(io), (byte) 02);
        if (devs != null) {
            instance = (ISA_X) devs;
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
        commontest.check_item(list, dev_mock.NK_COM);
        commontest.check_item(list, dev_mock.NCL_COM);
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置config测试">
    @Test
    public void test_setconfig() throws Exception {
        //设置配置
        commontest.setconfiglist_setup();
        ArrayList<SConfigItem> config = instance.GetConfigList();
        commontest.set_item(config, dev_mock.NK_COM, "2.0");
        commontest.set_item(config, dev_mock.NCL_COM, "3.0");

        //下发
        instance.SetConfigList(config);
        dev_mock.ReadREGS();

        //检查配置
        commontest.setconfiglist_check();
        assertEquals(dev_mock.NK_COM.GetValue().toString(), "2.0");
        assertEquals(dev_mock.NCL_COM.GetValue().toString(), "3.0");
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="读取calpar测试">
    @Test
    public void test_readcalpar() throws Exception {
        commontest.check_configlist();
        ArrayList<SConfigItem> list = instance.GetCalParList();
        commontest.check_item(list, dev_mock.NTEMP_CAL);
        for (int i = 0; i < instance.GetCalDataList().length - 1; i++) {
            commontest.check_item(list, instance.GetCalDataList()[i].data_name + dev_mock.NAS[i].toString(), dev_mock.NAS[i].GetValue().toString());
            commontest.check_item(list, instance.GetCalDataList()[i].data_name + dev_mock.NES[i].toString(), dev_mock.NES[i].GetValue().toString());
            commontest.check_item(list, instance.GetCalDataList()[i].data_name + dev_mock.NFS[i].toString(), dev_mock.NFS[i].GetValue().toString());
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置calpar测试">
    @Test
    public void test_setcalpar() throws Exception {
        //设置配置
        ArrayList<SConfigItem> list = instance.GetCalParList();
        commontest.set_item(list, dev_mock.NTEMP_CAL, "3.0");
        for (int i = 0; i < instance.GetCalDataList().length - 1; i++) {
            commontest.set_item(list, instance.GetCalDataList()[i].data_name + dev_mock.NAS[i], "3.0");
            commontest.set_item(list, instance.GetCalDataList()[i].data_name + dev_mock.NES[i], "22.0");
            commontest.set_item(list, instance.GetCalDataList()[i].data_name + dev_mock.NFS[i], "12.0");
        }
        //下发
        instance.SetCalParList(list);
        dev_mock.ReadREGS();

        assertEquals(dev_mock.NTEMP_CAL.GetValue().toString(), "3.0");
        for (int i = 0; i < instance.GetCalDataList().length - 1; i++) {
            assertEquals(dev_mock.NAS[i].GetValue().toString(), "3.0");
            assertEquals(dev_mock.NES[i].GetValue().toString(), "22.0");
            assertEquals(dev_mock.NFS[i].GetValue().toString(), "12.0");
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="采集测试">
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
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标测试">
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
                commontest.printREG(dev_mock.NTEMP_CAL);
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
                    commontest.printREG(dev_mock.SCLNUM);
                    commontest.printREG(dev_mock.SCLODATA[0]);
                    commontest.printREG(dev_mock.SCLODATA[1]);
                    commontest.printREG(dev_mock.SCLTDATA[0]);
                    commontest.printREG(dev_mock.SCLTDATA[1]);
                }
            }
        }
    }
    // </editor-fold> 

}
