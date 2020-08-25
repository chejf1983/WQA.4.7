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
    private MigpClient client = null;

    public MOCKIO(MigpClient client) {
        this.client = client;
    }

    @Override
    public boolean IsClosed() {
        return isclosed;
    }

    @Override
    public void Open() throws Exception {
        this.isclosed = false;
    }

    @Override
    public void Close() {
        this.isclosed = true;
    }

    @Override
    public void SendData(byte[] data) throws Exception {
        PrintLog.PrintIO("SEN:");
        for (int i = 0; i < data.length; i++) {
            PrintLog.PrintIO(String.format("%02X ", data[i]));
        }
        PrintLog.PrintlnIO("");
        this.client.ReceiveCmd(data);
    }

    @Override
    public int ReceiveData(byte[] data, int timeout) throws Exception {
        byte[] mem = this.client.Reply();
        System.arraycopy(mem, 0, data, 0, mem.length);
        if (mem.length > 0) {
            PrintLog.PrintIO("REC:");
            for (int i = 0; i < mem.length; i++) {
                PrintLog.PrintIO(String.format("%02X ", mem[i]));
            }
            PrintLog.PrintlnIO("");
        }
        return mem.length;
    }

    @Override
    public IOInfo GetConnectInfo() {
        return new IOInfo(IOInfo.COM, "COM9", "9600");
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
