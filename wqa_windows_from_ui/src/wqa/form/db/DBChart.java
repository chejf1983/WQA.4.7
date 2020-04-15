/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.db;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;
import org.jfree.data.time.TimeSeries;
import wqa.chart.ChartRangePaint;
import wqa.chart.DataChart;

/**
 *
 * @author chejf
 */
public class DBChart extends javax.swing.JPanel {

    /**
     * Creates new form DataChart
     */
    private DataChart chart = new DataChart();

    public DBChart() {
        initComponents();

        this.chart.dataChart.getLegend().setVisible(true);

        this.Panel_chart.setLayout(new CardLayout());
        this.Panel_chart.add(chart);

        this.Button_add.setToolTipText("当前曲线复制成背景曲线");
        this.Button_del.setToolTipText("删除最后一条背景曲线");

        this.CheckBox_isYEnable.setVisible(false);
        this.initSnapShot();
    }

    private TimeSeries mainline = null;

    public void PaintLine(TimeSeries mainline, String[] describe) {
        this.mainline = mainline;
//        ArrayList<String> describe = new ArrayList();
//        try {
//            for (DataRecord data_ret1 : data_ret) {
//                if (!Float.isNaN(data_ret1.values[index])) {
//                    mainline.addOrUpdate(new Second(data_ret1.time), data_ret1.values[index]);
//                    describe.add(data_ret1.value_strings[index]);
//                }
//            }
//            this.chart.PaintMainLine(mainline, describe.toArray(new String[0]));
//        } catch (Exception ex) {
//            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
//        }
        this.chart.PaintMainLine(mainline, describe);
    }

    // <editor-fold defaultstate="collapsed" desc="SnapShot Line"> 
//    private TimeSeries[] sn_lines;
//    private int current_index = 0;
    private ArrayList<TimeSeries> sn_lines = new ArrayList();
    private int max_num = chart.line_color.length;

    private void initSnapShot() {
        sn_lines = new ArrayList();
        max_num = chart.line_color.length;
    }

    public void CopyToSnapShot() {
        if (mainline != null) {
            //增加一路新曲线
            if (sn_lines.size() >= max_num) {
                LogCenter.Instance().ShowMessBox(Level.SEVERE, "已达上限，无法再增加");
                return;
            }
            sn_lines.add(mainline);
            mainline = null;
            this.chart.PaintSnapShot(sn_lines.toArray(new TimeSeries[0]));
        }
    }

    public void DelLastSnapShot() {
        if (!sn_lines.isEmpty()) {
            sn_lines.remove(sn_lines.size() - 1);
        }
        this.chart.PaintSnapShot(sn_lines.toArray(new TimeSeries[0]));
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

        Panel_chart = new javax.swing.JPanel();
        Button_add = new javax.swing.JButton();
        Button_del = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        CheckBox_isYEnable = new javax.swing.JCheckBox();

        javax.swing.GroupLayout Panel_chartLayout = new javax.swing.GroupLayout(Panel_chart);
        Panel_chart.setLayout(Panel_chartLayout);
        Panel_chartLayout.setHorizontalGroup(
            Panel_chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        Panel_chartLayout.setVerticalGroup(
            Panel_chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 271, Short.MAX_VALUE)
        );

        Button_add.setText("+");
        Button_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_addActionPerformed(evt);
            }
        });

        Button_del.setText("-");
        Button_del.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_delActionPerformed(evt);
            }
        });

        jButton3.setText("量程自动");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("量程手动");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        CheckBox_isYEnable.setForeground(new java.awt.Color(255, 255, 255));
        CheckBox_isYEnable.setText("添加Y轴标线");
        CheckBox_isYEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBox_isYEnableActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel_chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Button_add)
                .addGap(3, 3, 3)
                .addComponent(Button_del)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CheckBox_isYEnable)
                .addGap(0, 188, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_add)
                    .addComponent(jButton3)
                    .addComponent(Button_del)
                    .addComponent(jButton4)
                    .addComponent(CheckBox_isYEnable))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Panel_chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void Button_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_addActionPerformed
        this.CopyToSnapShot();
    }//GEN-LAST:event_Button_addActionPerformed

    private void Button_delActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_delActionPerformed
        this.DelLastSnapShot();
    }//GEN-LAST:event_Button_delActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.chart.chart_range.ChangeType(ChartRangePaint.RangeType.AutoRange);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        ManualRangeDialog ranged = new ManualRangeDialog(null, true);
        ranged.SetCurrentRange(this.chart.xyplot.getRangeAxis().getRange());
        ranged.setVisible(true);
        if (ranged.GetYRange() != null) {
            this.chart.chart_range.SetManualRange(ranged.GetYRange().getLowerBound(), ranged.GetYRange().getUpperBound());
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void CheckBox_isYEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBox_isYEnableActionPerformed
        this.chart.crosshair.SetXModel(!this.CheckBox_isYEnable.isSelected());
    }//GEN-LAST:event_CheckBox_isYEnableActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_add;
    private javax.swing.JButton Button_del;
    private javax.swing.JCheckBox CheckBox_isYEnable;
    private javax.swing.JPanel Panel_chart;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    // End of variables declaration//GEN-END:variables

    public DataChart GetChartPane() {
        return this.chart;
    }
}
