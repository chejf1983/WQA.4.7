package com.naqing.monitor;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.naqing.common.TableElement;
import com.naqing.wqa_android_ui_1.R;

import java.util.ArrayList;
import java.util.logging.Level;

import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import nahon.comm.faultsystem.LogCenter;
import wqa.control.common.SDisplayData;
import wqa.control.config.DevCalConfig;
import wqa.dev.data.SDataElement;


public class fragment_dev_config_cal extends Fragment {

    private DevCalConfig cal_config;
    private View root;
    private Activity parent;

    // <editor-fold desc="初始化">
    public fragment_dev_config_cal(DevCalConfig cal_config) {
        this.cal_config = cal_config;
    }

    // <editor-fold desc="生命周期">
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parent = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_dev_config_cal, container, false);

        this.initView();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        cal_config.SetStartGetData(true);
    }

    @Override
    public void onStop() {
        super.onStop();

        cal_config.SetStartGetData(false);
    }
    // </editor-fold>

    // <editor-fold desc="刷新界面">
    private void initView() {
        this.init_cal_type();

        if (eventListener == null) {
            eventListener = new EventListener<SDisplayData>() {
                @Override
                public void recevieEvent(Event<SDisplayData> event) {
                    Message msg = new Message();
                    msg.what = DATA;
                    msg.obj = event.GetEvent();
                    messagehandler.sendMessage(msg);
                }
            };
            this.cal_config.CalDataEvent.RegeditListener(eventListener);
        }

        root.findViewById(R.id.dcc_set).setOnClickListener((View view) -> {
            start_cal();
        });
    }

    public void reset_calconfig(DevCalConfig cal_config) {
        this.cal_config = cal_config;
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

    private EventListener<SDisplayData> eventListener;

    /**
     * 定标采集数据响应
     */
    private void updateData(SDisplayData data) {
        if (this.selected_type == null) {
            return;
        }
        /**获取指定数据*/
        SDataElement maindata = data.GetDataElement(this.selected_type);
        SDataElement maindata_ora = data.GetOraDataElement(this.selected_type);
        SDataElement temper = data.GetDataElement("温度");

        /**刷新定标值*/
        for (row_line line : lines) {
            if (line.swt) {
                line.std.setText(maindata.mainData + "");
                line.ora.setText(maindata_ora.mainData + "");
            }
        }
        /** 刷新当前值*/
        ((TextView) root.findViewById(R.id.dcc_ctempr)).setText(temper.mainData + "°C");
        ((TextView) root.findViewById(R.id.dcc_cvalue)).setText(maindata.mainData + "");
    }
    // </editor-fold>

    // <editor-fold desc="初始化定标类型">
    /**
     * 选中的类型
     */
    private String selected_type = "";

    private void init_cal_type() {
        ((TextView) root.findViewById(R.id.dct_title)).setText("参数校准");
        /**添加支持定标的种类*/
        TextView sp_devtype = root.findViewById(R.id.dcc_cal_type);
        //获取种类列表
        String[] typelist = new String[cal_config.GetCalType().length];
        for (int i = 0; i < typelist.length; i++) {
            typelist[i] = cal_config.GetCalType()[i];
        }
        //设置默认值
        if (typelist.length > 0) {
            selected_type = typelist[0];
            /**设置选中*/
            TableElement.initSelectView(sp_devtype, parent, selected_type, typelist, (View view) -> {
                selected_type = sp_devtype.getText().toString();
                /**刷新定标控制行*/
                init_cal_num(selected_type);
            });
            /**刷新定标控制行*/
            init_cal_num(selected_type);
        }
    }
    // </editor-fold>

    // <editor-fold desc="初始化定标点数">
    private int selected_num = 1;

    private void init_cal_num(String s_type) {
        /**添加支持的定标点个数*/
        TextView sp_num = root.findViewById(R.id.dcc_cal_num);
        int num = cal_config.GetCalMaxNum(s_type);
        if(num > 0) {
            String[] num_list = new String[num];
            for (int i = 0; i < num_list.length; i++) {
                num_list[i] = i + 1 + "";
            }
            /**设置选中处理*/
            TableElement.initSelectView(sp_num, parent, selected_num + "", num_list, (View view) -> {
                selected_num = Integer.valueOf(sp_num.getText().toString());
                /**刷新定标控制行*/
                init_cal_lines(selected_num);
            });
            init_cal_lines(selected_num);
        }
    }
    // </editor-fold>

    // <editor-fold desc="初始化定标行">
    private void init_cal_lines(int num) {
        /** 清除表格项*/
        LinearLayout layout = root.findViewById(R.id.dcc_list);
        layout.removeAllViews();
        lines.clear();

        /** 根据当前定标点数，添加定标控制行*/
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 45);
        lp.setMargins(0, 10, 0, 0);
        for (int i = 1; i <= num; i++) {
            layout.addView(crate_calline(i), lp);
        }
    }

    /**
     * 初始化定标控制行
     */
    private View crate_calline(int index) {
        /** 新建一行*/
        LinearLayout row = new LinearLayout(parent);
        row.setWeightSum(5);
        row.setOrientation(TableRow.HORIZONTAL);

        //保存line
        row_line line = new row_line();
        this.lines.add(line);

        /** 添加名称项目*/
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        /** 添加标文字*/
        lp.weight = 1;
        row.addView(TableElement.createTextView(parent, "标" + index + ":"), lp);

        /** 添加原始值输入框*/
        line.std = (TextView) TableElement.createInputView(parent, "标" + index + ":", "", null);
        if (selected_type.contentEquals("溶解氧")) {
            String[] info = new String[]{"饱和氧", "无氧"};
            row.addView(TableElement.createTextView(parent, info[index]), lp);
        } else {
            row.addView(line.std, lp);
        }
        /** 添加原始值标签*/
        row.addView(TableElement.createTextView(parent, "原始值:"), lp);
        /** 添加原始值输入框*/
        line.ora = (TextView) TableElement.createInputView(parent, "原始值:", "", null);
        row.addView(line.ora, lp);
        /** 添加开关按钮*/
        row.addView(TableElement.createSwitchView(parent, line.swt, (CompoundButton var1, boolean var2) -> {
            line.swt = var2;
        }), lp);
        return row;
    }

    private ArrayList<row_line> lines = new ArrayList<>();

    private class row_line {
        TextView std, ora;
        boolean swt = true;
    }
    // </editor-fold>

    // <editor-fold desc="开始效准">
    private void start_cal() {
        float[] stddata = new float[this.selected_num];
        float[] oradata = new float[this.selected_num];
        for (int i = 0; i < this.lines.size(); i++) {
            if (this.lines.get(i).swt) {
                LogCenter.Instance().ShowMessBox(Level.INFO, "标" + (i + 1) + "还未就绪");
                return;
            } else {
                try {
                    stddata[i] = Float.valueOf(lines.get(i).std.getText().toString());
                    oradata[i] = Float.valueOf(lines.get(i).ora.getText().toString());
                } catch (Exception ex) {
                    LogCenter.Instance().SendFaultReport(Level.INFO, ex.getMessage());
                    return;
                }
            }
        }
        this.cal_config.CalParameter(this.selected_type, oradata, stddata);
    }
    // </editor-fold>
}
