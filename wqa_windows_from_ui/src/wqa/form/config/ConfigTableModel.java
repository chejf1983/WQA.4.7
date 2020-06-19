/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.config;

import javax.swing.table.AbstractTableModel;
import wqa.dev.intf.SConfigItem;

/**
 *
 * @author chejf
 */
public class ConfigTableModel extends AbstractTableModel {

    private SConfigItem[] list = new SConfigItem[0];
    private String[] names = new String[]{"名称(范围)", "数值"};
    public static int[] column_len = {200, 140};

    public ConfigTableModel(SConfigItem[] list) {
        this.list = list;
    }

    public SConfigItem[] GetValues() {
        return this.list;
    }

    @Override
    public String getColumnName(int i) {
        return this.names[i];
    }

    @Override
    public int getRowCount() {
        return this.list.length;
    }

    @Override
    public int getColumnCount() {
        return names.length;
    }

    private String GetRange(int rowIndex) {
        return list[rowIndex].unit.contentEquals("") ? "" : "(" + list[rowIndex].unit + ")";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            if (list[rowIndex].inputtype == SConfigItem.ItemType.W
                    || list[rowIndex].inputtype == SConfigItem.ItemType.S
                    || list[rowIndex].inputtype == SConfigItem.ItemType.B) {
                return "* " + list[rowIndex].data_name + GetRange(rowIndex);
            } else {
                return list[rowIndex].data_name + GetRange(rowIndex);
            }
        } else {
            if (list[rowIndex].inputtype == SConfigItem.ItemType.B) {
                return Boolean.valueOf(list[rowIndex].GetValue());
            } else {
                return list[rowIndex].GetValue();
            }
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            return list[rowIndex].inputtype == SConfigItem.ItemType.W
                    || list[rowIndex].inputtype == SConfigItem.ItemType.S
                    || list[rowIndex].inputtype == SConfigItem.ItemType.B;
        } else {
            return false;
        }
    }

    @Override
    public void setValueAt(Object o, int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            list[rowIndex].SetValue(o.toString());
        }
    }

}
