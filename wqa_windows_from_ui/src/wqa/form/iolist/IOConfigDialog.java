/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.iolist;

import java.awt.Color;
import wqa.common.ListFlowLayout;
import java.awt.FlowLayout;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.SIOInfo;
import wqa.bill.io.ShareIO;
import wqa.system.WQAPlatform;
import wqa.winio.adapter.WComManager;

/**
 *
 * @author chejf
 */
public class IOConfigDialog extends javax.swing.JDialog {

    /**
     * Creates new form IOConfigDialog
     *
     * @param parent
     * @param modal
     */
    public IOConfigDialog(java.awt.Frame parent, boolean modal) {

        super(parent, modal);
        initComponents();

        //居中显示
        setLocationRelativeTo(parent);

        this.RefreshIO();

        initDialog();
    }

    private void initDialog() {

        this.getContentPane().setBackground(new Color(34, 88, 149));

        this.ScrollPane.getHorizontalScrollBar().setAutoscrolls(false);
        this.ScrollPane.getVerticalScrollBar().setAutoscrolls(true);
        this.IO_PaneDesk.setLayout(new ListFlowLayout(FlowLayout.LEFT, 1, 10, true, false));
//        this.IO_PaneDesk.setBounds(200, 200, 500, 500);

        this.Button_MIGP.setVisible(WQAPlatform.GetInstance().is_internal);
    }

    private void RefreshIO() {
        this.IO_PaneDesk.removeAll();
        WComManager.GetInstance().InitAllCom();
        ShareIO[] iolist = WComManager.GetInstance().GetAllCom();
        for (ShareIO iolist1 : iolist) {
            if (SIOInfo.COM.equals(iolist1.GetConnectInfo().iotype)) {
                COMPane com = new COMPane();
                com.SetIO(iolist1);
                this.IO_PaneDesk.add(com);
            }
        }
        this.IO_PaneDesk.updateUI();
    }

    public static int SEARCH_CANCEL = 0x00;
    public static int SEARCH_MODBUS = 0x01;
    public static int SEARCH_MIGP = 0x02;
    private int result = SEARCH_CANCEL;

    public int GetResult() {
        return this.result;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Button_ModBus = new javax.swing.JButton();
        Button_Cancel = new javax.swing.JButton();
        Button_MIGP = new javax.swing.JButton();
        Button_AddCom = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        TextField_ComName = new javax.swing.JTextField();
        ScrollPane = new javax.swing.JScrollPane();
        IO_PaneDesk = new javax.swing.JPanel();
        ToggleButton_AMODBUS = new javax.swing.JToggleButton();
        ToggleButton_AMIGP = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        Button_ModBus.setText("ModBus搜索");
        Button_ModBus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_ModBusActionPerformed(evt);
            }
        });

        Button_Cancel.setText("取消");
        Button_Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_CancelActionPerformed(evt);
            }
        });

        Button_MIGP.setText("MIGP搜索");
        Button_MIGP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_MIGPActionPerformed(evt);
            }
        });

        Button_AddCom.setText("新增串口");
        Button_AddCom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_AddComActionPerformed(evt);
            }
        });

        jLabel1.setText("串口号:");

        TextField_ComName.setText("COM1");

        javax.swing.GroupLayout IO_PaneDeskLayout = new javax.swing.GroupLayout(IO_PaneDesk);
        IO_PaneDesk.setLayout(IO_PaneDeskLayout);
        IO_PaneDeskLayout.setHorizontalGroup(
            IO_PaneDeskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 347, Short.MAX_VALUE)
        );
        IO_PaneDeskLayout.setVerticalGroup(
            IO_PaneDeskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 384, Short.MAX_VALUE)
        );

        ScrollPane.setViewportView(IO_PaneDesk);

        ToggleButton_AMODBUS.setText("ModBus自动搜索");

        ToggleButton_AMIGP.setText("MIGP自动搜索");
        ToggleButton_AMIGP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ToggleButton_AMIGPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(ToggleButton_AMIGP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Button_MIGP, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(TextField_ComName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_AddCom))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(ToggleButton_AMODBUS, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(Button_ModBus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Button_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(ScrollPane, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ToggleButton_AMODBUS)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Button_ModBus)
                            .addComponent(Button_Cancel)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ToggleButton_AMIGP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_MIGP)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_AddCom)
                    .addComponent(jLabel1)
                    .addComponent(TextField_ComName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Button_ModBusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_ModBusActionPerformed
        // TODO add your handling code here:
        this.result = SEARCH_MODBUS;
        this.dispose();
    }//GEN-LAST:event_Button_ModBusActionPerformed

    private void Button_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_CancelActionPerformed
        // TODO add your handling code here:
        this.result = SEARCH_CANCEL;
        this.dispose();
    }//GEN-LAST:event_Button_CancelActionPerformed

    private void Button_MIGPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_MIGPActionPerformed
        // TODO add your handling code here:
        this.result = SEARCH_MIGP;
        this.dispose();
    }//GEN-LAST:event_Button_MIGPActionPerformed

    private void Button_AddComActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_AddComActionPerformed
        // TODO add your handling code here:
        String COMName = TextField_ComName.getText().trim();
        if (WComManager.GetInstance().AddCom(COMName)){
            this.RefreshIO();
            try {
                int nm = Integer.valueOf(COMName.substring(3));
                String newName = "COM" + (nm + 1);
                TextField_ComName.setText(newName);
            } catch (Exception ex) {

            }
        }else{
            LogCenter.Instance().ShowMessBox(Level.SEVERE, "串口已经存在");
        }
    }//GEN-LAST:event_Button_AddComActionPerformed

    private void ToggleButton_AMIGPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ToggleButton_AMIGPActionPerformed
        
    }//GEN-LAST:event_ToggleButton_AMIGPActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_AddCom;
    private javax.swing.JButton Button_Cancel;
    private javax.swing.JButton Button_MIGP;
    private javax.swing.JButton Button_ModBus;
    private javax.swing.JPanel IO_PaneDesk;
    private javax.swing.JScrollPane ScrollPane;
    private javax.swing.JTextField TextField_ComName;
    private javax.swing.JToggleButton ToggleButton_AMIGP;
    private javax.swing.JToggleButton ToggleButton_AMODBUS;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
