/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.config;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import wqa.control.config.DevConfigTable;
import wqa.dev.intf.SConfigItem;

/**
 *
 * @author chejf
 */
public class ConfigTablePane extends javax.swing.JPanel {

    /**
     * Creates new form ConfigTablePane
     */
    private DevConfigTable config;

    public ConfigTablePane(DevConfigTable config) {
        initComponents();
        this.config = config;
        this.InitTable(this.config.GetConfigList());
    }

    private ConfigTableModel tablemodel;

    public void Refresh() {
        InitTable(this.config.GetConfigList());
    }

    class MyTableRenderer extends JCheckBox implements TableCellRenderer {
        // 此方法可以查考JDK文档的说明

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Boolean b = (Boolean) value;
            this.setSelected(b.booleanValue());
            return this;
        }
    }

    private void InitTable(SConfigItem[] list) {
        this.Label_des.setForeground(Color.BLACK);
        tablemodel = new ConfigTableModel(list);
        Table_config = new JTable() {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 1 && list[row].inputtype == SConfigItem.ItemType.B) {
                    //设置默认设备名称下拉列表
                    JCheckBox combox = new JCheckBox();
                    boolean value = Boolean.valueOf(list[row].GetValue());
                    combox.setSelected(value);
//                    return new DefaultCellRenderer(combox);
                    return new MyTableRenderer();
                }
                return super.getCellRenderer(row, column); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 1 && list[row].inputtype == SConfigItem.ItemType.S) {
                    //设置默认设备名称下拉列表
                    JComboBox combox = new JComboBox();
                    for (String item : list[row].range) {
                        combox.addItem(item);
                    }
                    return new DefaultCellEditor(combox);
                } else if (column == 1 && list[row].inputtype == SConfigItem.ItemType.B) {
                    //设置默认设备名称下拉列表
                    JCheckBox combox = new JCheckBox();
                    boolean value = Boolean.valueOf(list[row].GetValue());
                    combox.setSelected(value);
                    return new DefaultCellEditor(combox);
//                    return DefaultCellEditor(combox);
                }
                return super.getCellEditor(row, column);
            }
        };

//        Table_config.getTableHeader().setResizingAllowed(false);   // 不允许拉伸
        Table_config.getTableHeader().setReorderingAllowed(false); //不允许拖拽
        this.Table_config.setModel(tablemodel);
        JTableHeader header = Table_config.getTableHeader();
        for (int i = 0; i < ConfigTableModel.column_len.length; i++) {
            TableColumn column = Table_config.getColumnModel().getColumn(i);
            header.setResizingColumn(column); // 名称
            column.setWidth(ConfigTableModel.column_len[i]);
        }
//        this.Table_config.setTableHeader(null);
//        JTableHeader header = Table_config.getTableHeader();
//        TableColumn column = Table_config.getColumnModel().getColumn(0);
//        header.setResizingColumn(column); // 名称
//        column.setWidth(DataTableModel.column_len[i]);
        this.ScrollPane_table.setViewportView(Table_config);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ScrollPane_table = new javax.swing.JScrollPane();
        Table_config = new javax.swing.JTable();
        Button_set = new javax.swing.JButton();
        Button_read = new javax.swing.JButton();
        Label_des = new javax.swing.JLabel();

        Table_config.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        ScrollPane_table.setViewportView(Table_config);

        Button_set.setText("设置");
        Button_set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_setActionPerformed(evt);
            }
        });

        Button_read.setText("读取");
        Button_read.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_readActionPerformed(evt);
            }
        });

        Label_des.setText("'*'表示可以修改的选项");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ScrollPane_table, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Label_des)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                        .addComponent(Button_read)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_set)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ScrollPane_table, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_set)
                    .addComponent(Button_read)
                    .addComponent(Label_des)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void Button_readActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_readActionPerformed
        this.config.InitConfigTable();
        this.InitTable(this.config.GetConfigList());
    }//GEN-LAST:event_Button_readActionPerformed

    private void Button_setActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_setActionPerformed
        TableCellEditor cellEditor = this.Table_config.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
        this.config.SetConfigList(this.tablemodel.GetValues());
        InitTable(this.config.GetConfigList());
    }//GEN-LAST:event_Button_setActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_read;
    private javax.swing.JButton Button_set;
    private javax.swing.JLabel Label_des;
    private javax.swing.JScrollPane ScrollPane_table;
    private javax.swing.JTable Table_config;
    // End of variables declaration//GEN-END:variables
}
