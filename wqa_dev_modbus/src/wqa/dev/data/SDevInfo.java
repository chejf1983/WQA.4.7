/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.dev.data;

import nahon.comm.io.AbstractIO;



/**
 *
 * @author chejf
 */
public class SDevInfo {
      //协议类型
    public enum ProType{
        MIGP,MODEBUS
    }

    public SDevInfo(AbstractIO io, int dev_type, int dev_addr, String serial_num, ProType protype) {
        this.io = io;
        this.dev_type = dev_type;
        this.dev_addr = dev_addr;
        this.serial_num = serial_num;
        this.protype = protype;
    }

    public SDevInfo() {
    }
    
    public AbstractIO io;
    public int dev_type;
    public int dev_addr;
    public String serial_num;
    public ProType protype;

    public boolean EqualsTo(SDevInfo other) {
        return this.io == other.io
                && this.dev_type == other.dev_type
                && this.dev_addr == other.dev_addr
                && this.serial_num.contentEquals(other.serial_num);
    }
}
