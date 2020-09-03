/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.dev.intf;

import wqa.adapter.factory.CDevDataTable;
import wqa.dev.data.CollectData;
import wqa.dev.data.LogNode;
import wqa.dev.data.SDevInfo;


/**
 *
 * @author chejf
 */
public interface IDevice {  
    
    // <editor-fold defaultstate="collapsed" desc="设备控制">      
    //初始化设备
    public void InitDevice() throws Exception;
    
    //获取配置列表
    public IConfigList[] GetConfigLists();  
    
    public CollectData CollectData() throws Exception;
    
    //重新获取设备
    public boolean ReTestType(int retry);       
    // </editor-fold>      
    
    // <editor-fold defaultstate="collapsed" desc="基本信息">    
    //获取连接信息
    public SDevInfo GetDevInfo();    
    // </editor-fold>  
    
    
    // <editor-fold defaultstate="collapsed" desc="定标接口">   
    //max_num， 表示有几点定标， 
    public CDevDataTable.DataInfo[] GetCalDataList();
    
    //输入定标数据
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception;
    // </editor-fold>  
               
}