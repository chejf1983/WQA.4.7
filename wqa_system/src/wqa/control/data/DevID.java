/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.data;

import wqa.adapter.factory.CDevDataTable;

/**
 *
 * @author chejf
 */
public class DevID {

    public final int dev_type;
    public final int dev_addr;
    public final String serial_num;

    public DevID(int dev_type, int dev_addr, String serial_num) {
        this.dev_addr = dev_addr;
        this.dev_type = dev_type;
        this.serial_num = serial_num;
    }

    public DevID(String id_string) throws Exception {
        String[] split = id_string.split("_");
        if (split.length == 2) {
            dev_type = Integer.valueOf(split[0]);
            dev_addr = Integer.valueOf(split[1]);
            if (id_string.endsWith("_")) {
                serial_num = "";
            } else {
                serial_num = "old";
            }
        } else if (split.length == 3) {
            dev_type = Integer.valueOf(split[0]);
            dev_addr = Integer.valueOf(split[1]);
            serial_num = split[2];
        } else {
            dev_type = 0;
            dev_addr = 0;
            serial_num = "";
        }
    }

    public String ToChineseString() {
        return CDevDataTable.GetInstance().namemap.get(dev_type).dev_name_ch + "(" + this.dev_addr + ")(" + this.serial_num + ")";
    }

    @Override
    public String toString() {
        if (this.serial_num.contentEquals("old")) {
            return dev_type + "_" + dev_addr;
        } else {
            return dev_type + "_" + dev_addr + "_" + this.serial_num;
        }
    }

    public boolean EqualsTo(DevID other) {
        return this.dev_type == other.dev_type
                && this.dev_addr == other.dev_addr
                && this.serial_num.contentEquals(other.serial_num);
    }
}
