/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB;

import wqa.control.DB.data.DataRecord;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;
import wqa.control.DB.impl.JDBHelper;
import wqa.control.common.DevControl;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class DBHelper {

    // <editor-fold defaultstate="collapsed" desc="定时设置"> 
    private int time_span = 10;
    private final String Time_Span_Key = "CollectTime";
    private final int max_time = 3600;
    private final int min_time = 1;

    public void SetCollectTime(int time_span) {
        if (time_span > this.max_time) {
            time_span = this.max_time;
        }

        if (time_span < this.min_time) {
            time_span = this.min_time;
        }

        if (this.time_span != time_span) {
            this.time_span = time_span;
            WQAPlatform.GetInstance().GetConfig().setProperty(Time_Span_Key, String.valueOf(time_span));
        }
    }

    //返回秒
    public int GetCollectTimeBySecond() {
        return this.time_span;
    }

    public int GetMaxCollectTimeBySecond() {
        return this.max_time;
    }

    public int GetMinCollectTimeBySecond() {
        return this.min_time;
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="定时采集"> 
    private boolean is_record_start = false;
    private Date LastTime = new Date();
    private IJDBHelper db_instance;

    public void SetDB(IJDBHelper helper) {
        this.db_instance = helper;
    }

    public void Init(String path) throws Exception {
        if (!this.is_record_start) {
            //设置启动标志
            this.is_record_start = true;
            if (db_instance == null) {
                db_instance = new JDBHelper();
            }
            db_instance.Init(path);
            //读取时间间隔配置
            String stime_spane = WQAPlatform.GetInstance().GetConfig().getProperty(Time_Span_Key, "10");
            try {
                this.time_span = Integer.valueOf(stime_spane);
            } catch (NumberFormatException ex) {
                this.time_span = 10;
            }
            //启动进程
            WQAPlatform.GetInstance().GetThreadPool().submit(new Runnable() {
                @Override
                public void run() {
                    LastTime.setTime(((long) (new Date().getTime() / 1000)) * 1000);

                    while (!WQAPlatform.GetInstance().GetThreadPool().isShutdown()) {
                        //如果时间差超过时间间隔(ms),启动一次采集
                        if (new Date().getTime() >= LastTime.getTime()) {
                            //更新时间
                            while (new Date().getTime() >= LastTime.getTime()) {
                                LastTime.setTime(LastTime.getTime() + time_span * 1000);
                            }

                            CollectData();
                        }

                        //休息
                        try {
                            TimeUnit.MILLISECONDS.sleep(500);
                        } catch (InterruptedException ex) {
                            LogCenter.Instance().PrintLog(Level.SEVERE, ex);
                        }
                    }
                }
            });
        }
    }

    //采集数据
    private void CollectData() {
        //遍历所有设备控制器
        DevControl[] controls = WQAPlatform.GetInstance().GetManager().GetAllControls();
        for (DevControl control : controls) {
            //获取DB缓存栈
            DataRecord data = control.GetCollector().ReceiveByDB();
            //如果没有数据，不保存
            if (data == null || data.names.length == 0) {
                continue;
            }

            try {
                //添加数据到设备数据表
                this.GetDataDB().SaveData(data);
            } catch (Exception ex) {
                LogCenter.Instance().PrintLog(Level.SEVERE, ex);
            }
        }
    }

    public void Close() {
        db_instance.Close();
    }
    // </editor-fold>  

    public IDBFix GetDBFix() {
        return this.db_instance.GetDBFix();
    }

    public IAlarmHelper GetAlarmDB() {
        return this.db_instance.GetAlarmDB();
    }

    public IDataHelper GetDataDB() {
        return this.db_instance.GetDataDB();
    }
}
