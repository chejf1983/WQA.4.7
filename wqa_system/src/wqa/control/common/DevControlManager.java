/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.common;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import nahon.comm.event.EventCenter;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.ShareIO;
import wqa.control.data.IMainProcess;
import wqa.dev.intf.IDevice;
import wqa.dev.intf.IDeviceSearch;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class DevControlManager {

    public DevControlManager() {
        max_auto_num = Integer.valueOf(WQAPlatform.GetInstance().GetConfig().getProperty(AUTOMAX, "14"));
    }

    // <editor-fold defaultstate="collapsed" desc="搜索设备"> 
    private static final int MAX_ADDR = 0x20;

    public void SearchDevice(IDeviceSearch input_drv, ShareIO[] iolist, IMainProcess<Boolean> process) {
        //检查驱动
        if (input_drv == null) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "没有加载驱动");
            if (process != null) {
                process.Finish(false);
            }
            return;
        }

        //罗列所有物理口
        float max_num = iolist.length * MAX_ADDR;
        float search_num = 0;

        //遍历所有物理口
        for (ShareIO io : iolist) {
            try {
                io.Lock();
                //重新开关一次串口
                io.Close();
                io.Open();
                //遍历32个地址
                for (int i = 1; i <= MAX_ADDR; i++) {
                    if (process != null) {
                        process.SetValue(100 * (search_num++) / max_num);
                    }

                    //检查是否已经存在该地址了，不搜索重复地址的设备
                    boolean isexist = false;
                    for (DevControl control : control_list) {
                        if (control.GetDevID().dev_addr == i) {
                            isexist = true;
                            break;
                        }
                    }
                    if (isexist) {
                        continue;
                    }

                    try {
                        //搜索设备基本信息，根据基本信息创建虚拟设备
                        DevControl newcontrol = AddNewDevice(input_drv.SearchOneDev(io, (byte) i));
                        if (newcontrol != null) {
                            newcontrol.Start();
                        } else {
                            System.out.println("地址没有设备:" + i);
                        }
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (Exception ex) {
                        //超时表示没有搜索到设备
                        System.out.println(ex);
                    }
                }
            } catch (Exception ex) {
                //IO打开失败，开始下一个IO口
//                System.out.println(ex.getMessage());
                //break;
            } finally {
                io.UnLock();
            }
        }

        //遍历所有控制器，再搜索完毕后初始化所有设备
//        for (DevControl control : control_list) {
//        control_list.forEach((control) -> {
//        }
        if (process != null) {
            process.Finish(true);
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="自动搜索"> 
    private String AUTOMAX = "AUTOMAX";
    private int max_auto_num = 14;

    public void SetMaxAutoNum(int num) {
        this.max_auto_num = num;
        WQAPlatform.GetInstance().GetConfig().setProperty(AUTOMAX, num + "");
    }

    public int GetMaxAutoNum() {
        return this.max_auto_num;
    }

    private ArrayList<ShareIO> iolist = new ArrayList();
    private final ReentrantLock iolock = new ReentrantLock(true);

    public void AddAutoSearchIO(ShareIO io) {
        iolock.lock();
        try {
            for (ShareIO tio : iolist) {
                if (tio == io) {
                    return;
                }
            }
            iolist.add(io);
        } finally {
            iolock.unlock();
        }
    }

    public void DelAutoSearchIO(ShareIO io) {
        iolock.lock();
        try {
            iolist.remove(io);
        } finally {
            iolock.unlock();
        }
    }

    private IDeviceSearch dev_drv;

    public IDeviceSearch GetAutoSearchDriver() {
        return this.dev_drv;
    }

    public void ChangeAutoSeachDriver(IDeviceSearch dev_drv) {
        if (!isstart) {
            isstart = true;
            this.InitProcess();
        }
        this.dev_drv = dev_drv;
    }

    private boolean isstart = false;

    private void InitProcess() {
        WQAPlatform.GetInstance().GetThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                while (!WQAPlatform.GetInstance().GetThreadPool().isShutdown()) {
                    //选择第一个IO
                    ShareIO getio = null;
                    iolock.lock();
                    try {
                        if (!iolist.isEmpty()) {
                            getio = iolist.remove(0);
                            iolist.add(getio);
                        }
                    } finally {
                        iolock.unlock();
                    }

                    if (getio != null) {
                        AutoConnect(getio);
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DevControlManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    private void AutoConnect(ShareIO io) {
        for (int i = 1; i <= max_auto_num; i++) {
            if (io.IsClosed() || dev_drv == null) {
                return;
            }

            io.Lock();
            try {
                //检查是否已经存在该地址了，不搜索重复地址的设备
                boolean isexist = false;
                for (DevControl control : control_list) {
                    if (control.GetDevID().dev_addr == i) {
                        isexist = control.GetProType().contentEquals(this.dev_drv.ProType());
                        break;
                    }
                }
                if (isexist) {
                    continue;
                }

                //搜索设备基本信息，根据基本信息创建虚拟设备
                DevControl newcontrol = AddNewDevice(dev_drv.SearchOneDev(io, (byte) i));
                if (newcontrol != null) {
                    newcontrol.Start();
                }
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (Exception ex) {
                //超时表示没有搜索到设备
//                System.out.println(ex);
            } finally {
                io.UnLock();
            }

            try {
                TimeUnit.MILLISECONDS.sleep(200 + this.control_list.size() * 200);
            } catch (InterruptedException ex) {
                Logger.getLogger(DevControlManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="设备增加删除接口"> 
    public enum DevNumChange {
        ADD,
        DEL
    }
    //增减设备事件
    public EventCenter<DevNumChange> StateChange = new EventCenter();
    //设备数组
    private ArrayList<DevControl> control_list = new ArrayList();
    private final ReentrantLock list_lock = new ReentrantLock(true);

    //获取所有设备
    public DevControl[] GetAllControls() {
        return this.control_list.toArray(new DevControl[0]);
    }

    //添加新设备
    public DevControl AddNewDevice(IDevice dev) {
        if (dev == null) {
            return null;
        }

        DevControl newdev = null;
        list_lock.lock();
        try {
            //避免重复添加
            for (DevControl tmp : this.control_list) {
                if (dev.GetDevInfo().dev_type == tmp.GetDevID().dev_type
                        && dev.GetDevInfo().dev_addr == tmp.GetDevID().dev_addr) {
                    //已经存在就不再搜索
                    return null;
                }
            }
            //生成新控制器
            newdev = new DevControl(dev);
            this.control_list.add(newdev);
        } finally {
            list_lock.unlock();
        }

        //超时表示没有搜索到设备
        System.out.println("找到设备地址:" + dev.GetDevInfo().dev_addr);

        //通知新设备添加，生成界面
        StateChange.CreateEvent(DevControlManager.DevNumChange.ADD, newdev);
        return newdev;
    }

    //删除控制器
    public void DeleteDevControl(DevControl del_dev) {
        list_lock.lock();
        try {
            for (int i = 0; i < this.control_list.size(); i++) {
                if (this.control_list.get(i).equals(del_dev)) {
                    del_dev.End();
                    this.control_list.remove(del_dev);
                    //通知设备删除，刷新界面
                    StateChange.CreateEvent(DevControlManager.DevNumChange.DEL, del_dev);
                    break;
                }
            }
        } finally {
            list_lock.unlock();
        }
    }

    public void DeleteAllControls() {
        list_lock.lock();
        try {
            for (DevControl control : control_list) {
                control.End();
                //通知设备删除，刷新界面
                StateChange.CreateEvent(DevControlManager.DevNumChange.DEL, control);
            }

            this.control_list.clear();
        } finally {
            list_lock.unlock();
        }
    }
    // </editor-fold> 
}
