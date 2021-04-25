/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.OSA;

import java.util.ArrayList;
import java.util.logging.Level;
import modebus.pro.NahonConvert;
import modebus.register.FREG;
import modebus.register.IREG;
import wqa.adapter.factory.AbsDevice;
import wqa.adapter.factory.CDevDataTable;
import wqa.adapter.factory.CErrorTable;
import wqa.dev.data.CollectData;
import wqa.dev.data.LogNode;
import wqa.dev.data.SDevInfo;
import wqa.dev.intf.SConfigItem;

/**
 *
 * @author chejf
 */
public class MOSAIIDevice extends AbsDevice {

    private final IREG ALARM = new IREG(0x00, 1, "报警码");//R
    private final FREG MDATA1 = new FREG(0x01, 2, "主参数数据1");      //R
    private final FREG MDATA2 = new FREG(0x03, 2, "主参数数据2");      //R
    private final FREG TEMPER = new FREG(0x05, 2, "当前温度");//R
    private final IREG ODATA1 = new IREG(0x07, 1, "原始光强数据1");//R
    private final IREG ODATA2 = new IREG(0x08, 1, "原始光强数据2");//R

    private final IREG RANGE1 = new IREG(0x09, 1, "量程");//R/W
    private final IREG RANGE2 = new IREG(0x0A, 1, "量程");//R/W
    private final IREG AVR1 = new IREG(0x0B, 1, "平均次数", 1, 100);//R/W
    private final IREG AVR2 = new IREG(0x0C, 1, "平均次数", 1, 100);//R/W
    private final FREG DOCOM = new FREG(0x0D, 2, "浊度补偿系数");//R/W

    private final IREG CLDTYPE = new IREG(0x2F, 1, "定标参数"); //R/W
    private final IREG CLRANGE = new IREG(0x30, 1, "定标量程"); //R/W
    private final IREG[] CLODATA = new IREG[]{new IREG(0x31, 1, "原始光强1"), new IREG(0x34, 1, "原始光强2"), new IREG(0x37, 1, "原始光强3")}; //R/W
    private final FREG[] CLTDATA = new FREG[]{new FREG(0x32, 2, "定标数据1"), new FREG(0x35, 2, "定标数据2"), new FREG(0x38, 2, "定标数据3")}; //R/W
    private final IREG CLSTART = new IREG(0x3A, 1, "启动定标", 1, 3); //R/W
    private final FREG CLTEMPER = new FREG(0x3B, 2, "温度定标参数");    //R/W
    private final IREG CLTEMPERSTART = new IREG(0x3D, 1, "温度启动定标");//R/W

    private final IREG RANGNUM1 = new IREG(0x50, 1, "1量程个数"); //R
    private final FREG[] RANGN1 = new FREG[]{new FREG(0x51, 2, "量程1"), new FREG(0x53, 2, "量程2"), new FREG(0x55, 2, "量程3"), new FREG(0x57, 2, "量程4")}; //R    
    private final IREG RANGNUM2 = new IREG(0x59, 1, "2量程个数"); //R
    private final FREG[] RANGN2 = new FREG[]{new FREG(0x5A, 2, "量程1"), new FREG(0x5C, 2, "量程2"), new FREG(0x5E, 2, "量程3"), new FREG(0x60, 2, "量程4")}; //R

    public MOSAIIDevice(SDevInfo info) {
        super(info);
    }

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice();

        //初始化寄存器
        this.ReadREG(RANGNUM1, RANGN1[0], RANGN1[1], RANGN1[2], RANGN1[3], RANGNUM2, RANGN2[0], RANGN2[1], RANGN2[2], RANGN2[3]);
        this.ReadREG(RANGE1, AVR1, RANGE2, AVR2, DOCOM);

        //初始化最大量程信息
        this.range_strings1 = this.init_range_string();
        this.range_strings2 = this.init_range_string2();
    }

    // <editor-fold defaultstate="collapsed" desc="量程数据"> 
    private String[] range_strings1;
    private String[] range_strings2;

    public String get_range_string(int index) {
        if (index < 0 || index >= range_strings1.length) {
            return "未知量程" + index;
        }
        return range_strings1[index];
    }

    //获取量程字符串描述
    private String[] init_range_string() {
        if (this.RANGNUM1.GetValue() >= 0 && this.RANGNUM1.GetValue() < 4) {
            String[] ret = new String[this.RANGNUM1.GetValue() + 1];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = "(0-" + RANGN1[i].GetValue() + ")";
            }
            return ret;
        } else {
            java.util.logging.Logger.getGlobal().log(Level.WARNING, "RANGNUM个数显示错误:{0}", RANGNUM1.GetValue());
            return CDevDataTable.GetInstance().namemap.get(this.GetDevInfo().dev_type).data_list[0].data_range;
        }
    }

    public String get_range_string2(int index) {
        if (index < 0 || index >= range_strings2.length) {
            return "未知量程" + index;
        }
        return range_strings2[index];
    }

    //获取量程字符串描述
    private String[] init_range_string2() {
        if (this.RANGNUM2.GetValue() >= 0 && this.RANGNUM2.GetValue() < 4) {
            String[] ret = new String[this.RANGNUM2.GetValue() + 1];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = "(0-" + RANGN2[i].GetValue() + ")";
            }
            return ret;
        } else {
            java.util.logging.Logger.getGlobal().log(Level.WARNING, "RANGNUM个数显示错误:{0}", RANGNUM2.GetValue());
            return CDevDataTable.GetInstance().namemap.get(this.GetDevInfo().dev_type).data_list[2].data_range;
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="浊度额外设置"> 
    //private String[] dataRange = new String[]{"(0-100)NTU", "(0-500)NTU", "(0-2000)NTU", "(0-4000)NTU"};
    @Override
    public ArrayList<SConfigItem> GetConfigList() {
        ArrayList<SConfigItem> list = super.GetConfigList(); //To change body of generated methods, choose Tools | Templates.
        String[] data_names = this.GetDataNames();
        list.add(SConfigItem.CreateSItem(data_names[0] + RANGE1.toString(), get_range_string(this.RANGE1.GetValue()), "", range_strings1));
        list.add(SConfigItem.CreateRWItem(data_names[0] + AVR1.toString(), AVR1.GetValue().toString(), ""));
        list.add(SConfigItem.CreateSItem(data_names[1] + RANGE2.toString(), get_range_string2(this.RANGE2.GetValue()), "", range_strings2));
        list.add(SConfigItem.CreateRWItem(data_names[1] + AVR2.toString(), AVR2.GetValue().toString(), ""));
        if (this.GetDevInfo().dev_type == 0x1110 || this.GetDevInfo().dev_type == 0x1111 || this.GetDevInfo().dev_type == 0x1112) {
            list.add(SConfigItem.CreateRWItem(this.DOCOM.toString(), DOCOM.GetValue().toString(), ""));
        }
        return list;
    }

    @Override
    public void SetConfigList(ArrayList<SConfigItem> list) throws Exception {
        super.SetConfigList(list);
        String[] data_names = this.GetDataNames();

        for (SConfigItem item : list) {
            //设置平均次数
            if (item.IsKey(data_names[0] + this.AVR1.toString())) {
                this.SetConfigREG(AVR1, item.GetValue());

            }

            //设置量程范围
            if (item.IsKey(data_names[0] + RANGE1.toString())) {
                for (int i = 0; i < this.range_strings1.length; i++) {
                    if (item.GetValue().contentEquals(this.range_strings1[i])) {
                        this.SetConfigREG(RANGE1, String.valueOf(i));
                        break;
                    }
                }
            }
            if (item.IsKey(DOCOM.toString())) {
                this.SetConfigREG(DOCOM, item.GetValue());
            }
            //设置平均次数
            if (item.IsKey(data_names[1] + this.AVR2.toString())) {
                this.SetConfigREG(AVR2, item.GetValue());
            }

            //设置量程范围
            if (item.IsKey(data_names[1] + RANGE2.toString())) {
                for (int i = 0; i < this.range_strings2.length; i++) {
                    if (item.GetValue().contentEquals(this.range_strings2[i])) {
                        this.SetConfigREG(RANGE2, String.valueOf(i));
                        break;
                    }
                }
            }
        }
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="采集控制"> 
    @Override
    public CollectData CollectData() throws Exception {
        CollectData disdata = this.BuildDisplayData();
        this.ReadREG(ALARM, MDATA1, TEMPER, ODATA1, MDATA2, ODATA2);
        this.ReadREG(OTEMPER);
//        CommonDataPacket sph_data = this.trub_drv.GetData();

        disdata.datas[0].mainData = NahonConvert.TimData(MDATA1.GetValue(), 2);
        disdata.datas[0].range_info = this.get_range_string(this.RANGE1.GetValue());
        disdata.datas[1].mainData = NahonConvert.TimData(ODATA1.GetValue(), 2);
        if (this.GetDevInfo().dev_type == 0x1111 || this.GetDevInfo().dev_type == 0x1112) {
            if (disdata.datas[0].range_info.length() > "(0-20000)".length()) {
                disdata.datas[0].unit = "细胞/ml";
            }
        }

        disdata.datas[2].mainData = NahonConvert.TimData(MDATA2.GetValue(), 2);
        disdata.datas[2].range_info = this.get_range_string2(this.RANGE2.GetValue());
        disdata.datas[3].mainData = NahonConvert.TimData(ODATA2.GetValue(), 2);
        if (this.GetDevInfo().dev_type == 0x1113 || this.GetDevInfo().dev_type == 0x1114) {
            if (disdata.datas[2].range_info.length() > "(0-20000)".length()) {
                disdata.datas[2].unit = "细胞/ml";
            }
        }
        
        disdata.datas[4].mainData = NahonConvert.TimData(TEMPER.GetValue(), 2);
        disdata.datas[5].mainData = NahonConvert.TimData(OTEMPER.GetValue(), 2);

        disdata.alarm = ALARM.GetValue();
        String info = CErrorTable.GetInstance().GetErrorString(((this.GetDevInfo().dev_type & 0xFF00) << 8) | disdata.alarm);
        disdata.alram_info = info == null ? "" : info;
        return disdata;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标信息"> 
    @Override
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception {
        if (type.contentEquals("温度")) {
            CalTemer(testdata[0]);
        } else {
            CalDevice(type, oradata, testdata);
        }
        return LogNode.CALOK();
    }

    private void CalDevice(String type, float[] oradata, float[] caldata) throws Exception {
        if (CLODATA.length < oradata.length) {
            throw new Exception("定标个数异常");
        }

        if (type.contentEquals(this.GetDataNames()[0])) {
            this.CLDTYPE.SetValue(0);
            this.CLRANGE.SetValue(this.RANGE1.GetValue());
        } else {
            this.CLDTYPE.SetValue(1);
            this.CLRANGE.SetValue(this.RANGE2.GetValue());
        }

        for (int i = 0; i < oradata.length; i++) {
            CLODATA[i].SetValue((int) oradata[i]);
            CLTDATA[i].SetValue(caldata[i]);
//            System.arraycopy(NahonConvert.UShortToByteArray((int) oradata[i]), 0, data, i * 6, 2);
//            System.arraycopy(NahonConvert.FloatToByteArray(caldata[i]), 0, data, i * 6 + 2, 4);
        }
        CLSTART.SetValue(oradata.length);
        this.SetREG(CLDTYPE, CLRANGE, CLODATA[0], CLODATA[1], CLODATA[2], CLTDATA[0], CLTDATA[1], CLTDATA[2], CLSTART);
    }

    private void CalTemer(float caltemper) throws Exception {
        this.CLTEMPER.SetValue(caltemper);
        this.CLTEMPERSTART.SetValue(0x01);
        this.SetREG(CLTEMPER, CLTEMPERSTART);
    }
    // </editor-fold>  

}
