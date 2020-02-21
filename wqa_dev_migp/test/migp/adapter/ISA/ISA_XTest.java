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
            PrintLog.PintSwitch = true;
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
            if (item.IsKey(dev_mock.NK_COM.toString())) {
                assertEquals(dev_mock.NK_COM.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NK_COM.toString())) {
                assertEquals(dev_mock.NK_COM.GetValue().toString(), item.value);
            }
        });
        PrintConfigItem(result);
    }

    private void checkcal_par() throws Exception {
        PrintLog.println("CalList:");
        dev_mock.ReadREGS();
        ArrayList<SConfigItem> result = instance.GetCalParList();
//        PrintConfigItem(result);
        //设备读取应该与本地寄存器相同
        result.forEach(item -> {
            if (item.IsKey(dev_mock.NTEMP_CAL.toString())) {
                assertEquals(dev_mock.NTEMP_CAL.GetValue().toString(), item.value);
            }

            for (int i = 0; i < instance.GetCalDataList().length - 1; i++) {
                if (item.IsKey(dev_mock.NAS[i].toString())) {
                    assertEquals(dev_mock.NAS[i].GetValue().toString(), item.value);
                }
                if (item.IsKey(dev_mock.NES[i].toString())) {
                    assertEquals(dev_mock.NES[i].GetValue().toString(), item.value);
                }
                if (item.IsKey(dev_mock.NFS[i].toString())) {
                    assertEquals(dev_mock.NFS[i].GetValue().toString(), item.value);
                }
            }
        });
    }

    // </editor-fold> 
    /**
     * Test of SetConfigList method, of class ISA_X.
     */
    @Test
    public void testSetConfigList() throws Exception {
        PrintLog.println("*****************************************");
        PrintLog.println("SetConfigList");
        ArrayList<SConfigItem> list = instance.GetConfigList();
        list.forEach(item -> {
            if (item.IsKey(dev_mock.NK_COM.toString())) {
                item.SetValue(dev_mock.NK_COM.GetValue() + 1 + "");
            }
            if (item.IsKey(dev_mock.NCL_COM.toString())) {
                item.SetValue(dev_mock.NCL_COM.GetValue() + 1 + "");
            }
        });
        instance.SetConfigList(list);
        check_config();
    }

    /**
     * Test of SetCalParList method, of class ISA_X.
     */
    @Test
    public void testSetCalParList() throws Exception {
        
        PrintLog.println("*****************************************");
        PrintLog.println("SetCalParList");
        ArrayList<SConfigItem> list = instance.GetCalParList();
        list.forEach(item -> {
            if (item.IsKey(dev_mock.NTEMP_CAL.toString())) {
                item.SetValue(dev_mock.NTEMP_CAL.GetValue() - 1 + "");
            }
            for (int i = 0; i < instance.GetCalDataList().length - 1; i++) {
                if (item.IsKey(dev_mock.NAS[i].toString())) {
                    item.SetValue(dev_mock.NAS[i].GetValue() + 1 + "");
                }
                if (item.IsKey(dev_mock.NES[i].toString())) {
                    item.SetValue(dev_mock.NES[i].GetValue() + 1 + "");
                }
                if (item.IsKey(dev_mock.NFS[i].toString())) {
                    item.SetValue(dev_mock.NFS[i].GetValue() + 1 + "");
                }
            }
        });
        instance.SetCalParList(list);
        this.checkcal_par();
    }
    
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
            assertEquals(String.format("%.2f", Float.valueOf(regs[i].GetValue().toString())), String.format("%.2f", (float)data.mainData));
        }
        PrintLog.println("报警码" + result.alarm + "-------" + dev_mock.MALARM.toString() + dev_mock.MALARM.GetValue());
        assertEquals(dev_mock.MALARM.GetValue() + "", result.alarm + "");
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
                this.checkcal_par();
            } else {
                for (int i = 1; i <= info.cal_num; i++) {
                    float[] oradata = new float[i];
                    float[] testdata = new float[i];
                    for (int j = 0; j < oradata.length; j++) {
                        oradata[j] = 132f + (float) Math.random() * j;
                        testdata[j] = 130f - (float) Math.random() * j;
                    }
                    instance.CalParameter(info.data_name, oradata, testdata);
                    this.checkcal_par();
                }
            }
        }
    }

}
