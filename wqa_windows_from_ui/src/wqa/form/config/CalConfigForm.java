/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.config;

import java.awt.Frame;
import wqa.control.config.DevConfigBean;
import wqa.form.config.cal.CalPanel;

/**
 *
 * @author chejf
 */
public class CalConfigForm extends ConfigForm{

    public CalConfigForm(Frame parent, boolean modal, String name) {
        super(parent, modal, name);
    }
    
    public boolean InitModel(DevConfigBean config) {
        config.SetMessageImple((String msg) -> {
            java.awt.EventQueue.invokeLater(() -> {
                SetConfigText(msg);
            });
        });

        AddPane(new CalPanel(config.GetDevCalConfig()));
        config.GetDevCalConfig().SetStartGetData(true);
        
        return true;
    }
}
