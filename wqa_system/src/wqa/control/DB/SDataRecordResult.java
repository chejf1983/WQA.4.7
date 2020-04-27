/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import wqa.adapter.factory.CDevDataTable;
import wqa.bill.db.JDBDataTable;
import wqa.control.data.DevID;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
//搜索结果
public class SDataRecordResult {

    public long search_num;
    public ArrayList<DataRecord> data;

    public SDataRecordResult() {
        search_num = 0;
        data = new ArrayList();
    }
}
