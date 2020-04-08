/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.form.config;

import java.awt.Frame;
import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import wqa.control.config.DevConfigBean;
import wqa.form.config.cal.CalPanel;

/**
 *
 * @author chejf
 */
public class CalConfigForm extends ConfigForm {

    public CalConfigForm(Frame parent, boolean modal, String name) {
        super(parent, modal, name);
    }

    private DevConfigBean config;

    public boolean InitModel(DevConfigBean config) {
        this.config = config;

        config.SetMessageImple((String msg) -> {
            java.awt.EventQueue.invokeLater(() -> {
                SetConfigText(msg);
            });
        });

        AddPane(new CalPanel(config.GetDevCalConfig()));
        config.GetDevCalConfig().SetStartGetData(true);

        this.config.CloseEvent.RegeditListener(new EventListener() {
            @Override
            public void recevieEvent(Event event) {
                CalConfigForm.this.dispose();    
            }
        });

        return true;
    }

    @Override
    public void Close() {
        config.Quit();
        super.Close(); //To change body of generated methods, choose Tools | Templates.
    }
}
