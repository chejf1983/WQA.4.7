/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.config;

import java.awt.Frame;
import java.util.ArrayList;
import javax.swing.JTabbedPane;
import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import wqa.control.config.DevConfigBean;
import wqa.control.config.DevConfigTable;
import wqa.form.config.brush.BrushConfig;
import wqa.form.monitor.DataVector;

/**
 *
 * @author chejf
 */
public class CommonConfigForm extends ConfigForm{
    
    public CommonConfigForm(Frame parent, boolean modal, String name) {
        super(parent, modal, name);
    }
    
    private ArrayList<ConfigTablePane> pane = new ArrayList();
    private JTabbedPane TabbedPane = new JTabbedPane();
    private DevConfigBean config;

    public boolean InitModel(DevConfigBean config) throws Exception{
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

//        config.UpdateConfigEvent.RegeditListener(new EventListener() {
//            @Override
//            public void recevieEvent(Event event) {
//                pane.forEach(pane -> {
//                    pane.Refresh();
//                });
//            }
//        });

//        if (this.config.GetDevCalConfig() != null) {
//            TabbedPane.add("校准", new CalPanel(this.config.GetDevCalConfig()));
//            TabbedPane.addChangeListener((ChangeEvent ce) -> {
//                config.GetDevCalConfig().SetStartGetData("校准".equals(TabbedPane.getTitleAt(TabbedPane.getSelectedIndex())));
//            });
//        }
        if (config.GetMotorConfig() != null) {
            TabbedPane.add("清扫设置", new BrushConfig(config.GetMotorConfig()));
        }

        this.AddPane(this.TabbedPane);
        
        this.config.CloseEvent.RegeditListener(new EventListener() {
            @Override
            public void recevieEvent(Event event) {
                CommonConfigForm.this.dispose();    
            }
        });
        return true;
    }
    
    public void InitViewConfig(DataVector viewConfig) throws Exception{
        TabbedPane.add("界面配置", new ViewConfigPane(viewConfig));
    }
    
    @Override
    public void Close() {
        config.Quit();
        super.Close(); //To change body of generated methods, choose Tools | Templates.
    }

}
