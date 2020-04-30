package com.naqing.control;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.naqing.common.TableElement;
import com.naqing.io.AndroidIO;
import com.naqing.io.ComManager;
import com.naqing.wqa_android_ui_1.R;

import java.util.logging.Level;

import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.SIOInfo;


public class fragment_control_io extends Fragment {
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

    private SIOInfo sioInfo;

    private void initView() {
        ((TextView) root.findViewById(R.id.dct_title)).setText("通信设置");

        /** 清除表格项*/
        LinearLayout layout = root.findViewById(R.id.dct_list);
        layout.removeAllViews();

        start = false;
        AddLine(layout, TableElement.CreateTextViewLine(parent, "探头端口设置", ""));
        sioInfo = new SIOInfo(AndroidIO.GetInstance().GetDevIO().GetDevConfigIO().GetConnectInfo());


        AddLine(layout, TableElement.CreateTextViewLine(parent, "端口名称", sioInfo.par[0]));
        AddLine(layout, TableElement.CreateSelectViewLine(parent, "波特率", sioInfo.par[1], ComManager.BandRate, (View view) -> {
            sioInfo.par[1] = ((TextView) view).getText().toString();
            try {
                AndroidIO.GetInstance().GetComManager().ChangeBandRate(AndroidIO.GetInstance().GetDevIO().GetDevConfigIO(), sioInfo.par[1]);
                AndroidIO.GetInstance().GetDevIO().GetDevConfigIO().Close();
                AndroidIO.GetInstance().GetDevIO().GetDevConfigIO().Open();
            }catch (Exception ex){
                LogCenter.Instance().ShowMessBox(Level.INFO, "设置失败");
            }
        }));
    }

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

    private boolean start = false;
}
