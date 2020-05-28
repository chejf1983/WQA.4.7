package com.naqing.dev_views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.naqing.common.TableElement;
import com.naqing.wqa_android_ui_1.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import wqa.control.DB.AlarmRecord;
import wqa.control.data.IMainProcess;
import wqa.system.WQAPlatform;

public class fragment_dev_alarm extends Fragment {
    private model_dev_view config_table;
    private ArrayList<AlarmRecord> alarmRecords = new ArrayList<>();
    private View root;
    private Activity parent;

    public fragment_dev_alarm(model_dev_view config_table) {
        this.config_table = config_table;
    }

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
        root = inflater.inflate(R.layout.fragment_config_table, container, false);

        InitTable();

        /** 刷新列表*/
        root.findViewById(R.id.dct_read).setOnClickListener((View view) -> {
            ReadAlarm();
        });

        /** 设置参数*/
        TextView view = root.findViewById(R.id.dct_set);
        view.setText("删除");
        root.findViewById(R.id.dct_set).setOnClickListener((View v) -> {
            DelAlarm();
        });
        return root;
    }

    private boolean signal = false;
    private void DelAlarm(){
        if(signal){
            return;
        }
        signal = true;
        WQAPlatform.GetInstance().GetDBHelperFactory().GetAlarmDB().DeleteAlarm(config_table.control.GetDevID(), new Date());
        alarmRecords.clear();
        messagehandler.sendEmptyMessage(READ);
    }
    private void ReadAlarm() {
        if(signal){
            return;
        }
        signal = true;
        WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
            WQAPlatform.GetInstance().GetDBHelperFactory().GetAlarmDB().SearchAlarmInfo(config_table.control.GetDevID(), null, new Date(), new IMainProcess<AlarmRecord[]>() {
                @Override
                public void SetValue(float v) {

                }

                @Override
                public void Finish(AlarmRecord[] records) {
                    alarmRecords.clear();
                    alarmRecords.addAll(Arrays.asList(records));
                    messagehandler.sendEmptyMessage(READ);
                }
            });
        });
    }

    // <editor-fold desc="消息中心">
    //activity 消息
    private int READ = 0x01;
    private Handler messagehandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == READ) {
                InitTable();
            }
        }
    };
    // </editor-fold>

    // <editor-fold desc="显示界面">
    private String TIMEFORMATE = "yyyy-MM-dd HH:mm:ss";

    private void InitTable() {
        /** 清除表格项*/
        LinearLayout layout = root.findViewById(R.id.dct_list);
        layout.removeAllViews();

        /** 设置行高*/
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50);

        /** 添加表头*/
        String[] columns = new String[]{
                "时间",
                "报警码",
                "报警信息"};
        View column_name = CreateLine(parent, columns);
        layout.addView(column_name, lp);

        /** 添加行*/
        for (int i = 0; i < this.alarmRecords.size(); i++) {
            columns = new String[]{
                    new SimpleDateFormat(TIMEFORMATE).format(alarmRecords.get(i).time),
                    alarmRecords.get(i).alarm + "",
                    alarmRecords.get(i).alarm_info};
            View view = CreateLine(parent, columns);
            if (i % 2 == 0) {
                /** 偶数层替换背景*/
                view.setBackgroundColor(Color.parseColor("#001942"));
            }
            layout.addView(view, lp);
        }

        signal = false;
    }

    /**
     * 创建输入文本
     */
    public static View CreateLine(Activity activity, String[] data_name) {
        /** 新建一行*/
        LinearLayout row = TableElement.createLine(activity);
        row.setWeightSum(6);

        /** 添加名称项目*/
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        lp.weight = 2;
        /** 添加名称项目*/
        row.addView(TableElement.createTextView(activity, data_name[0]), lp);
        lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        lp.weight = 1;
        /** 添加名称项目*/
        row.addView(TableElement.createTextView(activity, data_name[1]), lp);
        lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        lp.weight = 3;
        /** 添加名称项目*/
        row.addView(TableElement.createTextView(activity, data_name[2]), lp);

        return row;
    }
    // </editor-fold>
}
