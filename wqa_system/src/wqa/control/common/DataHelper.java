/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.common;

import java.util.ArrayList;
import wqa.adapter.factory.CDevDataTable;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class DataHelper {

    public static String[] GetSupportDataName(int dev_type) {
        //单位信息
        CDevDataTable.DevInfo d_infos = CDevDataTable.GetInstance().namemap.get(dev_type);
        ArrayList<String> list = new ArrayList();
        if (d_infos != null) {
            for (CDevDataTable.DataInfo info : d_infos.data_list) {
                if (info.internal_only) {
                    if (WQAPlatform.GetInstance().is_internal) {
                        list.add(info.data_name);
                    }
                } else {
                    list.add(info.data_name);
                }
            }
        }
        return list.toArray(new String[0]);
    }

    public static String[] GetAllData(int dev_type) {
        CDevDataTable.DevInfo d_infos = CDevDataTable.GetInstance().namemap.get(dev_type);
        ArrayList<String> list = new ArrayList();
        if (d_infos != null) {
            for (CDevDataTable.DataInfo info : d_infos.data_list) {
                list.add(info.data_name);
            }
        }
        return list.toArray(new String[0]);
    }

    public static int GetDataToDBIndex(int dev_type, String data_name) {
        String[] all_datas = GetAllData(dev_type);
        for (int i = 0; i < all_datas.length; i++) {
            if (all_datas[i].contentEquals(data_name)) {
                return i;
            }
        }
        return -1;
    }

    public static Integer[] GetSupportTeamNum(int dev_type) {
        CDevDataTable.DataInfo[] d_infos = CDevDataTable.GetInstance().namemap.get(dev_type).data_list;
        ArrayList<Integer> indexs = new ArrayList();
        for (int i = 0; i < d_infos.length; i++) {
            if (d_infos[i].internal_only && WQAPlatform.GetInstance().is_internal && !indexs.contains(d_infos[i].team)) {
                indexs.add(d_infos[i].team);
            } else if (!indexs.contains(d_infos[i].team)) {
                indexs.add(d_infos[i].team);
            }
        }
        return indexs.toArray(new Integer[0]);
    }
    
    public static String[] GetTeamName(int dev_type, int index){
        CDevDataTable.DataInfo[] d_infos = CDevDataTable.GetInstance().namemap.get(dev_type).data_list;
        ArrayList<String> names = new ArrayList();
        for (int i = 0; i < d_infos.length; i++) {
            if(d_infos[i].team == index){
                names.add(d_infos[i].data_name);
            }
        }
        return names.toArray(new String[0]);
    }
}
