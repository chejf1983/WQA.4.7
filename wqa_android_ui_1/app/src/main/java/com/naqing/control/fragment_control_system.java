package com.naqing.control;

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
import androidx.fragment.app.Fragment;

import com.naqing.common.Security;
import com.naqing.common.TableElement;
import com.naqing.wqa_android_ui_1.R;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import nahon.comm.faultsystem.LogCenter;
import wqa.control.DB.DBHelper;
import wqa.control.DB.IDataHelper;
import wqa.system.WQAPlatform;

public class fragment_control_system extends Fragment {
    private View root;
    private Activity parent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.parent = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_config_table2, container, false);

        initView();

        return root;
    }

    // <editor-fold desc="初始化界面">
    private void initView() {
        ((TextView) root.findViewById(R.id.dct_title)).setText("系统设置");

        /** 清除表格项*/
        LinearLayout layout = root.findViewById(R.id.dct_list);
        layout.removeAllViews();

        start = false;

        AddLine(layout, TableElement.CreateDateViewLine(parent, "-系统时间-", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), (View view) -> {
            SetTime(((TextView)view).getText().toString());
        }));
        AddLine(layout, TableElement.CreateSwitchViewLine(parent, "-密码开关-", Security.Instance().IsEnable(), (CompoundButton var1, boolean var2) -> {
            Security.Instance().EnableSecurity(var2);
        }));

        AddLine(layout, TableElement.CreateTextViewLine(parent, "",""));
        DBHelper data_db = WQAPlatform.GetInstance().GetDBHelperFactory();
        AddLine(layout, TableElement.CreateInputViewLine(parent, "-数据库保存时间(秒)-", data_db.GetCollectTimeBySecond() + "", (View var1)->{
            data_db.SetCollectTime(Integer.valueOf(((TextView)var1).getText().toString()));
        }));

        View vdb_size = TableElement.CreateTextViewLine(parent, "数据库大小", WQAPlatform.GetInstance().GetDBHelperFactory().GetDBFix().GetDBSize());
        AddLine(layout, vdb_size);

        AddLine(layout, TableElement.CreateInputViewLine(parent, "数据库显示密度", fragment_control_history.MaxPointNum + "", (View view)->{
            fragment_control_history.MaxPointNum = Integer.valueOf(((TextView)view).getText().toString());
        }));
    }

    private boolean start = false;
    private void AddLine(LinearLayout layout, View view) {
        /** 设置行高*/
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50);
        if (start) {
//            /** 偶数层替换背景*/
            view.setBackgroundColor(Color.parseColor("#001942"));
        }
        layout.addView(view, lp);
        start = !start;
    }
    // </editor-fold>

    private void SetTime(String datetime) {
        try {
            datetime = new SimpleDateFormat("yyyyMMdd.HHmmss").format( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime));
            Process process = Runtime.getRuntime().exec("su");
//            String datetime = "20131023.112800"; //测试的设置的时间【时间格式 yyyyMMdd.HHmmss】
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT\n");
            os.writeBytes("/system/bin/date -s " + datetime + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (Exception e) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, e.getMessage());
        }
    }
}
