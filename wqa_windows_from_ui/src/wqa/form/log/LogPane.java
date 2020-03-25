/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.log;

import java.awt.Color;
import java.awt.Component;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.SDataPacket;
import wqa.common.InitPaneHelper;
import wqa.bill.io.IOManager;
import wqa.form.errormsg.MsgBoxFactory;

/**
 *
 * @author chejf
 */
public class LogPane extends javax.swing.JPanel {

    /**
     * Creates new form WorkPane
     */
//    private ArrayList<CheckBox> cklist = new ArrayList();
    public LogPane() {
        initComponents();

        dlm = new ComLogTable();
        this.Table_Log.setModel(dlm);
        this.Table_Log.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.Table_Log.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                Component comm = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value.toString().contains(SDataPacket.IOEvent.Receive.toString())) {
                    //  this.setForeground(Color.BLUE);
                    comm.setForeground(Color.BLUE);
                } else {
                    comm.setForeground(Color.BLACK);
                }
                return comm;
            }
        });

        IOManager.GetInstance().SendReceive.RegeditListener(new EventListener<String>() {
            @Override
            public void recevieEvent(Event<String> event) {
                java.awt.EventQueue.invokeLater(() -> {
                    //刷新数据列表
                    if (!ToggleButton_Pause.isSelected() && LogPane.this.isVisible()) {
                        PrintLog(event.GetEvent());
                    }
                });
            }
        });

        ToggleButton_FilterActionPerformed(null);
    }

    private ComLogTable dlm;
    private int maxcolum_width = 0;
    private int current_len = 0;

    public void PrintLog(String log) {
        //检查过滤
        String filter_cmd = TransportFilter(this.TextField_filter.getText());
        
        if (ToggleButton_Filter.isSelected() && !filter_cmd.contentEquals("")) {
            try {
                if (!Pattern.compile(filter_cmd).matcher(log).find()) {
                    return;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        dlm.AddLog(log); //数据

        //更新列宽
        int row = this.Table_Log.getRowCount() - 1;
        int col = 0;
        //获取最新一列数据的宽度
        int preferedWidth = (int) Table_Log.getCellRenderer(row, col).getTableCellRendererComponent(Table_Log,
                Table_Log.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
        //找到所有比较历史记录中最宽列宽
        maxcolum_width = Math.max(maxcolum_width, preferedWidth);
        //刷新列宽
        if (current_len < Math.max(jPanel1.getSize().width - 15, maxcolum_width)) {
            current_len = Math.max(jPanel1.getSize().width - 15, maxcolum_width);
            JTableHeader header = Table_Log.getTableHeader();
            header.setResizingColumn(Table_Log.getColumnModel().getColumn(col)); // 此行很重要
            //设置列宽为表格宽度和数据最大宽度里的最大值
            Table_Log.getColumnModel().getColumn(col).setWidth(current_len);
        }

        // 刷新到最后的JList窗口
        //this.List_log.setSelectedIndex(dlm.getSize() - 1);
        //this.List_log.ensureIndexIsVisible(dlm.getSize() - 1);
//        this.Table_Log.changeSelection(dlm.getRowCount() + 1, 0, false, false);
        Log_ScrollPane.getVerticalScrollBar().setValue(Log_ScrollPane.getVerticalScrollBar().getModel().getMaximum());
    }

    private String TransportFilter(String input) {
        if (input.startsWith("#ADDR")) {
            try {
                String addr = input.substring(input.indexOf("#ADDR") + 5);
                int iaddr = Integer.valueOf(addr.trim());
                addr = String.format("%02X", iaddr);
                return "Send: 55 AA 7B 7B " + addr + " F0 | Receive: 55 AA 7B 7B F0 " + addr
                        + "| Send: " + addr + " | Receive: " + addr;
            } catch (NumberFormatException ex) {
                return "";
            }
        }

        return input;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Button_Clear = new javax.swing.JButton();
        ToggleButton_Pause = new javax.swing.JToggleButton();
        jPanel1 = new javax.swing.JPanel();
        Log_ScrollPane = new javax.swing.JScrollPane();
        Table_Log = new javax.swing.JTable();
        Button_SaveAs = new javax.swing.JButton();
        TextField_filter = new javax.swing.JTextField();
        Label_filter = new javax.swing.JLabel();
        ToggleButton_Filter = new javax.swing.JToggleButton();

        Button_Clear.setText("清空");
        Button_Clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_ClearActionPerformed(evt);
            }
        });

        ToggleButton_Pause.setText("暂停");

        Log_ScrollPane.setAutoscrolls(true);

        Table_Log.setModel(new javax.swing.table.DefaultTableModel(
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
        Log_ScrollPane.setViewportView(Table_Log);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(Log_ScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Log_ScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
        );

        Button_SaveAs.setText("导出");
        Button_SaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_SaveAsActionPerformed(evt);
            }
        });

        Label_filter.setText("过滤条件:");

        ToggleButton_Filter.setText("过滤");
        ToggleButton_Filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ToggleButton_FilterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(Label_filter, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TextField_filter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ToggleButton_Filter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_SaveAs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_Clear)
                        .addGap(5, 5, 5)
                        .addComponent(ToggleButton_Pause)))
                .addGap(4, 4, 4))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {Button_Clear, Button_SaveAs, ToggleButton_Filter, ToggleButton_Pause});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_Clear)
                    .addComponent(ToggleButton_Pause)
                    .addComponent(Button_SaveAs)
                    .addComponent(TextField_filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Label_filter)
                    .addComponent(ToggleButton_Filter))
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void ToggleButton_FilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ToggleButton_FilterActionPerformed
        TextField_filter.setVisible(this.ToggleButton_Filter.isSelected());
        Label_filter.setVisible(this.ToggleButton_Filter.isSelected());
    }//GEN-LAST:event_ToggleButton_FilterActionPerformed

    private void Button_ClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_ClearActionPerformed
        dlm.Clear();
    }//GEN-LAST:event_Button_ClearActionPerformed

    private void Button_SaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_SaveAsActionPerformed

        boolean selected = this.ToggleButton_Pause.isSelected();
        this.ToggleButton_Pause.setSelected(true);
        String file = InitPaneHelper.GetFilePath(".txt");
        if (file != null) {
            try {
                try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
                    for (int i = 0; i < dlm.getRowCount(); i++) {
                        out.write(dlm.getValueAt(i, 0).toString() + "\r\n");
                    }
                }
                MsgBoxFactory.Instance().ShowMsgBox("保存成功!");
            } catch (IOException ex) {
                LogCenter.Instance().SendFaultReport(Level.SEVERE, "保存记录失败!");
                Logger.getGlobal().log(Level.SEVERE, null, ex);
            }
        }
        this.ToggleButton_Pause.setSelected(selected);

    }//GEN-LAST:event_Button_SaveAsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_Clear;
    private javax.swing.JButton Button_SaveAs;
    private javax.swing.JLabel Label_filter;
    private javax.swing.JScrollPane Log_ScrollPane;
    private javax.swing.JTable Table_Log;
    private javax.swing.JTextField TextField_filter;
    private javax.swing.JToggleButton ToggleButton_Filter;
    private javax.swing.JToggleButton ToggleButton_Pause;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
