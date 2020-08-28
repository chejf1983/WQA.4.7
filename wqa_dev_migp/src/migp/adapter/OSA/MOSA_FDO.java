/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.OSA;

import base.migp.mem.NVPA;
import base.migp.reg.IMEG;
import base.migp.reg.MEG;
import java.util.ArrayList;
import wqa.dev.data.SDevInfo;
import wqa.dev.intf.SConfigItem;

/**
 *
 * @author chejf
 */
public class MOSA_FDO extends OSA_FDOII {

    IMEG NPGAB = new IMEG(new NVPA(12, 2), "蓝光PGA");
    IMEG NPGAR = new IMEG(new NVPA(14, 2), "红光PGA");

    public MOSA_FDO(SDevInfo devinfo) {
        super(devinfo);
    }

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice();
        this.ReadMEG(NPGAB, NPGAR);
    }

    @Override
    public ArrayList<SConfigItem> GetCalParList() {
        ArrayList<SConfigItem> item = super.GetCalParList(); //To change body of generated methods, choose Tools | Templates.
        item.add(SConfigItem.CreateRWItem(NPGAB.toString(), this.NPGAB.GetValue().toString(), ""));
        item.add(SConfigItem.CreateRWItem(NPGAB.toString(), this.NPGAB.GetValue().toString(), ""));
        return item;
    }

    @Override
    public void SetCalParList(ArrayList<SConfigItem> list) throws Exception {
        super.SetCalParList(list);
        MEG[] reglist = new MEG[]{NPGAB, NPGAR};
        for (SConfigItem item : list) {
            for (MEG mem : reglist) {
                if (item.IsKey(mem.toString())) {
                    this.SetConfigREG(mem, item.GetValue());
                    break;
                }
            }
        }
    }
}
