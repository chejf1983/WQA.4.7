package com.naqing.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.naqing.monitor.activity_dev_config;
import com.naqing.wqa_android_ui_1.R;

import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import wqa.control.common.DevControl;
import wqa.dev.data.SDevInfo;
import wqa.dev.intf.IDevice;
import wqa.system.WQAPlatform;

public class model_dev_holder{
    private View deviceView;
    private DevControl control;
    private Activity parentActivity;


    public model_dev_holder(Context parent, DevControl control){
        this.control = control;
        parentActivity = (Activity) parent;
        LayoutInflater from = LayoutInflater.from(parent);
        this.deviceView = from.inflate(R.layout.model_dev,null);

//        this.deviceView.setBackgroundColor(Color.RED);
        /* Init Device View */
        InitViewComponents();
    }

    //初始化视图组件
    private void InitViewComponents(){
        TextView dev_name = this.deviceView.findViewById(R.id.md_dev_info);
        String stype = control.GetProType().contentEquals("MIGP") ? "*" : "";
        dev_name.setText(stype + control.ToString());

        TextView dev_del = this.deviceView.findViewById(R.id.md_dev_del);
        dev_del.setOnClickListener((View view)->{
            WQAPlatform.GetInstance().GetManager().DeleteDevControl(this.control);
        });

        /** 触发状态响应*/
        control.StateChange.RegeditListener(new EventListener<DevControl.ControlState>() {
            @Override
            public void recevieEvent(Event<DevControl.ControlState> event) {
                Message msg = new Message();
                msg.what = STATE;
                msg.obj = event;
                messagehandler.sendMessage(msg);
            }
        });
        this.initState(control.GetState(), "");
    }

    public DevControl getCurrentControl(){
        return this.control;
    }

    public View getDeviceView(){
        return this.deviceView;
    }

    // <editor-fold desc="显示配置界面">
    private void showConfigActivity() {
        /**进入配置状态 */
        activity_dev_config.configbean = this.control.StartConfig();
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

    // <editor-fold desc="消息中心">
    //activity 消息
    private int STATE = 0x01;
    private Handler messagehandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == STATE) {
                Event<DevControl.ControlState> event = (Event<DevControl.ControlState>)msg.obj;
                initState(event.GetEvent(), event.Info().toString());
            }
        }
    };
    // </editor-fold>

    private void initState(DevControl.ControlState state, String info){
        View viewById = deviceView.findViewById(R.id.md_dev_state);
        switch (state){
            case CONNECT:
                viewById.setBackground(parentActivity.getResources().getDrawable(R.drawable.circle_green));
                break;
            case ALARM:
                viewById.setBackground(parentActivity.getResources().getDrawable(R.drawable.circle_yellow));
                break;
            case DISCONNECT:
                viewById.setBackground(parentActivity.getResources().getDrawable(R.drawable.circle_grey));
                break;
            case CONFIG:
                viewById.setBackground(parentActivity.getResources().getDrawable(R.drawable.circle_blue));
                break;
        }
    }
}
