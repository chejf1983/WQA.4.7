/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB;

import java.util.Date;
import wqa.control.data.DevID;
import wqa.control.data.IMainProcess;
import wqa.dev.data.CollectData;

/**
 *
 * @author chejf
 */
public interface IAlarmHelper {


    // <editor-fold defaultstate="collapsed" desc="数据存储"> 
    public DevID[] ListAllDevice();

    //删除指定设备表
    public void DeleteAlarm(DevID devinfo);


    public void SaveAlarmInfo(CollectData info);
    
    //搜索记录
    public void SearchAlarmInfo(DevID dev_name, Date start, Date stop, IMainProcess<AlarmRecord[]> process);
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="导出Excel"> 
    //搜索记录
    public void ExportToExcel(String file_name, DevID dev_name, Date start, Date stop, IMainProcess process);
    // </editor-fold>  
}
