/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.config;

import wqa.bill.log.LogNode;
import wqa.control.common.CDevDataTable;
import wqa.control.dev.collect.SDisplayData;
import wqa.control.common.IDevice;

/**
 *
 * @author chejf
 */
public interface ICalibrate  extends IDevice{
        
    //max_num && 0xFF， 表示有几点定标， 
    //max_num && 0xFF00 == 1 表示没有采样值，只有原始值，用来做溶氧的饱和氧和无氧
    //max_num && 0xFF00 == 2 只有原始值，没有采样值 温度没有原始值
    public CDevDataTable.DataInfo[] GetCalDataList();
    
    public SDisplayData CollectData() throws Exception;

    //输入定标数据
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception;
}
