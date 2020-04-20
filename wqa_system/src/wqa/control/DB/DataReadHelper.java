/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import nahon.comm.exl2.XlsSheetWriter;
import nahon.comm.exl2.xlsTable_W;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.db.JDBDataTable;
import wqa.bill.db.H2DBSaver;
import wqa.control.data.DevID;
import wqa.control.data.IMainProcess;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class DataReadHelper {

    private final H2DBSaver db_instance;
    private final DBHelperFactory parent;

    public DataReadHelper(DBHelperFactory parent, H2DBSaver db_instance) {
        this.db_instance = db_instance;
        this.parent = parent;
    }

    // <editor-fold defaultstate="collapsed" desc="表信息"> 
    //列出所存储设备列表名称
    public DevID[] ListAllDevice() {
        db_instance.dbLock.lock();
        try {
            return new JDBDataTable(this.db_instance).ListAllDevice();
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "获取设备列表失败", ex);
            return new DevID[0];
        } finally {
            db_instance.dbLock.unlock();
        }
    }

    public void DeleteTable(DevID table_name) throws Exception {
        new JDBDataTable(this.db_instance).DropTable(table_name);
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="数据读取接口"> 
    //搜索数据
    private ResultSet SearchData(DevID table_name, Date start, Date stop) throws Exception {
        db_instance.dbLock.lock();
        try {
            JDBDataTable db_helper = new JDBDataTable(db_instance);
            //搜索数据库结果
            return db_helper.SearchRecords(table_name, start, stop);
        } finally {
            db_instance.dbLock.unlock();
        }

    }

    public void SearchLimitData(DevID table_name, Date start, Date stop, int limit_num, wqa.control.data.IMainProcess<SDataRecordResult> process) {
        WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
            db_instance.dbLock.lock();
            try (ResultSet ret_set = SearchData(table_name, start, stop)) {
                SDataRecordResult ret = new SDataRecordResult();

                //检查是否为空集
                if (!ret_set.first()) {
                    process.Finish(new SDataRecordResult());
                }

                //统计记录个数
                ret_set.last();
                long data_count = ret_set.getRow();
                ret.search_num = data_count;
                ret_set.first();

                //计算跳跃次数
                double data_to_jump = ((double) data_count / limit_num);
                if (data_to_jump < 1) {
                    data_to_jump = 1;//int count = 0;
                }

                int row = 1;
                //跳跃搜索数据
                while (ret_set.absolute(row)) {
                    //增加一个转换结果
                    SDataRecordResult.DataRecord record = ret.new DataRecord(table_name);
                    record.InitData(ret_set);
                    //保存结果
                    ret.data.add(record);
                    row += data_to_jump;
                    //count = 0;
                    process.SetValue(100 * ret_set.getRow() / data_count);
                }

                //通知完成
                process.Finish(ret);
            } catch (Exception ex) {
                LogCenter.Instance().SendFaultReport(Level.SEVERE, "搜索失败", ex);
                process.Finish(new SDataRecordResult());
            } finally {
                db_instance.dbLock.unlock();
            }
        });
    }

    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc="转换到Excel"> 
    public void ExportToFile(String file_name, DevID table_name, Date start, Date stop, IMainProcess process) {
        WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
            db_instance.dbLock.lock();
            try (ResultSet ret_set = SearchData(table_name, start, stop)) {
                //检查是否为空
                if (!ret_set.first()) {
                    return;
                }
                //统计总条数
                ret_set.last();
                long data_count = ret_set.getRow();
                ret_set.beforeFirst();
                int tmp_index = 0;

                String sheet_name = table_name.ToChineseString();
                //创建excel文件
                try (XlsSheetWriter xl_saver = XlsSheetWriter.CreateSheet(file_name, sheet_name)) {
                    String[] names = GetNames(table_name);
                    //写列名
                    xlsTable_W table = xl_saver.CreateNewTable(table_name.ToChineseString(), data_count, names);
                    while (ret_set.next()) {
                        tmp_index++;
                        //添加EXCEL行
                        table.WriterLine(GetValue(table_name, ret_set));
                        if (tmp_index++ % 100 == 0) {
                            process.SetValue((100 * tmp_index) / data_count);
                        }
                    }
                    table.Finish();
                }
            } catch (Exception ex) {
                LogCenter.Instance().SendFaultReport(Level.SEVERE, "保存失败", ex);
            } finally {
                db_instance.dbLock.unlock();
                process.Finish(true);
            }
        });
    }

    public Object[] GetValue(DevID devid, ResultSet set) throws Exception {
        String names[] = SDataRecordResult.GetSupportData(devid);
        Object[] ret = new Object[names.length * 2 + 1];
        ret[0] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(set.getTimestamp(JDBDataTable.Time_Key));
        for (int i = 0; i < names.length; i++) {
            int index = SDataRecordResult.GetDataToDBIndex(devid, names[i]);
            ret[i * 2 + 1] = set.getFloat(JDBDataTable.DataIndexKey + index);
            String info = set.getString(JDBDataTable.UnitIndexKey + index);
            ret[i * 2 + 2] = info.contentEquals("") ? "--" : info;
        }
        return ret;
    }

    public String[] GetNames(DevID dev_info) {
        String[] element = SDataRecordResult.GetSupportData(dev_info);
        String[] names = new String[element.length * 2 + 1];
        names[0] = "时间";
        for (int i = 0; i < element.length; i++) {
            names[i * 2 + 1] = element[i];
            names[i * 2 + 2] = "(量程)单位";
        }
        return names;
    }
    // </editor-fold>  
}
