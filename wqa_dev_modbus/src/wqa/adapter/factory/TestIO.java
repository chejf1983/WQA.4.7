/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.factory;

import nahon.comm.io.AbstractIO;
import nahon.comm.io.IOInfo;

/**
 *
 * @author chejf
 */
public class TestIO implements AbstractIO {

    @Override
    public boolean IsClosed() {
        return false;
    }

    @Override
    public void Open() throws Exception {
        return;
    }

    @Override
    public void Close() {
        return;
    }

    @Override
    public void SendData(byte[] bytes) throws Exception {
        for (int i = 0; i < bytes.length; i++) {
            System.out.print(String.format("%02X ", bytes[i]));
        }
        System.out.println();
    }

    @Override
    public int ReceiveData(byte[] bytes, int i) throws Exception {
        return 0;
    }

    @Override
    public void Cancel() {
        return;
    }

    @Override
    public IOInfo GetConnectInfo() {
        return new IOInfo("TEST", "1");
    }

    @Override
    public void SetConnectInfo(IOInfo ioinfo) {
        return;
    }

    @Override
    public int MaxBuffersize() {
        return 65535;
    }
}
