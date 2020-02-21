/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.mock;

import base.migp.mem.EIA;
import base.migp.mem.VPA;
import base.migp.reg.IMEG;
import base.migp.reg.SMEG;
import wqa.adapter.model.MigpClient;

/**
 *
 * @author chejf
 */
public class DevMock {
    // <editor-fold defaultstate="collapsed" desc="DO寄存器"> 

    public final IMEG VDEVTYPE = new IMEG(new VPA(0x00, 2), "设备类型");  //R  
    public SMEG EDEVNAME = new SMEG(new EIA(0x00, 0x10), "设备名称");
    public SMEG EHWVER = new SMEG(new EIA(0x10, 0x01), "硬件版本");
    public SMEG ESWVER = new SMEG(new EIA(0x18, 0x04), "软件版本");
    public SMEG EBUILDSER = new SMEG(new EIA(0x20, 0x10), "序列号");
    public SMEG EBUILDDATE = new SMEG(new EIA(0x30, 0xA), "生产日期");
    // </editor-fold> 

    public MigpClient client = new MigpClient();

    public DevMock() {
        client.RegisterREGS(
                VDEVTYPE,
                EDEVNAME,
                ESWVER,
                EBUILDSER,
                EHWVER,
                EBUILDDATE);
    }

    public void ResetREGS() throws Exception {
        EDEVNAME.SetValue("TestDO");
        EBUILDSER.SetValue("201912261415DO");
        ESWVER.SetValue("SW01");
        EBUILDDATE.SetValue("20200106");
        EHWVER.SetValue("H");
        client.addr = 2;
        client.bandrate = 1;
        WriteREGS();
    }

    public void ReadREGS() throws Exception {
        this.client.Refresh();
    }

    public void WriteREGS() throws Exception {
        this.client.DowloadRegs();
    }
}
