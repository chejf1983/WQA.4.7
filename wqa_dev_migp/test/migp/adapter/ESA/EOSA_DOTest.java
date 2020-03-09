/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.ESA;

import base.migp.reg.MEG;
import migp.adapter.factory.MIGPDevFactory;
import migp.adapter.mock.DOMock;
import org.junit.Test;
import static org.junit.Assert.*;
import wqa.adapter.io.ShareIO;
import wqa.adapter.model.ABS_Test;
import wqa.adapter.model.MOCKIO;
import wqa.adapter.model.PrintLog;
import wqa.control.common.CDevDataTable;
import wqa.control.common.IDevice;
import wqa.control.dev.collect.SDataElement;
import wqa.control.dev.collect.SDisplayData;

/**
 *
 * @author chejf
 */
public class EOSA_DOTest extends ABS_Test {

    public EOSA_DOTest() throws Exception {
        super();
        if (dev_mock == null) {
            this.InitDevice(new DOMock());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="初始化">
    public static EOSA_DO instance;
    public static DOMock dev_mock;

    private void InitDevice(DOMock mock) throws Exception {
        dev_mock = mock;
        dev_mock.ResetREGS();
        MOCKIO io = new MOCKIO(dev_mock.client);
        io.Open();
        IDevice devs = new MIGPDevFactory().SearchOneDev(new ShareIO(io), (byte) 02);
        if (devs != null) {
            instance = (EOSA_DO) devs;
            instance.InitDevice();
        }
        super.InitDevice(instance, dev_mock);
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="参数设置">
    /**
     * Test of SetConfigList method, of class EOSA_DO.
     */
    @Test
    public void testSetConfigList() throws Exception {
        PrintLog.println("*****************************************");
        PrintLog.println("SetConfigList");
        this.check_config_item(dev_mock.NPASCA, "2.0");
        this.check_config_item(dev_mock.NSALT, "3.0");
        this.check_config_item(dev_mock.NTEMPER_COM, "44.0");
        if (dev_mock.VVATOKEN.GetValue() > 0) {
            this.check_config_item(dev_mock.NAVR, "33");
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标系数设置">
    /**
     * Test of SetCalParList method, of class EOSA_DO.
     */
    @Test
    public void testSetCalParList() throws Exception {
        PrintLog.println("*****************************************");
        this.check_cal_item(dev_mock.NPTEMPER, "12.0");
        this.check_cal_item(dev_mock.NA, "13.0");
        this.check_cal_item(dev_mock.NB, "24.0");
        this.check_cal_item(dev_mock.NCLTEMPER, "25.0");
        if (dev_mock.VVATOKEN.GetValue() > 0) {
            this.check_cal_item(dev_mock.NPA, "33.01");
            this.check_cal_item(dev_mock.NPB, "33.02");
            this.check_cal_item(dev_mock.NPC, "33.03");
            this.check_cal_item(dev_mock.NPD, "33.04");
            this.check_cal_item(dev_mock.NPE, "33.05");
            this.check_cal_item(dev_mock.NPF, "33.06");
            this.check_cal_item(dev_mock.NPG, "33.07");
            this.check_cal_item(dev_mock.NDO100, "333.03");
            this.check_cal_item(dev_mock.NDO0, "133.01");
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="采集测试">
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
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标测试">
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
                printREG(dev_mock.NPTEMPER);
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
                    printREG(dev_mock.NB);
                    printREG(dev_mock.NCLTEMPER);
                }
            }
        }
    }
    // </editor-fold> 
}
