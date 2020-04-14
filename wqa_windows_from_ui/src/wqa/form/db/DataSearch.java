/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.db;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import wqa.common.InitPaneHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import nahon.comm.faultsystem.LogCenter;
import wqa.common.Chooser;
import wqa.control.DB.DataReadHelper;
import wqa.control.DB.DataReadHelper.SearchResult;
import wqa.adapter.factory.CDevDataTable;
import wqa.bill.db.DataRecord;
import wqa.control.data.IMainProcess;
import wqa.form.main.MainForm;
import wqa.form.main.ProcessDialog;
import wqa.form.monitor.DataVector;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class DataSearch extends javax.swing.JPanel {

    /**
     * Creates new form DBSearch
     */
    public DataSearch() {
        initComponents();

        this.InitJList();

        this.InitDBChart();

        Button_refreshActionPerformed(null);
    }

    // <editor-fold defaultstate="collapsed" desc="初始化列表">    
    private DefaultListModel<String> model = new DefaultListModel<>();

    private void InitJList() {
        //this.PopupMenu;
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
        this.ComboBox_devtypes.removeAllItems();
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="初始化曲线">
    private final DBChart DataChart = new DBChart();
    private static final String TIMEFORMATE = "yyyy-MM-dd HH:mm:ss";
    public static int Max_ChartPoint = 2048;
    private DataReadHelper.DevTableInfo[] Dev_list;
    //搜索到的数据
    private DataRecord[] data_set = new DataRecord[0];

    private void InitDBChart() {
        Date now = new Date();
        now.setTime(new Date().getTime() - 24 * 3600 * 1000);

        this.Chart_Panel.setLayout(new CardLayout());
        this.Chart_Panel.add(DataChart);

        this.TextField_start_time.setText(new SimpleDateFormat(TIMEFORMATE).format(now));
        Chooser.getInstance().register(TextField_start_time);
        this.TextField_end_time.setText(new SimpleDateFormat(TIMEFORMATE).format(new Date()));
        Chooser.getInstance().register(TextField_end_time);

        String max_p_string = WQAPlatform.GetInstance().GetConfig().getProperty("MAX_CHART_POINT", "2048");
        try {
            Max_ChartPoint = Integer.valueOf(max_p_string);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    
    //更新探头的数据类型
    private void UpdateSelectIndex() {
        if (List_devlist.getSelectedIndex() < 0) {
            return;
        }
        this.ComboBox_devtypes.removeAllItems();
        int dev_type = this.Dev_list[this.List_devlist.getSelectedIndex()].dev_id.dev_type;
        CDevDataTable.DataInfo[] GetSupportData = DataVector.GetSupportData(dev_type);
        for (CDevDataTable.DataInfo devtype : GetSupportData) {
            this.ComboBox_devtypes.addItem(devtype.data_name);
        }
    }

    //刷新数据图表
    private void UpdateChart() {
        if (data_set.length > 0 && ComboBox_devtypes.getSelectedItem() != null && List_devlist.getSelectedIndex() >= 0) {
            DataReadHelper.DevTableInfo devname = Dev_list[List_devlist.getSelectedIndex()];
            String data_name = devname.dev_id.ToChineseString() + ":" + ComboBox_devtypes.getSelectedItem().toString();
            String select_data = ComboBox_devtypes.getSelectedItem().toString();
            int data_index = devname.GetSelectIndex(select_data);
            if (data_index >= 0) {
                DataChart.PaintLine(data_name, data_set, data_index);
            }
        }
    }

    // </editor-fold> 
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
        jLabel1 = new javax.swing.JLabel();
        ComboBox_devtypes = new javax.swing.JComboBox<>();
        TextField_start_time = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        Button_Search = new javax.swing.JButton();
        Button_now = new javax.swing.JButton();
        Button_Export = new javax.swing.JButton();
        Button_Set = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        Label_DataNum = new javax.swing.JLabel();
        TextField_end_time = new javax.swing.JTextField();
        Chart_Panel = new javax.swing.JPanel();

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

        jLabel1.setText("起始:");

        ComboBox_devtypes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ComboBox_devtypes.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboBox_devtypesItemStateChanged(evt);
            }
        });

        TextField_start_time.setText("jTextField1");

        jLabel2.setText("终止:");

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

        Button_Set.setText("设置");
        Button_Set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_SetActionPerformed(evt);
            }
        });

        jLabel4.setText("搜索到数据:");

        Label_DataNum.setText(" ");

        TextField_end_time.setText("jTextField1");

        javax.swing.GroupLayout Chart_PanelLayout = new javax.swing.GroupLayout(Chart_Panel);
        Chart_Panel.setLayout(Chart_PanelLayout);
        Chart_PanelLayout.setHorizontalGroup(
            Chart_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        Chart_PanelLayout.setVerticalGroup(
            Chart_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Button_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TextField_start_time, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TextField_end_time, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_now, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_Search)
                        .addGap(2, 2, 2)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Label_DataNum, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_devtypes, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Button_Export)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_Set)
                        .addGap(0, 13, Short.MAX_VALUE))
                    .addComponent(Chart_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(Label_DataNum)
                        .addComponent(Button_Search)
                        .addComponent(Button_Export)
                        .addComponent(Button_Set)
                        .addComponent(ComboBox_devtypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(TextField_start_time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(Button_now)
                        .addComponent(Button_refresh)
                        .addComponent(TextField_end_time, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addComponent(Chart_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    //刷新设备列表
    private void Button_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_refreshActionPerformed
        //获取设备列表
        Dev_list = WQAPlatform.GetInstance().GetDBHelperFactory().GetDataFinder().ListAllDevice();

        //清空表格
        model.clear();
        for (DataReadHelper.DevTableInfo devname : Dev_list) {
            //添加列表（设备中文名[地址]);
            model.addElement(devname.dev_id.ToChineseString());
        }
        //默认选择第0个设备
        if (Dev_list.length > 0) {
            this.List_devlist.setSelectedIndex(0);
        }
    }//GEN-LAST:event_Button_refreshActionPerformed

    //更新终止时间
    private void Button_nowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_nowActionPerformed
        this.TextField_end_time.setText(new SimpleDateFormat(TIMEFORMATE).format(new Date()));
    }//GEN-LAST:event_Button_nowActionPerformed

    //搜索
    private void Button_SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_SearchActionPerformed
        UpdateSelectIndex();
        //获取起止时间
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

        //检查设备选择
        if (this.Dev_list == null || this.List_devlist.getSelectedIndex() < 0) {
            LogCenter.Instance().ShowMessBox(Level.SEVERE, "请先选择需要搜索的设备");
            return;
        }

//        this.dataChart1.crosshair.DeleteCrossHair();
//        long start = System.currentTimeMillis();
        ProcessDialog.ApplyGlobalProcessBar();
        //开始搜索
        WQAPlatform.GetInstance().GetDBHelperFactory().GetDataFinder().SearchLimitData(this.Dev_list[this.List_devlist.getSelectedIndex()],//选择的设备
                start_time, stop_time, this.Max_ChartPoint, new IMainProcess<SearchResult>() {
            @Override
            public void SetValue(float pecent) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        //更新进度条
                        if (ProcessDialog.GetGlobalProcessBar() != null) {
                            ProcessDialog.GetGlobalProcessBar().GetProcessBar().setValue((int) pecent);
                        }
                    }
                });
            }

            @Override
            public void Finish(SearchResult data_ret) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        //更新数据
                        data_set = data_ret.data;
                        //刷新图标
                        UpdateChart();
                        Label_DataNum.setText(data_ret.search_num + "");
                        ProcessDialog.ReleaseGlobalProcessBar();
                        //JOptionPane.showMessageDialog(null, "new:" + (System.currentTimeMillis() - start));
                    }
                });
            }
        });
    }//GEN-LAST:event_Button_SearchActionPerformed

    //输出到EXCEL
    private void Button_ExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_ExportActionPerformed
        //获取起止时间
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

        //检查选择
        if (this.Dev_list == null || this.List_devlist.getSelectedIndex() < 0) {
            LogCenter.Instance().ShowMessBox(Level.SEVERE, "请先选择需要搜索的设备");
            return;
        }

        //获取导出文件名
        String filepath = InitPaneHelper.GetFilePath(".xls");

        //检查导出文件名
        if (filepath == null) {
            return;
        } 

        //去始能导出按钮
        this.Button_Export.setEnabled(false);

        ProcessDialog.ApplyGlobalProcessBar();
        //导出到excel
        WQAPlatform.GetInstance().GetDBHelperFactory().GetDataFinder().ExportToFile(filepath,
                this.Dev_list[this.List_devlist.getSelectedIndex()],
                start_time, stop_time, new IMainProcess() {
            @Override
            public void SetValue(float pecent) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        //刷新进度条
                        if (ProcessDialog.GetGlobalProcessBar() != null) {
                            ProcessDialog.GetGlobalProcessBar().GetProcessBar().setValue((int) pecent);
                        }
                    }
                });
            }

            @Override
            public void Finish(Object result) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        //始能按钮
                        ProcessDialog.ReleaseGlobalProcessBar();
                        LogCenter.Instance().ShowMessBox(Level.SEVERE, "导出完毕");
                        Button_Export.setEnabled(true);
                    }
                });
            }
        });
    }//GEN-LAST:event_Button_ExportActionPerformed

    //设置数据库对话框入口
    private void Button_SetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_SetActionPerformed
        DBConfigDialog dbConfigDialog = new DBConfigDialog(MainForm.main_parent, false);
        dbConfigDialog.setVisible(true);
    }//GEN-LAST:event_Button_SetActionPerformed

    //删除一个探头的数据
    private void MenuItem_DelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItem_DelActionPerformed
        try {
            int[] indcs = this.List_devlist.getSelectedIndices();
            for (int i = 0; i < indcs.length; i++) {
                WQAPlatform.GetInstance().GetDBHelperFactory().GetDataFinder().DeleteTable(this.Dev_list[indcs[i]]);
            }
//            WQAPlatform.GetInstance().GetDBHelperFactory().GetDataFinder().DeleteTable(this.devlist[this.List_devlist.getSelectedIndex()]);
            Button_refreshActionPerformed(null);
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "删除失败", ex);
        }
    }//GEN-LAST:event_MenuItem_DelActionPerformed

    //数据类型刷新，更新表
    private void ComboBox_devtypesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboBox_devtypesItemStateChanged
        UpdateChart();
    }//GEN-LAST:event_ComboBox_devtypesItemStateChanged

//    private boolean is_Auto = false;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_Export;
    private javax.swing.JButton Button_Search;
    private javax.swing.JButton Button_Set;
    private javax.swing.JButton Button_now;
    private javax.swing.JButton Button_refresh;
    private javax.swing.JPanel Chart_Panel;
    private javax.swing.JComboBox<String> ComboBox_devtypes;
    private javax.swing.JLabel Label_DataNum;
    private javax.swing.JList<String> List_devlist;
    private javax.swing.JMenuItem MenuItem_Del;
    private javax.swing.JPopupMenu PopupMenu;
    private javax.swing.JTextField TextField_end_time;
    private javax.swing.JTextField TextField_start_time;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
