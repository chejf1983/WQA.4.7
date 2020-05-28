package com.naqing.dev_views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.naqing.control.fragment_control_dev;
import com.naqing.wqa_android_ui_1.fragment_monitor_main;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import wqa.control.DB.AlarmRecord;
import wqa.control.common.DevControl;
import wqa.control.common.SDisplayData;
import wqa.control.data.IMainProcess;
import wqa.system.WQAPlatform;

public class model_dev_view {
    public DevControl control;

    private model_dev_holder dev_holder;
    private fragment_control_dev devHolderAdapter;

    private model_monitor_holder[] monitors;
    private fragment_monitor_main monitorHolderAdapter;

    public model_dev_view(DevControl control, fragment_control_dev devHolderAdapter, fragment_monitor_main monitorHolderAdapter) {
        this.control = control;
        this.devHolderAdapter = devHolderAdapter;
        this.dev_holder = devHolderAdapter.CreateHoler(this);

        this.monitorHolderAdapter = monitorHolderAdapter;
        this.monitors = monitorHolderAdapter.CreateMonitor(this);
        this.RefreshVisable();

        this.control.StateChange.RegeditListener(new EventListener<DevControl.ControlState>() {
            @Override
            public void recevieEvent(Event<DevControl.ControlState> event) {
                Message msg = new Message();
                msg.what = STATE;
                msg.obj = event;
                messagehandler.sendMessage(msg);
            }
        });
        dev_holder.initState(control.GetState(), "");


        /** 注册数据采集事件*/
        control.GetCollector().DataEvent.RegeditListener(new EventListener<SDisplayData>() {
            @Override
            public void recevieEvent(Event<SDisplayData> event) {
                Message msg = new Message();
                msg.obj = event.GetEvent();
                msg.what = DATA;
                messagehandler.sendMessage(msg);
            }
        });
    }

    public void Close() {
        WQAPlatform.GetInstance().GetDBHelperFactory().GetAlarmDB().DeleteAlarm(control.GetDevID(), new Date());
        devHolderAdapter.DelControl(dev_holder);
        for (model_monitor_holder monitor : monitors) {
            monitorHolderAdapter.DelDevice(monitor);
        }
    }

    private int STATE = 0x01;
    private int DATA = 0x02;
    //activity 消息
    private Handler messagehandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == STATE) {
                Event<DevControl.ControlState> event = (Event<DevControl.ControlState>) msg.obj;
                dev_holder.initState(event.GetEvent(), event.Info().toString());
            }

            if (msg.what == DATA) {
                for (model_monitor_holder holder : monitors) {
                    holder.updateData((SDisplayData) msg.obj);
                }
            }
        }
    };


    // <editor-fold desc="设置数据显示类型">
    public model_monitor_holder[] GetMonitors() {
        return this.monitors;
    }

    public void RefreshVisable() {
        for (model_monitor_holder monitor : this.monitors) {
            if (monitor.isVisable) {
                monitorHolderAdapter.AddDevice(monitor);
            }else{
                monitorHolderAdapter.DelDevice(monitor);
            }
        }
    }
    // </editor-fold>

    // <editor-fold desc="显示配置界面">
    public void showConfigActivity(Activity parentActivity) {
        /**进入配置状态 */
        activity_dev_config.configbean = this.control.StartConfig();
        activity_dev_config.dev_view = this;
        if(activity_dev_config.configbean == null){
            return;
        }

//        Bundle bundle = new Bundle();
//        bundle.putString("name", this.control.toString());
        Intent intent = new Intent(parentActivity, activity_dev_config.class);
        intent.putExtras(new Bundle());
        intent.putExtra("name", control.ToString());
        parentActivity.startActivityForResult(intent, 21);
    }
    // </editor-fold>
}
