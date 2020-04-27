/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.common;

import java.util.ArrayList;
import wqa.dev.data.CollectData;
import wqa.dev.intf.ICollect;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import nahon.comm.event.EventCenter;
import nahon.comm.faultsystem.LogCenter;
import wqa.adapter.factory.CDevDataTable;
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

    public boolean CollectData() {
        data_lock.lock();
        try {
            this.tmpdata = this.dev.CollectData();
            this.SaveAlarmInfo(tmpdata);
            if (this.tmpdata.alarm != 0) {
                parent.ChangeState(DevControl.ControlState.ALARM, this.tmpdata.alram_info);
            } else {
                parent.ChangeState(DevControl.ControlState.CONNECT);
            }
            DataEvent.CreateEvent(CreateDisplayData(this.tmpdata));
            return true;
        } catch (Exception ex) {
            LogCenter.Instance().PrintLog(Level.SEVERE, "采集数据失败", ex);
            return false;
        } finally {
            data_lock.unlock();
        }
    }

    private CollectData tmpdata = null;

    public CollectData ReceiveByDB() {
        data_lock.lock();
        try {
            CollectData ret = tmpdata;
            tmpdata = null;
            return ret;
        } finally {
            data_lock.unlock();
        }
    }

    private int last_alarm = -1;

    private void SaveAlarmInfo(CollectData data) {
        if (data.alarm != this.last_alarm) {
            //ainfo.SaveTo(db_instance);
            if (last_alarm == -1 && data.alarm == 0) {
                data.alram_info = "添加新设备" + parent.ToString();
            }
            this.last_alarm = data.alarm;
            WQAPlatform.GetInstance().GetDBHelperFactory().GetAlarmDB().SaveAlarmInfo(data);
        }
    }
    // </editor-fold>   

    public String[] GetSupportDataName() {
        //单位信息
        CDevDataTable.DevInfo d_infos = CDevDataTable.GetInstance().namemap.get(this.parent.GetDevID().dev_type);
        ArrayList<String> list = new ArrayList();
        if (d_infos != null) {
            for (CDevDataTable.DataInfo info : d_infos.data_list) {
                if (info.internal_only) {
                    if (WQAPlatform.GetInstance().is_internal) {
                        list.add(info.data_name);
                    }
                } else {
                    list.add(info.data_name);
                }
            }
        }
        return list.toArray(new String[0]);
    }

    public DevControl GetParent1() {
        return this.parent;
    }

    private SDisplayData CreateDisplayData(CollectData data){
        SDisplayData tmp = new SDisplayData(this.parent.GetDevID());
        tmp.time = data.time;
        tmp.alarm = data.alarm;
        tmp.alram_info = data.alram_info;
        String[] display_datas = this.GetSupportDataName();
        tmp.datas = new SDataElement[display_datas.length];
        for(int i = 0; i < display_datas.length; i++){
            tmp.datas[i] = data.GetDataElement(display_datas[i]);
        }
        return tmp;
    }
}
