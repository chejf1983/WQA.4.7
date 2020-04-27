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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.db.H2DBSaver;
import wqa.bill.io.IOManager;
import wqa.control.common.DevControlManager;
import wqa.bill.log.DevLog;
import wqa.control.DB.DBHelper;
import wqa.dev.intf.IDeviceSearch;

/**
 *
 * @author chejf
 */
public class WQAPlatform {

    private static WQAPlatform instance;
    private boolean isinited = false;
    private String def_path = "./";

    private WQAPlatform() {
    }

    public static WQAPlatform GetInstance() {
        if (instance == null) {
            instance = new WQAPlatform();
        }
        return instance;
    }

    // <editor-fold defaultstate="collapsed" desc="IO驱动和设备驱动"> 
    public static void LoadDriver(IDeviceSearch factory) {
        DevControlManager.SetDevDriver(factory);
    }
    // </editor-fold>  

    public void InitSystem(String path) throws Exception {
        this.def_path = path;

        LogCenter.Instance().SetLogPath(this.def_path + "/log");
        LogCenter.Instance().PrintLog(Level.INFO, "开始记录LOG");

        //初始化设备日志信息
        DevLog.Instance().InitDir(this.def_path + "/cal_log");

        this.InitConfig();

        this.GetDBHelperFactory().Init(def_path);
        this.isinited = true;
    }

    public void InitSystem() throws Exception {
        this.InitSystem("./");
    }

    public void CloseSystem() {
        this.SaveConfig();

        this.GetManager().DeleteAllControls();

        this.GetDBHelperFactory().Close();
        
        this.GetThreadPool().shutdown();
    }

    public boolean IsInited() {
        return this.isinited;
    }

    // <editor-fold defaultstate="collapsed" desc="系统模块"> 
    private DBHelper data_saver;

    public DBHelper GetDBHelperFactory() {
        if (data_saver == null) {
            data_saver = new DBHelper();
        }

        return data_saver;
    }
    //获取设备控制器Manager
    private DevControlManager devcontrol_manager;

    public DevControlManager GetManager() {
        if (devcontrol_manager == null) {
            devcontrol_manager = new DevControlManager();
        }
        return devcontrol_manager;
    }

    private IOManager ioManager;

    public IOManager GetIOManager() {
        if (ioManager == null) {
            ioManager = new IOManager();
            ioManager.InitLogWatchDog();
        }
        return ioManager;
    }

    //线程池
    ExecutorService threadpools;

    public ExecutorService GetThreadPool() {
        if (threadpools == null) {
            threadpools = Executors.newFixedThreadPool(200);
        }
//        System.out.println("当前激活线程:" + ((ThreadPoolExecutor) threadpools).getActiveCount());
        return threadpools;
    }

    public boolean is_internal = false;

    private Properties Config = new Properties();

    public Properties GetConfig() {
        return this.Config;
    }

    private void InitConfig() {
        File file = new File(this.def_path, "dev_config");
        if (file.exists()) {
            try {
                Config.loadFromXML(new FileInputStream(file));
                this.is_internal = Config.getProperty("IPS", "").contentEquals("Naqing");
            } catch (IOException ex) {
                LogCenter.Instance().PrintLog(Level.SEVERE, "没有找到配置文件", ex);
            }
        }
    }

    public void SaveConfig() {
        File file = new File(this.def_path, "dev_config");
        try {
            Config.storeToXML(new FileOutputStream(file), "");
        } catch (IOException ex) {
            LogCenter.Instance().PrintLog(Level.SEVERE, "保存配置失败！", ex);
        }
    }
    // </editor-fold>  
}
