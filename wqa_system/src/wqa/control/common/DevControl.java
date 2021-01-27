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
import nahon.comm.event.NEventCenter;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.ShareIO;
import wqa.control.data.DevID;
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
    public NEventCenter<ControlState> StateChange = new NEventCenter();

    public void ChangeState(ControlState state, String info) {
        if (this.state != state) {
            this.state = state;
            LogCenter.Instance().PrintLog(Level.SEVERE, "切换状态->" + state);
        }
        this.StateChange.CreateEvent(state, info);
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
        return this.GetDevID().ToChineseString() + "(" + device.GetDevInfo().io.GetConnectInfo().par[0] + ")";
    }
    // </editor-fold>    

    // <editor-fold defaultstate="collapsed" desc="动作"> 
    private void KeepAlive() throws Exception {
        //检查到一次成功，就重连设备
        if (this.device.ReTestType(1)) {
            device.InitDevice();
            ChangeState(ControlState.CONNECT);
        }
    }

    private void MainAction() {
        try {
            ((ShareIO) device.GetDevInfo().io).Lock();
            //连接状态下，获取数据
            if (GetState() == ControlState.DISCONNECT) {
                KeepAlive();
                if (GetState() == ControlState.CONNECT) {
                    if (!GetCollector().CollectData(this.LastTime)) {
                        ChangeState(ControlState.DISCONNECT);
                    }
                }
            } else {
                if (!GetCollector().CollectData(this.LastTime)) {
                    ChangeState(ControlState.DISCONNECT);
                }
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
                if (new Date().getTime() >= LastTime.getTime()) {
                    //更新时间
                    while (new Date().getTime() >= LastTime.getTime()) {
                        LastTime.setTime(LastTime.getTime() + 2 * 1000);
                    }
                    state_lock.lock();
                    try {
                        if (this.is_start) {
                            MainAction();
                        } else {
                            break;
                        }
                    } finally {
                        state_lock.unlock();
                    }
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
                WQAPlatform.GetInstance().GetThreadPool().submit(run_process);
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
            this.collect_instance = new DevMonitor(this, this.device);
        }
        return this.collect_instance;
    }

    DevConfigBean configmodel = new DevConfigBean(this);

    //开始配置
    public DevConfigBean GetConfig() {
        state_lock.lock();
        try {
            if (this.GetState() != ControlState.DISCONNECT
                    && this.GetState() != ControlState.CONFIG) {
                this.configmodel = new DevConfigBean(this);
                this.configmodel.InitDevConfig(this.device);
                this.ChangeState(ControlState.CONFIG);
                return configmodel;
            } else {
                return null;
            }
        } finally {
            state_lock.unlock();
        }
    }

    public void ReleasConfig() {
        state_lock.lock();
        try {
            if (this.GetState() == ControlState.CONFIG) {
                this.ChangeState(ControlState.CONNECT);
            }
        } finally {
            state_lock.unlock();
        }
    }
    // </editor-fold>   

}
