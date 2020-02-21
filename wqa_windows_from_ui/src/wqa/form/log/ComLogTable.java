/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.log;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jiche
 */
public class ComLogTable extends AbstractTableModel {

    private int maxline = 1000;
    private ArrayList<String> log = new ArrayList();

    public void AddLog(String onelog) {
        if (this.log.size() > this.maxline) {
            this.log.remove(0);
        }
        this.log.add(onelog);
        this.fireTableDataChanged();
    }

    public void Clear() {
        this.log.clear();
        this.fireTableDataChanged();
    }

    @Override
    public String getColumnName(int i) {
        return "日志";
    }

    @Override
    public int getRowCount() {
        return log.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int i, int i1) {
        return log.get(i);
    }
}
