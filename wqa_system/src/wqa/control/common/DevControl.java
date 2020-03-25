/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.common;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import wqa.control.config.DevConfigBean;
import nahon.comm.event.EventCenter;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.ShareIO;
import wqa.dev.intf.ICollect;
import wqa.dev.data.SConnectInfo;
import wqa.dev.intf.IDevice;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class DevControl {

    // <editor-fold defaultstate="collapsed" desc="控制器状态"> 
    public enum ControlState {
        CONNECT,
        DISCONNECT,
        CONFIG,
        CLOSE
    }

    private ControlState state = ControlState.CLOSE;

    public ControlState GetState() {
        return this.state;
    }
    public EventCenter<ControlState> StateChange = new EventCenter();

    public void ChangeState(ControlState state) {
        if (this.state != ControlState.CLOSE) {
            if (this.state != state) {
                this.state = state;
                this.StateChange.CreateEvent(state);
                LogCenter.Instance().PrintLog(Level.SEVERE, "切换状态->" + state);
            }
        }
    }
    // </editor-fold>    

    // <editor-fold defaultstate="collapsed" desc="基本信息"> 
    private final IDevice device;

    public DevControl(IDevice device) {
        this.device = device;
    }

    public void Init() throws Exception {
        try {
            ((ShareIO) device.GetIO()).Lock();
            this.state = ControlState.DISCONNECT;
            this.ChangeState(ControlState.CONNECT);
            this.InitProcess();
            ((ShareIO) device.GetIO()).UserNum++;
        } finally {
            ((ShareIO) device.GetIO()).UnLock();
        }
    }

    public void Close() {
        if (this.configmodel != null) {
            this.configmodel.Close();
        }
        this.ChangeState(ControlState.CLOSE);
//        WQAPlatform.GetInstance().GetManager().DeleteDevControl(this);
        ((ShareIO) device.GetIO()).UserNum--;
    }

    public SConnectInfo GetConnectInfo() {
        return device.GetConnectInfo();
    }

    public IDevice.ProType GetProType() {
        return this.device.GetProType();
    }

    public String ToString() {
        //获取数据key
        return this.device.GetConnectInfo().dev_id.ToChineseString() + "(" + GetConnectInfo().io.GetConnectInfo().par[0] + ")";
    }
    // </editor-fold>    

    // <editor-fold defaultstate="collapsed" desc="循环进程"> 
    private void InitProcess() {
        WQAPlatform.GetInstance().GetThreadPool().submit(new Runnable() {
            @Override
            public void run() {
//                while (GetState() != ControlState.CLOSE && !WQAPlatform.GetInstance().GetThreadPool().isShutdown()) {
                while (GetState() != ControlState.CLOSE) {
                    try {
                        ((ShareIO) device.GetIO()).Lock();
                        //连接状态下，获取数据
                        if (GetState() == ControlState.CONNECT) {
                            if (!GetCollector().CollectData()) {
                                ChangeState(ControlState.DISCONNECT);
                            }
                        } else {
                            //其他状态下，开心跳检查重连设备
                            if (ReConnect()) {
                                if (GetState() == ControlState.DISCONNECT) {
                                    device.InitDevice();
                                    ChangeState(ControlState.CONNECT);
                                }
                            } else {
                                //心跳包多一次检查
                                if (!ReConnect()) {
                                    ChangeState(ControlState.DISCONNECT);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ChangeState(ControlState.DISCONNECT);
                        LogCenter.Instance().PrintLog(Level.SEVERE, ex.getMessage());
                    } finally {
                        ((ShareIO) device.GetIO()).UnLock();
                    }

                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DevMonitor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    public boolean ReConnect() {
        try {
            int devtype = this.device.ReTestType();
            return this.device.GetConnectInfo().dev_id.dev_type == devtype;
        } catch (Exception ex) {
            return false;
        }
    }
    // </editor-fold>    

    // <editor-fold defaultstate="collapsed" desc="模块"> 
    private DevMonitor collect_instance;

    public DevMonitor GetCollector() {
        if (this.collect_instance == null) {
            this.collect_instance = new DevMonitor(this, (ICollect) this.device);
        }
        return this.collect_instance;
    }

    private DevConfigBean configmodel;

    public DevConfigBean GetConfigImpl() {
        //连接状态下，才可以配置设备
        if (this.GetState() == ControlState.CONNECT) {
//            try {
            //初始化需要的配置信息
//                this.device.InitDevice();
            configmodel = new DevConfigBean(this);
            configmodel.InitDevConfig(this.device);
            ChangeState(ControlState.CONFIG);
            return configmodel;
//            } catch (Exception ex) {
//                LogCenter.Instance().SendFaultReport(Level.SEVERE, "读取配置失败:" + ex);
//                return null;
//            }
        } else {
            return null;
        }
    }
    // </editor-fold>   
}
