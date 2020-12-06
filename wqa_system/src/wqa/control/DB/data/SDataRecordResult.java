/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB.data;

import java.util.ArrayList;

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
