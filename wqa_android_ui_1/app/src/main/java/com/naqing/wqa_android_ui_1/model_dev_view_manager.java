package com.naqing.wqa_android_ui_1;

import android.os.Handler;
import android.os.Message;
import com.naqing.control.fragment_control_dev;
import com.naqing.dev_views.model_dev_view;
import com.naqing.io.AndroidIO;

import java.util.ArrayList;
import java.util.logging.Level;

import migp.adapter.factory.MIGPDevFactory;
import nahon.comm.event.Event;
import nahon.comm.event.EventListener;
import nahon.comm.faultsystem.LogCenter;
import wqa.adapter.factory.ModBusDevFactory;
import wqa.control.common.DevControl;
import wqa.control.common.DevControlManager;
import wqa.dev.intf.IDevice;
import wqa.system.WQAPlatform;

public class model_dev_view_manager {
    private EventListener<DevControlManager.DevNumChange> eventListener;

    private static model_dev_view_manager instance;
    public static model_dev_view_manager Instance(){
        if(instance == null){
            instance = new model_dev_view_manager();
        }
        return instance;
    }

    private model_dev_view_manager() {
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
//       InitDevs();
    }

    // <editor-fold desc="注册设备界面，和数据界面">
    private fragment_control_dev devHolderAdapter;
    public void SetDevHolderAdapter(fragment_control_dev adapter){
        this.devHolderAdapter = adapter;
    }
    private fragment_monitor_main monitorHolderAdapter;
    public void SetMonHolderAdapter(fragment_monitor_main adapter){
        this.monitorHolderAdapter = adapter;
    }
    // </editor-fold>

    // <editor-fold desc="添加删除设备">
    private ArrayList<model_dev_view> dev_views = new ArrayList<>();
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

    private void AddControl(DevControl control){
//        control.
        dev_views.add(new model_dev_view(control, this.devHolderAdapter, monitorHolderAdapter));
        this.SaveDevInfo(control);
    }

    private void DelControl(DevControl control){
        for(model_dev_view view : dev_views){
            if(view.control == control){
                view.Close();
                this.dev_views.remove(view);
                this.DelDevInfo(control);
                return;
            }
        }
    }
    // </editor-fold>

    // <editor-fold desc="设备存储">
    private ArrayList<String> devinfos = new ArrayList<>();
    private String DevInfoKey = "DEVLIST";
    private String split = "#";

    private String ConvertKey(DevControl control){
        String info = "";
        info += control.GetProType() + "_";
        info += control.GetDevID().dev_addr + "_";
        info += control.GetDevID().dev_type;
        return info;
    }
    private void SaveDevInfo(DevControl control){
        String tmp = ConvertKey(control);
        for(String info : devinfos) {
            if(info.contentEquals(tmp)){
                return;
            }
        }
        devinfos.add(tmp);
        SaveConfig();
    }
    private void DelDevInfo(DevControl control){
        String tmp = ConvertKey(control);
        for(String info : devinfos) {
            if(info.contentEquals(tmp)){
                devinfos.remove(info);
                break;
            }
        }
        SaveConfig();
    }
    private void SaveConfig(){
        String data = "";
        for(String info : this.devinfos){
            data += info + split;
        }
        WQAPlatform.GetInstance().GetConfig().setProperty(DevInfoKey, data);
        WQAPlatform.GetInstance().SaveConfig();
    }

    private void ReadDevInfo(String info){
        try{
            String[] strings = info.split("_");
            if(strings.length == 3){
                String type = strings[0];
                int dev_addr = Integer.valueOf(strings[1]);
                int dev_type = Integer.valueOf(strings[2]);
                if(type.contentEquals("MIGP")){
                    IDevice dev = new MIGPDevFactory().BuildDevice(AndroidIO.GetInstance().GetDevIO().GetDevConfigIO(), (byte)dev_addr, dev_type);
                    WQAPlatform.GetInstance().GetManager().AddNewDevice(dev).Start();
                }else{
                    IDevice dev =  new ModBusDevFactory().BuildDevice(AndroidIO.GetInstance().GetDevIO().GetDevConfigIO(), (byte)dev_addr, dev_type);
                    WQAPlatform.GetInstance().GetManager().AddNewDevice(dev).Start();
                }
            }
        }catch (Exception ex){
            devinfos.remove(info);
            SaveConfig();
            LogCenter.Instance().PrintLog(Level.INFO, "无法识别的设备配置信息");
        }
    }
    public boolean IsInited(){
        return this.devinfos.size() > 0;
    }
    public void InitDevs(){
        String devlists = WQAPlatform.GetInstance().GetConfig().getProperty(DevInfoKey, "");
        System.out.println("Read list***********" + devlists);
        String[] splits = devlists.split(split);
        System.out.println("Read list***********" + splits.length);
        for(String info : splits){
            System.out.println("Read***********" + info);
            if(!info.contentEquals("")){
                this.devinfos.add(info);
                ReadDevInfo(info);
            }
        }
    }

    // </editor-fold>
}
