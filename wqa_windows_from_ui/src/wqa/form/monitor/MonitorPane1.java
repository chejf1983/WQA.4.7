/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.monitor;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import nahon.comm.faultsystem.LogCenter;
import org.jfree.chart.axis.DateAxis;
import wqa.chart.DataChart;
import wqa.common.JImagePane;
import wqa.dev.data.SDisplayData;
import wqa.control.common.DevControl.ControlState;
import wqa.control.common.DevMonitor;
import wqa.control.config.DevConfigBean;
import wqa.dev.data.SDevInfo;
import wqa.form.config.CalConfigForm;
import wqa.form.config.CommonConfigForm;
import wqa.form.errormsg.ConfirmBox;
import wqa.form.log.DevLogForm;
import wqa.form.main.MainForm;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class MonitorPane1 extends javax.swing.JPanel {

    /**
     * Creates new form MonitorPane2
     */
    private final MonitorPaneDesk parent;
    private final DataVector data_vector;

    public MonitorPane1(MonitorPaneDesk parent, DevMonitor dev) {
        this.currentdev = dev;
        this.parent = parent;
        this.data_vector = new DataVector(dev);

        initComponents();

        //初始化按钮图标
        initMonitorPane();

        //初始化数据表格
        this.InitTable();

        //初始化界面
        this.initChartPane();

        //初始化设备
        this.initDevice();
    }

    // <editor-fold defaultstate="collapsed" desc="初始化chart区域">
    private DataChart chartPane1;
    private MChartPane m_chart;
//    private DBChart m_chart;

    private void initChartPane() {
        //初始化界面
        m_chart = new MChartPane();

        //赋值chart区域
        this.chartPane1 = this.m_chart.GetChartPane();
        //设置chart时间坐标单位
        ((DateAxis) (chartPane1.xyplot.getDomainAxis())).setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
//        this.chartPane1.PrintMainLine(this.data_vector.mainline);
//        this.data_vector.mainline = this.chartPane1.GetMainLine();

        this.m_chart.GetComboBox().addItemListener((java.awt.event.ItemEvent evt) -> {
            if (m_chart.GetComboBox().getSelectedItem() != null) {
                data_vector.SetSelectName(m_chart.GetComboBox().getSelectedItem().toString());
                chartPane1.PaintMainLine(this.data_vector.GetdateTimeSeries(), this.data_vector.GetDataTimeDescribe());
            }
        });

        this.m_chart.GetChartPane().MenuItem_Del.addActionListener((java.awt.event.ActionEvent evt) -> {
            data_vector.Clean();
        });

        this.UpdateComboBox();

        //显示到展示区
        this.DisplayArea.add("CHART", this.m_chart);
//        m_chart.setVisible(false);
    }

    private void UpdateComboBox() {
        m_chart.GetComboBox().removeAllItems();
        //初始化曲线下拉框
        for (String name : currentdev.GetSupportDataName()) {
            m_chart.GetComboBox().addItem(name);
        }

        if (currentdev.GetSupportDataName().length > 0) {
            data_vector.SetSelectName(data_vector.GetSupportDataName()[0]);
            m_chart.GetComboBox().setSelectedItem(currentdev.GetSupportDataName()[0]);
        } else {
            data_vector.SetSelectName("");
            m_chart.GetComboBox().setSelectedItem("");
        }
        this.chartPane1.PaintMainLine(this.data_vector.GetdateTimeSeries(), this.data_vector.GetDataTimeDescribe());
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="表格"> 
    //初始化数据表
    private void InitTable() {
        if (!WQAPlatform.GetInstance().is_internal) {
            this.DisplayArea.add("TABLE", init_mtabel());
        } else {
            this.DisplayArea.add("TABLE", initCommonTable());
        }
    }

    private JComponent initCommonTable() {
        JTable Table_data;
        JScrollPane scroll_pane;
        //初始化数据表格
        Table_data = new JTable();
        scroll_pane = new JScrollPane();
        scroll_pane.setViewportView(Table_data);
//        Table_data.getTableHeader().setResizingAllowed(false);   // 不允许拉伸
        Table_data.getTableHeader().setReorderingAllowed(false); //不允许拖拽
//        Table_data.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);    //不自动调整
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER); // 设置字体居中
        r.setAlignmentX(0);
        Table_data.setDefaultRenderer(Object.class, r);
        Table_data.getTableHeader().setDefaultRenderer(r);

        Table_data.setModel(data_vector.table_model);
        JTableHeader header = Table_data.getTableHeader();
        for (int i = 0; i < DataVector.column_len.length; i++) {
            TableColumn column = Table_data.getColumnModel().getColumn(i);
            header.setResizingColumn(column); // 名称
            column.setWidth(DataVector.column_len[i]);
        }
        return scroll_pane;
    }

    private JComponent init_mtabel() {
        MTable table = new MTable();
        table.SetDataSet(data_vector);
        return table;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="初始化设备事件">
    final DevMonitor currentdev;
    private final ReentrantLock dsiplay_lock = new ReentrantLock(true);

    private void initDevice() {
        //初始化数据刷新监听
        this.currentdev.DataEvent.RegeditListener(new EventListener<SDisplayData>() {
            @Override
            public void recevieEvent(Event<SDisplayData> event) {
                /* Create and display the dialog */
                java.awt.EventQueue.invokeLater(() -> {
                    UpdateData(event.GetEvent());
                });
            }
        });

        //初始化状态刷新监听
        this.currentdev.GetParent1().StateChange.RegeditListener(new EventListener<ControlState>() {
            @Override
            public void recevieEvent(Event<ControlState> event) {
                java.awt.EventQueue.invokeLater(() -> {
                    switch (event.GetEvent()) {
                        case CONNECT:
                            Lable_Title.setText(GetDevName());
                            Lable_Title.setForeground(Color.WHITE);
                            ChangeState(state.connect);
                            break;
                        case DISCONNECT:
                            Lable_Title.setForeground(Color.RED);
                            ChangeState(state.connect);
                            break;
                        case CONFIG:
                            Lable_Title.setForeground(Color.GREEN);
                            ChangeState(state.config);
                            break;
                        case ALARM:
                            ChangeState(state.warning);
                            break;
                        default:
                            throw new AssertionError(event.GetEvent().name());
                    }
                    updateUI();
                });
            }
        });

        //初始化数据容器跟新消息
        this.data_vector.ElementChange.RegeditListener(new EventListener() {
            @Override
            public void recevieEvent(Event event) {
                dsiplay_lock.lock();
                try {
                    data_vector.RefreshData();
                    UpdateComboBox();
                } finally {
                    dsiplay_lock.unlock();
                }
            }
        });
    }

    //刷新数据
    private void UpdateData(SDisplayData data) {
        //刷新报警界面
        if (data.alarm != 0) {
            Label_AlarmInfo.setToolTipText(data.alram_info);
        } else {
            Label_AlarmInfo.setToolTipText(null);
        }

        dsiplay_lock.lock();
        try {
            //刷新数据列表
            this.data_vector.InputData(data);
            this.chartPane1.PaintMainLine(this.data_vector.GetdateTimeSeries(), this.data_vector.GetDataTimeDescribe());
        } finally {
            dsiplay_lock.unlock();
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

        Head_Pane = new wqa.common.JImagePane();
        Lable_Title = new javax.swing.JLabel();
        Label_AlarmInfo = new javax.swing.JLabel();
        Button_min = new javax.swing.JButton();
        Button_max = new javax.swing.JButton();
        Button_del = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        Body_Pane = new wqa.common.JImagePane();
        DisplayArea = new javax.swing.JPanel();
        ButtonCal = new javax.swing.JButton();
        Button_Config = new javax.swing.JButton();
        Button_ChartSwitch = new javax.swing.JButton();
        Button_DevLog = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(420, 300));

        Lable_Title.setText("jLabel1");

        Button_min.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/monitor/resource/m_min.png"))); // NOI18N
        Button_min.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_minActionPerformed(evt);
            }
        });

        Button_max.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/monitor/resource/m_max.png"))); // NOI18N
        Button_max.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_maxActionPerformed(evt);
            }
        });

        Button_del.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/monitor/resource/m_del.png"))); // NOI18N
        Button_del.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_delActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Head_PaneLayout = new javax.swing.GroupLayout(Head_Pane);
        Head_Pane.setLayout(Head_PaneLayout);
        Head_PaneLayout.setHorizontalGroup(
            Head_PaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Head_PaneLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(Label_AlarmInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Lable_Title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Button_min, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Button_max, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Button_del, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        Head_PaneLayout.setVerticalGroup(
            Head_PaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Head_PaneLayout.createSequentialGroup()
                .addGroup(Head_PaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Lable_Title, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Label_AlarmInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(0, 20, Short.MAX_VALUE))
            .addGroup(Head_PaneLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(Head_PaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Button_del, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_max, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_min, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout DisplayAreaLayout = new javax.swing.GroupLayout(DisplayArea);
        DisplayArea.setLayout(DisplayAreaLayout);
        DisplayAreaLayout.setHorizontalGroup(
            DisplayAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 380, Short.MAX_VALUE)
        );
        DisplayAreaLayout.setVerticalGroup(
            DisplayAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 177, Short.MAX_VALUE)
        );

        ButtonCal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/monitor/resource/m_cal.png"))); // NOI18N
        ButtonCal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonCalActionPerformed(evt);
            }
        });

        Button_Config.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/monitor/resource/m_config.png"))); // NOI18N
        Button_Config.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_ConfigActionPerformed(evt);
            }
        });

        Button_ChartSwitch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/monitor/resource/m_chart.png"))); // NOI18N
        Button_ChartSwitch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_ChartSwitchActionPerformed(evt);
            }
        });

        Button_DevLog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/monitor/resource/history_h.png"))); // NOI18N
        Button_DevLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_DevLogActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Body_PaneLayout = new javax.swing.GroupLayout(Body_Pane);
        Body_Pane.setLayout(Body_PaneLayout);
        Body_PaneLayout.setHorizontalGroup(
            Body_PaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Body_PaneLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(Body_PaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Body_PaneLayout.createSequentialGroup()
                        .addComponent(ButtonCal, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(Button_Config, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(Button_ChartSwitch, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(Button_DevLog, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(DisplayArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        Body_PaneLayout.setVerticalGroup(
            Body_PaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Body_PaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DisplayArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Body_PaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(Body_PaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ButtonCal, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Button_Config, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Button_ChartSwitch, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Button_DevLog, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Head_Pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Body_Pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(Head_Pane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Body_Pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="初始化界面">
    private String GetDevName() {
        SDevInfo devinfo = currentdev.GetDevID();
        String stype = devinfo.protype == SDevInfo.ProType.MIGP ? "*" : "";
        return stype + devinfo.dev_id.ToChineseString();
    }

    private final CardLayout display_layout = new CardLayout();

    private void initMonitorPane() {
        this.DisplayArea.setLayout(display_layout);

        this.setOpaque(false);
        this.Head_Pane.setBackgroundImage(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/monitor/resource/monitor_head.png")).getImage());
        this.Head_Pane.setImageDisplayMode(JImagePane.SCALED);
        this.Body_Pane.setBackgroundImage(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/monitor/resource/monitor_body.png")).getImage());
        this.Body_Pane.setImageDisplayMode(JImagePane.SCALED);

        //初始化删除按钮
        this.Button_del.setToolTipText("删除设备");

        //初始化设备配置按钮
        this.Lable_Title.setText(GetDevName());
        this.Button_max.setToolTipText("全屏");
        this.Button_min.setToolTipText("缩小");
        this.ButtonCal.setToolTipText("校准");
        this.Button_Config.setToolTipText("设置");
        this.Button_ChartSwitch.setToolTipText("显示曲线");
        this.Button_DevLog.setToolTipText("设备日志");

        ChaneChartMode();

        this.ChangeState(state.connect);
    }

    private boolean chart_visable = false;

    private void ChaneChartMode() {
        if (chart_visable) {
            this.display_layout.show(this.DisplayArea, "CHART");
        } else {
            this.parent.SwitchMCard();
            this.display_layout.show(this.DisplayArea, "TABLE");
        }
        this.Button_max.setVisible(chart_visable);
        this.Button_min.setVisible(chart_visable);
    }

    private enum state {
        connect,
        disconnect,
        config,
        warning
    }

    private void ChangeState(state st) {
        String png = "";
        switch (st) {
            case connect:
                png = "/wqa/form/monitor/resource/m_connect.png";
                break;
            case disconnect:
                png = "/wqa/form/monitor/resource/disconnect_24.png";
                break;
            case config:
                png = "/wqa/form/monitor/resource/m_config.png";
                break;
            case warning:
                png = "/wqa/form/monitor/resource/warning_24p.png";
                break;
            default:
                throw new AssertionError(st.name());
        }

        Label_AlarmInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource(png)));
    }

    private void Button_ChartSwitchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_ChartSwitchActionPerformed
        chart_visable = !chart_visable;
        ChaneChartMode();
    }//GEN-LAST:event_Button_ChartSwitchActionPerformed

    private CommonConfigForm config_form;
    private void Button_ConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_ConfigActionPerformed
        DevConfigBean config = currentdev.GetParent1().StartConfig();
        if (config == null) {
            return;
        }
        config_form = new CommonConfigForm(MainForm.main_parent, false, GetDevName());
        try {
            if (config_form.InitModel(config)) {
                config_form.InitViewConfig(data_vector);
                config_form.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent we) {
                        config_form.Close();
                    }
                });
                config_form.setVisible(true);
            }
        } catch (Exception ex) {
            config.Quit();
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
        }
    }//GEN-LAST:event_Button_ConfigActionPerformed

    private CalConfigForm cal_form;
    private void ButtonCalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonCalActionPerformed
        DevConfigBean config = currentdev.GetParent1().StartConfig();
        if (config == null) {
            return;
        }
        cal_form = new CalConfigForm(MainForm.main_parent, false, GetDevName());
        try {
            if (cal_form.InitModel(config)) {
                cal_form.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent we) {
                        cal_form.Close();
                    }
                });
                cal_form.setVisible(true);
            }
        } catch (Exception ex) {
            config.Quit();
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
        }
    }//GEN-LAST:event_ButtonCalActionPerformed

    private void Button_minActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_minActionPerformed
        this.parent.SwitchMCard();
    }//GEN-LAST:event_Button_minActionPerformed

    private void Button_maxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_maxActionPerformed
        this.parent.SwitchCard(this);
    }//GEN-LAST:event_Button_maxActionPerformed

    private void Button_delActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_delActionPerformed
        if (ConfirmBox.ShowConfirmBox(MainForm.main_parent, "是否删除该设备?")) {
            WQAPlatform.GetInstance().GetManager().DeleteDevControl(this.currentdev.GetParent1());
        }
    }//GEN-LAST:event_Button_delActionPerformed

    private void Button_DevLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_DevLogActionPerformed
        DevLogForm logform = new DevLogForm(MainForm.main_parent, false, GetDevName());
        logform.InitLog(this.currentdev.GetDevID().dev_id);
        logform.setVisible(true);
    }//GEN-LAST:event_Button_DevLogActionPerformed

    // </editor-fold>

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private wqa.common.JImagePane Body_Pane;
    private javax.swing.JButton ButtonCal;
    private javax.swing.JButton Button_ChartSwitch;
    private javax.swing.JButton Button_Config;
    private javax.swing.JButton Button_DevLog;
    private javax.swing.JButton Button_del;
    private javax.swing.JButton Button_max;
    private javax.swing.JButton Button_min;
    private javax.swing.JPanel DisplayArea;
    private wqa.common.JImagePane Head_Pane;
    private javax.swing.JLabel Label_AlarmInfo;
    private javax.swing.JLabel Lable_Title;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
