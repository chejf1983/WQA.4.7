/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.iolist;

import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;
import wqa.dev.data.SIOInfo;
import wqa.bill.io.ShareIO;
import wqa.bill.io.IOManager;

/**
 *
 * @author chejf
 */
public class COMPane extends javax.swing.JPanel {

    /**
     * Creates new form COMPane
     */
    public COMPane() {
        initComponents();
    }

    String[] BandRate = new String[]{"4800", "9600", "19200", "38400", "57600", "115200"};

    private ShareIO io;

    public void SetIO(ShareIO io_instance) {
        this.io = io_instance;
        this.Lable_ComName.setText(io.GetConnectInfo().par[0]);

        this.ComboBox_bandrate.removeAllItems();
        this.ComboBox_bandrate.setEditable(true);
        this.ComboBox_bandrate.setSelectedItem(io.GetConnectInfo().par[1]);
        for (int i = 0; i < BandRate.length; i++) {
            this.ComboBox_bandrate.addItem(BandRate[i]);
        }
        UpdateState();
    }

    private boolean IsAvailable() {
        return !io.IsClosed();
    }

    private void UpdateState() {
        this.ToggleButton_Open.setSelected(IsAvailable());
        if (!IsAvailable()) {
            Label_State.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/iolist/resource/disconnect_32p.png"))); // NOI18N
        } else {
            Label_State.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/iolist/resource/connect_32p.png"))); // NOI18N
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        Lable_ComName = new javax.swing.JLabel();
        ToggleButton_Open = new javax.swing.JToggleButton();
        Label_State = new javax.swing.JLabel();
        ComboBox_bandrate = new javax.swing.JComboBox<>();

        jLabel1.setText("波特率:");

        jLabel3.setText("端口:");

        Lable_ComName.setText("jLabel4");

        ToggleButton_Open.setText("开关");
        ToggleButton_Open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ToggleButton_OpenActionPerformed(evt);
            }
        });

        Label_State.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/iolist/resource/connect_32p.png"))); // NOI18N

        ComboBox_bandrate.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Lable_ComName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Label_State)
                .addGap(10, 10, 10)
                .addComponent(ToggleButton_Open)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_bandrate, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ComboBox_bandrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(Label_State)
                            .addComponent(ToggleButton_Open))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(Lable_ComName, javax.swing.GroupLayout.Alignment.CENTER))
                        .addGap(12, 12, 12)))
                .addGap(12, 12, 12))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void ToggleButton_OpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ToggleButton_OpenActionPerformed
        if (ToggleButton_Open.isSelected()) {
            try {
                SIOInfo newinfo = io.GetConnectInfo();
                newinfo.par[1] = this.ComboBox_bandrate.getSelectedItem().toString();
                io.SetConnectInfo(newinfo);
                io.Open();
            } catch (Exception ex) {
                LogCenter.Instance().SendFaultReport(Level.SEVERE, "打开串口失败", ex);
            }
        } else {
            io.Close();
        }
        UpdateState();
    }//GEN-LAST:event_ToggleButton_OpenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_bandrate;
    private javax.swing.JLabel Label_State;
    private javax.swing.JLabel Lable_ComName;
    private javax.swing.JToggleButton ToggleButton_Open;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
