package com.naqing.wqa_android_ui_1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.naqing.dev_views.model_dev_view;
import com.naqing.dev_views.model_monitor_holder;

import java.util.ArrayList;

import wqa.adapter.factory.CDevDataTable;

public class fragment_monitor_main extends Fragment {
    private Activity parent;
    private View root;
    MonitorHolderAdapter monitorHolderAdapter = new MonitorHolderAdapter();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_monitor_main, container, false);

        /** 刷新界面*/
        dev_screen = root.findViewById(R.id.monitor_main_list);
        dev_screen.setAdapter(monitorHolderAdapter);


        /** 初始化密码窗体 */
        Display display = parent.getWindowManager().getDefaultDisplay();
        display.getSize(monitorHolderAdapter.display_size);
        return root;
    }

    // <editor-fold desc="添加删除设备">
    private GridView dev_screen;

    public model_monitor_holder[] CreateMonitor(model_dev_view dev_view) {
        ArrayList<model_monitor_holder> tmp = new ArrayList<>();
        CDevDataTable.DataInfo[] dataInfos = CDevDataTable.GetInstance().GetStanderDatas(dev_view.control.GetDevID().dev_type, false, false);
        for (int i = 0; i < dataInfos.length; i++) {
            if (!dataInfos[i].data_name.contentEquals("温度")) {
                model_monitor_holder holder = new model_monitor_holder(parent, dev_view, dataInfos[i].data_name);
                tmp.add(holder);
            }
        }
        return tmp.toArray(new model_monitor_holder[0]);
    }

    //添加设备
    public void AddDevice(model_monitor_holder monitor) {
        for (model_monitor_holder holder : monitorHolderAdapter.dev_holders) {
            if(monitor == holder) {
                return;
            }
        }
        System.out.println("add" + monitor.data_name);
        monitorHolderAdapter.dev_holders.add(monitor);
        dev_screen.setNumColumns(monitorHolderAdapter.dev_holders.size() > 4 ? 4 : monitorHolderAdapter.dev_holders.size());
        monitorHolderAdapter.notifyDataSetChanged();
    }

    public void DelDevice(model_monitor_holder monitor) {
        for (model_monitor_holder holder : monitorHolderAdapter.dev_holders) {
            if(monitor == holder) {
                System.out.println("del" + monitor.data_name);
                monitorHolderAdapter.dev_holders.remove(holder);
                dev_screen.setNumColumns(monitorHolderAdapter.dev_holders.size() > 4 ? 4 : monitorHolderAdapter.dev_holders.size());
                monitorHolderAdapter.notifyDataSetChanged();
                return;
            }
        }
    }
    // </editor-fold>
}

class MonitorHolderAdapter extends BaseAdapter {
    public ArrayList<model_monitor_holder> dev_holders = new ArrayList();
    public Point display_size = new Point();

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
                ListView.LayoutParams lp = new ListView.LayoutParams(display_size.x * 1 / 4, (int) (display_size.y * 4.3 / 5));

////              LayoutInflater inflater1=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////              LayoutInflater inflater2 = getLayoutInflater();
//                (10, 30, 0, 30);
                convertView = ((model_monitor_holder) this.getItem(position)).getView();
                convertView.setLayoutParams(lp);
            }
        }

        return convertView;
    }
}