/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.bill.io;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import nahon.comm.event.Event;
import nahon.comm.event.EventCenter;
import nahon.comm.faultsystem.LogCenter;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class IOManager {

    // <editor-fold defaultstate="collapsed" desc="IO管理"> 
    private ArrayList<ShareIO> localio = new ArrayList();

    public ShareIO FindIO(SIOInfo info) {
        for (ShareIO io : localio) {
            if (io.GetConnectInfo().equalto(info)) {
                return io;
            }
        }
        return null;
    }

    //创建共享IO口
    public ShareIO ADDShareIO(IAbstractIO io) {
        //创建IO
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
            return sio;
        }
        return null;
    }

    public ShareIO[] GetAllIO() {
        return localio.toArray(new ShareIO[0]);
    }

    public ShareIO[] GetAllOpenIO() {
        ArrayList<ShareIO> open_coms = new ArrayList();
        for (ShareIO io : GetAllIO()) {
            if (!io.IsClosed()) {
                open_coms.add(io);
            }
        }
        return open_coms.toArray(new ShareIO[0]);
    }

    private HashMap<String, IChangeConfig> changeArray = new HashMap();

    public interface IChangeConfig {

        String changeConfig(ShareIO io_instance, SIOInfo info);
    }

    public void RegitedConfig(String type, IChangeConfig instance) {
        this.changeArray.put(type, instance);
    }

    public void ChangeIOConfig(ShareIO io_instance, SIOInfo info) throws Exception {
        if (this.FindIO(io_instance.GetConnectInfo()) == null) {
            throw new Exception("没有找到接口");
        }

        IChangeConfig config = changeArray.get(io_instance.GetConnectInfo().iotype);
        if (config == null) {
            throw new Exception("未知类型");
        }

        config.changeConfig(io_instance, info);
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

    public void InitLogWatchDog() {
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
                                if(MaxLogNum < temp_log.size()){
                                    temp_log.remove(0);
                                }
                                temp_log.add(log);
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
    
    private final ArrayList<String> temp_log = new ArrayList();
    private static int MaxLogNum = 1000;
    public ArrayList<String> GetLaterLog(){
        return this.temp_log;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="IO存储"> 
    private int par_num = 2;
    private HashMap<String, IKeyConvert> converts = new HashMap();

    public interface IKeyConvert {

        String getKey(SIOInfo info);
    }

    public void RegitedConvert(String type, IKeyConvert instance) {
        this.converts.put(type, instance);
    }

    public String GetKey(SIOInfo info) throws Exception {
        IKeyConvert convert = this.converts.get(info.iotype);
        if (convert == null) {
            throw new Exception("未知类型");
        }

        return convert.getKey(info);
    }

    public void SaveIOConfig(String Key, ShareIO io) {
        SIOInfo sioInfo = io.GetConnectInfo();
        this.SaveIOConfig(Key, sioInfo);
    }

    public void SaveIOConfig(String Key, SIOInfo sioInfo) {
        WQAPlatform.GetInstance().GetConfig().setProperty(Key, sioInfo.iotype);
        for (int i = 0; i < par_num; i++) {
            if (sioInfo.par.length > i) {
                WQAPlatform.GetInstance().GetConfig().setProperty(Key + "P" + i, sioInfo.par[i]);
            } else {
                WQAPlatform.GetInstance().GetConfig().setProperty(Key + "P" + i, "");
            }
        }
    }

    public SIOInfo GetIOConfig(String Key) {
        String iotype = WQAPlatform.GetInstance().GetConfig().getProperty(Key, "NON");
        if (iotype.contentEquals("NON")) {
            return null;
        }

        ArrayList<String> pars = new ArrayList<>();
        for (int i = 0; i < par_num; i++) {
            String par = WQAPlatform.GetInstance().GetConfig().getProperty(Key + "P" + i, "NON");
            if (par.contentEquals("NON")) {
                break;
            } else {
                pars.add(par);
            }
        }

        return new SIOInfo(iotype, pars.toArray(new String[0]));
    }
    // </editor-fold>   
}
