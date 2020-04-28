/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.bill.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import nahon.comm.exl2.XlsSheetWriter;
import nahon.comm.exl2.xlsTable_W;
import nahon.comm.faultsystem.LogCenter;
import wqa.control.DB.DataRecord;
import wqa.control.DB.IDataHelper;
import wqa.control.DB.SDataRecordResult;
import wqa.control.common.SDisplayData;
import wqa.control.data.DevID;
import wqa.control.data.IMainProcess;
import wqa.dev.data.CollectData;

/**
 *
 * @author chejf
 */
public class DataReadHelper implements IDataHelper {

    private final H2DBSaver db_instance;

    public DataReadHelper(H2DBSaver db_instance) {
        this.db_instance = db_instance;
    }

    // <editor-fold defaultstate="collapsed" desc="表信息"> 
    //列出所存储设备列表名称
    @Override
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

    @Override
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

    @Override
    public void SearchLimitData(DevID table_name, Date start, Date stop, int limit_num, wqa.control.data.IMainProcess<SDataRecordResult> process) {
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
                //保存结果
                ret.data.add(BuildRecord(table_name, ret_set));
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
    }

    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc="转换到Excel"> 
    @Override
    public void ExportToFile(String file_name, DevID table_name, Date start, Date stop, IMainProcess process) {
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
                String[] names = DataRecord.GetNames(table_name);
                //写列名
                xlsTable_W table = xl_saver.CreateNewTable(table_name.ToChineseString(), data_count, names);
                while (ret_set.next()) {
                    tmp_index++;
                    //添加EXCEL行
                    table.WriterLine(BuildRecord(table_name, ret_set).GetValue());
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
    }
    // </editor-fold>  

    public DataRecord BuildRecord(DevID id, ResultSet set) throws SQLException {
        DataRecord record = new DataRecord(id);
        //读取时间
        record.time = set.getTimestamp(JDBDataTable.Time_Key);

        //获取静态数据表
        for (int i = 0; i < record.values.length; i++) {
            int index = DataRecord.GetDataToDBIndex(id, record.names[i]);
            //根据显示数据内容查找静态数据表的序号，对应到数据库中的位置
            record.values[i] = set.getFloat(JDBDataTable.DataIndexKey + index);
            record.value_strings[i] = set.getString(JDBDataTable.UnitIndexKey + index);
        }

        return record;
    }

    @Override
    public void SaveData(SDisplayData data) throws Exception {
        JDBDataTable data_dbhelper = new JDBDataTable(this.db_instance);
        //然后创建设备数据表
        data_dbhelper.CreateTableIfNotExist(data.dev_id);
        //添加数据到设备数据表
        data_dbhelper.AddData(data);
    }
}
