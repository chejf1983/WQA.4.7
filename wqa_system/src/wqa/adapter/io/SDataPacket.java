/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.io;

import java.util.Date;

/**
 *
 * @author chejf
 */
public class SDataPacket {
    public enum IOEvent {
        Send,
        Receive
    }

    public SDataPacket(SIOInfo info, IOEvent type, byte[] data){
        this.info = info;
        this.type = type;
        this.data = data;
        this.time = new Date();
    }
    
    public Date time;
    public SIOInfo info;
    public IOEvent type;
    public byte[] data;
}
