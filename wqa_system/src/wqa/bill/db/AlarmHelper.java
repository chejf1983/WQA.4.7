/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.bill.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import nahon.comm.exl2.XlsSheetWriter;
import nahon.comm.exl2.xlsTable_W;
import nahon.comm.faultsystem.LogCenter;
import static wqa.bill.db.JDBAlarmTable.*;
import wqa.control.DB.AlarmRecord;
import wqa.control.DB.IAlarmHelper;
import wqa.control.common.SDisplayData;
import wqa.control.data.DevID;
import wqa.system.WQAPlatform;
import wqa.control.data.IMainProcess;
import wqa.dev.data.CollectData;

/**
 *
 * @author chejf
 */
public class AlarmHelper implements IAlarmHelper {

    private final H2DBSaver db_instance;

    public AlarmHelper(H2DBSaver db_instance) {
        this.db_instance = db_instance;
    }

    // <editor-fold defaultstate="collapsed" desc="表信息"> 
    //罗列所有设备表
    @Override
    public DevID[] ListAllDevice() {
        db_instance.dbLock.lock();
        try {
            new JDBAlarmTable(db_instance).CreateTable();
            DevID[] dev_data_tables = new JDBAlarmTable(db_instance).ListAllDevice();
            return dev_data_tables;
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "获取设备列表失败", ex);
            return new DevID[0];
        } finally {
            db_instance.dbLock.unlock();
        }
    }

    //删除指定设备表
    @Override
    public void DeleteAlarm(DevID devinfo) {
        db_instance.dbLock.lock();
        try {
            new JDBAlarmTable(db_instance).DeleteData(devinfo);
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "删除异常", ex);
        } finally {
            db_instance.dbLock.unlock();
        }
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="搜索记录"> 
    //搜索记录
    @Override
    public void SearchAlarmInfo(DevID dev_name, Date start, Date stop, IMainProcess<AlarmRecord[]> process) {
        WQAPlatform.GetInstance().GetThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                db_instance.dbLock.lock();
                //搜索报警信息
                try (ResultSet ret_set = new JDBAlarmTable(db_instance).SearchAlarm(dev_name, start, stop)) {
                    //检查是否为空
                    if (!ret_set.first()) {
                        process.Finish(new AlarmRecord[0]);
                    }

                    //获取记录条数
                    ret_set.last();
                    long data_count = ret_set.getRow();
                    ret_set.beforeFirst();

                    ArrayList<AlarmRecord> infolist = new ArrayList();
                    //转换记录
                    while (ret_set.next()) {
                        infolist.add(BuildRecord(ret_set));
                        if (ret_set.getRow() % 10 == 0) {
                            process.SetValue(100 * ret_set.getRow() / data_count);
                        }
                    }
                    process.Finish(infolist.toArray(new AlarmRecord[0]));
                } catch (Exception ex) {
                    LogCenter.Instance().SendFaultReport(Level.SEVERE, "搜索失败", ex);
                    process.Finish(new AlarmRecord[0]);
                } finally {
                    db_instance.dbLock.unlock();
                }

            }
        });
    }

    private AlarmRecord BuildRecord(ResultSet ret_set) throws SQLException {
        if (ret_set != null) {
            AlarmRecord ret = new AlarmRecord();
            ret.alarm = ret_set.getInt(Alarm_Key);
            ret.alarm_info = ret_set.getString(AlarmInfo_Key);
//        ainfo.dev_name = ret.getString(DevInfo_Key);
            ret.time = ret_set.getTimestamp(Time_Key);
            return ret;
        }
        return null;
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="导出Excel"> 
    //搜索记录
    @Override
    public void ExportToExcel(String file_name, DevID dev_name, Date start, Date stop, IMainProcess process) {
        WQAPlatform.GetInstance().GetThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                db_instance.dbLock.lock();
                //搜索报警信息
                try (ResultSet ret_set = new JDBAlarmTable(db_instance).SearchAlarm(dev_name, start, stop)) {
                    //检查是否为空
                    if (!ret_set.first()) {
                        process.Finish(new AlarmRecord[0]);
                    }

                    //获取记录条数
                    ret_set.last();
                    long data_count = ret_set.getRow();
                    ret_set.beforeFirst();

                    int tmp_index = 0;
                    String sheet_name = dev_name.ToChineseString();
                    //创建excel文件
                    try (XlsSheetWriter xl_saver = XlsSheetWriter.CreateSheet(file_name, sheet_name)) {
                        //写列名
                        xlsTable_W table = xl_saver.CreateNewTable("报警信息", data_count, new AlarmRecord().GetColumnName());

                        while (ret_set.next()) {
                            tmp_index++;
                            AlarmRecord data = BuildRecord(ret_set);
                            //添加EXCEL行
                            table.WriterLine(data.GetValue());
                            if (tmp_index++ % 10 == 0) {
                                process.SetValue((100 * tmp_index) / data_count);
                            }
                        }
                        table.Finish();
                    }
                } catch (Exception ex) {
                    LogCenter.Instance().SendFaultReport(Level.SEVERE, "搜索失败", ex);
                    process.Finish(new AlarmRecord[0]);
                } finally {
                    process.Finish(true);
                    db_instance.dbLock.unlock();
                }

            }
        });
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="保存报警信息"> 
    @Override
    public void SaveAlarmInfo(SDisplayData info) {
        db_instance.dbLock.lock();
        try {
            new JDBAlarmTable(db_instance).CreateTable();
            new JDBAlarmTable(db_instance).AddAlarm(info);
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "保存", ex);
        } finally {
            db_instance.dbLock.unlock();
        }
    }
    // </editor-fold>  
}
