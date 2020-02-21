/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.dev.collect;

import java.util.Date;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;
import wqa.control.common.CDevDataTable;
import static wqa.control.common.CDevDataTable.ORA_Flag;
import wqa.control.data.DevID;

/**
 *
 * @author chejf
 */
public class SDisplayData {

    public SDisplayData(DevID dev_id) {
        this.dev_id = dev_id;
        this.time = new Date();

        try {
            //单位信息
            CDevDataTable.DataInfo[] data_list = CDevDataTable.GetInstance().namemap.get(this.dev_id.dev_type).data_list;
            datas = new SDataElement[data_list.length];
            for (int i = 0; i < data_list.length; i++) {
                datas[i] = new SDataElement();
                datas[i].name = data_list[i].data_name;
                datas[i].internal_only = data_list[i].internal_only;
                datas[i].unit = data_list[i].data_unit;
                datas[i].range_info = data_list[i].data_range[0];
            }
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
        }
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
