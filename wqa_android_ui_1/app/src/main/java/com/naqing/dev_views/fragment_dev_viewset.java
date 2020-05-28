package com.naqing.dev_views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.naqing.common.IosAlertDialog;
import com.naqing.common.TableElement;
import com.naqing.wqa_android_ui_1.R;

public class fragment_dev_viewset extends Fragment {
    private model_dev_view config_table;
    private View root;
    private Activity parent;

    public fragment_dev_viewset(model_dev_view config_table) {
        this.config_table = config_table;
    }

    // <editor-fold desc="初始化">
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parent = (Activity) context;
    }

    public void reset_calconfig(model_dev_view config_table) {
        this.config_table = config_table;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_config_table2, container, false);

        /** 设置列表名称*/
        ((TextView) root.findViewById(R.id.dct_title)).setText("显示设置");

        /** 创建表格*/ /** 清除表格项*/
        LinearLayout layout = root.findViewById(R.id.dct_list);
        layout.removeAllViews();

        /** 添加行*/
        model_monitor_holder[] tables = config_table.GetMonitors();
        for (int i = 0; i < tables.length; i++) {
            /** 设置行高*/
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50);
            model_monitor_holder table = tables[i];
            View view = TableElement.CreateSwitchViewLine(parent, table.data_name, table.isVisable, (CompoundButton var1, boolean var2) -> {
                table.isVisable = var2;

                /** 检查是否所有数据都隐藏了*/
                boolean ret = false;
                for (model_monitor_holder holder : config_table.GetMonitors()) {
                    ret |= holder.isVisable;
                }

                if (ret) {
                    config_table.RefreshVisable();
                } else {
                    new IosAlertDialog(parent).builder().setTitle("提示")
                            .setMsg("确认所有数据都不显示吗? 如果确认,需要在传感器界面重新设置数据显示")
                            .setPositiveButton("确认", (View tt) -> {
                                config_table.RefreshVisable();
                            })
                            .setNegativeButton("取消", (View tt) -> {
                                ((ToggleButton)var1).setChecked(true);
                                table.isVisable = true;
                            }).show();
                }
            });
            if (i % 2 == 0) {
                /** 偶数层替换背景*/
                view.setBackgroundColor(Color.parseColor("#001942"));
            }
            layout.addView(view, lp);
        }

        return root;
    }
}
