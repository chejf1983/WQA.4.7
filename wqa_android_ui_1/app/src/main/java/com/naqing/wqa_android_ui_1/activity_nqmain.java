package com.naqing.wqa_android_ui_1;

import android.app.ActivityManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naqing.adb.ADBHelper;
import com.naqing.common.ErrorExecutor;
import com.naqing.common.InputDialog;
import com.naqing.common.NQProcessDialog;
import com.naqing.common.Security;
import com.naqing.control.fragment_control_main;
import com.naqing.io.AndroidIO;
import com.naqing.monitor.fragment_monitor_main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import nahon.comm.faultsystem.LogCenter;
import wqa.adapter.factory.ModBusDevFactory;
import wqa.bill.log.DevLog;
import wqa.system.WQAPlatform;

public class activity_nqmain extends AppCompatActivity {


    //    private ArrayList<Device> list = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nqmain);

        this.initTime();

        this.init_switch();

        initSystem();
    }


    /**
     * 初始化系统
     */
    private void initSystem() {
        /** 关闭密码显示*/
//        Security.Instance().EnableSecurity(false);

        /** 初始化驱动*/
        WQAPlatform.LoadDriver(new ModBusDevFactory());
        /** 设置Error窗口的Activity*/
        ErrorExecutor.lastparent = this;
        try {
            /** 初始化SQLite数据库*/
            ADBHelper adb = new ADBHelper(this);
            /** 替换数据库*/
            WQAPlatform.GetInstance().GetDBHelperFactory().SetDB(adb);
            /** 初始化系统模块(默认文件路径)*/
            WQAPlatform.GetInstance().InitSystem(this.getFilesDir().getPath());
            /** 关闭设备定标LOG*/
            DevLog.Instance().SetLogSwitch(false);
            /** 初始化错误提示信息*/
            LogCenter.Instance().RegisterFaultEvent(new EventListener<Level>() {
                @Override
                public void recevieEvent(Event<Level> event) {
                    WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
                        ErrorExecutor.PrintErrorInfo(event.Info().toString());
                    });
                }
            });

            /**初始化Android串口*/
            AndroidIO.GetInstance().InitIO();
        } catch (Exception ex) {
            ErrorExecutor.PrintErrorInfo("初始化失败:" + ex.getMessage().toString());
        }
    }

    // <editor-fold desc="消息中心">
    //activity 消息
    private int HIDSCREEN = 0x01;
    private int TIME = 0x02;
    private Handler messagehandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == TIME) {
                updatetime();
            }
            if (msg.what == HIDSCREEN) {
                if (ishide) {
                    if (showNavigation() != -1) {
                        ishide = false;
                    }
                } else {
                    if (hideNavigation() != -1) {
                        ishide = true;
                    }
                }
            }
        }
    };
    // </editor-fold>

    // <editor-fold desc="全屏切换">
    private boolean ishide = true;

    private void initHide() {
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener((View view)->{
//                messagehandler.sendEmptyMessage(HIDSCREEN);
//        });
    }

    private int hideNavigation() {
        int ishide = -1;
        try {
            String command;
            command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib service call activity 42 s16 com.android.systemui";
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            ishide = proc.waitFor();
            System.out.println("ok");
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            ishide = -1;
        }
        return ishide;
    }

    private int showNavigation() {
        int isshow;
        try {
            String command;
            command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib am startservice -n com.android.systemui/.SystemUIService";
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            isshow = proc.waitFor();
            System.out.println("ok");
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            isshow = -1;
        }
        return isshow;
    }
    // </editor-fold>

    // <editor-fold desc="系统时间显示">
    private void initTime() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                messagehandler.sendEmptyMessage(TIME);
            }
        }, 0, 1000);
    }

    private void updatetime() {
        TextView time_display = findViewById(R.id.nqmain_time);
        time_display.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
    // </editor-fold>

    // <editor-fold desc="控制和设备列表切换">
    fragment_monitor_main dev_main = new fragment_monitor_main();
    fragment_control_main control_main = new fragment_control_main();
    private boolean dev_view = true;

    private void init_switch() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.nqmain_area, dev_main);
        fragmentTransaction.add(R.id.nqmain_area, control_main);
        fragmentTransaction.hide(control_main);
        fragmentTransaction.show(dev_main).commit();

        TextView tool_home = findViewById(R.id.nqmain_home);
        tool_home.setOnClickListener((View view) -> {
//            System.out.println(((ActivityManager)getSystemService(activity_nqmain.this.ACTIVITY_SERVICE)).getMemoryClass()+ "************************");
            if (dev_view) {
                Security.CheckPassword(activity_nqmain.this, new Handler() {
                    public void handleMessage(Message msg) {
                        if(msg.what == Security.CHECK_OK) {
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.hide(dev_main);
                            fragmentTransaction.show(control_main).commit();
                        }else if(msg.what == Security.START_BACKDOOR){
                            messagehandler.sendEmptyMessage(HIDSCREEN);
                        }
                    }
                });
            } else {
                FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction1.hide(control_main);
                fragmentTransaction1.show(dev_main).commit();
            }
            dev_view = !dev_view;

        });

        hideNavigation();
    }
    // </editor-fold>
}
