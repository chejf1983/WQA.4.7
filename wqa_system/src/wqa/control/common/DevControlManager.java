/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.common;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import nahon.comm.event.EventCenter;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.ShareIO;
import wqa.control.data.IMainProcess;
import wqa.dev.intf.IDevice;
import wqa.dev.intf.IDeviceSearch;

/**
 *
 * @author chejf
 */
public class DevControlManager {

    private static IDeviceSearch dev_drv;
    private static final int max_addr = 0x20;

    public static void SetDevDriver(IDeviceSearch instance) {
        dev_drv = instance;
    }

    public static IDeviceSearch GetDevDriver() {
        return dev_drv;
    }

    // <editor-fold defaultstate="collapsed" desc="搜索设备"> 
    public void SearchDevice(ShareIO[] iolist, IMainProcess<Boolean> process) {
        //检查驱动
        if (DevControlManager.dev_drv == null) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "没有加载驱动");
            if (process != null) {
                process.Finish(false);
            }
            return;
        }

        //罗列所有物理口
//        ShareIO[] iolist = IOManager.GetInstance().GetAllIO();

        float max_num = iolist.length * max_addr;
        float search_num = 0;

        //遍历所有物理口
        for (ShareIO io : iolist) {
            try {
                io.Lock();
                //重新开关一次串口
                io.Close();
                io.Open();
                //遍历32个地址
                for (int i = 1; i < max_addr; i++) {
                    if (process != null) {
                        process.SetValue(100 * (search_num++) / max_num);
                    }
                    try {
                        //搜索设备基本信息，根据基本信息创建虚拟设备
                        AddNewDevice(DevControlManager.dev_drv.SearchOneDev(io, (byte) i));
                        
                        //超时表示没有搜索到设备
                        System.out.println("找到设备地址:" + i);
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (Exception ex) {
                        //超时表示没有搜索到设备
                        System.out.println(ex);
                    }
                }
            } catch (Exception ex) {
                //IO打开失败，开始下一个IO口
                System.out.println(ex.getMessage());
                //break;
            } finally {
                io.UnLock();
            }
        }

        //遍历所有控制器，再搜索完毕后初始化所有设备
        for (DevControl control : control_list) {
//        control_list.forEach((control) -> {
            try {
                //只初始化关闭状态下的控制器
                control.Start();
            } catch (Exception ex) {
                LogCenter.Instance().SendFaultReport(Level.SEVERE, "设备初始化失败", ex);
            }
        };
        if (process != null) {
            process.Finish(true);
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

    //获取所有设备
    public DevControl[] GetAllControls() {
        return this.control_list.toArray(new DevControl[0]);
    }

    //添加新设备
    private DevControl AddNewDevice(IDevice dev) {
        if (dev == null) {
            return null;
        }

        try {
            dev.InitDevice();
            //避免重复添加
            for (DevControl tmp : this.control_list) {
                if (dev.GetDevInfo().EqualsTo(tmp.GetConnectInfo())) {
                    //已经存在就不再搜索
                    return null;
                }
            }
            //生成新控制器
            DevControl newdev = new DevControl(dev);
            this.control_list.add(newdev);
            //通知新设备添加，生成界面
            StateChange.CreateEvent(DevControlManager.DevNumChange.ADD, newdev);
            return newdev;
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "设备初始化失败", ex);
            return null;
        }
    }

    //删除控制器
    public void DeleteDevControl(DevControl del_dev) {
        for (int i = 0; i < this.control_list.size(); i++) {
            if (this.control_list.get(i).equals(del_dev)) {
                del_dev.End();
                this.control_list.remove(del_dev);
                //通知设备删除，刷新界面
                StateChange.CreateEvent(DevControlManager.DevNumChange.DEL, del_dev);
                break;
            }
        }
    }

    public void DeleteAllControls() {
        for (DevControl control : this.GetAllControls()) {
            control.End();
            //通知设备删除，刷新界面
            StateChange.CreateEvent(DevControlManager.DevNumChange.DEL, control);
        }

        this.control_list.clear();
    }
    // </editor-fold> 
}
