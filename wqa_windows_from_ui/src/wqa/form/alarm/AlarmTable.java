/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.alarm;

import java.text.SimpleDateFormat;
import javax.swing.table.AbstractTableModel;
import wqa.bill.db.AlarmHelper;
import wqa.adapter.factory.CErrorTable;
import wqa.control.DB.AlarmRecord;

/**
 *
 * @author chejf
 */
public class AlarmTable extends AbstractTableModel {

    private String[] table_names = new String[]{"序号", "时间", "报警号", "报警信息"};
    public static int[] table_with = new int[]{60, 150, 90, 0};
    private AlarmRecord[] infos;

    public AlarmTable(AlarmRecord[] ainfo_list) {
        this.infos = ainfo_list;
    }

    public AlarmRecord[] GetAlarmInfo() {
        return this.infos;
    }

    @Override
    public String getColumnName(int i) {
        return this.table_names[i];
    }

    @Override
    public int getRowCount() {
        return infos.length;
    }

    @Override
    public int getColumnCount() {
        return this.table_names.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AlarmRecord info = this.infos[rowIndex];
        if (columnIndex == 0) {
            return rowIndex;
        } else if (columnIndex == 1) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(info.time);
        } else if (columnIndex == 2) {
            return String.format("0x%04X", CErrorTable.GetInstance().TranslateErrorCode(info.alarm));
        } else if (columnIndex == 3) {
            return info.alarm_info;
        } else {
            return null;
        }
    }

}
