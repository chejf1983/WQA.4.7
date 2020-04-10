/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.winio.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import wqa.bill.io.ShareIO;
import java.util.logging.Level;
import java.util.logging.Logger;
import nahon.comm.event.Event;
import nahon.comm.event.EventCenter;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.IAbstractIO;
import wqa.bill.io.SDataPacket;
import wqa.bill.io.SIOInfo;
import wqa.system.WQAPlatform;
import wqa.dev.intf.IMAbstractIO;

/**
 *
 * @author chejf
 */
public class IOManager implements EventListener {

    private static IOManager instance;

    private IOManager() {
        InitLogWatchDog();
    }

    public static IOManager GetInstance() {
        if (instance == null) {
            instance = new IOManager();
        }
        return instance;
    }

    // <editor-fold defaultstate="collapsed" desc="IO驱动"> 
    private static IIOFactory iofactory;

    public static void SetIOFactory(IIOFactory instance) {
        iofactory = instance;
    }
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="IO管理"> 
    private ArrayList<ShareIO> localio = new ArrayList();

    public ShareIO GetSIO(SIOInfo info) {
        for (ShareIO io : localio) {
            if (io.GetConnectInfo().equalto(info)) {
                return io;
            }
        }
        return null;
    }

    //手动添加串口
    private ArrayList<SIOInfo> add_com = new ArrayList();

    public boolean AddUserCOM(String COM) {
//        if(this.GetSIO(info))
        for (ShareIO io : this.GetAllIO()) {
            if (COM.contentEquals(io.GetConnectInfo().par[0])) {
                return false;
            }
        }
        SIOInfo sioInfo = new SIOInfo(SIOInfo.COM, COM, WQAPlatform.GetInstance().GetConfig().getProperty(COM, "9600"));
        this.add_com.add(sioInfo);
        return true;
    }

    //创建共享IO口
    private void BuildShareIO(SIOInfo info) {
        //如果IO已经存在，不创建IO
        if (this.GetSIO(info) != null) {
            return;
        }

        //创建IO
        IAbstractIO io = iofactory.CreateIO(info.iotype, info.par);
        if (io != null) {
            ShareIO sio = new ShareIO(io);
            //添加IO监听数据
            sio.SendReceive.RegeditListener(new nahon.comm.event.EventListener<SDataPacket>() {
                @Override
                public void recevieEvent(Event<SDataPacket> event) {
                    WriterData(event.GetEvent());
                }
            });
            localio.add(sio);
        }
    }

    //刷新IO口
    public ShareIO[] RefleshAllIO() {
        //检查驱动是否加载
        if (iofactory == null) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "没有IO驱动");
            return GetAllIO();
        }

        //清除没有打开的IO
        ArrayList<ShareIO> open_io = new ArrayList();
        this.localio.forEach(io -> {
            //保留已经打开的IO和有用户的IO
            if (!io.IsClosed() || io.UserNum > 0) {
                open_io.add(io);
            }
        });
        this.localio.clear();
        this.localio = open_io;

        //添加手动添加的IO口
        for (int i = 0; i < add_com.size(); i++) {
            BuildShareIO(add_com.get(i));
        }

        //添加系统自动列出的IO口
        String[] com_names = iofactory.ListAllCom();
        for (int i = 0; i < com_names.length; i++) {
            this.BuildShareIO(new SIOInfo(SIOInfo.COM, com_names[i], WQAPlatform.GetInstance().GetConfig().getProperty(com_names[i], "9600")));
        }

        return GetAllIO();
    }

    public ShareIO[] GetAllIO() {
        return localio.toArray(new ShareIO[0]);
    }
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="IOlog处理"> 
    private final ReentrantLock log_lock = new ReentrantLock();

    public EventCenter<String> SendReceive = new EventCenter();

    private ArrayList<String> buffer_A = new ArrayList();
    private ArrayList<String> buffer_B = new ArrayList();
    private ArrayList<String> buffer_in = buffer_A;
    private ArrayList<String> buffer_out = buffer_B;

    private void WriterData(SDataPacket data) {
        log_lock.lock();
        try {
            String sdata = "";
            for (byte bdata : data.data) {
                sdata += String.format("%02X ", bdata);
            }

            buffer_in.add(
                    data.info.par[0] + ": " //串口号
                    + new SimpleDateFormat("HH:mm:ss SSS:  ").format(data.time) //时间
                    + data.type.toString() + ": " //发送还是接收
                    + sdata); //数据
        } finally {
            log_lock.unlock();
        }
    }

    private void InitLogWatchDog() {
        WQAPlatform.GetInstance().GetThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                while (!WQAPlatform.GetInstance().GetThreadPool().isShutdown()) {
                    //检查是否有输入log
                    if (buffer_in.size() > 0) {
                        //交换输入输出buffer
                        log_lock.lock();
                        try {
                            ArrayList<String> tmp = buffer_in;
                            buffer_in = buffer_out;
                            buffer_out = tmp;
                        } finally {
                            log_lock.unlock();
                        }

                        try {
                            for (String log : buffer_out) {
                                //打印log
                                SendReceive.CreateEvent(log);
                            }
                        } catch (Exception ex) {
                            LogCenter.Instance().SendFaultReport(Level.SEVERE, "刷新错误", ex);
                        }

                        buffer_out.clear();
                    }

                    //等待400ms
                    try {
                        TimeUnit.MILLISECONDS.sleep(400);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(IOManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        });
    }
    // </editor-fold>   
}
