/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.dev.data;

import java.util.Date;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;
import wqa.adapter.factory.CDevDataTable;
import static wqa.adapter.factory.CDevDataTable.ORA_Flag;

/**
 *
 * @author chejf
 */
public class CollectData {

    public CollectData(
            int dev_type,
            int dev_addr,
            String serial_num) {
        this.dev_type = dev_type;
        this.dev_addr = dev_addr;
        this.serial_num = serial_num;
        this.time = new Date();

        try {
            //单位信息
            CDevDataTable.DataInfo[] data_list = CDevDataTable.GetInstance().namemap.get(this.dev_type).data_list;
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

    public int dev_type;
    public int dev_addr;
    public String serial_num;
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
