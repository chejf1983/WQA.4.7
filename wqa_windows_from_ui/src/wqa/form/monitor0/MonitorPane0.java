/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.monitor0;

import wqa.form.monitor.*;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.PopupFactory;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import nahon.comm.event.NEvent;
import nahon.comm.event.NEventListener;
import nahon.comm.faultsystem.LogCenter;
import org.jfree.chart.axis.DateAxis;
import wqa.chart.DataChart;
import wqa.common.JImagePane;
import wqa.control.common.DevControl.ControlState;
import wqa.control.common.DevMonitor;
import wqa.control.common.SDisplayData;
import wqa.control.config.DevConfigBean;
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
public class MonitorPane0 extends javax.swing.JPanel {

    /**
     * Creates new form MonitorPane2
     */
    private final MonitorPaneDesk parent;
    private final DataVector0 data_vector;
    JLabel alarm_label = new JLabel("连接正常");
    javax.swing.Popup pop = null;

    public MonitorPane0(MonitorPaneDesk parent, DevMonitor dev, int index) {
        this.currentdev = dev;
        this.parent = parent;
        String[] temperteam = dev.GetArrayName(0); //温度数组
        String[] datateam = dev.GetArrayName(index); //数据数组
        String[] names = new String[temperteam.length + datateam.length];
        System.arraycopy(datateam, 0, names, 0, datateam.length);
        System.arraycopy(temperteam, 0, names, datateam.length, temperteam.length);
        this.data_vector = new DataVector0(names);

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

    private void initChartPane() {
        //初始化界面
        m_chart = new MChartPane();

        //赋值chart区域
        this.chartPane1 = this.m_chart.GetChartPane();
        //设置chart时间坐标单位
        ((DateAxis) (chartPane1.xyplot.getDomainAxis())).setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));

        //设置chart commbobox的下拉切换事件
        this.m_chart.GetComboBox().addItemListener((java.awt.event.ItemEvent evt) -> {
            if (m_chart.GetComboBox().getSelectedItem() != null) {
                data_vector.SetSelectName(m_chart.GetComboBox().getSelectedItem().toString());
                chartPane1.PaintMainLine(this.data_vector.GetdateTimeSeries(), this.data_vector.GetDataTimeDescribe());
            }
        });

        //右键清除曲线
        this.m_chart.GetChartPane().MenuItem_Del.addActionListener((java.awt.event.ActionEvent evt) -> {
            data_vector.Clean();
        });

        //显示到展示区
        this.DisplayArea.add("CHART", this.m_chart);
        //刷新commbobox
        UpdateComboBox();
    }

    private void UpdateComboBox() {
        m_chart.GetComboBox().removeAllItems();
        //初始化曲线下拉框
        String[] names = data_vector.GetVisableName();
        for (String name : names) {
            m_chart.GetComboBox().addItem(name);
        }

        //设置选中的名称
        if (names.length > 0) {
            data_vector.SetSelectName(names[0]);
            m_chart.GetComboBox().setSelectedItem(names[0]);
        } else {
            data_vector.SetSelectName("");
            m_chart.GetComboBox().setSelectedItem("");
        }
        //显示曲线
        this.chartPane1.PaintMainLine(this.data_vector.GetdateTimeSeries(), this.data_vector.GetDataTimeDescribe());
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="表格"> 
    //初始化数据表
    private void InitTable() {
        this.DisplayArea.add("TABLE", init_mtabel());
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
        this.currentdev.DataEvent.RegeditListener(new NEventListener<SDisplayData>() {
            @Override
            public void recevieEvent(NEvent<SDisplayData> event) {
                /* Create and display the dialog */
                java.awt.EventQueue.invokeLater(() -> {
                    UpdateData(event.GetEvent());
                });
            }
        });

        //初始化状态刷新监听
        this.currentdev.GetParent1().StateChange.RegeditListener(new NEventListener<ControlState>() {
            @Override
            public void recevieEvent(NEvent<ControlState> event) {
                java.awt.EventQueue.invokeLater(() -> {
                    switch (event.GetEvent()) {
                        case CONNECT:
                            ChangeState(state.connect);
                            break;
                        case DISCONNECT:
                            ChangeState(state.disconnect);
                            break;
                        case ALARM:
                            ChangeState(state.warning);
                            break;
                        case CONFIG:
                            ChangeState(state.config);
                            break;
                        default:
                            throw new AssertionError(event.GetEvent().name());
                    }
                    updateUI();
                });
            }
        });

        //初始化数据容器跟新消息
        this.data_vector.ElementChange.RegeditListener(new NEventListener() {
            @Override
            public void recevieEvent(NEvent event) {
                dsiplay_lock.lock();
                try {
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
            alarm_label.setText(data.alram_info);
        } else {
            alarm_label.setText("连接正常");
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

        Label_AlarmInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

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
                .addComponent(Label_AlarmInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Lable_Title, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
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
        String stype = currentdev.GetParent1().GetProType().contentEquals("MIGP") ? "*" : "";
        return stype + currentdev.GetParent1().GetDevID().ToChineseString();
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

        alarm_label.setBackground(Color.white);
        alarm_label.setForeground(Color.BLACK);
        alarm_label.setOpaque(true);

        //自定义popmenu
        Body_Pane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                super.mouseEntered(me); //To change body of generated methods, choose Tools | Templates.
                if (pop != null) {
                    pop.hide();
                    pop = null;
                }
            }
        });
        Lable_Title.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                super.mouseEntered(me); //To change body of generated methods, choose Tools | Templates.
                if (pop != null) {
                    pop.hide();
                    pop = null;
                }
            }
        });
        this.Label_AlarmInfo.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me
            ) {
                if (me.getButton() == MouseEvent.BUTTON1) {
                    if (pop != null) {
                        pop.hide();
                        pop = null;
                    }

                    pop = PopupFactory.getSharedInstance().getPopup(Label_AlarmInfo, alarm_label,
                            Label_AlarmInfo.getLocationOnScreen().x, Label_AlarmInfo.getLocationOnScreen().y);
                    pop.show();
//                    PopupMenu.show(alarm_label, me.getX(), me.getY());
                }
                //jPopupMenu.show(jList,e.getX(),e.getY());
            }
        });

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
        warning,
        config
    }

    private void ChangeState(state st) {
        String png = "";
        switch (st) {
            case connect:
                Lable_Title.setText(GetDevName());
                Lable_Title.setForeground(Color.WHITE);
                png = "/wqa/form/monitor/resource/m_connect.png";
                alarm_label.setText("连接正常");
                break;
            case disconnect:
                Lable_Title.setForeground(Color.RED);
                if (this.config_form != null) {
                    this.config_form.dispose();
                }
                if (this.cal_form != null) {
                    this.cal_form.dispose();
                }
                png = "/wqa/form/monitor/resource/disconnect_24.png";
                alarm_label.setText("连接中断");
                break;
            case warning:
                png = "/wqa/form/monitor/resource/warning_24p.png";
                break;
            case config:
                Lable_Title.setForeground(Color.GREEN);
                png = "/wqa/form/monitor/resource/config_24p.png";
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
        DevConfigBean config = currentdev.GetParent1().GetConfig();
        if (config == null) {
            return;
        }

        config_form = new CommonConfigForm(MainForm.main_parent, false, GetDevName());
        try {
            config_form.InitModel(config);
            config_form.InitViewConfig(data_vector.GetConfigTableModel());
        } catch (Exception ex) {
            currentdev.GetParent1().ReleasConfig();
            config_form = null;
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
            return;
        }

        config_form.Refresh();
        config_form.setVisible(true);
    }//GEN-LAST:event_Button_ConfigActionPerformed

    private CalConfigForm cal_form;
    private void ButtonCalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonCalActionPerformed
        DevConfigBean config = currentdev.GetParent1().GetConfig();
        if (config == null || config.GetDevCalConfig().GetCalType().length == 0) {
            return;
        }
        cal_form = new CalConfigForm(MainForm.main_parent, false, GetDevName());
        try {
            cal_form.InitModel(config);
        } catch (Exception ex) {
            currentdev.GetParent1().ReleasConfig();
            cal_form = null;
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
            return;
        }
        cal_form.setVisible(true);
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
        logform.InitLog(this.currentdev.GetParent1().GetDevID());
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
