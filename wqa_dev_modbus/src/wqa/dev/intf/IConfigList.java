/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.dev.intf;

import java.util.ArrayList;

/**
 *
 * @author chejf
 */
public interface IConfigList {
    //获取配置列表名称
    public String GetListName();

    //获取设备配置信息
    public ArrayList<SConfigItem> GetItemList();
    
    //设置设备配置信息
    public void SetItemList(ArrayList<SConfigItem> list) throws Exception;
}
