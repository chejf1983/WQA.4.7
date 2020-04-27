/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB;

/**
 *
 * @author chejf
 */
public interface IJDBHelper {
    public void Init(String defpath)throws Exception;
    
    public void Close();
    
    public IDBFix GetDBFix();


    public IAlarmHelper GetAlarmDB();


    public IDataHelper GetDataDB();
}
