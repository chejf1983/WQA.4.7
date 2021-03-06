/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.config.brush;

import javax.swing.table.TableCellEditor;
import wqa.control.config.DevMotorConfig;
import wqa.dev.data.SMotorParameter;
import wqa.dev.data.SMotorParameter.CleanMode;
import wqa.form.config.ConfigTableModel;

/**
 *
 * @author chejf
 */
public class BrushConfig extends javax.swing.JPanel {

    /**
     * Creates new form MotorConfig
     */
    private DevMotorConfig config;

    public BrushConfig(DevMotorConfig config) {
        initComponents();
        this.config = config;
        this.buttonGroup.add(this.RadioButton_auto);
        this.buttonGroup.add(this.RadioButton_manual);
//        this.jTable1.setTableHeader(null);
        if (this.config.GetMotoPara().mode == CleanMode.Auto) {
            this.RadioButton_auto.setSelected(true);
            this.jTable1.setModel(new ConfigTableModel(this.config.GetMotoPara().auto_config));
        } else {
            this.RadioButton_manual.setSelected(true);
            this.jTable1.setModel(new ConfigTableModel(this.config.GetMotoPara().manu_config));
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

        buttonGroup = new javax.swing.ButtonGroup();
        RadioButton_manual = new javax.swing.JRadioButton();
        RadioButton_auto = new javax.swing.JRadioButton();
        Button_Setup = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        Button_manual = new javax.swing.JButton();

        RadioButton_manual.setText("手动清洗");
        RadioButton_manual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RadioButton_manualActionPerformed(evt);
            }
        });

        RadioButton_auto.setText("自动清洗");
        RadioButton_auto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RadioButton_autoActionPerformed(evt);
            }
        });

        Button_Setup.setText("设置");
        Button_Setup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_SetupActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        Button_manual.setText("立即清扫");
        Button_manual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_manualActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(RadioButton_manual)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RadioButton_auto))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(Button_manual)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_Setup, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RadioButton_manual)
                    .addComponent(RadioButton_auto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_Setup)
                    .addComponent(Button_manual))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void Button_SetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_SetupActionPerformed
        CleanMode mode = this.RadioButton_auto.isSelected() ? CleanMode.Auto : CleanMode.Manu;
        TableCellEditor cellEditor = this.jTable1.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }

        if (mode == CleanMode.Auto) {
            config.SetMotoPara(new SMotorParameter(mode, ((ConfigTableModel) this.jTable1.getModel()).GetValues(), this.config.GetMotoPara().manu_config));
        } else {
            config.SetMotoPara(new SMotorParameter(mode, this.config.GetMotoPara().auto_config, ((ConfigTableModel) this.jTable1.getModel()).GetValues()));
        }
    }//GEN-LAST:event_Button_SetupActionPerformed

    private void RadioButton_manualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RadioButton_manualActionPerformed
        // TODO add your handling code here:
        this.jTable1.setModel(new ConfigTableModel(this.config.GetMotoPara().manu_config));
    }//GEN-LAST:event_RadioButton_manualActionPerformed

    private void RadioButton_autoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RadioButton_autoActionPerformed
        // TODO add your handling code here:
        this.jTable1.setModel(new ConfigTableModel(this.config.GetMotoPara().auto_config));
    }//GEN-LAST:event_RadioButton_autoActionPerformed

    private void Button_manualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_manualActionPerformed
        config.StartManual();
    }//GEN-LAST:event_Button_manualActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_Setup;
    private javax.swing.JButton Button_manual;
    private javax.swing.JRadioButton RadioButton_auto;
    private javax.swing.JRadioButton RadioButton_manual;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
