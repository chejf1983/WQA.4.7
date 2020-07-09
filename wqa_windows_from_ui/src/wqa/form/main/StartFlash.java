/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.main;

import wqa.form.errormsg.MsgBoxFactory;
import java.awt.Color;
import java.awt.Toolkit;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import nahon.comm.faultsystem.LogCenter;
import wqa.adapter.factory.ModBusDevFactory;
import wqa.bill.io.IOManager;
import wqa.system.WQAPlatform;

/**
 *
 * @author Administrator
 */
public class StartFlash extends javax.swing.JFrame {

    /**
     * Creates new form StartFlash
     */
    public StartFlash() {
        //去除对话框标题栏
        initComponents();
        ToolTipManager.sharedInstance().setInitialDelay(1000);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
        this.getRootPane().setWindowDecorationStyle(0);

        //设置ICON
        Toolkit tk = Toolkit.getDefaultToolkit();
        java.net.URL disurl = MainForm.class.getResource("/wqa/form/main/resource/ObserverIcon2.png");
        java.awt.Image image = tk.createImage(disurl);
        this.setIconImage(image);

        //居中显示
        this.setLocationRelativeTo(null);

        //透明对话框
        this.getRootPane().setOpaque(false);
        this.getContentPane().setBackground(new Color(0, 0, 0, 0));
        this.setBackground(new Color(0, 0, 0, 0));

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                //初始化系统
                WQAPlatform.GetInstance().InitSystem();
                //iolog设置10000条
                IOManager.MaxLogNum = 10000;
                //注册弹出窗口
                LogCenter.Instance().RegisterFaultEvent(new EventListener<Level>() {
                    @Override
                    public void recevieEvent(Event<Level> event) {
                        WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
                            //JOptionPane.showm
                            MsgBoxFactory.Instance().ShowMsgBox(event.Info().toString());
                        });
                    }
                });
                //初始化界面
                MainForm mainForm = new MainForm();
                TimeUnit.MILLISECONDS.sleep(100);
                //删除快照
                StartFlash.this.setVisible(false);
                StartFlash.this.dispose();
                //显示主窗口
                mainForm.setVisible(true);
            } catch (Exception ex) {
                //初始化失败，可能系统进程还未完成
                JOptionPane.showMessageDialog(rootPane, "初始化失败:" + ex.getMessage());
                StartFlash.this.setVisible(false);
                StartFlash.this.dispose();
                System.exit(0);
            }
        });
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/main/resource/splash.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(StartFlash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StartFlash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StartFlash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StartFlash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        try {
            //设置皮肤文字没有阴影
            UIManager.getDefaults().put("SyntheticaAluOxide.textShadowEnabled", false);
            UIManager.getDefaults().put("SyntheticaAluOxide.useSimpleTextShadow", false);
            //修改皮肤
            javax.swing.UIManager.setLookAndFeel("de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StartFlash().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
