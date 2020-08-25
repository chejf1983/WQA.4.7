/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.model;

import nahon.comm.io.AbstractIO;
import nahon.comm.io.IOInfo;

/**
 *
 * @author chejf
 */
public class MOCKIO implements AbstractIO {

    private boolean isclosed = true;
    private ModbusClient client = null;

    public MOCKIO(ModbusClient client) {
        this.client = client;
    }

    @Override
    public boolean IsClosed() {
        return isclosed;
    }

    public void Open() throws Exception {
        this.isclosed = false;
    }

    public void Close() {
        this.isclosed = true;
    }

    @Override
    public void SendData(byte[] data) throws Exception {
        PrintLog.printIO("SEN:");
        for (int i = 0; i < data.length; i++) {
            PrintLog.printIO(String.format("%02X ", data[i]));
        }
        PrintLog.printlnIO("");
        this.client.ReceiveCmd(data);
    }

    @Override
    public int ReceiveData(byte[] data, int timeout) throws Exception {
        byte[] mem = this.client.Reply();
        System.arraycopy(mem, 0, data, 0, mem.length);        
        if (mem.length > 0) {
            PrintLog.printIO("REC:");
            for (int i = 0; i < mem.length; i++) {
                PrintLog.printIO(String.format("%02X ", mem[i]));
            }
            PrintLog.printlnIO("");
        }
        return mem.length;
    }

    @Override
    public IOInfo GetConnectInfo() {
        return new IOInfo("COM", new String[]{"COM9", "9600"});
    }

    @Override
    public int MaxBuffersize() {
        return 65535;
    }

    @Override
    public void Cancel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void SetConnectInfo(IOInfo ioinfo) {
    }
}
