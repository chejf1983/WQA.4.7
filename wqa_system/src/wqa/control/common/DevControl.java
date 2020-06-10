/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.common;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import wqa.control.config.DevConfigBean;
import nahon.comm.event.EventCenter;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.ShareIO;
import wqa.control.data.DevID;
import wqa.dev.intf.ICollect;
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
        ALARM,
        DISCONNECT,
        CONFIG
    }

    private ControlState state = ControlState.DISCONNECT;
    private final ReentrantLock state_lock = new ReentrantLock(true);

    public ControlState GetState() {
        return this.state;
    }
    public EventCenter<ControlState> StateChange = new EventCenter();

    public void ChangeState(ControlState state, String info) {
        if (this.state != state) {
            this.state = state;
            this.StateChange.CreateEvent(state, info);
            LogCenter.Instance().PrintLog(Level.SEVERE, "切换状态->" + state);
        }
    }

    public void ChangeState(ControlState state) {
        ChangeState(state, "");
    }
    // </editor-fold>    

    // <editor-fold defaultstate="collapsed" desc="基本信息"> 
    private final IDevice device;

    public DevControl(IDevice device) {
        this.device = device;
    }

    public DevID GetDevID() {
        return new DevID(device.GetDevInfo().dev_type, device.GetDevInfo().dev_addr, device.GetDevInfo().serial_num);
    }

    public String GetProType() {
        return this.device.GetDevInfo().protype.toString();
    }

    public String ToString() {
        //获取数据key
        return this.GetDevID().ToChineseString() + "(" + device.GetDevInfo().io.GetIOInfo().par[0] + ")";
    }
    // </editor-fold>    

    // <editor-fold defaultstate="collapsed" desc="动作"> 
    private void KeepAlive() throws Exception {
        //其他状态下，开心跳检查重连设备
        //在断开情况下
        if (GetState() == ControlState.DISCONNECT) {
            //检查到一次成功，就重连设备
            if (ReConnect(1)) {
                device.InitDevice();
                ChangeState(ControlState.CONNECT);
            }
        } else {
            //在配置状态下，重连3次失败才认为设备断开
            if (!ReConnect(3)) {
                //心跳包多一次检查
                ChangeState(ControlState.DISCONNECT);
                StopConfig();
            }
        }
    }

    public boolean ReConnect(int retry) {
        return this.device.ReTestType(retry);
    }

    private void MainAction() {
        try {
            ((ShareIO) device.GetDevInfo().io).Lock();
            //连接状态下，获取数据
            if (GetState() == ControlState.CONNECT) {
                if (!GetCollector().CollectData(this.LastTime)) {
                    ChangeState(ControlState.DISCONNECT);
                }
            } else if (GetState() == ControlState.ALARM) {
                if (!GetCollector().CollectData(this.LastTime)) {
                    ChangeState(ControlState.DISCONNECT);
                }
            } else if (GetState() == ControlState.DISCONNECT) {
                KeepAlive();
            } else if (GetState() == ControlState.CONFIG) {
                KeepAlive();
            }
        } catch (Exception ex) {
            ChangeState(ControlState.DISCONNECT);
            LogCenter.Instance().PrintLog(Level.SEVERE, ex);
        } finally {
            ((ShareIO) device.GetDevInfo().io).UnLock();

        }
    }

    private Date LastTime = new Date();

    private class Process implements Runnable {

        boolean is_start = true;

        @Override
        public void run() {
            LastTime.setTime(((long) (new Date().getTime() / 1000)) * 1000);
            while (is_start) {
                state_lock.lock();
                try {
                    if (new Date().getTime() >= LastTime.getTime()) {
                        //更新时间
                        while (new Date().getTime() >= LastTime.getTime()) {
                            LastTime.setTime(LastTime.getTime() + 2 * 1000);
                        }
                        MainAction();
                    }
                } finally {
                    state_lock.unlock();
                }

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DevMonitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
// </editor-fold>    

// <editor-fold defaultstate="collapsed" desc="输入">
    private Process run_process = null;

    public void Start() {
        state_lock.lock();
        try {
            if (this.run_process == null) {
                run_process = new Process();
//                this.ChangeState(ControlState.CONNECT);
                WQAPlatform.GetInstance().GetThreadPool().submit(run_process);
            }
        } finally {
            state_lock.unlock();
        }
    }

    private DevConfigBean configmodel;

    //开始配置
    public DevConfigBean StartConfig() {
        state_lock.lock();
        try {
            if (this.GetState() == ControlState.CONNECT
                    || this.GetState() == ControlState.ALARM) {
                configmodel = new DevConfigBean(this);
                configmodel.InitDevConfig(this.device);
                ChangeState(ControlState.CONFIG);
                return configmodel;
            } else {
                return null;
            }
        } finally {
            state_lock.unlock();
        }
    }

    //关闭配置
    public void StopConfig() {
        state_lock.lock();
        try {
            if (this.configmodel != null) {
//                this.configmodel.GetDevCalConfig()
                if (this.configmodel.GetDevCalConfig() != null) {
                    this.configmodel.GetDevCalConfig().SetStartGetData(false);
                }
                this.configmodel.CloseEvent.CreateEvent(null);

                if (this.GetState() == DevControl.ControlState.CONFIG) {
                    this.ChangeState(DevControl.ControlState.CONNECT);
                }
                this.configmodel = null;
            }
        } finally {
            state_lock.unlock();
        }
    }

    //停止控制
    public void End() {
        state_lock.lock();
        try {
            if (this.run_process != null) {
                this.StopConfig();
                this.ChangeState(ControlState.DISCONNECT);
                run_process.is_start = false;
                run_process = null;
            }
        } finally {
            state_lock.unlock();
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
    // </editor-fold>   

}
