/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.system;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.IOManager;
import wqa.control.common.DevControlManager;
import wqa.bill.log.DevLog;
import wqa.control.DB.DBHelper;

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

    public void InitSystem(String path) throws Exception {
        this.def_path = path;

        LogCenter.Instance().SetLogPath(this.def_path + "/log");
        LogCenter.Instance().PrintLog(Level.INFO, "开始记录LOG");

        this.GetConfig().InitConfig(def_path);
        is_internal = this.GetConfig().getProperty("IPS", "").contentEquals("Naqing");
        
        //初始化设备日志信息
        DevLog.Instance().InitDir(this.def_path + "/cal_log");

        this.GetDBHelperFactory().Init(def_path);
        this.isinited = true;
    }

    public void InitSystem() throws Exception {
        this.InitSystem("./");
    }

    public void CloseSystem() {
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

    private Config config;

    public Config GetConfig() {
        if (config == null) {
            config = new Config();
        }
        return this.config;
    }

    // </editor-fold>  
}
