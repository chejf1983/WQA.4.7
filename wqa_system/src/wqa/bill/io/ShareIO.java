/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.bill.io;

import java.util.concurrent.locks.ReentrantLock;
import nahon.comm.event.EventCenter;
import nahon.comm.io.AbstractIO;
import nahon.comm.io.IOInfo;
import wqa.bill.io.SDataPacket.IOEvent;

/**
 *
 * @author chejf
 */
public class ShareIO implements AbstractIO {

    private AbstractIO io;
    private final ReentrantLock share_lock = new ReentrantLock(true);

    public EventCenter<SDataPacket> SendReceive = new EventCenter();

    public ShareIO(AbstractIO io) {
        this.io = io;
    }

    // <editor-fold defaultstate="collapsed" desc="IO控制">   
    public void Lock() {
        share_lock.lock();
    }

    public void UnLock() {
        if (share_lock.isLocked()) {
            share_lock.unlock();
        }
    }

    @Override
    public void Open() throws Exception {
        this.io.Open();
    }

    @Override
    public void Close() {
        this.io.Close();
    }

    @Override
    public IOInfo GetConnectInfo() {
        return this.io.GetConnectInfo();
    }

    @Override
    public void SetConnectInfo(IOInfo info) {
        this.io.SetConnectInfo(info);
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="IMAbstractIO接口"> 
    @Override
    public void SendData(byte[] data) throws Exception {
        if (!this.io.IsClosed()) {
            byte[] tmp = new byte[data.length];
            System.arraycopy(data, 0, tmp, 0, data.length);
            this.SendReceive.CreateEventAsync(new SDataPacket(this.io.GetConnectInfo(), IOEvent.Send, tmp));
            this.io.SendData(data);
        }
    }

    @Override
    public int ReceiveData(byte[] data, int timeout) throws Exception {
        if (!this.io.IsClosed()) {
            int reclen = this.io.ReceiveData(data, timeout);
            if (reclen > 0) {
                byte[] tmp = new byte[reclen];
                System.arraycopy(data, 0, tmp, 0, reclen);
                this.SendReceive.CreateEventAsync(new SDataPacket(this.io.GetConnectInfo(), IOEvent.Receive, tmp));
            }
            return reclen;
        } else {
            return 0;
        }
    }

    @Override
    public boolean IsClosed() {
        return this.io.IsClosed();
    }

    @Override
    public int MaxBuffersize() {
        return this.io.MaxBuffersize();
    }
    // </editor-fold>  

    @Override
    public void Cancel() {
        this.io.Cancel();
    }
}
