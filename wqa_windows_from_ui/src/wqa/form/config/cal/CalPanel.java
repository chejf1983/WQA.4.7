/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.config.cal;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import nahon.comm.event.NEvent;
import nahon.comm.event.NEventListener;
import nahon.comm.faultsystem.LogCenter;
import wqa.common.ListFlowLayout;
import wqa.control.common.SDisplayData;
import wqa.dev.data.SDataElement;
import wqa.control.config.DevCalConfig;

/**
 *
 * @author chejf
 */
public class CalPanel extends javax.swing.JPanel {

    /**
     * Creates new form CalPanel
     */
    private DevCalConfig calbean;
    //选择的类型
    private String selecttype;
    //选择的点数
    private int select_point;

    public CalPanel(DevCalConfig calbean) {
        initComponents();
        this.calbean = calbean;
        //初始化数据类型选择框
        ComboBox_DataType.removeAllItems();

        //遍历可定标类型
        for (String type : this.calbean.GetCalType()) {
            if (!type.contains("温度")) {
                ComboBox_DataType.addItem(type);
            }
        }

        //默认选择第一个定标数据
        this.ComboBox_DataType.setSelectedIndex(0);
        //保存选择类型
        this.selecttype = this.ComboBox_DataType.getSelectedItem().toString();

        this.ComboBox_DataType.addItemListener((ItemEvent ie) -> {
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                //保存选择类型
                selecttype = ComboBox_DataType.getSelectedItem().toString();
                //初始化定标点数
                InitCalNum();
            }
        });

        //初始化定标输入区域
        this.Cal_Panel.setLayout(new ListFlowLayout(FlowLayout.LEADING, 1, 5, true, false));

        //注册定标采集数据响应
        this.calbean.RegisterCalListener(new NEventListener<SDisplayData>() {
            @Override
            public void recevieEvent(NEvent<SDisplayData> event) {
                /* Create and display the dialog */
                java.awt.EventQueue.invokeLater(() -> {
                    //获取采样值
                    SDataElement data = event.GetEvent().GetDataElement(selecttype);
                    //获取原始值
                    SDataElement data_o = event.GetEvent().GetOraDataElement(selecttype);
                    //原始值不为空显示
                    if (data != null && data_o != null) {
                        //设置值
                        itemlist.forEach((item) -> {
                            item.SetValue(data_o.mainData, data.mainData);
                        });
                        //显示当前采样值
                        Label_value.setText(data.mainData + data.unit);

                        //显示当前温度
                        SDataElement temp = event.GetEvent().GetDataElement("温度");
                        SDataElement temp_o = event.GetEvent().GetOraDataElement("温度");
                        Label_temper.setText(temp.mainData + temp.unit);
                        temp_item.SetValue(temp_o.mainData, temp.mainData);
                    }
                });
            }
        });

        //注册定标点数选择响应
        this.ComboBox_CalNum.addItemListener((ItemEvent ie) -> {
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                //确定选择点数
                select_point = Integer.valueOf(ComboBox_CalNum.getSelectedItem().toString());
                InitCalItem();
            }
        });

        //更新定标点数
        this.InitCalNum();

        this.InitTempItem();
    }

    //初始化定标点数
    private void InitCalNum() {
        //刷新定标点个数
        this.ComboBox_CalNum.removeAllItems();

        for (int i = 1; i <= this.calbean.GetCalMaxNum(this.selecttype); i++) {
            this.ComboBox_CalNum.addItem(String.valueOf(i));
        }

        if (this.ComboBox_CalNum.getItemCount() > 0) {
            this.ComboBox_CalNum.setSelectedIndex(0);
            this.select_point = Integer.valueOf(this.ComboBox_CalNum.getSelectedItem().toString());
        } else {
            this.select_point = 0;
        }
        //初始化定标采样item
        this.InitCalItem();
    }

    private ArrayList<CalItem> itemlist = new ArrayList();

    //初始化定标采样item
    private void InitCalItem() {
        this.Cal_Panel.removeAll();
        itemlist.clear();
        for (int i = 0; i < this.select_point; i++) {
            CalItem item = new CalItem(i + 1);
            //溶解氧没有测量值输入，赋值为文字
//            if (calbean.GetTestLable(selecttype) != null) {
            if (selecttype.contentEquals("溶解氧")) {
                String[] info = new String[]{"饱和氧", "无氧"};
                item.SetTestLable(info[i]);
            }
            itemlist.add(item);
            this.Cal_Panel.add(item);
        }
        this.Cal_Panel.updateUI();
    }

    private CalItem temp_item;

    private void InitTempItem() {
        this.CalTemp_Panel.setLayout(new ListFlowLayout(FlowLayout.LEADING, 1, 5, true, false));
        this.CalTemp_Panel.removeAll();
        temp_item = new CalItem(1);
        this.CalTemp_Panel.add(temp_item);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ComboBox_DataType = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ComboBox_CalNum = new javax.swing.JComboBox<>();
        Button_TempCal = new javax.swing.JButton();
        Cal_Panel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        Label_temper = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        Label_value = new javax.swing.JLabel();
        CalTemp_Panel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        Button_DataCal = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        ComboBox_DataType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("数据类型:");

        jLabel2.setText("校准类型:");

        ComboBox_CalNum.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        Button_TempCal.setText("校准");
        Button_TempCal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_TempCalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Cal_PanelLayout = new javax.swing.GroupLayout(Cal_Panel);
        Cal_Panel.setLayout(Cal_PanelLayout);
        Cal_PanelLayout.setHorizontalGroup(
            Cal_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 334, Short.MAX_VALUE)
        );
        Cal_PanelLayout.setVerticalGroup(
            Cal_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 251, Short.MAX_VALUE)
        );

        jLabel3.setText("当前温度:");

        Label_temper.setText(" ");

        jLabel4.setText("当前值:");

        Label_value.setText(" ");

        javax.swing.GroupLayout CalTemp_PanelLayout = new javax.swing.GroupLayout(CalTemp_Panel);
        CalTemp_Panel.setLayout(CalTemp_PanelLayout);
        CalTemp_PanelLayout.setHorizontalGroup(
            CalTemp_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        CalTemp_PanelLayout.setVerticalGroup(
            CalTemp_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        jLabel5.setText("温度校准:");

        Button_DataCal.setText("校准");
        Button_DataCal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_DataCalActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(34, 88, 149));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 281, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CalTemp_Panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Cal_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ComboBox_DataType, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ComboBox_CalNum, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Label_temper, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Button_TempCal, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel4)
                                .addGap(5, 5, 5)
                                .addComponent(Label_value, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Button_DataCal)))
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ComboBox_CalNum, ComboBox_DataType});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {Button_DataCal, Button_TempCal});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ComboBox_DataType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(ComboBox_CalNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Cal_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Button_DataCal)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(Label_value)
                        .addComponent(jLabel4)))
                .addGap(5, 5, 5)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(5, 5, 5)
                .addComponent(CalTemp_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Button_TempCal)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel3)
                        .addComponent(Label_temper)))
                .addGap(5, 5, 5))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void Button_TempCalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_TempCalActionPerformed
        float[] oradata = new float[1];
        float[] testdata = new float[1];
        try {
            if (!this.temp_item.IsSet()) {
                LogCenter.Instance().ShowMessBox(Level.INFO, "温度校准点没有确认,无法进行校准,请先点击'停止'按钮.");
                return;
            }
            float[] input = this.temp_item.GetInputData();
            oradata[0] = input[0];
            testdata[0] = input[1];
            calbean.CalParameter("温度", oradata, testdata);
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
        }
    }//GEN-LAST:event_Button_TempCalActionPerformed

    private void Button_DataCalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_DataCalActionPerformed
        float[] oradata = new float[this.select_point];
        float[] testdata = new float[this.select_point];
        try {
            for (int i = 0; i < this.select_point; i++) {
                if (!this.itemlist.get(i).IsSet()) {
                    LogCenter.Instance().ShowMessBox(Level.INFO, "校准点-" + (i + 1) + " 没有确认,无法进行校准,请先点击'停止'按钮.");
                    return;
                }
                float[] input = this.itemlist.get(i).GetInputData();
                oradata[i] = input[0];
                testdata[i] = input[1];
            }
            calbean.CalParameter(selecttype, oradata, testdata);
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
        }
    }//GEN-LAST:event_Button_DataCalActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_DataCal;
    private javax.swing.JButton Button_TempCal;
    private javax.swing.JPanel CalTemp_Panel;
    private javax.swing.JPanel Cal_Panel;
    private javax.swing.JComboBox<String> ComboBox_CalNum;
    private javax.swing.JComboBox<String> ComboBox_DataType;
    private javax.swing.JLabel Label_temper;
    private javax.swing.JLabel Label_value;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
