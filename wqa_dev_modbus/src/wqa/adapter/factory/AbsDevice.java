/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.factory;

import java.util.ArrayList;
import modebus.pro.ModeBusNode;
import modebus.register.*;
import wqa.adapter.factory.CDevDataTable.DataInfo;
import wqa.dev.data.MIOInfo;
import wqa.dev.data.SDevInfo;
import wqa.dev.data.CollectData;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public abstract class AbsDevice implements IDevice, ICalibrate, ICollect {

    protected ModeBusNode base_drv;
    public static int DEF_TIMEOUT = 400;
    public static int RETRY_TIME = 3;
    //波特率范围
    public static final String[] SBandRate = new String[]{"4800", "9600", "19200", "38400", "57600", "115200"};
    protected final SREG DEVNAME = new SREG(0x10, 8, "设备名称");//R   
    protected final SREG SERIANUM = new SREG(0x18, 8, "序列号");//R
    protected final SREG HWVER = new SREG(0x20, 1, "硬件版本");//R
    protected final SREG SWVER = new SREG(0x21, 2, "软件版本");//R
    protected final IREG DEVADDR = new IREG(0x23, 1, "设备地址", 1, 32);//R/W
    protected final IREG BANDRANGEI = new IREG(0x24, 1, "波特率", 0, SBandRate.length - 1);//R/W
    protected final IREG DEVTYPE = new IREG(0x25, 1, "设备类型");//R

    protected final FREG OTEMPER = new FREG(0x40, 2, "温度原始值");//R 
    protected final BREG SDTEMPSWT = new BREG(0x42, 1, "手动温补开关"); //R/W
    protected final FREG SDTEMP = new FREG(0x43, 2, "手动温补值", 0, 60);//R/W

    public AbsDevice(IMAbstractIO io, byte addr) {
        this.base_drv = new ModeBusNode(io, addr);
    }

    @Override
    public void InitDevice() throws Exception {
        //获取eia信息
        this.base_drv.ReadREG(RETRY_TIME, DEF_TIMEOUT, SERIANUM, HWVER, SWVER, DEVADDR, BANDRANGEI, DEVTYPE);
        this.base_drv.ReadREG(RETRY_TIME, DEF_TIMEOUT, SDTEMPSWT, SDTEMP);

        //赋值设备地址，按照搜索出来的结果赋值，设备读出来不准确
        DEVADDR.SetValue((int) this.base_drv.GetCurrentAddr());
        //波特率序号也根据IO信息来，设备读出来不准确
        MIOInfo comm_info = this.base_drv.GetIO().GetIOInfo();
        if (comm_info.iotype.equals(MIOInfo.COM)) {
            String sbandrate = comm_info.par[1];
            for (int i = 0; i < this.SBandRate.length; i++) {
                if (sbandrate.contentEquals(this.SBandRate[i])) {
                    this.BANDRANGEI.SetValue(i);
                    break;
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="公共接口接口">
    @Override
    public IMAbstractIO GetIO() {
        return this.base_drv.GetIO();
    }

    @Override
    public int ReTestType() {
        try {
            //读设备类型寄存器
            IREG TDEVTYPE = new IREG(0x25, 1, "设备类型");
            this.base_drv.ReadREG(1, DEF_TIMEOUT, TDEVTYPE);
//            this.base_drv.ReadMemory(DEF_TIMEOUT, RETRY_TIME, RETRY_TIME, DEF_TIMEOUT)
            //返回值
            return TDEVTYPE.GetValue();
        } catch (Exception ex) {
            return -1;
        }
    }
    private SDevInfo info = new SDevInfo();

    @Override
    public SDevInfo GetDevInfo() {
        //初始化连接信息
        info.io = this.base_drv.GetIO();
        info.dev_addr = this.DEVADDR.GetValue();
        info.dev_type = this.DEVTYPE.GetValue();
        info.serial_num = this.SERIANUM.GetValue();
        info.protype = SDevInfo.ProType.MODEBUS;
        return info;
    }

    //只返回不同测量类型的数据(没有原始值这个标志的数据)
    public String[] GetDataNames() {
        DataInfo[] data_list = CDevDataTable.GetInstance().namemap.get(this.DEVTYPE.GetValue()).data_list;
        ArrayList<String> data_names = new ArrayList();
        for (int i = 0; i < data_list.length; i++) {
            if (!data_list[i].data_name.endsWith(CDevDataTable.ORA_Flag)) {
                data_names.add(data_list[i].data_name);
            }
        }
        return data_names.toArray(new String[0]);
    }

    @Override
    public DataInfo[] GetCalDataList() {
        ArrayList<DataInfo> list = new ArrayList();
        DataInfo[] data_list = CDevDataTable.GetInstance().namemap.get(this.DEVTYPE.GetValue()).data_list;
//        int[] data_cal_num = new int[data_list.length];
        for (int i = 0; i < data_list.length; i++) {
            if (data_list[i].cal_num > 0) {
                list.add(data_list[i]);
            }
        }
        return list.toArray(new DataInfo[0]);
    }

    public CollectData BuildDisplayData() {
        return new CollectData(this.DEVTYPE.GetValue(), this.DEVADDR.GetValue(), this.SERIANUM.GetValue());
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="设备信息接口"> 
    public ArrayList<SConfigItem> GetInfoList() {
        ArrayList<SConfigItem> list = new ArrayList();
        list.add(SConfigItem.CreateRItem(DEVNAME.toString(), CDevDataTable.GetInstance().namemap.get(DEVTYPE.GetValue()).dev_name_ch, ""));
        list.add(SConfigItem.CreateRItem(SERIANUM.toString(), SERIANUM.GetValue(), ""));
        list.add(SConfigItem.CreateRItem(SWVER.toString(), SWVER.GetValue(), ""));
        list.add(SConfigItem.CreateRItem(HWVER.toString(), HWVER.GetValue(), ""));
        list.add(SConfigItem.CreateRItem(DEVTYPE.toString(), String.format("0X%04X", DEVTYPE.GetValue()), ""));
        return list;
    }

    public void SetInfoList(ArrayList<SConfigItem> list) throws Exception {

    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="设备配置接口">   
//    private String[] tswitch_value = new String[]{"自动补偿", "手动补偿"};
    public ArrayList<SConfigItem> GetConfigList() {
        ArrayList<SConfigItem> list = new ArrayList();
        list.add(SConfigItem.CreateRWItem(DEVADDR.toString(), DEVADDR.GetValue().toString(), DEVADDR.min + "-" + DEVADDR.max));
        list.add(SConfigItem.CreateSItem(BANDRANGEI.toString(), SBandRate[BANDRANGEI.GetValue()], "", SBandRate));
//        list.add(SConfigItem.CreateBItem(SDTEMPSWT.toString(), SDTEMPSWT.GetValue()));
//        list.add(SConfigItem.CreateRWItem(SDTEMP.toString(), SDTEMP.GetValue().toString(), SDTEMP.min + "-" + SDTEMP.max));
        return list;
    }

    public void SetConfigList(ArrayList<SConfigItem> list) throws Exception {
        for (SConfigItem item : list) {
            //修改设备地址
            if (item.IsKey(DEVADDR.toString())) {
                try {
                    ModeBusNode base = new ModeBusNode(this.base_drv.GetIO(), Integer.valueOf(item.value).byteValue());
                    base.ReadMemory(DEVADDR.RegAddr(), DEVADDR.RegNum(), 1, DEF_TIMEOUT);
                    throw new Exception("该地址已经存在!");
                } catch (Exception ex) {
                    this.SetConfigREG(DEVADDR, item.value);
                    this.base_drv.addr = DEVADDR.GetValue().byteValue();
                }
            }

            //修改波特率
            if (item.IsKey(BANDRANGEI.toString())) {
                for (int i = 0; i < SBandRate.length; i++) {
                    if (item.value.contentEquals(SBandRate[i])) {
                        this.SetConfigREG(BANDRANGEI, String.valueOf(i));
                        break;
                    }
                }
            }

            //修改温补开关
            if (item.IsKey(SDTEMPSWT.toString())) {
                this.SetConfigREG(SDTEMPSWT, item.value);
            }

            //修改温补值
            if (item.IsKey(SDTEMP.toString())) {
                this.SetConfigREG(SDTEMP, item.value);
            }
        }
    }

    protected void SetConfigREG(REG reg, String value) throws Exception {
        String lastvalue = reg.GetValue().toString();
        if (!reg.ConmpareTo(reg.Convert(value))) {
            try {
                reg.SetValue(reg.Convert(value));
                this.base_drv.SetREG(RETRY_TIME, DEF_TIMEOUT, reg);
            } catch (Exception ex) {
                reg.SetValue(reg.Convert(lastvalue));
                throw ex;
            }
        }
    }
    // </editor-fold>  

    @Override
    public IConfigList[] GetConfigLists() {
        return new IConfigList[]{
            new IConfigList() {
                @Override
                public String GetListName() {
                    return "设备信息";
                }

                @Override
                public ArrayList<SConfigItem> GetItemList() {
                    return GetInfoList();
                }

                @Override
                public void SetItemList(ArrayList<SConfigItem> list) throws Exception {
                    SetInfoList(list);
                }
            },
            new IConfigList() {
                @Override
                public String GetListName() {
                    return "参数设置";
                }

                @Override
                public ArrayList<SConfigItem> GetItemList() {
                    return GetConfigList();
                }

                @Override
                public void SetItemList(ArrayList<SConfigItem> list) throws Exception {
                    SetConfigList(list);
                }
            }
        };
    }
}
