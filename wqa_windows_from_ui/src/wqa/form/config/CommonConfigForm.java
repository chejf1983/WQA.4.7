/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.config;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JTabbedPane;
import javax.swing.table.AbstractTableModel;
import wqa.control.config.DevConfigBean;
import wqa.control.config.DevConfigTable;
import wqa.form.config.brush.BrushConfig;

/**
 *
 * @author chejf
 */
public class CommonConfigForm extends ConfigForm{
    
    public CommonConfigForm(Frame parent, boolean modal, String name) {
        super(parent, modal, name);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                config.Close();
            }
        });
    }
    
    private ArrayList<ConfigTablePane> pane = new ArrayList();
    private JTabbedPane TabbedPane = new JTabbedPane();
    private DevConfigBean config;

    public void InitModel(DevConfigBean config) throws Exception{
        this.config = config;

        config.SetMessageImple((String msg) -> {
            java.awt.EventQueue.invokeLater(() -> {
                SetConfigText(msg);
            });
        });

        pane.clear();
        for (DevConfigTable table : config.GetBaseDevConfig()) {
            ConfigTablePane configTablePane = new ConfigTablePane(table);
            this.pane.add(configTablePane);
            TabbedPane.add(table.GetListName(), configTablePane);
        }

        if (config.GetMotorConfig() != null) {
            TabbedPane.add("清扫设置", new BrushConfig(config.GetMotorConfig()));
        }

        this.AddPane(this.TabbedPane);     
    }
    
    public void Refresh(){
        for(ConfigTablePane pane1 : pane){
            pane1.Refresh();
        }
    }
    
    public void InitViewConfig(AbstractTableModel viewConfig) throws Exception{
        TabbedPane.add("界面配置", new ViewConfigPane(viewConfig));
    } 
}
