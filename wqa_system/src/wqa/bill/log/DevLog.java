/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.bill.log;

import wqa.dev.data.LogNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import nahon.comm.faultsystem.LogCenter;
import wqa.control.data.DevID;

/**
 *
 * @author Administrator
 */
public class DevLog {

    private static DevLog instance;

    private DevLog() {
    }

    public static DevLog Instance() {
        if (instance == null) {
            instance = new DevLog();
        }

        return instance;
    }

    private boolean log_switch = true;

    public void SetLogSwitch(boolean value) {
        this.log_switch = value;
    }

    // <editor-fold defaultstate="collapsed" desc="设置LOG信息"> 
    public static String SyslogFile = ".log";//系统日志文件名称
    private String def_path = "./cal_log";
    private int maxfilenum = 50;
    private final ReentrantLock calog_lock = new ReentrantLock(true);

    private int left_line = 200;
    private int maxLinenum = left_line + 100;

    public void InitDir(String filepath) {
        this.def_path = filepath;
        File dir = new File(filepath);
        //如果文件夹不存在，创建文件夹
        if (!dir.exists()) {
            try {
                dir.mkdir();
                dir = new File(filepath);
            } catch (Exception ex) {
            }
        }

        //查看log下旧的文件个数，超过最大值，就清除旧的文件
        int morefile = dir.listFiles().length - maxfilenum;

        for (File f : dir.listFiles()) {
            if (morefile >= 0) {
                morefile--;
                f.delete();
            } else {
                break;
            }
        }

        log_switch = true;
    }

    private File GetLogFile(String dev_serial) {
        try {
            if (!def_path.endsWith("/")) {
                def_path += "/";
            }

            String filename = def_path + dev_serial + SyslogFile;
            File log_file = new File(filename);

            if (!log_file.exists()) {
                log_file.createNewFile();
            }

            return log_file;
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
            return null;
        }
    }

    public void AddLog(DevID id, LogNode... log) {
        if (!log_switch) {
            return;
        }

        File logfile = this.GetLogFile(id.ToChineseString());

        if (logfile != null) {
            calog_lock.lock();
            try {
                FileWriter fileWriter = new FileWriter(logfile, true);
                LogNode node = new LogNode("时间", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                node.children.addAll(Arrays.asList(log));
                PrintNode(fileWriter, node);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException ex) {
                LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
            } finally {
                calog_lock.unlock();
            }
        }
    }

    private String FormateLog(String name, Object... values) {
        String ret = name + ":";
        for (int i = 0; i < values.length; i++) {
            ret += values[i] + " ";
        }
        return ret + "\r\n";
    }

    private void PrintNode(FileWriter fileWriter, LogNode log) throws IOException {
        PrintNode(fileWriter, "", log);
    }

    private void PrintNode(FileWriter fileWriter, String tab, LogNode log) throws IOException {
        fileWriter.write(tab + FormateLog(log.name, log.value));
        for (LogNode node : log.children) {
            try {
                PrintNode(fileWriter, tab + "    ", node);
            } catch (IOException ex) {
                Logger.getLogger(DevLog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void CleanLog(DevID id, ArrayList<String> logs) {
        //确保超过最大log数目
        if (logs.size() < this.maxLinenum) {
            return;
        }

        //只留下最少行数
        File logfile = this.GetLogFile(id.toString());
        if (logfile != null) {
            try (FileWriter fileWriter = new FileWriter(logfile)) {
                for (int i = logs.size() - left_line; i < logs.size(); i++) {
                    fileWriter.write(logs.get(i) + "\r\n");
                }
                fileWriter.flush();
            } catch (IOException ex) {
                LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
            }
        }
    }

    public ArrayList<String> ReadLog(DevID id) {
        File logfile = this.GetLogFile(id.ToChineseString());
        ArrayList<String> resultStr = new ArrayList<>();
        if (logfile != null) {
            calog_lock.lock();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(logfile))) {
                String str = null;
                while (null != (str = bufferedReader.readLine())) {
                    resultStr.add(str);
                }
            } catch (IOException ex) {
                LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
            } finally {
                calog_lock.unlock();
            }

            calog_lock.lock();
            try {
                CleanLog(id, resultStr);
            } catch (Exception ex) {
                LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
            } finally {
                calog_lock.unlock();
            }
        }

        return resultStr;
    }

    public void DelFile(DevID id) {
        File logfile = this.GetLogFile(id.ToChineseString());
        if (logfile != null) {
            logfile.delete();
        }
    }
    // </editor-fold>    
}
