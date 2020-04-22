/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.config;

import java.util.ArrayList;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.ShareIO;
import wqa.dev.intf.IConfigList;
import wqa.dev.intf.IDevice;
import wqa.dev.intf.SConfigItem;

/**
 *
 * @author chejf
 */
public class DevConfigTable {

    private IConfigList configlist;
    private IDevice dev;
    private DevConfigBean msg_instance;

    public DevConfigTable(IDevice dev, IConfigList configlist, DevConfigBean msg_instance) {
        this.configlist = configlist;
        this.dev = dev;
        this.msg_instance = msg_instance;
    }

    public String GetValue(String key) {
        for (SConfigItem item : configlist.GetItemList()) {
            if (item.data_name.contentEquals(key)) {
                return item.GetValue();
            }
        }
        return "";
    }

    public void InitConfigTable(){
        try {
            ((ShareIO)dev.GetIO()).Lock();
            this.dev.InitDevice();
//            this.msg_instance.UpdateConfigEvent.CreateEvent(null);
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
        } finally {
            ((ShareIO)dev.GetIO()).UnLock();
        }
    }

    public SConfigItem[] GetConfigList() {
        try {
            return configlist.GetItemList().toArray(new SConfigItem[0]);
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }

    public String GetListName() {
        return this.configlist.GetListName();
    }

    public void SetConfigList(SConfigItem[] list) {
        try {
            ((ShareIO)dev.GetIO()).Lock();

            ArrayList<SConfigItem> changelist = new ArrayList();
            for (int i = 0; i < list.length; i++) {
                if (list[i].IsChanged()) {
                    changelist.add(list[i]);
                    list[i].Updated();
                }
            }
            configlist.SetItemList(changelist);
            this.msg_instance.SetMessage("设置成功");
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
        } finally {
            ((ShareIO)dev.GetIO()).UnLock();
        }
    }

}
