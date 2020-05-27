package com.naqing.wqa_android_ui_1;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import com.naqing.common.ErrorExecutor;
import com.naqing.common.NQProcessDialog2;
import com.naqing.control.fragment_control_dev;
import com.naqing.dev_views.model_dev_view;
import com.naqing.io.AndroidIO;

import java.util.ArrayList;
import java.util.concurrent.Future;

import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import wqa.bill.io.ShareIO;
import wqa.control.common.DevControl;
import wqa.control.common.DevControlManager;
import wqa.system.WQAPlatform;

public class model_dev_view_manager {
    private EventListener<DevControlManager.DevNumChange> eventListener;

    private static model_dev_view_manager instance;
    public static model_dev_view_manager Instance(){
        if(instance == null){
            instance = new model_dev_view_manager();
        }
        return instance;
    }

    private model_dev_view_manager() {
        /** 响应设备添加删除事件 */
        if (eventListener == null) {
            eventListener = new EventListener<DevControlManager.DevNumChange>() {
                @Override
                public void recevieEvent(Event<DevControlManager.DevNumChange> event) {
                    Message msg = new Message();
                    msg.obj = event.Info();
                    switch (event.GetEvent()) {
                        case ADD:
                            msg.what = ADD;
                            break;
                        case DEL:
                            msg.what = DEL;
                            break;
                    }
                    messagehandler.sendMessage(msg);
                }
            };
            WQAPlatform.GetInstance().GetManager().StateChange.RegeditListener(eventListener);
        }
    }

    private fragment_control_dev devHolderAdapter;
    public void SetDevHolderAdapter(fragment_control_dev adapter){
        this.devHolderAdapter = adapter;
    }
    private fragment_monitor_main monitorHolderAdapter;
    public void SetMonHolderAdapter(fragment_monitor_main adapter){
        this.monitorHolderAdapter = adapter;
    }

    // <editor-fold desc="添加删除设备">
    private ArrayList<model_dev_view> dev_views = new ArrayList<>();
    private int ADD = 0;
    private int DEL = 1;
    private Handler messagehandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == ADD) {
                AddControl((DevControl) msg.obj);
            }
            if (msg.what == DEL) {
                DelControl((DevControl) msg.obj);
            }
        }
    };

    private void AddControl(DevControl control){
        dev_views.add(new model_dev_view(control, this.devHolderAdapter, monitorHolderAdapter));
    }

    private void DelControl(DevControl control){
        for(model_dev_view view : dev_views){
            if(view.control == control){
                view.Close();
                this.dev_views.remove(view);
                return;
            }
        }
    }
    // </editor-fold>
}
