/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.common;

import java.util.Date;
import wqa.dev.data.CollectData;
import wqa.dev.intf.ICollect;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import nahon.comm.event.EventCenter;
import nahon.comm.faultsystem.LogCenter;
import wqa.control.data.DevID;
import wqa.dev.data.SDataElement;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class DevMonitor {

    private final ICollect dev;
    private final DevControl parent;
    private final ReentrantLock data_lock = new ReentrantLock();

    public DevMonitor(DevControl parent, ICollect dev) {
        this.dev = dev;
        this.parent = parent;
    }

    // <editor-fold defaultstate="collapsed" desc="采集数据"> 
    public EventCenter<SDisplayData> DataEvent = new EventCenter();

    public boolean CollectData(Date time) {
        data_lock.lock();
        try {
            CollectData CollectData = this.dev.CollectData();
            CollectData.time.setTime(time.getTime());
            this.tmpdata = CreateDBData(CollectData);
            this.SaveAlarmInfo(tmpdata);
            if (this.tmpdata.alarm != 0) {
                parent.ChangeState(DevControl.ControlState.ALARM, this.tmpdata.alram_info);
            } else {
                parent.ChangeState(DevControl.ControlState.CONNECT);
            }
            DataEvent.CreateEvent(CreateDisplayData(CollectData));
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
            if (WQAPlatform.GetInstance().GetDBHelperFactory().GetAlarmDB() != null) {
                WQAPlatform.GetInstance().GetDBHelperFactory().GetAlarmDB().SaveAlarmInfo(data);
            }
        }
    }
    // </editor-fold>   

    public DevControl GetParent1() {
        return this.parent;
    }

    private SDisplayData CreateDBData(CollectData data) {
        SDisplayData tmp = new SDisplayData(new DevID(data.dev_type, data.dev_addr, data.serial_num));
        tmp.time = data.time;
        tmp.alarm = data.alarm;
        tmp.alram_info = data.alram_info;
        tmp.datas = new SDataElement[data.datas.length];
        for (int i = 0; i < tmp.datas.length; i++) {
            tmp.datas[i] = data.datas[i];
        }
        return tmp;
    }

    private SDisplayData CreateDisplayData(CollectData data) {
        SDisplayData tmp = new SDisplayData(new DevID(data.dev_type, data.dev_addr, data.serial_num));
        tmp.time = data.time;
        tmp.alarm = data.alarm;
        tmp.alram_info = data.alram_info;        
        String[] display_datas = DataHelper.GetSupportDataName(parent.GetDevID().dev_type);
        tmp.datas = new SDataElement[display_datas.length];
        for (int i = 0; i < display_datas.length; i++) {
            tmp.datas[i] = data.GetDataElement(display_datas[i]);
        }
        return tmp;
    }
}
