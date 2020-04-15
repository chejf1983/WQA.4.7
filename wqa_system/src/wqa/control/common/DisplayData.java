/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.common;

import java.util.Date;
import static wqa.adapter.factory.CDevDataTable.ORA_Flag;
import wqa.dev.data.DevID;
import wqa.dev.data.SDataElement;

/**
 *
 * @author chejf
 */
public class DisplayData {
    
    public DisplayData(DevID dev_id) {
        this.dev_id = dev_id;
    }

    public final DevID dev_id;
    public Date time;
    public SDataElement[] datas = new SDataElement[0];
    public int alarm = 0;
    public String alram_info = "";

    public SDataElement GetDataElement(String nametype) {
        for (SDataElement data : this.datas) {
            if (data.name.contentEquals(nametype)) {
                return data;
            }
        }

        return null;
    }

    public SDataElement GetOraDataElement(String nametype) {
        for (SDataElement data : this.datas) {
            if (data.name.contentEquals(nametype + ORA_Flag)) {
                return data;
            }
        }

        return null;
    }
}
