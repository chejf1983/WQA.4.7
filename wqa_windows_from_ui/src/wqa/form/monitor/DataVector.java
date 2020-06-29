/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.monitor;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.table.AbstractTableModel;
import nahon.comm.event.EventCenter;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import wqa.control.common.SDisplayData;
import wqa.dev.data.SDataElement;

/**
 *
 * @author chejf
 */
public class DataVector {
    private final ReentrantLock datalist_lock = new ReentrantLock();
    private final ArrayList<SDisplayData> datasource = new ArrayList();
    private boolean[] visable = new boolean[0];
    private String[] data_names = new String[0];
    private final int maxlen = 1800;

    public DataVector(String[] names) {
        data_names = names;
        visable = new boolean[data_names.length];
        for (int i = 0; i < data_names.length; i++) {
            visable[i] = true;
        }
        select_name = data_names[0];
    }

    // <editor-fold defaultstate="collapsed" desc="公共接口">  
    public String[] GetVisableName() {
        ArrayList<String> names = new ArrayList();
        for (int i = 0; i < data_names.length; i++) {
            if (visable[i]) {
                names.add(data_names[i]);
            }
        }
        return names.toArray(new String[0]);
    }

    //输入数据
    public void InputData(SDisplayData data) {
        datalist_lock.lock();
        try {
            while (datasource.size() > this.maxlen) {
                datasource.remove(0);
            }
            datasource.add(data);

            //刷新数据
            this.RefreshData();
        } finally {
            datalist_lock.unlock();
        }
    }

    //清除数据
    public void Clean() {
        datalist_lock.lock();
        try {
            SDisplayData data = this.GetLastData();
            this.datasource.clear();
            this.datasource.add(data);
            RefreshData();
        } finally {
            datalist_lock.unlock();
        }
    }

    //刷新界面
    public void RefreshData() {
        ((DataTableModel) this.table_model).Update();
    }

    public SDisplayData GetLastData() {
        datalist_lock.lock();
        try {
            if (this.datasource.isEmpty()) {
                return null;
            }

            return this.datasource.get(datasource.size() - 1);
        } finally {
            datalist_lock.unlock();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="MainData Line">  
    public String select_name = "";

    //设置显示曲线
    public void SetSelectName(String name) {
        this.select_name = name;
    }

    private ArrayList<String> describe = new ArrayList();

    public String[] GetDataTimeDescribe() {
        return describe.toArray(new String[0]);
    }

    public TimeSeries GetdateTimeSeries() {
        SDisplayData lastdata = this.GetLastData();
        TimeSeries mainline = new TimeSeries("");
        describe.clear();
        //清空数据
//        mainline.clear();
        //检查数据是否为空
        if (lastdata != null) {
            //找到选择的数据类型序号
            datalist_lock.lock();
            try {
                //遍历数据，找到只当类型的值，添加曲线
                for (int i = 0; i < this.datasource.size(); i++) {
                    if (!Float.isNaN(this.datasource.get(i).GetDataElement(select_name).mainData)) {
                        SDataElement e_data = this.datasource.get(i).GetDataElement(select_name);
                        mainline.addOrUpdate(new Second(this.datasource.get(i).time), e_data.mainData);
                        describe.add(e_data.range_info + e_data.unit);
                    }
                }
            } finally {
                datalist_lock.unlock();
            }
        }

        return mainline;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TableData">  
    public static int[] column_len = new int[]{110, 140, 100};
    public AbstractTableModel table_model = new DataTableModel();

    private class DataTableModel extends AbstractTableModel {

        private String[] columnname = new String[]{"类型", "数值(单位)", "量程"};
        private ArrayList<Object[]> rows = new ArrayList();

        public void Update() {
            SDisplayData lastdata = GetLastData();
            rows.clear();
            if (lastdata != null) {
                for (int i = 0; i < data_names.length; i++) {
                    SDataElement data = lastdata.GetDataElement(data_names[i]);
                    if (visable[i]) {
                        this.rows.add(new Object[]{data.name, data.mainData + data.unit, data.range_info});
                    }
                }
            }
            this.fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columnname.length;
        }

        @Override
        public String getColumnName(int i) {
            return columnname[i];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object[] data = rows.get(rowIndex);
            if (columnIndex < data.length) {
                return data[columnIndex];
            }

            return null;
        }

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Config Table">  
    public EventCenter ElementChange = new EventCenter();

    public AbstractTableModel GetConfigTableModel() {
        return new AbstractTableModel() {
            String[] columnnames = new String[]{"数据", "是否显示"};

            @Override
            public int getRowCount() {
                return data_names.length;
            }

            @Override
            public int getColumnCount() {
                return columnnames.length;
            }

            @Override
            public boolean isCellEditable(int i, int i1) {
                if (i1 == 1) {
                    return true;
                }
                return false;
            }

            @Override
            public Object getValueAt(int i, int i1) {
                if (i1 == 1) {
                    return visable[i];
                } else {
                    return data_names[i];
                }
            }

            @Override
            public void setValueAt(Object o, int i, int i1) {
                if (i1 == 1) {
                    visable[i] = (boolean) o;
                    ElementChange.CreateEvent(null);
                }
            }

            @Override
            public Class<?> getColumnClass(int i) {
                if (i == 1) {
                    return Boolean.class;

                } else {
                    return Object.class;
                }
            }

            @Override
            public String getColumnName(int i) {
                return this.columnnames[i];
            }

        };
    }
    // </editor-fold>
}
