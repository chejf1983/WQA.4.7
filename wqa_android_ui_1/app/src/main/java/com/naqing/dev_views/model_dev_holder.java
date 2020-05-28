package com.naqing.dev_views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.naqing.common.IosAlertDialog;
import com.naqing.wqa_android_ui_1.R;

import wqa.control.common.DevControl;
import wqa.system.WQAPlatform;

public class model_dev_holder {
    private View deviceView;
    private model_dev_view devview;
    private Activity parentActivity;


    public model_dev_holder(Context parent, model_dev_view control) {
        this.devview = control;
        parentActivity = (Activity) parent;
        LayoutInflater from = LayoutInflater.from(parent);
        this.deviceView = from.inflate(R.layout.model_dev, null);

//        this.deviceView.setBackgroundColor(Color.RED);
        /* Init Device View */
        InitViewComponents();
    }

    //初始化视图组件
    private void InitViewComponents() {
        TextView dev_name = this.deviceView.findViewById(R.id.md_dev_info);

        String stype = devview.control.GetProType().contentEquals("MIGP") ? "*" : "";
        dev_name.setText(stype + devview.control.ToString());
        dev_name.setOnClickListener((View view)->{
            devview.showConfigActivity(parentActivity);
        });

        TextView dev_del = this.deviceView.findViewById(R.id.md_dev_del);
        dev_del.setOnClickListener((View view) -> {
            new IosAlertDialog(parentActivity).builder().setMsg("是否确认删除探头？")
                    .setPositiveButton("确认", (View v) -> {
                        WQAPlatform.GetInstance().GetManager().DeleteDevControl(devview.control);
                    }).setNegativeButton("取消", (View v) -> {}).show();
        });
    }

    public model_dev_view getCurrentControl() {
        return this.devview;
    }

    public View getDeviceView() {
        return this.deviceView;
    }

    public void initState(DevControl.ControlState state, String info) {
        View viewById = deviceView.findViewById(R.id.md_dev_state);
        switch (state) {
            case CONNECT:
                TextView dev_name = this.deviceView.findViewById(R.id.md_dev_info);
                String stype = devview.control.GetProType().contentEquals("MIGP") ? "*" : "";
                dev_name.setText(stype + devview.control.ToString());
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
