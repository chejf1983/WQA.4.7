/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import nahon.comm.exl2.XlsSheetWriter;
import nahon.comm.exl2.xlsTable_W;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.db.JDBAlarmTable;
import wqa.bill.db.H2DBSaver;
import static wqa.bill.db.JDBAlarmTable.*;
import wqa.dev.data.DevID;
import wqa.system.WQAPlatform;
import wqa.control.data.IMainProcess;
import wqa.dev.data.SDisplayData;

/**
 *
 * @author chejf
 */
public class AlarmHelper {

    private final H2DBSaver db_instance;
    private WritableWorkbook workbook;
    private WritableSheet sheet;

    private int tableStart_column = 1;//新table的column启始位置
    private int tableStart_row = 1;   //新table的row启始位置

    private int[] column_len;

    public AlarmHelper(H2DBSaver db_instance) {
        this.db_instance = db_instance;
    }

    //罗列所有设备表
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

    //报警记录
    public class AlarmRecord {

        public int alarm;
        public String alarm_info;
        public Date time;

        public AlarmRecord(ResultSet ret) throws SQLException {
            if (ret != null) {
                alarm = ret.getInt(Alarm_Key);
                alarm_info = ret.getString(AlarmInfo_Key);
//        ainfo.dev_name = ret.getString(DevInfo_Key);
                time = ret.getTimestamp(Time_Key);
            }
        }

        public String[] GetColumnName() {
            return new String[]{"时间", "报警码", "报警信息"};
        }

        public Object[] GetValue() {
            return new Object[]{new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.time), this.alarm, this.alarm_info};
        }
    }

    //搜索记录
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
                        infolist.add(new AlarmRecord(ret_set));
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

    //搜索记录
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
                        xlsTable_W table = xl_saver.CreateNewTable("报警信息", data_count, new AlarmRecord(null).GetColumnName());

                        while (ret_set.next()) {
                            tmp_index++;
                            AlarmRecord data = new AlarmRecord(ret_set);
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
}
