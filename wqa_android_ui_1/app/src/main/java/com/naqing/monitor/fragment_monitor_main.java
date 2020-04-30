package com.naqing.monitor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.naqing.wqa_android_ui_1.R;

import java.util.ArrayList;

import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import wqa.adapter.factory.CDevDataTable;
import wqa.control.common.DevControl;
import wqa.control.common.DevControlManager;
import wqa.system.WQAPlatform;

public class fragment_monitor_main extends Fragment {
    private Activity parent;
    private View root;
    DevHolderAdapter devHolderAdapter = new DevHolderAdapter();

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
        dev_screen.setAdapter(devHolderAdapter);


        /** 初始化密码窗体 */
        Display display = parent.getWindowManager().getDefaultDisplay();
        display.getSize(devHolderAdapter.display_size);

        /** 注册设备添加删除事件*/
        if (eventListener == null) {
            eventListener = new EventListener<DevControlManager.DevNumChange>() {
                @Override
                public void recevieEvent(Event<DevControlManager.DevNumChange> event) {
                    Message msg = new Message();
                    msg.obj = event.Info();
                    switch (event.GetEvent()) {
                        case ADD:
                            msg.what = ADD;
                            break;
                        case DEL:
                            msg.what = DEL;
                            break;
                    }
                    messagehandler.sendMessage(msg);
                }
            };
            WQAPlatform.GetInstance().GetManager().StateChange.RegeditListener(eventListener);
        }

        return root;
    }
//
//    private void insertView(View view){
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(190, LinearLayout.LayoutParams.MATCH_PARENT);
//////              LayoutInflater inflater1=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//////              LayoutInflater inflater2 = getLayoutInflater();
//        lp.setMargins(10, 30, 0, 30);
//        dev_screen.addView(view,lp);
//    }

    // <editor-fold desc="添加删除设备">
    private EventListener<DevControlManager.DevNumChange> eventListener;
    private GridView dev_screen;

    private int ADD = 0;
    private int DEL = 1;
    private Handler messagehandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == ADD) {
                AddDevice((DevControl) msg.obj);
            }
            if (msg.what == DEL) {
                DelDevice((DevControl) msg.obj);
            }
        }
    };

    //添加设备
    private void AddDevice(DevControl control) {
        CDevDataTable.DataInfo[] dataInfos = CDevDataTable.GetInstance().GetStanderDatas(control.GetDevID().dev_type, false, false);
        for (int i = 0; i < dataInfos.length; i++) {
            if (!dataInfos[i].data_name.contentEquals("温度")) {
                model_monitor_holder holder = new model_monitor_holder(parent, control, dataInfos[i].data_name);
                devHolderAdapter.dev_holders.add(holder);

                dev_screen.setNumColumns(devHolderAdapter.dev_holders.size() > 4 ? 4 : devHolderAdapter.dev_holders.size());
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//                dev_screen.setLayoutParams(params);
//                dev_screen.setStretchMode(GridView.NO_STRETCH);
//                dev_screen.setNumColumns(devHolderAdapter.dev_holders.size());
                devHolderAdapter.notifyDataSetChanged();
            }
        }
    }

    private void DelDevice(DevControl control) {
        ArrayList<model_monitor_holder> tmp = new ArrayList<>();
        for (model_monitor_holder holder : devHolderAdapter.dev_holders) {
            if (holder.getDevControl() == control) {
                tmp.add(holder);
            }
        }

        for (model_monitor_holder holder : tmp) {
            devHolderAdapter.dev_holders.remove(holder);
            devHolderAdapter.notifyDataSetChanged();
        }
    }
    // </editor-fold>

}

class DevHolderAdapter extends BaseAdapter {
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
                ListView.LayoutParams lp = new ListView.LayoutParams(display_size.x * 1 / 4, (int)(display_size.y * 4.3 / 5));

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