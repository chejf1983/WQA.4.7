package com.naqing.control;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.naqing.common.ErrorExecutor;
import com.naqing.common.NQProcessDialog;
import com.naqing.common.NQProcessDialog2;
import com.naqing.io.AndroidIO;
import com.naqing.io.ComManager;
import com.naqing.wqa_android_ui_1.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import wqa.bill.io.ShareIO;
import wqa.control.common.DevControl;
import wqa.control.common.DevControlManager;
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
        this.dev_list = root.findViewById(R.id.cf_dev_list);
        this.dev_list.setAdapter(dev_contrl_adapter);

        /** 搜索设备 */
        Button search = root.findViewById(R.id.control_dev_search);
        search.setOnClickListener((View view) -> {
            search_deivces();
        });

        /** 响应设备添加删除事件 */
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

    @Override
    public void onStart() {
        super.onStart();
    }

    // <editor-fold desc="搜索进度条">
    NQProcessDialog2 mProgressDialog;
    private void search_deivces() {
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

    // <editor-fold desc="添加删除设备">
    ListView dev_list;
    private EventListener<DevControlManager.DevNumChange> eventListener;

    private void AddControl(DevControl control) {
        /**检查是否重复*/
        for (model_dev_holder tmp_holder : this.dev_contrl_adapter.dev_holders) {
            if (tmp_holder.getCurrentControl() == control) {
                return;
            }
        }

        /**添加视图*/
        model_dev_holder holder = new model_dev_holder(parent, control);
        this.dev_contrl_adapter.dev_holders.add(holder);
        this.dev_contrl_adapter.notifyDataSetChanged();
    }

    private void DelControl(DevControl control) {
        /**检查是否重复*/
        for (model_dev_holder tmp_holder : this.dev_contrl_adapter.dev_holders) {
            if (tmp_holder.getCurrentControl() == control) {
                this.dev_contrl_adapter.dev_holders.remove(tmp_holder);
                this.dev_contrl_adapter.notifyDataSetChanged();
                return;
            }
        }
    }

//    private void insertView(View view){
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50);
//////              LayoutInflater inflater1=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//////              LayoutInflater inflater2 = getLayoutInflater();
//        lp.setMargins(10, 30, 10, 30);
//        dev_list.addView(view, lp);
//    }

    private int ADD = 0;
    private int DEL = 1;
    private Handler messagehandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == ADD) {
                AddControl((DevControl) msg.obj);
            }
            if (msg.what == DEL) {
                DelControl((DevControl) msg.obj);
            }
        }
    };
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
