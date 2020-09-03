/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.OSA;

import base.migp.mem.NVPA;
import base.migp.reg.IMEG;
import base.migp.reg.MEG;
import base.pro.convert.NahonConvert;
import java.util.ArrayList;
import static migp.adapter.OSA.OSA_X.AMPPAR;
import wqa.dev.data.SDevInfo;
import wqa.dev.intf.SConfigItem;

/**
 *
 * @author chejf
 */
public class MOSA_FDO extends OSA_FDOII {

    IMEG NPGAB = new IMEG(new NVPA(12, 2), "蓝光PGA"); //4095
    IMEG NPGAR = new IMEG(new NVPA(14, 2), "红光PGA");

    public MOSA_FDO(SDevInfo devinfo) {
        super(devinfo);
    }

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice();
        this.ReadMEG(NPGAB, NPGAR);
    }

    private SConfigItem getAmplfyItem(IMEG reg) {
        if (reg.GetValue() == 0) {
            return (SConfigItem.CreateRWItem(reg.toString(), (int) (AMPPAR) + "", ""));
        } else {
            return (SConfigItem.CreateRWItem(reg.toString(), NahonConvert.TimData((float) AMPPAR / reg.GetValue(), 2) + "", ""));
        }
    }

    @Override
    public ArrayList<SConfigItem> GetCalParList() {
        ArrayList<SConfigItem> item = super.GetCalParList(); //To change body of generated methods, choose Tools | Templates.
        item.add(getAmplfyItem(NPGAB));
        item.add(getAmplfyItem(NPGAR));
        return item;
    }

    private void setAmplyfyItem(MEG reg, String value) throws Exception {
        float tmp = Float.valueOf(value);
        float famply = AMPPAR;
        if (tmp != 0) {
            famply = AMPPAR / Float.valueOf(value);
        }
        int amply = (int) (famply + 0.5);
        amply = amply > AMPPAR ? (int)AMPPAR : amply;
        this.SetConfigREG(reg, String.valueOf(amply));
    }

    @Override
    public void SetCalParList(ArrayList<SConfigItem> list) throws Exception {
        super.SetCalParList(list);
        MEG[] reglist = new MEG[]{NPGAB, NPGAR};
        for (SConfigItem item : list) {
            for (MEG mem : reglist) {
                if (item.IsKey(mem.toString())) {
                    this.setAmplyfyItem(mem, item.GetValue());
                    break;
                }
            }
        }
    }
}
