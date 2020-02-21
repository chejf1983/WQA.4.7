/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB;

import java.sql.SQLException;
import wqa.bill.db.H2DBSaver;

/**
 *
 * @author chejf
 */
public class DBHelperFactory {

    private H2DBSaver h2db;

    public void Init() throws Exception {
        h2db = new H2DBSaver();
        this.h2db.OPEN();

        recorder = new DataSaveHelper(this.h2db);
        this.recorder.InitRecordProcess();

        finder = new DataReadHelper(this, this.h2db);

        fix = new DBFixHelper(this.h2db);

        this.alam_finder = new AlarmHelper(this.h2db);
    }

    public void Close() throws SQLException {
        
        this.h2db.CLOSE();
    }

    private DataSaveHelper recorder;

    public DataSaveHelper GetDataRecorder() {
        return this.recorder;
    }

    private DataReadHelper finder;

    public DataReadHelper GetDataFinder() {
        return this.finder;
    }

    private DBFixHelper fix;

    public DBFixHelper GetDBFix() {
        return this.fix;
    }

    private AlarmHelper alam_finder;

    public AlarmHelper GetAlarmFind() {
        return this.alam_finder;
    }
}
