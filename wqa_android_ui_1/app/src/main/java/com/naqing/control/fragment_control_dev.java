package com.naqing.control;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;

import com.naqing.common.ErrorExecutor;
import com.naqing.common.NQProcessDialog2;
import com.naqing.dev_views.model_dev_holder;
import com.naqing.dev_views.model_dev_view;
import com.naqing.io.AndroidIO;
import com.naqing.wqa_android_ui_1.R;

import java.util.ArrayList;
import java.util.concurrent.Future;

import wqa.bill.io.ShareIO;
import wqa.control.data.IMainProcess;
import wqa.system.WQAPlatform;


public class fragment_control_dev extends Fragment {

    private Activity parent;
    private View root;
    private DevHolderAdapter dev_contrl_adapter = new DevHolderAdapter();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_control_dev, container, false);
        GridView dev_list = root.findViewById(R.id.cf_dev_list);
        dev_list.setAdapter(dev_contrl_adapter);


        /** 搜索设备 */
        Button search = root.findViewById(R.id.control_dev_search);
        search.setOnClickListener((View view) -> {
            search_deivces(parent);
        });

        return root;
    }

    /** 创建设备界面*/
    public model_dev_holder CreateHoler(model_dev_view control){
        /**检查是否重复*/
        for (model_dev_holder tmp_holder : dev_contrl_adapter.dev_holders) {
            if (tmp_holder.getCurrentControl() == control) {
                return tmp_holder;
            }
        }

        /**添加视图*/
        model_dev_holder holder = new model_dev_holder(parent, control);
        dev_contrl_adapter.dev_holders.add(holder);
//        dev_contrl_adapter.notifyDataSetChanged();
        GridView dev_list = root.findViewById(R.id.cf_dev_list);
        dev_list.setAdapter(dev_contrl_adapter);
        return holder;
    }

    /** 删除设备界面*/
    public void DelControl(model_dev_holder tmp_holder) {
        /**检查是否重复*/
        if(dev_contrl_adapter.dev_holders.contains(tmp_holder)){
            dev_contrl_adapter.dev_holders.remove(tmp_holder);
//            dev_contrl_adapter.notifyDataSetChanged();
            GridView dev_list = root.findViewById(R.id.cf_dev_list);
            dev_list.setAdapter(dev_contrl_adapter);
        }
    }

    // <editor-fold desc="搜索设备">
    NQProcessDialog2 mProgressDialog;
    public void search_deivces(Activity parent) {
        if(mProgressDialog != null && !mProgressDialog.isFinished()){
            return;
        }

        mProgressDialog = NQProcessDialog2.ShowProcessDialog(parent, "搜索设备...");

        Future<?> submit = WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
            WQAPlatform.GetInstance().GetManager().SearchDevice(new ShareIO[]{AndroidIO.GetInstance().GetDevIO().GetDevConfigIO()}, new IMainProcess<Boolean>() {
                @Override
                public void SetValue(float pecent) {
                    mProgressDialog.SetPecent((int) pecent + 10);
                }

                @Override
                public void Finish(Boolean result) {
                    mProgressDialog.Finish();
                }
            });
        });

        mProgressDialog.SetTimout(30000, () -> {
            if (!submit.isDone()) {
                submit.cancel(true);
                ErrorExecutor.PrintErrorInfo("搜索设备超时");
            }
        });
    }
    // </editor-fold>
}

class DevHolderAdapter extends BaseAdapter {
    public ArrayList<model_dev_holder> dev_holders = new ArrayList();

    @Override
    public int getCount() {
        return dev_holders.size();
    }

    @Override
    public Object getItem(int position) {
        return dev_holders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (this.getItem(position) != null) {
                ListView.LayoutParams lp = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, 50);
                convertView = ((model_dev_holder) this.getItem(position)).getDeviceView();
                convertView.setLayoutParams(lp);
            }
        }

        return convertView;
    }
}
