/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;

/**
 *
 * @author chejf
 */
public class Config extends Properties {
    private String def_path;
    
    public void InitConfig(String path) {
        this.def_path = path;
        File file = new File(def_path, "dev_config");
        if (file.exists()) {
            try {
                loadFromXML(new FileInputStream(file));
            } catch (IOException ex) {
                LogCenter.Instance().PrintLog(Level.SEVERE, "没有找到配置文件", ex);
            }
        }
    }

    private void SaveConfig() {
        File file = new File(def_path, "dev_config");
        try {
            storeToXML(new FileOutputStream(file), "");
        } catch (IOException ex) {
            LogCenter.Instance().PrintLog(Level.SEVERE, "保存配置失败！", ex);
        }
    }

    @Override
    public synchronized Object setProperty(String string, String string1) {
        Object ret = super.setProperty(string, string1); //To change body of generated methods, choose Tools | Templates.
        this.SaveConfig();
        return ret;
    }
}
