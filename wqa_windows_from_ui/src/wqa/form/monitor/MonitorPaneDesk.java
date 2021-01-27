/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.monitor;

import java.awt.CardLayout;
import wqa.common.ModifiedFlowLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import nahon.comm.event.NEvent;
import nahon.comm.event.NEventListener;
import wqa.control.common.DevControl;
import wqa.control.common.DevControlManager.DevNumChange;
import wqa.form.monitor0.MonitorPane0;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class MonitorPaneDesk extends javax.swing.JPanel {

    /**
     * Creates new form MonitorGround
     */
    public MonitorPaneDesk() {
        initComponents();

        this.InitWorkDesk();
    }

    // <editor-fold defaultstate="collapsed" desc="初始化设备窗口">      
    private ModifiedFlowLayout mlayout = new ModifiedFlowLayout(FlowLayout.LEFT, 20, 10);
    private CardLayout clayout = new CardLayout();
    private boolean is_mlayout = true;
    private final ReentrantLock pan_lock = new ReentrantLock(true);

    private void SortAddAllPane() {
//        Collection<MonitorPane1> values = bookpanes.values();
        ArrayList<DevControl> mlist = new ArrayList(bookpanes.keySet());
        Collections.sort(mlist, (DevControl m1, DevControl m2) -> {
            return m1.GetDevID().dev_addr > m2.GetDevID().dev_addr ? 1 : -1;
        });

        mlist.forEach((m) -> {
            for (JPanel pane : bookpanes.get(m)) {
                Panel_desk.add(pane);
            }
        });
    }

    private void InitWorkDesk() {
        this.ScrollPanel.setBorder(BorderFactory.createEmptyBorder());
        this.Panel_desk.setLayout(mlayout);

        //注册设备添加删除事件
        WQAPlatform.GetInstance().GetManager().StateChange.RegeditListener(new NEventListener<DevNumChange>() {
            @Override
            public void recevieEvent(NEvent<DevNumChange> event) {
                if (event.GetEvent() == DevNumChange.ADD) {
                    AddDataBook((DevControl) event.Info());
                }
                if (event.GetEvent() == DevNumChange.DEL) {
                    RemoveBook((DevControl) event.Info());
                }
            }
        });
    }

    public void Refresh() {
        pan_lock.lock();
        for (DevControl control : WQAPlatform.GetInstance().GetManager().GetAllControls()) {
            this.AddDataBook(control);
        }
        pan_lock.unlock();
    }

    //设备和界面字典
    private HashMap<DevControl, JPanel[]> bookpanes = new HashMap();

    //添加设备
    private void AddDataBook(DevControl dev) {
        pan_lock.lock();
        try {
            if (dev == null) {
                return;
            }

            //如果已经存在，就不添加了
            if (this.bookpanes.get(dev) != null) {
                return;
            }

            if (WQAPlatform.GetInstance().is_internal) {
                //添加到设备界面字典
                MonitorPane1 pane = new MonitorPane1(this, dev.GetCollector());
                this.bookpanes.put(dev, new JPanel[]{pane});
            } else {
                ArrayList<JPanel> lists = new ArrayList();
                for (int i : dev.GetCollector().GetMaxDataSort()) {
                    //添加到设备界面字典
                    if (i != 0) {
                        MonitorPane0 pane = new MonitorPane0(this, dev.GetCollector(), i);
                        lists.add(pane);
                    }
                }
                this.bookpanes.put(dev, lists.toArray(new JPanel[0]));
            }

            //如果有全屏的界面，则不刷新界面
            if (is_mlayout) {
                java.awt.EventQueue.invokeLater(() -> {
                    Panel_desk.removeAll();
                    SortAddAllPane();
                    Panel_desk.updateUI();
                });
            }
        } finally {
            pan_lock.unlock();
        }
    }

    //切换全屏界面
    public void SwitchCard(JPanel pane) {
        if (is_mlayout) {
            java.awt.EventQueue.invokeLater(() -> {
                Panel_desk.removeAll();
                updateUI();
                //切换成cardlayout
                Panel_desk.setLayout(clayout);
                Panel_desk.add(pane);
                updateUI();
                is_mlayout = false;
            });
        }
    }

    //切换回mflowlayout
    public void SwitchMCard() {
        if (!is_mlayout) {
            java.awt.EventQueue.invokeLater(() -> {
                Panel_desk.removeAll();
                updateUI();
                Panel_desk.setLayout(mlayout);
                SortAddAllPane();
                updateUI();
                is_mlayout = true;
            });
        }
    }

    //删除设备
    private void RemoveBook(DevControl dev) {
        pan_lock.lock();
        try {
            if (dev == null) {
                return;
            }

            JPanel[] panes = this.bookpanes.remove(dev);
            if (panes != null) {
                java.awt.EventQueue.invokeLater(() -> {
                    for (JPanel pane : panes) {
                        Panel_desk.remove(pane);
                    }
                    Panel_desk.updateUI();
                });
            }
        } finally {
            pan_lock.unlock();
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

        ScrollPanel = new javax.swing.JScrollPane();
        Panel_desk = new javax.swing.JPanel();

        javax.swing.GroupLayout Panel_deskLayout = new javax.swing.GroupLayout(Panel_desk);
        Panel_desk.setLayout(Panel_deskLayout);
        Panel_deskLayout.setHorizontalGroup(
            Panel_deskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 630, Short.MAX_VALUE)
        );
        Panel_deskLayout.setVerticalGroup(
            Panel_deskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 494, Short.MAX_VALUE)
        );

        ScrollPanel.setViewportView(Panel_desk);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ScrollPanel)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ScrollPanel)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Panel_desk;
    private javax.swing.JScrollPane ScrollPanel;
    // End of variables declaration//GEN-END:variables
}
