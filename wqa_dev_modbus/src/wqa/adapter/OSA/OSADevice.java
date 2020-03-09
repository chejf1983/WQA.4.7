/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.OSA;

import java.util.ArrayList;
import modebus.pro.NahonConvert;
import modebus.register.*;
import wqa.adapter.factory.AbsDevice;
import wqa.adapter.io.ShareIO;
import wqa.bill.log.LogNode;
import wqa.control.common.CDevDataTable;
import wqa.control.dev.collect.SDisplayData;
import wqa.control.common.CErrorTable;
import wqa.control.config.SConfigItem;
import wqa.control.config.IDevMotorConfig;
import wqa.control.data.SMotorParameter;
import wqa.control.data.SMotorParameter.CleanMode;

/**
 *
 * @author chejf
 */
public class OSADevice extends AbsDevice implements IDevMotorConfig {

    private final IREG ALARM = new IREG(0x00, 1, "报警码");//R
    private final FREG MDATA = new FREG(0x01, 2, "主参数数据(OSA-Turb:浊度、OSA-TS：悬浮物/浊度、OSA-ChlA：叶绿素、OSA-Cyano：蓝绿藻、OSA-Oil: 水中油)");      //R
    private final FREG TEMPER = new FREG(0x03, 2, "当前温度");//R
    private final IREG ODATA = new IREG(0x05, 1, "原始光强数据");//R

    private final IREG RANGE = new IREG(0x06, 1, "量程");//R/W
    private final IREG AVR = new IREG(0x07, 1, "平均次数", 1, 100);//R/W
    private final IREG CMODE = new IREG(0x08, 1, "清扫模式", 0, 2);//R/W
    private final IREG CTIME = new IREG(0x09, 1, "清扫次数", 1, 100);//R/W
    private final IREG CINTVAL = new IREG(0x0A, 1, "清扫间隔", 10, 60 * 24);//R/W

    private final IREG CLRANGE = new IREG(0x30, 1, "定标量程"); //R/W
    private final IREG[] CLODATA = new IREG[]{new IREG(0x31, 1, "原始光强1"), new IREG(0x34, 1, "原始光强2"), new IREG(0x37, 1, "原始光强3")}; //R/W
    private final FREG[] CLTDATA = new FREG[]{new FREG(0x32, 2, "定标数据1"), new FREG(0x35, 2, "定标数据2"), new FREG(0x38, 2, "定标数据3")}; //R/W
    private final IREG CLSTART = new IREG(0x3A, 1, "启动定标", 1, 3); //R/W
    private final FREG CLTEMPER = new FREG(0x3B, 2, "温度定标参数");    //R/W
    private final IREG CLTEMPERSTART = new IREG(0x3D, 1, "温度启动定标");//R/W

    private final IREG RANGNUM = new IREG(0x50, 1, "量程个数"); //R
    private final FREG[] RANGN = new FREG[]{new FREG(0x51, 2, "量程1"), new FREG(0x53, 2, "量程2"), new FREG(0x55, 2, "量程3"), new FREG(0x57, 2, "量程4")}; //R

    public OSADevice(ShareIO io, byte addr) {
        super(io, addr);
    }

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice();

        //初始化寄存器
        this.base_drv.ReadREG(RETRY_TIME, DEF_TIMEOUT, RANGNUM, RANGN[0], RANGN[1], RANGN[2], RANGN[3]);
        this.base_drv.ReadREG(RETRY_TIME, DEF_TIMEOUT, RANGE, AVR, CMODE, CTIME, CINTVAL);

        //初始化最大量程信息
        this.range_info = this.init_range_string();

        //保证量程合法，不合法，默认为0
        if (this.RANGE.GetValue() < 0 || this.RANGE.GetValue() >= range_info.length) {
            throw new Exception("量程信息异常,设备量程是" + this.RANGE.GetValue() + "支持的量程是" + this.range_info.length);
        }
    }

    public String[] range_info = new String[0];

    //初始化量程信息
    private String[] init_range_string() throws Exception {

        if (this.RANGNUM.GetValue() == 0) {
            return CDevDataTable.GetInstance().namemap.get(this.DEVTYPE.GetValue()).data_list[0].data_range;
        } else {
            String[] ret = new String[this.RANGNUM.GetValue()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = "(0-" + RANGN[i].GetValue() + ")";
            }
            return ret;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="浊度额外设置"> 
    //private String[] dataRange = new String[]{"(0-100)NTU", "(0-500)NTU", "(0-2000)NTU", "(0-4000)NTU"};
    @Override
    public ArrayList<SConfigItem> GetConfigList() {
        ArrayList<SConfigItem> list = super.GetConfigList(); //To change body of generated methods, choose Tools | Templates.
        list.add(SConfigItem.CreateSItem(RANGE.toString(), range_info[this.RANGE.GetValue()], "", range_info));
        list.add(SConfigItem.CreateRWItem(AVR.toString(), AVR.GetValue().toString(), ""));
        return list;
    }

    @Override
    public void SetConfigList(ArrayList<SConfigItem> list) throws Exception {
        super.SetConfigList(list);
        for (SConfigItem item : list) {
            //设置平均次数
            if (item.IsKey(this.AVR.toString())) {
                this.SetConfigREG(AVR, item.value);

            }

            //设置量程范围
            if (item.IsKey(RANGE.toString())) {
                for (int i = 0; i < this.range_info.length; i++) {
                    if (item.value.contentEquals(this.range_info[i])) {
                        this.SetConfigREG(RANGE, String.valueOf(i));
                        break;
                    }
                }
            }
        }
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="电机控制"> 
//    private MotorInfo motorinfo;
//    private int mode;
//    private static String CleanTime = "清扫次数";
//    private static String CleanInterval = "清扫间隔";
    @Override
    public SMotorParameter GetMotoPara() {
        SMotorParameter par = new SMotorParameter(
                this.CMODE.GetValue() == 00 ? CleanMode.Auto : CleanMode.Manu,
                new SConfigItem[]{
                    SConfigItem.CreateRWItem(this.CTIME.toString(), CTIME.GetValue().toString(), CTIME.min + "-" + CTIME.max),
                    SConfigItem.CreateRWItem(this.CINTVAL.toString(), CINTVAL.GetValue().toString(), CINTVAL.min + "-" + CINTVAL.max + "(min)")},
                new SConfigItem[0]);
        return par;
    }

    @Override
    public void SetMotoPara(SMotorParameter par) throws Exception {
        //设置参数
//        MotorInfo tminfo = new MotorInfo();

        for (SConfigItem item : par.auto_config) {
            if (item.IsKey(CTIME.toString())) {
                this.CTIME.SetValue(Integer.valueOf(item.value));

            }
            if (item.IsKey(CINTVAL.toString())) {
                CINTVAL.SetValue(Integer.valueOf(item.value));
            }
        }

        //设置模式
        this.CMODE.SetValue(par.mode == CleanMode.Auto ? 0 : 1);
        this.base_drv.SetREG(RETRY_TIME, DEF_TIMEOUT, CMODE, CTIME, CINTVAL);
    }

    @Override
    public void StartManual() throws Exception {
        int ora = this.CMODE.GetValue();
        //切到手动
        this.SetConfigREG(CMODE, "1");
        //清扫一次
        this.SetConfigREG(CMODE, "2");
        //恢复之前模式
        this.SetConfigREG(CMODE, String.valueOf(ora));
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="采集控制"> 
    @Override
    public SDisplayData CollectData() throws Exception {
        SDisplayData disdata = this.BuildDisplayData();
        this.base_drv.ReadREG(RETRY_TIME, DEF_TIMEOUT, ALARM, MDATA, TEMPER, ODATA);
        this.base_drv.ReadREG(RETRY_TIME, DEF_TIMEOUT, OTEMPER);
//        CommonDataPacket sph_data = this.trub_drv.GetData();

        disdata.datas[0].mainData = NahonConvert.TimData(MDATA.GetValue(), 2);
        disdata.datas[0].range_info = range_info[this.RANGE.GetValue()];

        disdata.datas[1].mainData = NahonConvert.TimData(ODATA.GetValue(), 2);
        disdata.datas[2].mainData = NahonConvert.TimData(TEMPER.GetValue(), 2);
        disdata.datas[3].mainData = NahonConvert.TimData(OTEMPER.GetValue(), 2);

        disdata.alarm = ALARM.GetValue();
        String info = CErrorTable.GetInstance().GetErrorString(((this.DEVTYPE.GetValue() & 0xFF00) << 8) | disdata.alarm);
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
//            this.trub_drv.SetCalRange(rangeindex);
//            this.trub_drv.CalDevice(oradata, testdata);
            CalDevice(this.RANGE.GetValue(), oradata, testdata);
        }
        return LogNode.CALOK();
    }

    private void CalDevice(int index, float[] oradata, float[] caldata) throws Exception {
        if (CLODATA.length < oradata.length) {
            throw new Exception("定标个数异常");
        }

        this.CLRANGE.SetValue(index);
        for (int i = 0; i < oradata.length; i++) {
            CLODATA[i].SetValue((int) oradata[i]);
            CLTDATA[i].SetValue(caldata[i]);
//            System.arraycopy(NahonConvert.UShortToByteArray((int) oradata[i]), 0, data, i * 6, 2);
//            System.arraycopy(NahonConvert.FloatToByteArray(caldata[i]), 0, data, i * 6 + 2, 4);
        }
        CLSTART.SetValue(oradata.length);
        this.base_drv.SetREG(RETRY_TIME, DEF_TIMEOUT, CLRANGE, CLODATA[0], CLODATA[1], CLODATA[2], CLTDATA[0], CLTDATA[1], CLTDATA[2], CLSTART);
    }

    private void CalTemer(float caltemper) throws Exception {
        this.CLTEMPER.SetValue(caltemper);
        this.CLTEMPERSTART.SetValue(0x01);
        this.base_drv.SetREG(RETRY_TIME, DEF_TIMEOUT, CLTEMPER, CLTEMPERSTART);
    }
    // </editor-fold>  

}
