/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.config;

import wqa.control.common.DevControl;
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
    DevControl mother;

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
        calconfig = new DevCalConfig(this.dev, this);

        //初始化电机配置
        if (this.dev instanceof IDevMotorConfig) {
            motorconfig = new DevMotorConfig((IDevMotorConfig) this.dev, this);
        }

        return true;
    }

    public void Close() {
        this.mother.ReleasConfig();
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
    public interface MessageInterface {

        public void SetMessage(String msg);
    }

    public void SetMessage(String message) {
        if (this.msg_instance != null);
        this.msg_instance.SetMessage(message);
    }

    private MessageInterface msg_instance;

    public void SetMessageImple(MessageInterface instance) {
        msg_instance = instance;
    }
    // </editor-fold>  
}
