/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.config;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import wqa.control.config.DevConfigBean;
import wqa.form.config.cal.CalPanel;

/**
 *
 * @author chejf
 */
public class CalConfigForm extends ConfigForm {

    public CalConfigForm(Frame parent, boolean modal, String name) {
        super(parent, modal, name);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                config.Close();
            }
        });
    }

    private DevConfigBean config;

    public void InitModel(DevConfigBean config) {
        this.config = config;

        config.SetMessageImple((String msg) -> {
            java.awt.EventQueue.invokeLater(() -> {
                SetConfigText(msg);
            });
        });

        AddPane(new CalPanel(config.GetDevCalConfig()));
    }
}
