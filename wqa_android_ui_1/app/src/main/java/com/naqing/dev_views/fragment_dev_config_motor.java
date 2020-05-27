package com.naqing.dev_views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.naqing.common.TableElement;
import com.naqing.wqa_android_ui_1.R;

import wqa.control.config.DevMotorConfig;
import wqa.dev.data.SMotorParameter;
import wqa.dev.intf.SConfigItem;

public class fragment_dev_config_motor extends Fragment {
    private View root;
    private Activity parent;
    private DevMotorConfig motor_config;

    public fragment_dev_config_motor(DevMotorConfig motor_config) {
        this.motor_config = motor_config;
    }

    public void reset_calconfig(DevMotorConfig cal_config) {
        this.motor_config = cal_config;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.parent = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_config_table, container, false);

        initView();

        return root;
    }

    // <editor-fold desc="创建动态列表">
    private SMotorParameter motor_pars;

    private void initView() {
        motor_pars = motor_config.GetMotoPara();

        ((TextView)root.findViewById(R.id.dct_title)).setText("电机控制");
        this.createTable();

        /** 刷新列表*/
        root.findViewById(R.id.dct_read).setOnClickListener((View view) -> {
            motor_pars = motor_config.GetMotoPara();
            this.createTable();
        });

        /** 设置参数*/
        root.findViewById(R.id.dct_set).setOnClickListener((View view) -> {
            this.motor_config.SetMotoPara(motor_pars);
        });
    }

    /**
     * 创建表格
     */
    private void createTable() {
        /** 清除表格项*/
        LinearLayout layout = root.findViewById(R.id.dct_list);
        layout.removeAllViews();

        /** 设置行高*/
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50);

        /** 添加清洗模式*/
        layout.addView( TableElement.CreateSwitchViewLine(parent, "清洗刷自动模式", motor_pars.mode == SMotorParameter.CleanMode.Auto, (CompoundButton var1, boolean var2) -> {
            motor_pars.mode = var2 ? SMotorParameter.CleanMode.Auto : SMotorParameter.CleanMode.Manu;
            createTable();
        }), lp);

        /** 添参数*/
        SConfigItem[] configItems;
        if (motor_pars.mode == SMotorParameter.CleanMode.Auto) {
            configItems = motor_pars.auto_config;
        } else {
            configItems = motor_pars.manu_config;
        }
        for (int i = 0; i < configItems.length; i++) {
            View view = TableElement.CreateLine(parent, configItems[i]);
            if (i % 2 == 0) {
                /** 偶数层替换背景*/
                view.setBackgroundColor(Color.parseColor("#001942"));
            }
            layout.addView(view, lp);
        }

        /** 手动清洗*/
        if (motor_pars.mode == SMotorParameter.CleanMode.Manu) {
            configItems = motor_pars.auto_config;
            /** 添加手动清洗按钮*/
            View view = TableElement.CreateButtonViewLine(parent, "手动清洗", "启动", (View tview) -> {
                motor_config.StartManual();
            });
            if (configItems.length % 2 == 0) {
                /** 偶数层替换背景*/
                view.setBackgroundColor(Color.parseColor("#001942"));
            }
            layout.addView(view, lp);
        }
    }
    // </editor-fold>
}
