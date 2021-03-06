/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.alarm;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.table.JTableHeader;
import nahon.comm.faultsystem.LogCenter;
import wqa.common.Chooser;
import wqa.control.data.IMainProcess;
import wqa.common.InitPaneHelper;
import wqa.control.DB.AlarmRecord;
import wqa.control.data.DevID;
import wqa.form.main.ProcessDialog;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class AlarmSearch extends javax.swing.JPanel {

    /**
     * Creates new form AlarmSearch
     */
    public AlarmSearch() {
        initComponents();

        this.InitPanel();
        //刷新数据
        Button_refreshActionPerformed(null);
    }

    private void InitPanel() {

        Date now = new Date();
        now.setTime(new Date().getTime() - 24 * 3600 * 1000);
        this.TextField_start_time.setText(new SimpleDateFormat(TIMEFORMATE).format(now));
        Chooser.getInstance().register(TextField_start_time);
        this.TextField_end_time.setText(new SimpleDateFormat(TIMEFORMATE).format(new Date()));
        Chooser.getInstance().register(TextField_end_time);

        this.List_devlist.setModel(model);
        this.List_devlist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON3 && List_devlist.getSelectedIndex() >= 0) {
                    PopupMenu.show(List_devlist, me.getX(), me.getY());
                }
                //jPopupMenu.show(jList,e.getX(),e.getY());
            }
        });
        this.List_devlist.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component ret = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (!isSelected && index % 2 == 0) {
                    ret.setBackground(new Color(222, 222, 222));
                }
                return ret;
            }
        });
//        this.Table_alarm.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PopupMenu = new javax.swing.JPopupMenu();
        MenuItem_Del = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        List_devlist = new javax.swing.JList<>();
        Button_refresh = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_alarm = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        TextField_start_time = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        TextField_end_time = new javax.swing.JTextField();
        Button_Search = new javax.swing.JButton();
        Button_now = new javax.swing.JButton();
        Button_Export = new javax.swing.JButton();

        MenuItem_Del.setText("删除");
        MenuItem_Del.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItem_DelActionPerformed(evt);
            }
        });
        PopupMenu.add(MenuItem_Del);

        List_devlist.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(List_devlist);

        Button_refresh.setText("刷新");
        Button_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_refreshActionPerformed(evt);
            }
        });

        Table_alarm.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(Table_alarm);

        jLabel1.setText("起始时间:");

        TextField_start_time.setText("jTextField1");

        jLabel2.setText("终止时间:");

        TextField_end_time.setText("jTextField1");

        Button_Search.setText("搜索");
        Button_Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_SearchActionPerformed(evt);
            }
        });

        Button_now.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/db/clock_16p.png"))); // NOI18N
        Button_now.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_nowActionPerformed(evt);
            }
        });

        Button_Export.setText("导出");
        Button_Export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_ExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TextField_start_time, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TextField_end_time, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_now, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_Search)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_Export)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(TextField_start_time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(TextField_end_time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Button_Search)
                        .addComponent(Button_now)
                        .addComponent(Button_Export))
                    .addComponent(Button_refresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    DefaultListModel<String> model = new DefaultListModel<>();
    private static final String TIMEFORMATE = "yyyy-MM-dd HH:mm:ss";
    private DevID[] Dev_List;

    //刷新设备表格
    private void Button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_refreshActionPerformed
        this.Dev_List = WQAPlatform.GetInstance().GetDBHelperFactory().GetAlarmDB().ListAllDevice();

        model.clear();
        for (DevID devname : Dev_List) {
            model.addElement(devname.ToChineseString());
        }

        if (Dev_List.length > 0) {
            this.List_devlist.setSelectedIndex(0);
        }
    }//GEN-LAST:event_Button_refreshActionPerformed

    //获取起止时间
    Date start_time = null;
    Date stop_time = null;

    //搜索报警信息
    private void Button_SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_SearchActionPerformed

        try {
            start_time = new SimpleDateFormat(TIMEFORMATE).parse(this.TextField_start_time.getText());
            stop_time = new SimpleDateFormat(TIMEFORMATE).parse(this.TextField_end_time.getText());
            this.TextField_start_time.setText(new SimpleDateFormat(TIMEFORMATE).format(start_time));
            this.TextField_end_time.setText(new SimpleDateFormat(TIMEFORMATE).format(stop_time));
            if (!start_time.before(stop_time)) {
                LogCenter.Instance().ShowMessBox(Level.SEVERE, "截至时间必须大于起始时间");
                return;
            }
        } catch (ParseException ex) {
            LogCenter.Instance().ShowMessBox(Level.SEVERE, ex.getMessage());
            return;
        }

        //检查输入
        if (this.Dev_List == null || this.List_devlist.getSelectedIndex() < 0) {
            LogCenter.Instance().ShowMessBox(Level.SEVERE, "请先选择需要搜索的设备");
            return;
        }

        ProcessDialog.ApplyGlobalProcessBar();
        WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
            //搜索
            WQAPlatform.GetInstance().GetDBHelperFactory().GetAlarmDB().SearchAlarmInfo(Dev_List[List_devlist.getSelectedIndex()],
                    start_time, stop_time, new IMainProcess<AlarmRecord[]>() {
                @Override
                public void SetValue(float pecent) {
                    java.awt.EventQueue.invokeLater(() -> {
                        if (ProcessDialog.GetGlobalProcessBar() != null) {
                            ProcessDialog.GetGlobalProcessBar().GetProcessBar().setValue((int) pecent);
                        }
                    });
                }

                @Override
                public void Finish(AlarmRecord[] ainfo_list) {
                    java.awt.EventQueue.invokeLater(() -> {
                        //刷新表格
                        ProcessDialog.ReleaseGlobalProcessBar();
                        Table_alarm.setModel(new AlarmTable(ainfo_list));
                        for (int i = 0; i < AlarmTable.table_with.length; i++) {
                            JTableHeader header = Table_alarm.getTableHeader();
                            header.setResizingColumn(Table_alarm.getColumnModel().getColumn(i)); // 此行很重要
                            //设置列宽为表格宽度和数据最大宽度里的最大值
                            Table_alarm.getColumnModel().getColumn(i).setWidth(AlarmTable.table_with[i]);
                            //Table_alarm.setModel(null);
                        }
                    });
                }
            });
        });
    }//GEN-LAST:event_Button_SearchActionPerformed

    //设置当前时间
    private void Button_nowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_nowActionPerformed
        this.TextField_end_time.setText(new SimpleDateFormat(TIMEFORMATE).format(new Date()));
    }//GEN-LAST:event_Button_nowActionPerformed

    //输出Excel
    private void Button_ExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_ExportActionPerformed
        Date start_time = null;
        Date stop_time = null;
        try {
            start_time = new SimpleDateFormat(TIMEFORMATE).parse(this.TextField_start_time.getText());
            stop_time = new SimpleDateFormat(TIMEFORMATE).parse(this.TextField_end_time.getText());
            this.TextField_start_time.setText(new SimpleDateFormat(TIMEFORMATE).format(start_time));
            this.TextField_end_time.setText(new SimpleDateFormat(TIMEFORMATE).format(stop_time));
            if (!start_time.before(stop_time)) {
                LogCenter.Instance().ShowMessBox(Level.SEVERE, "截至时间必须大于起始时间");
                return;
            }
        } catch (ParseException ex) {
            LogCenter.Instance().ShowMessBox(Level.SEVERE, ex.getMessage());
            return;
        }

        if (this.Dev_List == null || this.List_devlist.getSelectedIndex() < 0) {
            LogCenter.Instance().ShowMessBox(Level.SEVERE, "请先选择需要搜索的设备");
            return;
        }

        String filepath = InitPaneHelper.GetFilePath(".xls");

        if (filepath == null) {
            return;
        }

        this.Button_Export.setEnabled(false);
        WQAPlatform.GetInstance().GetDBHelperFactory().GetAlarmDB().ExportToExcel(filepath,
                this.Dev_List[this.List_devlist.getSelectedIndex()],
                start_time, stop_time, new IMainProcess() {
            @Override
            public void SetValue(float pecent) {
                java.awt.EventQueue.invokeLater(() -> {
                    if (ProcessDialog.GetGlobalProcessBar() != null) {
                        ProcessDialog.GetGlobalProcessBar().GetProcessBar().setValue((int) pecent);
                    }
                });
            }

            @Override
            public void Finish(Object result) {
                java.awt.EventQueue.invokeLater(() -> {
                    //刷新表格
                    ProcessDialog.ReleaseGlobalProcessBar();
                    LogCenter.Instance().ShowMessBox(Level.SEVERE, "导出完毕");
                    Button_Export.setEnabled(true);
                });
            }
        });
    }//GEN-LAST:event_Button_ExportActionPerformed

    //删除探头数据
    private void MenuItem_DelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItem_DelActionPerformed
        try {
            int[] indcs = this.List_devlist.getSelectedIndices();
            for (int i = 0; i < indcs.length; i++) {
                WQAPlatform.GetInstance().GetDBHelperFactory().GetAlarmDB().DeleteAlarm(this.Dev_List[indcs[i]]);
            }
//            WQAPlatform.GetInstance().GetDBHelperFactory().GetDataFinder().DeleteTable(this.devlist[this.List_devlist.getSelectedIndex()]);
            Button_refreshActionPerformed(null);
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "删除失败", ex);
        }
    }//GEN-LAST:event_MenuItem_DelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_Export;
    private javax.swing.JButton Button_Search;
    private javax.swing.JButton Button_now;
    private javax.swing.JButton Button_refresh;
    private javax.swing.JList<String> List_devlist;
    private javax.swing.JMenuItem MenuItem_Del;
    private javax.swing.JPopupMenu PopupMenu;
    private javax.swing.JTable Table_alarm;
    private javax.swing.JTextField TextField_end_time;
    private javax.swing.JTextField TextField_start_time;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
