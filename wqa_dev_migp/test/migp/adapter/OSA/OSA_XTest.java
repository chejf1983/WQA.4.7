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
            PrintLog.PintSwitch = false;
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
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="检查配置">
    private void PrintConfigItem(ArrayList<SConfigItem> result) {
        result.forEach(item -> {
            PrintLog.println(item.inputtype + "-" + item.data_name + ":" + item.value);
        });
    }

    private String get_range_string(int index) {
        return "(" + dev_mock.VDRANGE_MIN[index].GetValue() + "-" + dev_mock.NRANGE_MAX[index].GetValue() + ")";
    }
    
    private void check_config() throws Exception {
        PrintLog.println("ConfigList:");
        dev_mock.ReadREGS();
        ArrayList<SConfigItem> result = instance.GetConfigList();
        result.forEach(item -> {
            if (item.IsKey(dev_mock.NRANGE.toString())) {
                assertEquals(get_range_string(dev_mock.NRANGE.GetValue()), item.value);
            }
            if (item.IsKey(dev_mock.NAVR.toString())) {
                assertEquals(dev_mock.NAVR.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NTEMPER_COMP.toString())) {
                assertEquals(dev_mock.NTEMPER_COMP.GetValue().toString(), item.value);
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
            if (item.IsKey(dev_mock.NRANGE_NUM.toString())) {
                assertEquals(dev_mock.NRANGE_NUM.GetValue().toString(), item.value);
            }
            if (item.IsKey(dev_mock.NTEMPER_PAR.toString())) {
                assertEquals(dev_mock.NTEMPER_PAR.GetValue().toString(), item.value);
            }
            for (int i = 0; i < dev_mock.NAMPLIFY.length; i++) {
                if (item.IsKey(dev_mock.NRANGE_MAX[i].toString())) {
                    assertEquals(dev_mock.NRANGE_MAX[i].GetValue().toString(), item.value);
                }
                if (item.IsKey(dev_mock.NCLTEMPER[i].toString())) {
                    assertEquals(dev_mock.NCLTEMPER[i].GetValue().toString(), item.value);
                }
                if (item.IsKey(dev_mock.NCLPARA[i].toString())) {
                    assertEquals(dev_mock.NCLPARA[i].GetValue().toString(), item.value);
                }
                if (item.IsKey(dev_mock.NCLPARB[i].toString())) {
                    assertEquals(dev_mock.NCLPARB[i].GetValue().toString(), item.value);
                }
                if (item.IsKey(dev_mock.NCLPARC[i].toString())) {
                    assertEquals(dev_mock.NCLPARC[i].GetValue().toString(), item.value);
                }
                if (item.IsKey(dev_mock.NAMPLIFY[i].toString())) {
                    assertEquals(dev_mock.NAMPLIFY[i].GetValue().toString(), item.value);
                }
            }
        });
    }

    // </editor-fold> 
    
    /**
     * Test of SetConfigList method, of class OSA_X.
     */
    @Test
    public void testSetConfigList() throws Exception {
        PrintLog.println("*****************************************");
        PrintLog.println("SetConfigList");
        ArrayList<SConfigItem> list = instance.GetConfigList();
        list.forEach(item -> {
            if (item.IsKey(dev_mock.NRANGE.toString())) {
                item.SetValue(dev_mock.NRANGE.GetValue() + 1 + "");
            }
            if (item.IsKey(dev_mock.NAVR.toString())) {
                item.SetValue(dev_mock.NAVR.GetValue() + 1 + "");
            }
            if (item.IsKey(dev_mock.NTEMPER_COMP.toString())) {
                item.SetValue(dev_mock.NTEMPER_COMP.GetValue() + 1 + "");
            }
        });
        instance.SetConfigList(list);
        check_config();
    }

    /**
     * Test of SetCalParList method, of class OSA_X.
     */
    @Test
    public void testSetCalParList() throws Exception {
        PrintLog.println("*****************************************");
        PrintLog.println("SetCalParList");
        ArrayList<SConfigItem> list = instance.GetCalParList();
        list.forEach(item -> {
            if (item.IsKey(dev_mock.NRANGE_NUM.toString())) {
                item.SetValue(dev_mock.NRANGE_NUM.GetValue() - 1 + "");
            }
            if (item.IsKey(dev_mock.NTEMPER_PAR.toString())) {
                item.SetValue(dev_mock.NTEMPER_PAR.GetValue() + 1 + "");
            }
            for (int i = 0; i < dev_mock.NAMPLIFY.length; i++) {
                if (item.IsKey(dev_mock.NRANGE_MAX[i].toString())) {
                    item.SetValue(dev_mock.NRANGE_MAX[i].GetValue() + 1 + "");
                }
                if (item.IsKey(dev_mock.NCLTEMPER[i].toString())) {
                    item.SetValue(dev_mock.NCLTEMPER[i].GetValue() + 1 + "");
                }
                if (item.IsKey(dev_mock.NCLPARA[i].toString())) {
                    item.SetValue(dev_mock.NCLPARA[i].GetValue() + 1 + "");
                }
                if (item.IsKey(dev_mock.NCLPARB[i].toString())) {
                    item.SetValue(dev_mock.NCLPARB[i].GetValue() + 1 + "");
                }
                if (item.IsKey(dev_mock.NCLPARC[i].toString())) {
                    item.SetValue(dev_mock.NCLPARC[i].GetValue() + 1 + "");
                }
                if (item.IsKey(dev_mock.NAMPLIFY[i].toString())) {
                    item.SetValue(dev_mock.NAMPLIFY[i].GetValue() + 1 + "");
                }
            }
        });
        instance.SetCalParList(list);
        this.checkcal_par();
    }

    /**
     * Test of SetMotoPara method, of class OSA_X.
     */
    @Test
    public void testSetMotoPara() throws Exception {
        PrintLog.println("***********************************");
        PrintLog.println("SetMotoPara");
        SMotorParameter result = instance.GetMotoPara();
        assertEquals(dev_mock.NCMODE.GetValue() + "", result.mode == SMotorParameter.CleanMode.Auto ? "0" : "1");
        for (SConfigItem item : result.manu_config) {
            if (item.IsKey(dev_mock.NCTIME.toString())) {
                item.value = dev_mock.NCTIME.GetValue() + 1 + "";
            }
            if (item.IsKey(dev_mock.NCINTERVAL.toString())) {
                item.value = dev_mock.NCINTERVAL.GetValue() + 2 + "";
            }
            if (item.IsKey(dev_mock.NCBRUSH.toString())) {
                item.value = dev_mock.NCBRUSH.GetValue() + 3 + "";
            }
        }

        instance.SetMotoPara(result);
        dev_mock.ReadREGS();

//        result = instance.GetMotoPara();
        assertEquals(dev_mock.NCMODE.GetValue() + "", result.mode == SMotorParameter.CleanMode.Auto ? "0" : "1");
        for (SConfigItem item : result.manu_config) {
            if (item.IsKey(dev_mock.NCTIME.toString())) {
                assertEquals(item.value, dev_mock.NCTIME.GetValue() + "");
            }
            if (item.IsKey(dev_mock.NCINTERVAL.toString())) {
                assertEquals(item.value, dev_mock.NCINTERVAL.GetValue() + "");
            }
            if (item.IsKey(dev_mock.NCBRUSH.toString())) {
                assertEquals(item.value, dev_mock.NCBRUSH.GetValue() + "");
            }
        }
        for (SConfigItem item : result.manu_config) {
            if (item.IsKey(dev_mock.NCTIME.toString())) {
                assertEquals(item.value, dev_mock.NCTIME.GetValue() + "");
            }
            if (item.IsKey(dev_mock.NCINTERVAL.toString())) {
                assertEquals(item.value, dev_mock.NCINTERVAL.GetValue() + "");
            }
        }
//        assertEquals(result.manu_config.length, 0);
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

    /**
     * Test of CalParameter method, of class OSA_X.
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
