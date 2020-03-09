/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ESA;

import base.migp.reg.MEG;
import java.util.ArrayList;
import migp.adapter.factory.AbsDevice;
import migp.adapter.factory.MIGPDevFactory;
import migp.adapter.mock.PHMock;
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
public class ESA_PHTest extends ABS_Test {

    public ESA_PHTest() throws Exception {
        super();
        if (dev_mock == null) {
            this.InitDevice(new PHMock());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    public static ESA_PH instance;
    public static PHMock dev_mock;

    private void InitDevice(PHMock mock) throws Exception {
        dev_mock = mock;
        dev_mock.ResetREGS();
        MOCKIO io = new MOCKIO(dev_mock.client);
        io.Open();
        IDevice devs = new MIGPDevFactory().SearchOneDev(new ShareIO(io), (byte) 02);
        if (devs != null) {
            instance = (ESA_PH) devs;
            instance.InitDevice();
        }
        super.InitDevice(instance, dev_mock);
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="信息设置">
    /**
     * Test of SetCalParList method, of class ESA_PH.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetInfoParList() throws Exception {
        PrintLog.println("*****************************************");
        PrintLog.println("SetinfoConfig");

        this.check_info_item(dev_mock.EDEVNAME, "TestDO1");
        this.check_info_item(dev_mock.EBUILDSER, "202002210");
        this.check_info_item(dev_mock.EBUILDDATE, "20202020");
    }
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="参数设置">
     /**
     * Test of SetCalParList method, of class ESA_PH.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetConfigParList() throws Exception {
//        System.out.println("SetCalParList");
        PrintLog.println("*****************************************");
        PrintLog.println("SetConfig");
        ArrayList<SConfigItem> info_list = instance.GetInfoList();
        String bandrate = AbsDevice.BANDRATE_STRING[dev_mock.client.bandrate + 1];
        String addr = dev_mock.client.addr + 1 + "";
        info_list = instance.GetConfigList();
        info_list.forEach(item -> {
            if (item.IsKey(AbsDevice.SBandRate)) {
                item.SetValue(bandrate);
                PrintLog.println(item.inputtype + "-" + item.data_name + "[设置值]:" + bandrate + "[读取结果]:" + item.value);
            }
            if (item.IsKey(AbsDevice.SDevAddr)) {
                item.SetValue(addr);
                PrintLog.println(item.inputtype + "-" + item.data_name + "[设置值]:" + addr + "[读取结果]:" + item.value);
            }
        });
        instance.SetConfigList(info_list);
        info_list = instance.GetConfigList();
        info_list.forEach(info -> {
            if (info.IsKey(AbsDevice.SBandRate)) {
                assertEquals(info.value, bandrate);
            }
            if (info.IsKey(AbsDevice.SDevAddr)) {
                assertEquals(info.value, addr);
            }
        });
    }
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="定标系数设置">

    /**
     * Test of SetCalParList method, of class ESA_ORP.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSetCalParList() throws Exception {
        PrintLog.println("*****************************************");
        PrintLog.println("SetConfig");
        this.check_cal_item(dev_mock.NTEMP_CAL, "122.0");
        this.check_cal_item(dev_mock.NA, "32.01");
        this.check_cal_item(dev_mock.NE0, "22.01");
//        this.check_cal_item(dev_mock.NB, "33.0");
    }

    // </editor-fold> 
        
    // <editor-fold defaultstate="collapsed" desc="采集测试">
    /**
     * Test of CollectData method, of class ESA_PH.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCollectData() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("CollectData");

        SDisplayData result = instance.CollectData();
        MEG[] regs = new MEG[]{dev_mock.MPAR1, dev_mock.SR1, dev_mock.MPAR3, dev_mock.SR2};
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
     * Test of CalParameter method, of class ESA_PH.
     *
     * @throws java.lang.Exception
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
                printREG(dev_mock.NTEMP_CAL);
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
                    printREG(dev_mock.NA);
                    printREG(dev_mock.NE0);
                }
            }
        }
    }
    // </editor-fold> 

}
