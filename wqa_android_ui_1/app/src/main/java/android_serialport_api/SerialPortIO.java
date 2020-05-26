package android_serialport_api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.IAbstractIO;
import wqa.bill.io.SIOInfo;

public class SerialPortIO implements IAbstractIO {

    private boolean isclosed = true;
    private SerialPort sp;
    private SIOInfo info;

    public SerialPortIO(SIOInfo info) {
        this.info = info;
    }

    @Override
    public boolean IsClosed() {
        return isclosed;
    }

    @Override
    public void Open() throws Exception {
        if (this.isclosed) {
            sp = new SerialPort(new File("/dev/" + info.par[0]), Integer.valueOf(info.par[1]), 0);
//                String name = "/dev/ttyS" + i;
            this.isclosed = false;
        }
    }

    @Override
    public void Close() {
        if (!this.isclosed) {
            try {
                sp.getInputStream().close();
                sp.getOutputStream().close();
            }catch (Exception ex){
                LogCenter.Instance().SendFaultReport(Level.INFO, ex);
            }
            sp.close();
            this.isclosed = true;
        }
    }

    @Override
    public void SendData(byte[] data) throws Exception {
        if (isclosed) {
            return;
        }
        FileOutputStream out = (FileOutputStream) sp.getOutputStream();
        out.write(data);
    }

    @Override
    public int ReceiveData(byte[] data, int timeout) throws Exception {
        if (isclosed) {
            return 0;
        }

        FileInputStream comin = (FileInputStream)sp.getInputStream();

        int rclen = 0;
        //10ms一个周期
        long start_time = System.currentTimeMillis();
        byte[] tmp_data = new byte[1000];
        while (System.currentTimeMillis() - start_time < timeout) {
            //如果有数据，读一次
            if (comin.available() > 0) {
                //读取到临时buffer中
                int tmp_len = comin.read(tmp_data);
                //将临时buffer复制到data中
                System.arraycopy(tmp_data, 0, data, rclen, tmp_len);
                //增加读取的长度
                rclen += tmp_len;
            } else if (rclen > 0) {
                //如果没有数据，并且已经获得了一些数据，返回
                return rclen;
            }
            //等待10ms
            TimeUnit.MILLISECONDS.sleep(5);
//            Thread.sleep(1);
        }

        return rclen;
    }

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