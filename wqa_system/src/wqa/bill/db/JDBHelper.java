/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.bill.db;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import wqa.control.DB.IAlarmHelper;
import wqa.control.DB.IDBFix;
import wqa.control.DB.IDataHelper;
import wqa.control.DB.IJDBHelper;

/**
 *
 * @author chejf
 */
public class JDBHelper implements IJDBHelper {

    H2DBSaver saver = new H2DBSaver();
    IDBFix fix;
    IAlarmHelper ahelper;
    IDataHelper dhelper;

    @Override
    public void Init(String defpath) throws Exception {
        saver.SetDBPath(defpath);
        saver.OPEN();
        fix = new DBFixHelper(saver);
        ahelper = new AlarmHelper(saver);
        dhelper = new DataReadHelper(saver);
    }

    @Override
    public void Close() {
        try {
            saver.CLOSE();
        } catch (SQLException ex) {
            Logger.getLogger(JDBHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public IDBFix GetDBFix() {
        return this.fix;
    }

    @Override
    public IAlarmHelper GetAlarmDB() {
        return this.ahelper;
    }

    @Override
    public IDataHelper GetDataDB() {
        return dhelper;
    }
}
