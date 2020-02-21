/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.common;

import wqa.control.data.SConnectInfo;
import wqa.control.config.IConfigList;

/**
 *
 * @author chejf
 */
public interface IDevice {  
    
    // <editor-fold defaultstate="collapsed" desc="设备控制">  
    //锁定设备
    public void LockDev() throws Exception;
    
    //解锁设备
    public void UnLockDev();
    
    //初始化设备
    public void InitDevice() throws Exception;
    
    //获取配置列表
    public IConfigList[] GetConfigLists();  
    
    //重新获取设备
    public int ReTestType();   
    // </editor-fold>      
    
    // <editor-fold defaultstate="collapsed" desc="基本信息">  
    //协议类型
    public enum ProType{
        MIGP,MODEBUS
    }
    public ProType GetProType();
    
    //获取连接信息
    public SConnectInfo GetConnectInfo();    
    // </editor-fold>  
               
}