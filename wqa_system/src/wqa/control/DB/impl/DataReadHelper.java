/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import nahon.comm.exl2.XlsSheetWriter;
import nahon.comm.exl2.xlsTable_W;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.db.H2DBSaver;
import wqa.bill.db.JDBDataTable;
import wqa.control.DB.data.DataRecord;
import wqa.control.DB.IDataHelper;
import wqa.control.DB.data.SDataRecordResult;
import wqa.control.common.DataHelper;
import wqa.control.data.DevID;
import wqa.control.data.IMainProcess;

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

    // <editor-fold defaultstate="collapsed" desc="数据搜索接口"> 
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
            long tmp_index = 0;

            String sheet_name = table_name.ToChineseString();
            //创建excel文件
            try (XlsSheetWriter xl_saver = XlsSheetWriter.CreateSheet(file_name, sheet_name)) {

                //获取列名
                String[] tnames = DataHelper.GetSupportDataName(table_name.dev_type);
                String[] names = new String[tnames.length * 2 + 1];
                names[0] = "时间";
                for (int i = 0; i < tnames.length; i++) {
                    names[i * 2 + 1] = tnames[i];
                    names[i * 2 + 2] = "(量程)单位";
                }
                xlsTable_W table = xl_saver.CreateNewTable(table_name.ToChineseString(), data_count, names);

                //写数据
                while (ret_set.next()) {
                    tmp_index++;

                    //添加时间
                    Object[] ret = new Object[names.length];
                    ret[0] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ret_set.getTimestamp(JDBDataTable.Time_Key));
                    //添加项目
                    for (int i = 0; i < tnames.length; i++) {
                        int index = DataHelper.GetDataToDBIndex(table_name.dev_type, tnames[i]);
                        //根据显示数据内容查找静态数据表的序号，对应到数据库中的位置
                        ret[2 * i + 1] = ret_set.getFloat(JDBDataTable.DataIndexKey + index);
                        ret[2 * i + 2] = ret_set.getString(JDBDataTable.UnitIndexKey + index);
                    }
                    //添加EXCEL行
                    table.WriterLine(ret);
                    //更新进度条
                    if (tmp_index % 100 == 0) {
//                        System.out.println(tmp_index);
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

    // <editor-fold defaultstate="collapsed" desc="数据存储"> 
    private DataRecord BuildRecord(DevID id, ResultSet set) throws SQLException {
        DataRecord record = new DataRecord();
        //读取时间
        record.dev_info = id;
        record.time = set.getTimestamp(JDBDataTable.Time_Key);
        record.names = DataHelper.GetSupportDataName(id.dev_type);
        record.values = new Float[record.names.length];
        record.value_strings = new String[record.names.length];
        //获取静态数据表
        for (int i = 0; i < record.names.length; i++) {
            int index = DataHelper.GetDataToDBIndex(id.dev_type, record.names[i]);
            //根据显示数据内容查找静态数据表的序号，对应到数据库中的位置
            record.values[i] = set.getFloat(JDBDataTable.DataIndexKey + index);
            record.value_strings[i] = set.getString(JDBDataTable.UnitIndexKey + index);
        }

        return record;
    }

    @Override
    public void SaveData(DataRecord data) throws Exception {
        JDBDataTable data_dbhelper = new JDBDataTable(this.db_instance);
        //然后创建设备数据表
        data_dbhelper.CreateTableIfNotExist(data.dev_info);
        //添加数据到设备数据表
        data_dbhelper.AddData(data);
    }

    @Override
    public void DeleteTable(DevID table_name, Date beforetime) throws Exception {
        JDBDataTable data_dbhelper = new JDBDataTable(this.db_instance);
        this.db_instance.dbLock.lock();
        try {
            data_dbhelper.DeleteData(table_name, beforetime);
        } finally {
            this.db_instance.dbLock.unlock();
        }
    }
    // </editor-fold>  
}
