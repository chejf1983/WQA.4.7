/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.db.JDBDataTable;
import wqa.bill.db.H2DBSaver;
import wqa.control.common.CDevDataTable;
import wqa.control.dev.collect.SDisplayData;
import wqa.control.common.DevControl;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class DataSaveHelper {

    private final H2DBSaver db_instance;

    public DataSaveHelper(H2DBSaver db_instance) {
        this.db_instance = db_instance;
    }

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
            WQAPlatform.GetInstance().SaveConfig();
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

    public void InitRecordProcess() throws Exception {
        if (!this.is_record_start) {
            //设置启动标志
            this.is_record_start = true;
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
                    while (!WQAPlatform.GetInstance().GetThreadPool().isShutdown()) {
                        //如果时间差超过时间间隔(ms),启动一次采集
                        if (new Date().getTime() - LastTime.getTime() > time_span * 1000) {
                            //更新时间
                            LastTime = new Date();
                            CollectData();
                        }
                        
                        //休息
                        try {
                            TimeUnit.SECONDS.sleep(1);
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
            SDisplayData data = control.GetCollector().ReceiveByDB();

            //如果没有数据，不保存
            if (data == null) {
                continue;
            }

            this.db_instance.dbLock.lock();
            try {
                JDBDataTable data_dbhelper = new JDBDataTable(this.db_instance);
                //然后创建设备数据表
                data_dbhelper.CreateTableIfNotExist(data.dev_id, CDevDataTable.GetInstance().namemap.get(data.dev_id.dev_type).data_list.length);
                //添加数据到设备数据表
                data_dbhelper.AddData(data);
            } catch (Exception ex) {
                LogCenter.Instance().PrintLog(Level.SEVERE, ex);
            } finally {
                this.db_instance.dbLock.unlock();
            }
        }
    }
    // </editor-fold>  
}
