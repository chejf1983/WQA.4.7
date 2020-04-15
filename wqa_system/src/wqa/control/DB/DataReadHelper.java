/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import nahon.comm.exl2.XlsSheetWriter;
import nahon.comm.exl2.xlsTable_W;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.db.JDBDataTable;
import wqa.bill.db.H2DBSaver;
import wqa.adapter.factory.CDevDataTable;
import wqa.bill.db.DataRecord;
import wqa.dev.data.DevID;
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
    public static String[] GetSupportData(DevID dev_id) {
        CDevDataTable.DevInfo d_infos = CDevDataTable.GetInstance().namemap.get(dev_id.dev_type);
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
//    public class DevTableInfo {
//
//        public DevID dev_id;
//        public CDevDataTable.DataInfo[] data_element;
//
//        public DevTableInfo(DevID dev_id) {
//            this.dev_id = dev_id;
//            ArrayList<CDevDataTable.DataInfo> list = new ArrayList();
//            CDevDataTable.DevInfo d_infos = CDevDataTable.GetInstance().namemap.get(dev_id.dev_type);
//            if (d_infos != null) {
//                for (CDevDataTable.DataInfo info : d_infos.data_list) {
//                    if (info.internal_only) {
//                        if (WQAPlatform.GetInstance().is_internal) {
//                            list.add(info);
//                        }
//                    } else {
//                        list.add(info);
//                    }
//                }
//            }
//            data_element = list.toArray(new CDevDataTable.DataInfo[0]);
//        }
//
//        public int GetSelectIndex(String name) {
//            for (int i = 0; i < data_element.length; i++) {
//                if (name.contentEquals(data_element[i].data_name)) {
//                    return i;
//                }
//            }
//
//            return -1;
//        }
//    }

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
    //搜索结果
    public class SearchResult {

        public long search_num;
        public DataRecord[] data;

        public SearchResult() {
            search_num = 0;
            data = new DataRecord[0];
        }
    }

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

    public void SearchLimitData(DevID table_name, Date start, Date stop, int limit_num, wqa.control.data.IMainProcess<SearchResult> process) {
        WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
            db_instance.dbLock.lock();
            try (ResultSet ret_set = SearchData(table_name, start, stop)) {
                ArrayList<DataRecord> data = new ArrayList();
                SearchResult ret = new SearchResult();

                //检查是否为空集
                if (!ret_set.first()) {
                    process.Finish(new SearchResult());
                }

                //统计记录个数
                ret_set.last();
                long data_count = ret_set.getRow();
                ret.search_num = data_count;
                ret_set.first();

                //计算跳跃次数
                long data_to_jump = data_count / limit_num;
                if (data_to_jump == 0) {
                    data_to_jump++;//int count = 0;
                }

                //跳跃搜索数据
                while (ret_set.absolute((int) (ret_set.getRow() + data_to_jump))) {
                    //增加一个转换结果
                    data.add(new DataRecord(table_name, ret_set));
                    //count = 0;
                    process.SetValue(100 * ret_set.getRow() / data_count);
                }

                //保存结果
                ret.data = data.toArray(new DataRecord[0]);
                //通知完成
                process.Finish(ret);
            } catch (Exception ex) {
                LogCenter.Instance().SendFaultReport(Level.SEVERE, "搜索失败", ex);
                process.Finish(new SearchResult());
            } finally {
                db_instance.dbLock.unlock();
            }
        });
    }

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
                    String[] names = new DataRecord(table_name).GetNames();
                    //写列名
                    xlsTable_W table = xl_saver.CreateNewTable(table_name.ToChineseString(), data_count, names);
                    while (ret_set.next()) {
                        tmp_index++;
                        DataRecord data = new DataRecord(table_name, ret_set);
                        //添加EXCEL行
                        table.WriterLine(GetValue(data));
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

    public Object[] GetValue(DataRecord data) {
        Object[] ret = new Object[data.values.length * 2 + 1];
        ret[0] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data.time);
        for (int i = 0; i < data.values.length; i++) {
            ret[i * 2 + 1] = data.values[i];
            ret[i * 2 + 2] = data.value_strings[i].contentEquals("") ? "--" : data.value_strings[i];
        }
        return ret;
    }
    // </editor-fold>  
}
