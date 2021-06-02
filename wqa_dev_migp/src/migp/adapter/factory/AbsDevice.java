/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.factory;

import base.migp.impl.*;
import base.migp.mem.VPA;
import base.migp.node.MIGP_CmdSend;
import base.migp.reg.*;
import base.pro.convert.NahonConvert;
import java.util.ArrayList;
import nahon.comm.io.IOInfo;
import wqa.adapter.factory.CDevDataTable;
import wqa.adapter.factory.CDevDataTable.DataInfo;
import wqa.dev.intf.*;
import wqa.dev.data.*;

/**
 *
 * @author chejf
 */
public abstract class AbsDevice implements IDevice {

    protected MIGP_CmdSend base_drv;
    public static int DEF_TIMEOUT = 300; //ms
    public static int DEF_RETRY = 3;

    MIGPEia eiainfo = new MIGPEia(this.base_drv);
//    public final IMEG VDEVTYPE = new IMEG(new VPA(0x00, 2), "设备类型");  //R  

    public AbsDevice(SDevInfo devinfo) {
        this.base_drv = new MIGP_CmdSend(devinfo.io, (byte) 0xF0, (byte) devinfo.dev_addr);
        this.sinfo = devinfo;
    }

    // <editor-fold defaultstate="collapsed" desc="公共接口">
    //初始化设备
    @Override
    public void InitDevice() throws Exception {
        //获取eia信息
//        IMEG VDEVTYPE = new IMEG(new VPA(0x00, 2), "设备类型");
//        this.ReadMEG(VDEVTYPE);
        this.ReadMEG(eiainfo.EBUILDDATE, eiainfo.EBUILDSER, eiainfo.EDEVNAME, eiainfo.EHWVER, eiainfo.ESWVER);

//        if (GetDevInfo().dev_type != (VDEVTYPE.GetValue())) {
//            throw new Exception("探头信息不匹配");
//        }
        sinfo.serial_num = this.eiainfo.EBUILDSER.GetValue();
        IOInfo comm_info = this.GetDevInfo().io.GetConnectInfo();
        if (comm_info.iotype.equals(IOInfo.COM)) {
            //串口的情况下，参数1表示波特率
            String sbandrate = comm_info.par[1];
            for (int i = 0; i < BANDRATE_STRING.length; i++) {
                if (sbandrate.contentEquals(BANDRATE_STRING[i])) {
                    //比对出相同的波特率
                    this.bandrate_index = i;
                    break;
                }
            }
        } else {
            this.bandrate_index = this.getbandrate();
        }
    }

    //获取设备类型
    @Override
    public boolean ReTestType(int retry) {
        IMEG VDEVTYPE = new IMEG(new VPA(0x00, 2), "设备类型");
        try {
            this.base_drv.ReadMEG(retry, 200, VDEVTYPE);
            return VDEVTYPE.GetValue() == this.GetDevInfo().dev_type;
        } catch (Exception ex) {
//            System.out.println(ex);
            return false;
        }
    }

    //初始化连接信息
    private SDevInfo sinfo = new SDevInfo();

    //获取连接信息
    @Override
    public SDevInfo GetDevInfo() {
        return this.sinfo;
    }

    //定标点个数
    @Override
    public DataInfo[] GetCalDataList() {
        ArrayList<DataInfo> list = new ArrayList();
        DataInfo[] data_list = CDevDataTable.GetInstance().namemap.get(this.GetDevInfo().dev_type).data_list;
//        int[] data_cal_num = new int[data_list.length];
        for (int i = 0; i < data_list.length; i++) {
            if (data_list[i].cal_num > 0) {
                list.add(data_list[i]);
            }
        }
        return list.toArray(new DataInfo[0]);
    }

    public boolean IsOverVersion(int version_threshold) {
        if (this.eiainfo.ESWVER.GetValue().startsWith("D")) {
            try {
                int version = Integer.valueOf(this.eiainfo.ESWVER.GetValue().substring(1));
                return version >= version_threshold;
            } catch (NumberFormatException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return false;
    }
    public static int AMPPAR = 4095;

    public float getAmplyfy(int value) {
        if (value == 0) {
            return AMPPAR;
        } else {
            return NahonConvert.TimData((float) AMPPAR / value, 2);
        }
    }

    public int setAmplyfy(float value) {
        float famply = AMPPAR;
        if (value != 0) {
            famply = AMPPAR / value;
        }
        int amply = (int) (famply + 0.5);
        amply = amply > AMPPAR ? AMPPAR : amply;
        return amply;
    }

//    public SConfigItem getAmplfyItem(IMEG reg) {
//        if (reg.GetValue() == 0) {
//            return (SConfigItem.CreateRWItem(reg.toString(), AMPPAR + "", ""));
//        } else {
//            return (SConfigItem.CreateRWItem(reg.toString(), NahonConvert.TimData((float) AMPPAR / reg.GetValue(), 2) + "", ""));
//        }
//    }
//
//    public void setAmplyfyItem(MEG reg, String value) throws Exception {
//        float tmp = Float.valueOf(value);
//        float famply = AMPPAR;
//        if (tmp != 0) {
//            famply = AMPPAR / Float.valueOf(value);
//        }
//        int amply = (int) (famply + 0.5);
//        amply = amply > AMPPAR ? AMPPAR : amply;
//        this.SetConfigREG(reg, String.valueOf(amply));
//    }

    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="内部接口">
    //只返回不同测量类型的数据(没有原始值这个标志的数据)
    protected String[] GetDataNames() {
        DataInfo[] data_list = CDevDataTable.GetInstance().namemap.get(this.GetDevInfo().dev_type).data_list;
        ArrayList<String> data_names = new ArrayList();
        for (DataInfo data_list1 : data_list) {
            if (!data_list1.data_name.endsWith(CDevDataTable.ORA_Flag)) {
                data_names.add(data_list1.data_name);
            }
        }
        return data_names.toArray(new String[0]);
    }

    protected CollectData BuildDisplayData() {
        CollectData disdata = new CollectData(this.GetDevInfo().dev_type, this.base_drv.GetDstAddr(), this.eiainfo.EBUILDSER.GetValue());

        return disdata;
    }

    private void SortMEG(MEG[] megs) {
        for (int i = 0; i < megs.length; i++) {
            for (int j = i; j < megs.length; j++) {
                if (megs[i].GetMEM().addr > megs[j].GetMEM().addr) {
                    MEG tmp = megs[i];
                    megs[i] = megs[j];
                    megs[j] = tmp;
                }
            }
        }
    }

    private int max_num = 30;

    protected void ReadMEG(MEG... megs) throws Exception {
        if (megs.length > max_num) {
            SortMEG(megs);
        }

        for (int i = 0; i < megs.length; i += max_num) {
            MEG[] tmp;
            if (megs.length - i < max_num) {
                tmp = new MEG[megs.length - i];
            } else {
                tmp = new MEG[max_num];
            }

            System.arraycopy(megs, i, tmp, 0, tmp.length);
            this.base_drv.ReadMEG(DEF_RETRY, DEF_TIMEOUT, tmp);
        }
    }

    protected void SetMEG(MEG... megs) throws Exception {
        if (megs.length > max_num) {
            SortMEG(megs);
        }
        for (int i = 0; i < megs.length; i += max_num) {
            MEG[] tmp;
            if (megs.length - i < max_num) {
                tmp = new MEG[megs.length - i];
            } else {
                tmp = new MEG[max_num];
            }

            System.arraycopy(megs, i, tmp, 0, tmp.length);
            this.base_drv.SetMEG(DEF_RETRY, DEF_TIMEOUT, tmp);
        }
    }
    // </editor-fold>     

    // <editor-fold defaultstate="collapsed" desc="配置接口"> 
    // <editor-fold defaultstate="collapsed" desc="设备信息"> 
    //设备信息
    public ArrayList<SConfigItem> GetInfoList() {
        ArrayList<SConfigItem> list = new ArrayList();
        list.add(SConfigItem.CreateRWItem(eiainfo.EDEVNAME.toString(), eiainfo.EDEVNAME.GetValue(), ""));
        list.add(SConfigItem.CreateRWItem(eiainfo.EBUILDSER.toString(), eiainfo.EBUILDSER.GetValue().trim(), ""));
        list.add(SConfigItem.CreateRWItem(eiainfo.EBUILDDATE.toString(), eiainfo.EBUILDDATE.GetValue(), ""));
        list.add(SConfigItem.CreateRItem(eiainfo.ESWVER.toString(), eiainfo.ESWVER.GetValue(), ""));
        list.add(SConfigItem.CreateRItem(eiainfo.EHWVER.toString(), eiainfo.EHWVER.GetValue(), ""));
        list.add(SConfigItem.CreateRItem("设备类型", String.format("0X%04X", this.GetDevInfo().dev_type), ""));
        return list;
    }

    public void SetInfoList(ArrayList<SConfigItem> list) throws Exception {
        for (SConfigItem item : list) {
            //更新名字
            if (item.IsKey(eiainfo.EDEVNAME.toString())) {
                SetConfigREG(eiainfo.EDEVNAME, item.GetValue());
            }
            if (item.IsKey(eiainfo.EBUILDSER.toString())) {
                SetConfigREG(eiainfo.EBUILDSER, item.GetValue());
                this.sinfo.serial_num = eiainfo.EBUILDSER.GetValue();
            }
            if (item.IsKey(eiainfo.EBUILDDATE.toString())) {
                SetConfigREG(eiainfo.EBUILDDATE, item.GetValue());
            }
        }
    }

    protected void SetConfigREG(MEG reg, String value) throws Exception {
        String lastvalue = reg.GetValue().toString();
        if (!reg.ConmpareTo(reg.Convert(value))) {
            try {
                reg.SetValue(reg.Convert(value));
                this.SetMEG(reg);
            } catch (Exception ex) {
                reg.SetValue(reg.Convert(lastvalue));
                throw ex;
            }
        }
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="设备配置接口"> 
    public static final String[] BANDRATE_STRING = new String[]{"4800","9600", "19200", "38400", "57600", "115200"};

    public static String SDevAddr = "设备地址";
    public static String SBandRate = "波特率";
    private int bandrate_index = 1;
    public static final byte MIGP_CMD_SET_COMM_PARA = 0x07;
    public static final byte MIGP_CMD_GET_COMM_PARA = 0x08;

    //设置
    public ArrayList<SConfigItem> GetConfigList() {
        ArrayList<SConfigItem> list = new ArrayList();
        list.add(SConfigItem.CreateRWItem(SDevAddr, String.valueOf(this.base_drv.GetDstAddr()), "1~32"));
        list.add(SConfigItem.CreateSItem(SBandRate, String.valueOf(BANDRATE_STRING[this.bandrate_index]), "", BANDRATE_STRING));
        return list;
    }

    public void SetConfigList(ArrayList<SConfigItem> list) throws Exception {
        for (SConfigItem item : list) {
            //修改设备地址
            if (item.IsKey(SDevAddr)) {
                this.setaddr(Integer.valueOf(item.GetValue()));
            }

            //修改波特率
            if (item.IsKey(SBandRate)) {
                this.setbandrate(item.GetValue());
            }
        }
    }

    //修改设备地址
    private void setaddr(int devaddr) throws Exception {
        if (devaddr < 1 || devaddr > 32) {
            throw new Exception("地址需要在(1-32)");
        }

        if (devaddr != this.base_drv.GetDstAddr()) {
            MIGP_CmdSend base = new MIGP_CmdSend(this.GetDevInfo().io, (byte) 0xF0, (byte) devaddr);
            VPA devtype = new VPA(0x00, 2);
            try {
                base.GetMEM(devtype, devtype.length, 1, DEF_TIMEOUT);
            } catch (Exception ex) {
                new MIGPBoot(this.base_drv).SetDevNum((byte) devaddr);
                this.base_drv.SetDstAddr((byte) devaddr);
                this.sinfo.dev_addr = devaddr;
                return;
            }
            throw new Exception("该地址已经存在!");
        }
    }

    //修改波特率
    private void setbandrate(String value) throws Exception {
        for (int i = 0; i < this.BANDRATE_STRING.length; i++) {
            if (value.contentEquals(this.BANDRATE_STRING[i])) {
                if (i != this.bandrate_index) {
                    this.base_drv.SendRPC(this.MIGP_CMD_SET_COMM_PARA, new byte[]{0x00, (byte) i}, DEF_RETRY, DEF_TIMEOUT);
                    this.bandrate_index = i;
                }
                return;
            }
        }
        throw new Exception("超过波特率范围");
    }

    //获取波特率
    private int getbandrate() throws Exception {
        byte[] ret = this.base_drv.SendRPC(this.MIGP_CMD_GET_COMM_PARA, new byte[]{0x00}, DEF_RETRY, DEF_TIMEOUT);
        return (int) ret[0];
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="效准系数配置接口"> 
    public ArrayList<SConfigItem> GetCalParList() {
        return new ArrayList<>();
    }

    public void SetCalParList(ArrayList<SConfigItem> list) throws Exception {

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
            },
            new IConfigList() {
                @Override
                public String GetListName() {
                    return "校准系数";
                }

                @Override
                public ArrayList<SConfigItem> GetItemList() {
                    return GetCalParList();
                }

                @Override
                public void SetItemList(ArrayList<SConfigItem> list) throws Exception {
                    SetCalParList(list);
                }
            }
        };
    }
    // </editor-fold> 

}
