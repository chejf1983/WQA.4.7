/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.config;

import java.util.ArrayList;
import java.util.logging.Level;
import nahon.comm.event.EventCenter;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.ShareIO;
import wqa.adapter.factory.CDevDataTable;
import wqa.control.common.SDisplayData;
import wqa.dev.data.LogNode;
import wqa.dev.intf.ICalibrate;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class DevCalConfig {

    private final ICalibrate calbean;
    private DevConfigBean msg_instance;

    public DevCalConfig(ICalibrate calbean, DevConfigBean msg_instance) {
        this.calbean = calbean;
        this.msg_instance = msg_instance;
    }

    // <editor-fold defaultstate="collapsed" desc="定标接口"> 
    //获取定标数据类型
    public String[] GetCalType() {
        CDevDataTable.DataInfo[] datalist = this.calbean.GetCalDataList();
        ArrayList<String> ret = new ArrayList();
        for (int i = 0; i < datalist.length; i++) {
            if (datalist[i].internal_only) {
                if (WQAPlatform.GetInstance().is_internal) {
                    ret.add(datalist[i].data_name);
                }
            } else {
                ret.add(datalist[i].data_name);
            }
        }
        return ret.toArray(new String[0]);
    }

    private int GetIndex(String type) {
        for (int i = 0; i < this.GetCalType().length; i++) {
            if (type.contentEquals(this.GetCalType()[i])) {
                return i;
            }
        }

        return -1;
    }

    //获取指定类型最大定标点
    public int GetCalMaxNum(String type) {
        int index = GetIndex(type);
        if (index == -1) {
            return 0;
        }
        return this.calbean.GetCalDataList()[index].cal_num & 0xFF;
    }

    private LogNode RecordCalLog(String type, float[] oradata, float[] testdata) {
        LogNode ret = new LogNode("定标类型", type);

//        boolean isdo = (this.calbean.GetCalDataList()[GetIndex(type)].cal_num & 0xFF00) == 0x100;
        if (type.contentEquals("溶解氧")) {
//            return new String[]{"饱和氧", "无氧"};
            ret.children.add(new LogNode("校准系数", 1, "饱和氧", oradata[0]));
            if (testdata.length >= 2) {
                ret.children.add(new LogNode("校准系数", 2, "无氧", oradata[1]));
            }
            return ret;
        }

        for (int i = 0; i < oradata.length; i++) {
            ret.children.add(new LogNode("校准系数", i + 1, "原始值", oradata[i], "测量值", testdata[i]));
        }

        return ret;
    }

    public void CalParameter(String type, float[] oradata, float[] testdata) {
        LogNode condition = RecordCalLog(type, oradata, testdata);
        try {
            ((ShareIO) calbean.GetDevInfo().io).Lock();
            LogNode cal_ret = this.calbean.CalParameter(type, oradata, testdata);
            this.msg_instance.SetMessage("校准成功");
            this.msg_instance.PrintDevLog(condition, cal_ret);
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "校准失败", ex);
            this.msg_instance.PrintDevLog(condition, LogNode.CALFAIL());
        } finally {
            ((ShareIO) calbean.GetDevInfo().io).UnLock();
        }
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="定标采集"> 
    public EventCenter<SDisplayData> CalDataEvent = new EventCenter();      
    // </editor-fold> 

}
