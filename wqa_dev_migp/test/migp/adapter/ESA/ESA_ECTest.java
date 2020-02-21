/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ESA;

import base.migp.reg.MEG;
import base.pro.convert.NahonConvert;
import java.util.ArrayList;
import migp.adapter.factory.MIGPDevFactory;
import migp.adapter.mock.ECMock;
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
public class ESA_ECTest {

    public ESA_ECTest() throws Exception {
        if (instance == null) {
//            PrintLog.PintSwitch = true;
            this.InitDevice();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    public static ESA_EC instance;
    public static ECMock dev_mock;

    private void InitDevice() throws Exception {
        dev_mock = new ECMock();
        dev_mock.ResetREGS();
        MOCKIO io = new MOCKIO(dev_mock.client);
        io.Open();
        IDevice devs = new MIGPDevFactory().SearchOneDev(new ShareIO(io), (byte) 02);
        if (devs != null) {
            instance = (ESA_EC) devs;
            instance.InitDevice();
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="检查配置">
    private void PrintConfigItem(ArrayList<SConfigItem> result) {
        result.forEach(item -> {
            PrintLog.println(item.inputtype + "-" + item.data_name + ":" + item.value);
        });
    }

    private void check_config() throws Exception {
        PrintLog.println("ConfigList:");
        dev_mock.ReadREGS();
        ArrayList<SConfigItem> result = instance.GetConfigList();
        result.forEach(item -> {
            if (item.IsKey(dev_mock.NTEMP_COM.toString())) {
                assertEquals(dev_mock.NTEMP_COM.GetValue().toString(), item.value);
            }
        });
        PrintConfigItem(result);
    }

    private void checkcal_par() throws Exception {
        PrintLog.println("CalList:");
        dev_mock.ReadREGS();
        ArrayList<SConfigItem> result = instance.GetCalParList();
        PrintConfigItem(result);
        //设备读取应该与本地寄存器相同
        result.forEach(item -> {
            if (item.IsKey(dev_mock.NA.toString())) {
                assertEquals(dev_mock.NA.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NTEMP_CAL.toString())) {
                assertEquals(NahonConvert.TimData(dev_mock.NTEMP_CAL.GetValue(), 2) + "", item.value);
            }
        });
    }

    // </editor-fold> 
    /**
     * Test of SetCalParList method, of class ESA_EC.
     */
    @Test
    public void testSetCalParList() throws Exception {
        PrintLog.println("*****************************************");
        ArrayList<SConfigItem> info_list = instance.GetConfigList();
        info_list.forEach(info -> {
            if (info.IsKey(dev_mock.NTEMP_COM.toString())) {
                info.SetValue(dev_mock.NTEMP_COM.GetValue() + 1 + "");
            }
        });
        instance.SetConfigList(info_list);
        this.check_config();

        info_list = instance.GetCalParList();
        info_list.forEach(info -> {
            if (info.IsKey(dev_mock.NTEMP_CAL.toString())) {
                info.SetValue(dev_mock.NTEMP_CAL.GetValue() + 1 + "");
            }
            if (info.IsKey(dev_mock.NA.toString())) {
                info.SetValue(dev_mock.NA.GetValue() + 1 + "");
            }
        });
        instance.SetCalParList(info_list);
        this.checkcal_par();
    }

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
                this.checkcal_par();
            } else {
                for (int i = 1; i <= info.cal_num; i++) {
                    float[] oradata = new float[i];
                    float[] testdata = new float[i];
                    for (int j = 0; j < oradata.length; j++) {
                        oradata[j] = 32f + j;
                        testdata[j] = 30f - j;
                    }
                    instance.CalParameter(info.data_name, oradata, testdata);
                    this.checkcal_par();
                }
            }
        }
    }

}
