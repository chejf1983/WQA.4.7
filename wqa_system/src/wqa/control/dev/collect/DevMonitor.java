/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.dev.collect;

import wqa.dev.data.SDisplayData;
import wqa.dev.intf.ICollect;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import nahon.comm.event.EventCenter;
import nahon.comm.faultsystem.LogCenter;
import wqa.control.common.DevControl;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class DevMonitor {

    private final ICollect dev;
    private DevControl parent;
    private final ReentrantLock data_lock = new ReentrantLock();

    public DevMonitor(DevControl parent, ICollect dev) {
        this.dev = dev;
        this.parent = parent;
    }

    // <editor-fold defaultstate="collapsed" desc="采集数据"> 
    public EventCenter<SDisplayData> DataEvent = new EventCenter();
    
    public boolean CollectData() {
        data_lock.lock();
        try {
            this.tmpdata = this.dev.CollectData();
//            this.tmpdata.dev_name = this.parent.ToString();
            this.SaveAlarmInfo(tmpdata);
            DataEvent.CreateEvent(this.tmpdata);
            return true;
        } catch (Exception ex) {
            LogCenter.Instance().PrintLog(Level.SEVERE, "采集数据失败", ex);
            return false;
        } finally {
            data_lock.unlock();
        }
    }

    private SDisplayData tmpdata = null;

    public SDisplayData ReceiveByDB() {
        data_lock.lock();
        try {
            SDisplayData ret = tmpdata;
            tmpdata = null;
            return ret;
        } finally {
            data_lock.unlock();
        }
    }

    private int last_alarm = -1;

    private void SaveAlarmInfo(SDisplayData data) {
        if (data.alarm != this.last_alarm) {
            //ainfo.SaveTo(db_instance);
            if (last_alarm == -1 && data.alarm == 0) {
                data.alram_info = "添加新设备" + parent.ToString();
            }
            this.last_alarm = data.alarm;
            WQAPlatform.GetInstance().GetDBHelperFactory().GetAlarmFind().SaveAlarmInfo(data);
        }
    }
    // </editor-fold>   
}
