/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ESA;

import base.migp.reg.MEG;
import base.pro.convert.NahonConvert;
import java.util.ArrayList;
import static migp.adapter.ESA.EOSA_DOTest.dev_mock;
import static migp.adapter.ESA.EOSA_DOTest.instance;
import migp.adapter.factory.MIGPDevFactory;
import migp.adapter.mock.ECMock;
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
public class ESA_ECTest {

    public ESA_ECTest() throws Exception {
        if (dev_mock == null) {
            this.InitDevice();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    public static ESA_EC instance;
    public static ECMock dev_mock;
    public static ABS_Test commontest;

    private void InitDevice() throws Exception {
        dev_mock = new ECMock();
        dev_mock.ResetREGS();
        MOCKIO io = new MOCKIO(dev_mock.client);
        io.Open();
        IDevice devs = new MIGPDevFactory().SearchOneDev(new ShareIO(io), (byte) 02);
        if (devs != null) {
            instance = (ESA_EC) devs;
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
        commontest.check_item(list, dev_mock.NTEMP_COM);
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置config测试">
    @Test
    public void test_setconfig() throws Exception {
        //设置配置
        commontest.setconfiglist_setup();
        ArrayList<SConfigItem> list = instance.GetConfigList();
        commontest.set_item(list, dev_mock.NTEMP_COM, "113.0");
        
        instance.SetConfigList(list);
        dev_mock.ReadREGS();
        
        //检查配置
        commontest.setconfiglist_check();
        assertEquals(dev_mock.NTEMP_COM.GetValue().toString(), "113.0");
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="读取calpar测试">
    @Test
    public void test_readcalpar() throws Exception {
        commontest.check_configlist();
        ArrayList<SConfigItem> list = instance.GetCalParList();
        commontest.check_item(list, dev_mock.NTEMP_CAL);
        commontest.check_item(list, dev_mock.NA);
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设置calpar测试">
    @Test
    public void test_setcalpar() throws Exception {
        //设置配置
        ArrayList<SConfigItem> list = instance.GetCalParList();
        commontest.set_item(list, dev_mock.NTEMP_CAL, "112.0");
        commontest.set_item(list, dev_mock.NA, "132.1");
        //下发
        instance.SetCalParList(list);
        dev_mock.ReadREGS();

        assertEquals(dev_mock.NTEMP_CAL.GetValue().toString(), "112.0");
        assertEquals(dev_mock.NA.GetValue().toString(), "132.1");
    }
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="采集测试">
    /**
     * Test of CollectData method, of class ESA_EC.
     */
    @Test
    public void testCollectData() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("CollectData");

        SDisplayData result = instance.CollectData();
        MEG[] regs = new MEG[]{dev_mock.MPAR1, dev_mock.SR1, dev_mock.MPAR3, dev_mock.SR2, dev_mock.MPAR2};
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
     * Test of CalParameter method, of class ESA_EC.
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
                commontest.printREG(dev_mock.NTEMP_CAL);
            } else {
                for (int i = 1; i <= info.cal_num; i++) {
                    float[] oradata = new float[i];
                    float[] testdata = new float[i];
                    for (int j = 0; j < oradata.length; j++) {
                        oradata[j] = 32f + j;
                        testdata[j] = 30f - j;
                    }
                    PrintLog.println(i + "点定标");
                    instance.CalParameter(info.data_name, oradata, testdata);
                    commontest.printREG(dev_mock.NA);
                }
            }
        }
    }
    // </editor-fold> 

}
