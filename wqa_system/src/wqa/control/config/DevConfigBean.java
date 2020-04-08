/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.config;

import nahon.comm.event.EventCenter;
import wqa.bill.log.DevLog;
import wqa.dev.data.LogNode;
import wqa.control.common.DevControl;
import wqa.dev.intf.ICalibrate;
import wqa.dev.intf.IConfigList;
import wqa.dev.intf.IDevMotorConfig;
import wqa.dev.intf.IDevice;

/**
 *
 * @author chejf
 */
public class DevConfigBean {

    //设备适配器
    private IDevice dev;
    private DevControl mother;

    public DevConfigBean(DevControl mother) {
        this.mother = mother;
    }

    //初始化配置模块
    public boolean InitDevConfig(IDevice dev) {
        this.dev = dev;

        IConfigList[] iconfig_list = this.dev.GetConfigLists();
        //初始化基本信息
        this.config_list = new DevConfigTable[iconfig_list.length];
        for (int i = 0; i < iconfig_list.length; i++) {
            config_list[i] = new DevConfigTable(this.dev, iconfig_list[i], this);
        }

        //初始化定标配置
        if (this.dev instanceof ICalibrate) {
            calconfig = new DevCalConfig((ICalibrate) this.dev, this);
        }

        //初始化电机配置
        if (this.dev instanceof IDevMotorConfig) {
            motorconfig = new DevMotorConfig((IDevMotorConfig) this.dev, this);
        }

        return true;
    }
    
    public EventCenter CloseEvent = new EventCenter();

    public void Close() {
        if (this.GetDevCalConfig() != null) {
            this.GetDevCalConfig().SetStartGetData(false);
        }
        CloseEvent.CreateEvent(null);
    }

    // <editor-fold defaultstate="collapsed" desc="配置模块"> 
    //基本配置
    private DevConfigTable[] config_list;

    public DevConfigTable[] GetBaseDevConfig() {
        return config_list;
    }

    //定标配置
    private DevCalConfig calconfig;

    public DevCalConfig GetDevCalConfig() {
        return calconfig;
    }

    //电机配置
    private DevMotorConfig motorconfig;

    public DevMotorConfig GetMotorConfig() {
        return motorconfig;
    }

    // </editor-fold>   
    
    // <editor-fold defaultstate="collapsed" desc="显示消息提示"> 
    public void PrintDevLog(LogNode... nodes) {
        DevLog.Instance().AddLog(this.dev.GetConnectInfo().dev_id, nodes);
    }

    public interface MessageInterface {

        public void SetMessage(String msg);
    }

    public void SetMessage(String message) {
        this.msg_instance.SetMessage(message);
    }

    private MessageInterface msg_instance = (String msg) -> {
    };

    public void SetMessageImple(MessageInterface instance) {
        msg_instance = instance;
    }
    // </editor-fold>  

    public void Quit() {
        this.mother.StopConfig();
    }
}
