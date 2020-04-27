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
public interface IDataHelper {
    
    // <editor-fold defaultstate="collapsed" desc="数据读取接口"> 
    //列出所存储设备列表名称
    public DevID[] ListAllDevice();

    public void DeleteTable(DevID table_name) throws Exception ;

    public void SearchLimitData(DevID table_name, Date start, Date stop, int limit_num, wqa.control.data.IMainProcess<SDataRecordResult> process) ;

    public void SaveData(CollectData data) throws Exception ;
    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc="转换到Excel"> 
    public void ExportToFile(String file_name, DevID table_name, Date start, Date stop, IMainProcess process);
    // </editor-fold>  
}
