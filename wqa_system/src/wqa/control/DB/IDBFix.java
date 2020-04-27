/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB;

import java.util.Date;

/**
 *
 * @author chejf
 */
public interface IDBFix {
    public void DeleteData(Date beforetime, wqa.control.data.IMainProcess process);
    
    public float GetDBSize();
}
