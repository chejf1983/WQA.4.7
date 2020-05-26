/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.naqing.mockdev;

import wqa.adapter.factory.*;
import wqa.bill.io.IAbstractIO;
import wqa.bill.io.SIOInfo;
import wqa.dev.data.*;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class MOCKIO implements IAbstractIO {

    private boolean isclosed = true;
    private ModbusClient client = null;

    public MOCKIO(ModbusClient client, SIOInfo info) {
        this.client = client;
        this.info = info;
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
        if(isclosed){
            return;
        }

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

    private SIOInfo info = new SIOInfo(SIOInfo.COM, "COM9", "9600");
    @Override
    public SIOInfo GetConnectInfo() {
        return info;
    }

    @Override
    public void SetConnectInfo(SIOInfo info) {
        this.info = info;
    }

    @Override
    public int MaxBuffersize() {
        return 65535;
    }

}
