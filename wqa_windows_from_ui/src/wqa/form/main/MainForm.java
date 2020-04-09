/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.main;

import wqa.form.log.LogPane;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import static javax.swing.SwingConstants.*;
import nahon.comm.faultsystem.LogCenter;
import migp.adapter.factory.MIGPDevFactory;
import wqa.adapter.factory.ModBusDevFactory;
import wqa.bill.io.ShareIO;
import static wqa.common.JImagePane.*;
import wqa.control.data.IMainProcess;
import wqa.form.db.DataSearch;
import wqa.form.iolist.IOConfigDialog;
import wqa.form.monitor.MonitorPaneDesk;
import wqa.system.WQAPlatform;
import wqa.form.alarm.AlarmSearch;
import wqa.winio.adapter.IOManager;

/**
 *
 * @author chejf
 */
public class MainForm extends javax.swing.JFrame {

    public static MainForm main_parent;

    /**
     * Creates new form MainFrom
     */
    public MainForm() {
        initComponents();

        this.getRootPane().setWindowDecorationStyle(0);

        //居中显示
        setLocationRelativeTo(null);

        this.InitHeadPane();
        this.InitWorkPane();
        this.InitTaile();

        main_parent = this;
    }

    // <editor-fold defaultstate="collapsed" desc="初始化窗体头部">
    class HeadMove {

        int xOld = 0;
        int yOld = 0;

        public HeadMove(Component instance, Window window) {
            instance.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    int xOnScreen = e.getXOnScreen();
                    int yOnScreen = e.getYOnScreen();
                    int xx = xOnScreen - xOld;
                    int yy = yOnScreen - yOld;
                    window.setLocation(xx, yy);
                }
            });
            instance.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    xOld = e.getX();
                    yOld = e.getY();
                }
            });
        }
    }

    private void InitHeadPane() {
        //设置头背景
        Image image = new javax.swing.ImageIcon(MainForm.class.getResource("/wqa/form/main/resource/app_head_bak.png")).getImage();
        this.HeadPane.setBackgroundImage(image);
        this.HeadPane.setImageDisplayMode(SCALED);

        //处理拖动事件
        HeadMove headMove = new HeadMove(this.HeadPane, this);

        this.initLabelButton(LB_Search, "搜索设备", "search");
        this.initLabelButton(LB_DevList, "显示设备", "devlist");
        this.initLabelButton(LB_History, "历史记录", "history");
        this.initLabelButton(LB_Alarm, "报警信息", "alarm");
        this.initLabelButton(LB_IOLog, "通信日志", "iolog");
        this.initLabelButton(LB_Help, "帮助", "help");

        this.initLabelButton(LB_Min, "", "min");
        this.initLabelButton(LB_Max, "", "max");
        this.initLabelButton(LB_Del, "", "del", Color.RED);
    }

    private void initLabelButton(JLabel button, String txt, String bk_image) {
        this.initLabelButton(button, txt, bk_image, new Color(34, 141, 232));
    }

    private void initLabelButton(JLabel button, String txt, String bk_image, Color hv_color) {
        String path = "/wqa/form/main/resource/";
        //设置图片在上，文字在下
        button.setVerticalTextPosition(BOTTOM);
        button.setHorizontalTextPosition(CENTER);
        button.setHorizontalAlignment(CENTER);
        //设置文字
        button.setText(txt);
        //设置默认背景图片
        URL resource = getClass().getResource(path + bk_image + ".png");
        button.setIcon(new javax.swing.ImageIcon(resource));
        //设置背景颜色
        button.setBackground(hv_color);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                URL resource = getClass().getResource(path + bk_image + "_h.png");
                if (resource != null) {
                    button.setIcon(new javax.swing.ImageIcon(resource));
                }
                button.setOpaque(true);
                button.updateUI();
            }

            @Override
            public void mouseExited(MouseEvent me) {
                button.setOpaque(false);
                ImageIcon image = new javax.swing.ImageIcon(getClass().getResource(path + bk_image + ".png"));
                button.setIcon(image);
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="按钮相应">
    private void LB_SearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LB_SearchMouseClicked
        IOConfigDialog dialog = new IOConfigDialog(this, true);
        dialog.setVisible(true);
        if (dialog.GetResult() == IOConfigDialog.SEARCH_MODBUS) {
            WQAPlatform.LoadDriver(new ModBusDevFactory());
        } else if (dialog.GetResult() == IOConfigDialog.SEARCH_MIGP) {
            WQAPlatform.LoadDriver(new MIGPDevFactory());
        } else {
            return;
        }

        if (this.LB_Search.isEnabled()) {
            this.LB_Search.setEnabled(false);

            ProcessDialog.ApplyGlobalProcessBar();
            Future submit = WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
                WQAPlatform.GetInstance().GetManager().SearchDevice(IOManager.GetInstance().GetAllIO(), new IMainProcess() {
                    @Override
                    public void SetValue(float pecent) {
                        java.awt.EventQueue.invokeLater(() -> {
                            if (ProcessDialog.GetGlobalProcessBar() != null) {
                                ProcessDialog.GetGlobalProcessBar().GetProcessBar().setValue((int) pecent + 10);
                            }
                        });
                    }

                    @Override
                    public void Finish(Object result) {
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                ProcessDialog.ReleaseGlobalProcessBar();
                                LB_Search.setEnabled(true);
                            }
                        });
                    }
                });
            });

            int opio = 0;
            for(ShareIO io :IOManager.GetInstance().GetAllIO() ){
                if(!io.IsClosed()){
                    opio++;
                }
            }
            /**
             * 增加超时机制
             */
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!submit.isDone()) {
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                ProcessDialog.ReleaseGlobalProcessBar();
                                LB_Search.setEnabled(true);
                            }
                        });
                        submit.cancel(true);
                        LogCenter.Instance().SendFaultReport(Level.SEVERE, "搜索设备超时");
                    }
                }
            }, 3000 + 30000 * opio);
        }
    }//GEN-LAST:event_LB_SearchMouseClicked

    private void LB_DevListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LB_DevListMouseClicked
        work_layout.show(work_area, MonitorPaneDesk.class.getSimpleName());
        m_desk.Refresh();
    }//GEN-LAST:event_LB_DevListMouseClicked

    private void LB_HistoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LB_HistoryMouseClicked
        work_layout.show(work_area, DataSearch.class.getSimpleName());
    }//GEN-LAST:event_LB_HistoryMouseClicked

    private void LB_AlarmMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LB_AlarmMouseClicked
        work_layout.show(work_area, AlarmSearch.class.getSimpleName());
    }//GEN-LAST:event_LB_AlarmMouseClicked

    private int lastdivider = 320;
    private void LB_IOLogMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LB_IOLogMouseClicked
        logpane.setVisible(!logpane.isVisible());
        if (logpane.isVisible()) {
            chartAnddataSplit.setDividerLocation(lastdivider);
        } else {
            lastdivider = chartAnddataSplit.getDividerLocation();
        }
    }//GEN-LAST:event_LB_IOLogMouseClicked

    private void LB_HelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LB_HelpMouseClicked
        //File f = chooseFile.getSelectedFile();
        File f = new File("./help.pdf");
        if (f.exists()) {
            Runtime runtime = Runtime.getRuntime();
            try {
                System.out.println(f.getAbsolutePath());          //打开文件      
                runtime.exec("rundll32 url.dll FileProtocolHandler " + f.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            LogCenter.Instance().ShowMessBox(Level.SEVERE, "没有找到帮助文件");
        }
//        new AboutDialog(this, true).setVisible(true);
    }//GEN-LAST:event_LB_HelpMouseClicked

    private void LB_MinMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LB_MinMouseClicked
        setExtendedState(JFrame.ICONIFIED);//最小化窗体
    }//GEN-LAST:event_LB_MinMouseClicked

    private void LB_MaxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LB_MaxMouseClicked
        if (this.getExtendedState() == JFrame.NORMAL) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);//最大化窗体
        } else {
            setExtendedState(JFrame.NORMAL);
        }
    }//GEN-LAST:event_LB_MaxMouseClicked

    private void LB_DelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LB_DelMouseClicked
        WQAPlatform.GetInstance().CloseSystem();
        System.exit(0);
    }//GEN-LAST:event_LB_DelMouseClicked
    // </editor-fold> 

    private void HeadPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HeadPaneMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            if (this.getExtendedState() == JFrame.NORMAL) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);//最大化窗体
            } else {
                setExtendedState(JFrame.NORMAL);
            }
        }
    }//GEN-LAST:event_HeadPaneMouseClicked
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="初始化窗体中间">
    private JSplitPane chartAnddataSplit;
    private JPanel work_area = new JPanel();
    private CardLayout work_layout = new CardLayout();

    private LogPane logpane = new LogPane();
    private MonitorPaneDesk m_desk = new MonitorPaneDesk();

    private void InitWorkPane() {
        //设置ICON
        Toolkit tk = Toolkit.getDefaultToolkit();
        java.net.URL disurl = MainForm.class.getResource("/wqa/form/main/resource/ObserverIcon2.png");
        java.awt.Image image = tk.createImage(disurl);
        this.setIconImage(image);
        this.setTitle("水质探头分析测试软件");

        chartAnddataSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, work_area, logpane);
//        chartAnddataSplit.set(Panel_work.getHeight() - 200);
        chartAnddataSplit.setResizeWeight(0.5);
        chartAnddataSplit.setDividerSize(15);
//        chartAnddataSplit.setResizeWeight(WIDTH);
        chartAnddataSplit.setOneTouchExpandable(false);

        this.work_area.setLayout(work_layout);
        this.work_area.add(MonitorPaneDesk.class.getSimpleName(), m_desk);
        this.work_area.add(DataSearch.class.getSimpleName(), new DataSearch());
        this.work_area.add(AlarmSearch.class.getSimpleName(), new AlarmSearch());

        logpane.setVisible(false);
        /* Init Display Area */
        this.Panel_work.setLayout(new CardLayout());
        this.Panel_work.add(chartAnddataSplit);

    }
    // </editor-fold>

    private void InitTaile() {
        this.Label_Version.setText("当前版本:" + WQAPlatform.GetInstance().GetConfig().getProperty("VER", "0.0.0.0"));
        //显示时间
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                java.awt.EventQueue.invokeLater(() -> {
                    Label_Time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                });
            }
        }, 0, 1000);

        //给面板添加边框，边框添加 放大缩小功能
        contentPane.setBorder(new Border(new Color(0, 0, 0, 0), 2, this));

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        contentPane = new javax.swing.JPanel();
        HeadPane = new wqa.common.JImagePane();
        Label_Name = new javax.swing.JLabel();
        LB_Search = new javax.swing.JLabel();
        LB_DevList = new javax.swing.JLabel();
        LB_Alarm = new javax.swing.JLabel();
        LB_History = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        LB_Help = new javax.swing.JLabel();
        LB_IOLog = new javax.swing.JLabel();
        LB_Del = new javax.swing.JLabel();
        LB_Max = new javax.swing.JLabel();
        LB_Min = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        Label_Time = new javax.swing.JLabel();
        Label_Version = new javax.swing.JLabel();
        Panel_work = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1100, 553));

        HeadPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HeadPaneMouseClicked(evt);
            }
        });

        Label_Name.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/main/resource/app_name.png"))); // NOI18N
        Label_Name.setText("  ");

        LB_Search.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LB_Search.setText("jLabel2");
        LB_Search.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LB_Search.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        LB_Search.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LB_SearchMouseClicked(evt);
            }
        });

        LB_DevList.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LB_DevList.setText("jLabel2");
        LB_DevList.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LB_DevList.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        LB_DevList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LB_DevListMouseClicked(evt);
            }
        });

        LB_Alarm.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LB_Alarm.setText("jLabel2");
        LB_Alarm.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LB_Alarm.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        LB_Alarm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LB_AlarmMouseClicked(evt);
            }
        });

        LB_History.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LB_History.setText("jLabel2");
        LB_History.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LB_History.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        LB_History.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LB_HistoryMouseClicked(evt);
            }
        });

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/main/resource/split.png"))); // NOI18N
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        LB_Help.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LB_Help.setText("jLabel2");
        LB_Help.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LB_Help.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        LB_Help.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LB_HelpMouseClicked(evt);
            }
        });

        LB_IOLog.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LB_IOLog.setText("jLabel2");
        LB_IOLog.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LB_IOLog.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        LB_IOLog.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LB_IOLogMouseClicked(evt);
            }
        });

        LB_Del.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LB_Del.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/main/resource/del.png"))); // NOI18N
        LB_Del.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LB_Del.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        LB_Del.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LB_DelMouseClicked(evt);
            }
        });

        LB_Max.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LB_Max.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/main/resource/max.png"))); // NOI18N
        LB_Max.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LB_Max.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        LB_Max.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LB_MaxMouseClicked(evt);
            }
        });

        LB_Min.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LB_Min.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/main/resource/min.png"))); // NOI18N
        LB_Min.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LB_Min.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        LB_Min.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LB_MinMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout HeadPaneLayout = new javax.swing.GroupLayout(HeadPane);
        HeadPane.setLayout(HeadPaneLayout);
        HeadPaneLayout.setHorizontalGroup(
            HeadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HeadPaneLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(Label_Name)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(LB_Search, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(LB_DevList, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(LB_History, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(LB_Alarm, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(LB_IOLog, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(LB_Help, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(LB_Min, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(LB_Max, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(LB_Del, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        HeadPaneLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {LB_Del, LB_Max, LB_Min});

        HeadPaneLayout.setVerticalGroup(
            HeadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
            .addComponent(Label_Name, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HeadPaneLayout.createSequentialGroup()
                .addGroup(HeadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(LB_Del, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LB_Max, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LB_Help, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LB_IOLog, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LB_Alarm, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LB_History, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LB_DevList, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LB_Search, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LB_Min, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        Label_Time.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        Label_Time.setText("jLabel1");

        Label_Version.setText("jLabel1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Label_Version, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Label_Time, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Label_Time, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Label_Version))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout Panel_workLayout = new javax.swing.GroupLayout(Panel_work);
        Panel_work.setLayout(Panel_workLayout);
        Panel_workLayout.setHorizontalGroup(
            Panel_workLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        Panel_workLayout.setVerticalGroup(
            Panel_workLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 447, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout contentPaneLayout = new javax.swing.GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(HeadPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(Panel_work, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPaneLayout.createSequentialGroup()
                .addComponent(HeadPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Panel_work, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(contentPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contentPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="parameter"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private wqa.common.JImagePane HeadPane;
    private javax.swing.JLabel LB_Alarm;
    private javax.swing.JLabel LB_Del;
    private javax.swing.JLabel LB_DevList;
    private javax.swing.JLabel LB_Help;
    private javax.swing.JLabel LB_History;
    private javax.swing.JLabel LB_IOLog;
    private javax.swing.JLabel LB_Max;
    private javax.swing.JLabel LB_Min;
    private javax.swing.JLabel LB_Search;
    private javax.swing.JLabel Label_Name;
    private javax.swing.JLabel Label_Time;
    private javax.swing.JLabel Label_Version;
    private javax.swing.JPanel Panel_work;
    private javax.swing.JPanel contentPane;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>  
}
