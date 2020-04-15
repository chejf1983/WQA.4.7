/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.dev.data;

import wqa.dev.intf.IMAbstractIO;


/**
 *
 * @author chejf
 */
public class SDevInfo {
      //协议类型
    public enum ProType{
        MIGP,MODEBUS
    }
    
    public IMAbstractIO io;
    public DevID dev_id;
    public ProType protype;

    public boolean EqualsTo(SDevInfo other) {
        return this.io == other.io
                && this.dev_id.EqualsTo(other.dev_id);
    }
}
