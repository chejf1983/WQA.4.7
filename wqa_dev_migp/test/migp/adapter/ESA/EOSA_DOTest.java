/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ESA;

import base.migp.reg.MEG;
import java.util.ArrayList;
import migp.adapter.factory.MIGPDevFactory;
import migp.adapter.mock.DOMock;
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
public class EOSA_DOTest {

    public EOSA_DOTest() throws Exception {
        if (instance == null) {
            PrintLog.PintSwitch = true;
            this.InitDevice();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    public static EOSA_DO instance;
    public static DOMock dev_mock;

    private void InitDevice() throws Exception {
        dev_mock = new DOMock();
        dev_mock.ResetREGS();
        MOCKIO io = new MOCKIO(dev_mock.client);
        io.Open();
        IDevice devs = new MIGPDevFactory().SearchOneDev(new ShareIO(io), (byte) 02);
        if (devs != null) {
            instance = (EOSA_DO) devs;
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
            if (item.IsKey(dev_mock.NPASCA.toString())) {
                assertEquals(dev_mock.NPASCA.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NSALT.toString())) {
                assertEquals(dev_mock.NSALT.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NTEMPER_COM.toString())) {
                assertEquals(dev_mock.NTEMPER_COM.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NAVR.toString())) {
                assertEquals(dev_mock.NAVR.GetValue().toString(), item.value);
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
            if (item.IsKey(dev_mock.NPTEMPER.toString())) {
                assertEquals(dev_mock.NPTEMPER.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NA.toString())) {
                assertEquals(dev_mock.NA.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NB.toString())) {
                assertEquals(dev_mock.NB.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NCLTEMPER.toString())) {
                assertEquals(dev_mock.NCLTEMPER.GetValue().toString(), item.value);
            }

            if (item.IsKey(dev_mock.NPA.toString())) {
                assertEquals(dev_mock.NPA.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NPB.toString())) {
                assertEquals(dev_mock.NPB.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NPC.toString())) {
                assertEquals(dev_mock.NPC.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NPD.toString())) {
                assertEquals(dev_mock.NPD.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NPE.toString())) {
                assertEquals(dev_mock.NPE.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NPF.toString())) {
                assertEquals(dev_mock.NPF.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NPG.toString())) {
                assertEquals(dev_mock.NPG.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NDO100.toString())) {
                assertEquals(dev_mock.NDO100.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NDO0.toString())) {
                assertEquals(dev_mock.NDO0.GetValue().toString(), item.value);
            }
        });
    }

    // </editor-fold> 
    /**
     * Test of SetCalParList method, of class EOSA_DO.
     */
    @Test
    public void testSetCalParList() throws Exception {
        PrintLog.println("*****************************************");
        ArrayList<SConfigItem> info_list = instance.GetConfigList();
        info_list.forEach(info -> {
            for (int i = 0; i < dev_mock.list.length; i++) {
                if (info.IsKey(dev_mock.list[i].toString())) {
                    //reg.GetValue()
                    info.SetValue(i + 5 + "");
                }
            }
        });
        instance.SetConfigList(info_list);
        this.check_config();

        info_list = instance.GetCalParList();
        info_list.forEach(info -> {
            for (int i = 0; i < dev_mock.list.length; i++) {
                if (info.IsKey(dev_mock.list[i].toString())) {
                    //reg.GetValue()
                    info.SetValue(i + 5 + "");
                }
            }
        });
        instance.SetCalParList(info_list);
        this.checkcal_par();
    }

    /**
     * Test of CollectData method, of class EOSA_DO.
     */
    @Test
    public void testCollectData() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("CollectData");

        SDisplayData result = instance.CollectData();
        MEG[] regs = new MEG[]{dev_mock.MPAR1, dev_mock.MPAR2, dev_mock.SR1, dev_mock.MPAR3, dev_mock.SR2};
        if (dev_mock.VVATOKEN.GetValue() > 0) {
            regs = new MEG[]{dev_mock.MPAR1, dev_mock.MPAR2, dev_mock.SR1, dev_mock.MPAR3, dev_mock.SR2, dev_mock.SR3, dev_mock.SR4, dev_mock.SR5, dev_mock.SR6, dev_mock.SR7};
        }
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
     * Test of CalParameter method, of class EOSA_DO.
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
