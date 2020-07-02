/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.monitor0;

import java.util.ArrayList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import wqa.common.JImagePane;
import wqa.control.common.SDisplayData;
import wqa.dev.data.SDataElement;
import wqa.form.monitor.DataVector;

/**
 *
 * @author chejf
 */
public class MTable extends javax.swing.JPanel {

    /**
     * Creates new form MTable
     */
    public MTable() {
        initComponents();
//         //背景渐变颜色
//        GradientPaint gradientpaint
//                = new GradientPaint(0.0F, 0.0F, new Color(197, 199, 207), 0.0F, 0.0F, new Color(241, 244, 247));
//        this.setBackground(new Color(227, 230, 235));    
        this.IPane.setBackgroundImage(new javax.swing.ImageIcon(getClass().getResource("/wqa/form/monitor/resource/pc_62.png")).getImage());
        this.IPane.setImageDisplayMode(JImagePane.SCALED);

    }

    private DataVector0 data_vector;
    private String[] names;
    private int index = 0;

    public void SetDataSet(DataVector0 table_model) {
        this.data_vector = table_model;
        this.data_vector.NewData.RegeditListener(new EventListener() {
            @Override
            public void recevieEvent(Event event) {
                UpdateData();
            }
        });

        ArrayList<String> tmp = new ArrayList();
        for (String name : this.data_vector.GetVisableName()) {
             if(!name.startsWith("温度")){
                 tmp.add(name);
             }
        }
        names = tmp.toArray(new String[0]);
    }

    private void UpdateData() {
        SDisplayData data = this.data_vector.GetLastData();
        if (data == null) {
            return;
        }
        SDataElement tdata = data.GetDataElement(names[index]);
        this.mTRow1.SetData(tdata.name, tdata.range_info, String.valueOf(tdata.mainData), tdata.unit);
        tdata = data.GetDataElement("温度");
        this.mTRow2.SetData(tdata.name, tdata.range_info, String.valueOf(tdata.mainData), tdata.unit);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        IPane = new wqa.common.JImagePane();
        mTRow1 = new wqa.form.monitor0.MTRow();
        mTRow2 = new wqa.form.monitor0.MTRow();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        mTRow1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mTRow1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout IPaneLayout = new javax.swing.GroupLayout(IPane);
        IPane.setLayout(IPaneLayout);
        IPaneLayout.setHorizontalGroup(
            IPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(IPaneLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(IPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mTRow1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mTRow2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        IPaneLayout.setVerticalGroup(
            IPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(IPaneLayout.createSequentialGroup()
                .addComponent(mTRow1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(mTRow2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(IPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(IPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void mTRow1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mTRow1MouseClicked
        index = (index + 1) % names.length;
        UpdateData();
    }//GEN-LAST:event_mTRow1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private wqa.common.JImagePane IPane;
    private wqa.form.monitor0.MTRow mTRow1;
    private wqa.form.monitor0.MTRow mTRow2;
    // End of variables declaration//GEN-END:variables
}