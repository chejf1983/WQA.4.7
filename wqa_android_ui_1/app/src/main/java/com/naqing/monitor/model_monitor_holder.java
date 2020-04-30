package com.naqing.monitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.naqing.common.Security;
import com.naqing.wqa_android_ui_1.R;

import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import wqa.adapter.factory.CDevDataTable;
import wqa.control.common.DevControl;
import wqa.control.common.SDisplayData;
import wqa.control.data.DevID;
import wqa.dev.data.SDataElement;

public class model_monitor_holder {
    private Activity parentActivity;
    private View deviceView;
    private String data_name;
    private DevControl control;

    public model_monitor_holder(Activity parent, DevControl control, String data_name){
        this.parentActivity = parent;
        this.data_name = data_name;
        this.control = control;
        this.InitView(parent);
    }

    public void InitView(Activity parent){
        this.parentActivity = parent;
        /* Init Device View */
        InitViewComponents();
    }

    public View getView() {
        return this.deviceView;
    }

    public DevControl getDevControl() {
        return control;
    }

    // <editor-fold desc="初始化界面">
    private View config;
    private TextView md_name;
    private TextView md_main_data;
    private TextView md_range;
    private TextView md_temper;
    //初始化视图组件
    private void InitViewComponents() {
        LayoutInflater from = LayoutInflater.from(parentActivity);
        this.deviceView = from.inflate(R.layout.model_monitor, null);
        /** 初始化控件*/
        config = this.deviceView.findViewById(R.id.m_monitor_config_button);
        md_name = this.deviceView.findViewById(R.id.m_monitor_name);
        md_main_data = this.deviceView.findViewById(R.id.m_monitor_data);
        md_range = this.deviceView.findViewById(R.id.m_monitor_range);
        md_temper = this.deviceView.findViewById(R.id.m_monitor_temper);

        /** 显示配置界面*/
        config.setOnClickListener((View view) -> {
            Security.CheckPassword(parentActivity, new Handler() {
                public void handleMessage(Message msg) {
                    showConfigActivity();
                }
            });
        });

        /** 注册数据采集事件*/
        if (eventListener == null) {
            eventListener = new EventListener<SDisplayData>() {
                @Override
                public void recevieEvent(Event<SDisplayData> event) {
                    Message msg = new Message();
                    msg.obj = event.GetEvent();
                    msg.what = DATA;
                    messagehandler.sendMessage(msg);
                }
            };
            control.GetCollector().DataEvent.RegeditListener(eventListener);
        }
    }
    // </editor-fold>

    // <editor-fold desc="消息中心">
    //activity 消息
    private int DATA = 0x02;
    private Handler messagehandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == DATA) {
                updateData((SDisplayData) msg.obj);
            }
        }
    };
    // </editor-fold>

    // <editor-fold desc="刷新界面">
    private EventListener<SDisplayData> eventListener;

    private void updateData(SDisplayData data) {
        DevID id = this.control.GetDevID();
        md_name.setText("- "+ CDevDataTable.GetInstance().namemap.get(id.dev_type).dev_name_ch + "[" + id.dev_addr + "] -");

        SDataElement maindata = data.GetDataElement(this.data_name);
        md_range.setText(maindata.range_info);
        md_main_data.setText(maindata.mainData + "");
        md_temper.setText(data.GetDataElement("温度").mainData + "°C");
    }
    // </editor-fold>

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
}
