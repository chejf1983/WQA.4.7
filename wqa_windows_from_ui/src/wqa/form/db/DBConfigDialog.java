/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.db;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;
import wqa.control.data.IMainProcess;
import wqa.form.main.ProcessDialog;
import wqa.system.WQAPlatform;

/**
 *
 * @author jiche
 */
public class DBConfigDialog extends javax.swing.JDialog {

    private static final String TIMEFORMATE = "yyyy-MM-dd HH:mm:ss";

    /**
     * Creates new form ManualRangeDialog
     *
     * @param parent
     * @param modal
     */
    public DBConfigDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocationRelativeTo(null);

        this.InitCollectTime();

        this.InitDBFile();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        Label_Interval = new javax.swing.JLabel();
        TextField_dbtime = new javax.swing.JTextField();
        Label_CRange = new javax.swing.JLabel();
        Button_Config = new javax.swing.JButton();
        Label_MaxPoint = new javax.swing.JLabel();
        TextField_maxPnum = new javax.swing.JTextField();
        Label_pRange = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        Lable_1 = new javax.swing.JLabel();
        TextField_befortime = new javax.swing.JTextField();
        Button_OneM = new javax.swing.JButton();
        Button_ThreeM = new javax.swing.JButton();
        Label_3 = new javax.swing.JLabel();
        Button_HalfYear = new javax.swing.JButton();
        Lable_2 = new javax.swing.JLabel();
        Label_DBSize = new javax.swing.JLabel();
        Button_CleanDB = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        Label_Interval.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label_Interval.setText("采样周期:");

        TextField_dbtime.setText("0");

        Label_CRange.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label_CRange.setText("(1-3600)秒");

        Button_Config.setText("确定");
        Button_Config.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_ConfigActionPerformed(evt);
            }
        });

        Label_MaxPoint.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label_MaxPoint.setText("曲线点数:");

        TextField_maxPnum.setText("0");

        Label_pRange.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label_pRange.setText("(1-3600)秒");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 183, Short.MAX_VALUE)
                        .addComponent(Button_Config, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(Label_Interval)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(TextField_dbtime, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Label_CRange, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(Label_MaxPoint)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(TextField_maxPnum, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Label_pRange, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TextField_dbtime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Label_Interval)
                    .addComponent(Label_CRange))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TextField_maxPnum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Label_MaxPoint)
                    .addComponent(Label_pRange))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(Button_Config)
                .addGap(10, 10, 10))
        );

        jTabbedPane1.addTab("采样间隔", jPanel3);

        Lable_1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Lable_1.setText("删除:");

        TextField_befortime.setText("0");

        Button_OneM.setText("一月前");
        Button_OneM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_OneMActionPerformed(evt);
            }
        });

        Button_ThreeM.setText("三月前");
        Button_ThreeM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_ThreeMActionPerformed(evt);
            }
        });

        Label_3.setText("数据库容量:");

        Button_HalfYear.setText("半月前");
        Button_HalfYear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_HalfYearActionPerformed(evt);
            }
        });

        Lable_2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Lable_2.setText("之前的数据");

        Label_DBSize.setText(" ");

        Button_CleanDB.setText("确定");
        Button_CleanDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_CleanDBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(Button_OneM)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_ThreeM)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_HalfYear))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(Lable_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(TextField_befortime, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Lable_2))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(Label_3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Label_DBSize, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Button_CleanDB, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TextField_befortime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Lable_2)
                    .addComponent(Lable_1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_OneM)
                    .addComponent(Button_ThreeM)
                    .addComponent(Button_HalfYear))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Label_3)
                    .addComponent(Label_DBSize))
                .addGap(18, 18, 18)
                .addComponent(Button_CleanDB)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("内存管理", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="数据库清理">    
    //初始化数据库配置界面
    private void InitDBFile() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);;
        this.TextField_befortime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime()));
        Label_DBSize.setText(WQAPlatform.GetInstance().GetDBHelperFactory().GetDBFix().GetDBSize() + "MB");

        this.getContentPane().setBackground(new Color(34, 88, 149));

        Label_Interval.setForeground(Color.BLACK);
        Label_CRange.setForeground(Color.BLACK);
        Label_MaxPoint.setForeground(Color.BLACK);
        Label_pRange.setForeground(Color.BLACK);
        this.Lable_1.setForeground(Color.BLACK);
        this.Lable_2.setForeground(Color.BLACK);
        this.Label_3.setForeground(Color.BLACK);
        this.Label_DBSize.setForeground(Color.BLACK);
    }

    //清理数据库
    private void Button_CleanDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_CleanDBActionPerformed
        try {
            //获取时间标签
            Date beforedate = new SimpleDateFormat(TIMEFORMATE).parse(this.TextField_befortime.getText());
            //申请进度条
            ProcessDialog.ApplyGlobalProcessBar();
            //清理旧数据
            WQAPlatform.GetInstance().GetDBHelperFactory().GetDBFix().DeleteData(beforedate, new IMainProcess() {
                @Override
                public void SetValue(float pecent) {
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
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
                            ProcessDialog.ReleaseGlobalProcessBar();
                            Label_DBSize.setText(WQAPlatform.GetInstance().GetDBHelperFactory().GetDBFix().GetDBSize() + "MB");
                        }
                    });
                }
            });
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
        }

    }//GEN-LAST:event_Button_CleanDBActionPerformed

    private void Button_OneMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_OneMActionPerformed
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        this.TextField_befortime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime()));
    }//GEN-LAST:event_Button_OneMActionPerformed

    private void Button_ThreeMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_ThreeMActionPerformed
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 3);
        this.TextField_befortime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime()));
    }//GEN-LAST:event_Button_ThreeMActionPerformed

    private void Button_HalfYearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_HalfYearActionPerformed
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 6);
        this.TextField_befortime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime()));
    }//GEN-LAST:event_Button_HalfYearActionPerformed
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="配置">  
    private int max_p_num = 20480;
    private int min_p_num = 1;
    private int max_c_time = 3600; //s
    private int min_c_time = 1;

    //初始化配置参数
    private void InitCollectTime() {
        this.TextField_dbtime.setText(WQAPlatform.GetInstance().GetDBHelperFactory().GetCollectTimeBySecond() + "");
        this.Label_CRange.setText("(" + this.min_c_time + "-" + this.max_c_time + ")");

        this.TextField_maxPnum.setText(DataSearch.Max_ChartPoint + "");
        this.Label_pRange.setText("(" + this.min_p_num + "-" + this.max_p_num + ")");
    }

    private void Button_ConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_ConfigActionPerformed
        try {
            //设置采样间隔
            int ctime = Integer.valueOf(this.TextField_dbtime.getText());
            if (ctime > this.max_c_time) {
                ctime = this.max_c_time;
            }
            if (ctime < this.min_c_time) {
                ctime = this.min_c_time;
            }
            WQAPlatform.GetInstance().GetDBHelperFactory().SetCollectTime(ctime);
            this.TextField_dbtime.setText(WQAPlatform.GetInstance().GetDBHelperFactory().GetCollectTimeBySecond() + "");

            //设置曲线点个数设置
            DataSearch.Max_ChartPoint = Integer.valueOf(this.TextField_maxPnum.getText());
            if (DataSearch.Max_ChartPoint > max_p_num) {
                DataSearch.Max_ChartPoint = max_p_num;
            }

            if (DataSearch.Max_ChartPoint < min_p_num) {
                DataSearch.Max_ChartPoint = min_p_num;
            }

            this.TextField_maxPnum.setText(DataSearch.Max_ChartPoint + "");
            WQAPlatform.GetInstance().GetConfig().setProperty("MAX_CHART_POINT", DataSearch.Max_ChartPoint + "");
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
        }
    }//GEN-LAST:event_Button_ConfigActionPerformed
    // </editor-fold>

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_CleanDB;
    private javax.swing.JButton Button_Config;
    private javax.swing.JButton Button_HalfYear;
    private javax.swing.JButton Button_OneM;
    private javax.swing.JButton Button_ThreeM;
    private javax.swing.JLabel Label_3;
    private javax.swing.JLabel Label_CRange;
    private javax.swing.JLabel Label_DBSize;
    private javax.swing.JLabel Label_Interval;
    private javax.swing.JLabel Label_MaxPoint;
    private javax.swing.JLabel Label_pRange;
    private javax.swing.JLabel Lable_1;
    private javax.swing.JLabel Lable_2;
    private javax.swing.JTextField TextField_befortime;
    private javax.swing.JTextField TextField_dbtime;
    private javax.swing.JTextField TextField_maxPnum;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
