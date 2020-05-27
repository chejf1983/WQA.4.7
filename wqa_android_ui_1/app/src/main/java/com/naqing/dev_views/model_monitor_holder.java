package com.naqing.dev_views;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.naqing.common.Security;
import com.naqing.wqa_android_ui_1.R;

import wqa.control.common.SDisplayData;
import wqa.dev.data.SDataElement;

public class model_monitor_holder {
    private Activity parentActivity;
    private View deviceView;
    public String data_name;
    private model_dev_view control;
    public boolean isVisable = true;

    public model_monitor_holder(Activity parent, model_dev_view control, String data_name) {
        this.parentActivity = parent;
        this.data_name = data_name;
        this.control = control;
        this.InitView(parent);
    }

    public void InitView(Activity parent) {
        this.parentActivity = parent;
        /* Init Device View */
        InitViewComponents();
    }

    public View getView() {
        return this.deviceView;
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
                    control.showConfigActivity(parentActivity);
                }
            });
        });
    }
    // </editor-fold>

    // <editor-fold desc="刷新界面">
    public void updateData(SDisplayData data) {
//        DevID id = this.control.GetDevID();
//        md_name.setText("- "+ CDevDataTable.GetInstance().namemap.get(id.dev_type).dev_name_ch + "[" + id.dev_addr + "] -");
        md_name.setText(this.data_name);
        SDataElement maindata = data.GetDataElement(this.data_name);
        md_range.setText(maindata.range_info);
        md_main_data.setText(maindata.mainData + "");
        md_temper.setText(data.GetDataElement("温度").mainData + "°C");
    }
    // </editor-fold>

}