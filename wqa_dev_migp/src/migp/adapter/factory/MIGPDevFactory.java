/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.factory;

import migp.adapter.DO.ESA_DO;
import migp.adapter.AMMO.ISA_X;
import migp.adapter.AMMO.ESA_AMMO;
import migp.adapter.DO.MOSA_FDO;
import migp.adapter.DO.OSA_FDOII;
import migp.adapter.DO.OSA_FDOI;
import base.migp.impl.MIGPEia;
import base.migp.mem.VPA;
import base.migp.node.MIGP_CmdSend;
import base.migp.reg.IMEG;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import migp.adapter.ESA.*;
import migp.adapter.MPA.*;
import migp.adapter.OSA.*;
import nahon.comm.io.AbstractIO;
import wqa.dev.data.*;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class MIGPDevFactory implements IDeviceSearch {

    // <editor-fold defaultstate="collapsed" desc="设备类目录">
    private final HashMap<Integer, String> class_map = new HashMap<>();

    public MIGPDevFactory() {
        //ESA
        class_map.put(0x0200, ESA_PH.class.getName());
        class_map.put(0x0220, ESA_PH.class.getName());
        class_map.put(0x0201, OSA_FDOI.class.getName());
        class_map.put(0x0202, ESA_EC.class.getName());
        class_map.put(0x0221, ESA_EC.class.getName());
        class_map.put(0x0203, ESA_EC.class.getName());
        class_map.put(0x0204, ESA_CHL.class.getName());
        class_map.put(0x0205, ESA_CHLII.class.getName());
        class_map.put(0x0208, ESA_ORP.class.getName());
        class_map.put(0x0209, ESA_AMMO.class.getName());
        class_map.put(0x0210, OSA_FDOI.class.getName());
        class_map.put(0xA210, OSA_FDOII.class.getName());
        //MESA
        class_map.put(0x1200, MESA_PH.class.getName());
        class_map.put(0x1201, MESA_ORP.class.getName());
        class_map.put(0x1202, MESA_EC.class.getName());
        class_map.put(0x1203, OSA_FDOII.class.getName());

        //OSA
        class_map.put(0x0100, OSA_X.class.getName());
        class_map.put(0x0101, OSA_NX.class.getName());
        class_map.put(0x0102, OSA_X.class.getName());
        class_map.put(0x0104, OSA_X.class.getName());
        class_map.put(0x0106, OSA_X.class.getName());
        class_map.put(0x0108, OSA_X.class.getName());
        class_map.put(0x010A, OSA_X.class.getName());
        class_map.put(0x010E, OSA_X.class.getName());
        class_map.put(0x0110, ESA_DO.class.getName());
        class_map.put(0xA110, OSA_FDOII.class.getName());
        //MOSA
        class_map.put(0x1100, MOSA_X.class.getName());
        class_map.put(0x1101, MOSA_FDO.class.getName());
        class_map.put(0x1102, MOSA_X.class.getName());
        class_map.put(0x1103, MOSA_X.class.getName());
        class_map.put(0x1104, MOSA_X.class.getName());
        class_map.put(0x1105, MOSA_X.class.getName());
        class_map.put(0x1110, MOSAII_X.class.getName());
        class_map.put(0x1111, MOSAII_X.class.getName());
        class_map.put(0x1112, MOSAII_X.class.getName());
        class_map.put(0x1113, MOSAII_X.class.getName());
        class_map.put(0x1114, MOSAII_X.class.getName());

        //ISA
        class_map.put(0x0300, ISA_X.class.getName());
        class_map.put(0x0301, ISA_X.class.getName());
        class_map.put(0x0308, ISA_X.class.getName());
        class_map.put(0x0309, ISA_X.class.getName());
        class_map.put(0x0310, ISA_X.class.getName());
        class_map.put(0x0311, ISA_X.class.getName());
        class_map.put(0x0312, ISA_X.class.getName());
        class_map.put(0x0320, ISA_X.class.getName());
        //ECA
        class_map.put(0x2100, ESA_PH.class.getName());
        class_map.put(0x2101, ESA_ORP.class.getName());
        class_map.put(0x2102, ESA_EC.class.getName());
        class_map.put(0x2103, MOSA_FDO.class.getName());
        class_map.put(0x2104, OSA_NX.class.getName());
        class_map.put(0x2105, OSA_NX.class.getName());
        //MPA
        class_map.put(0x1400, MPACBDevice.class.getName());
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="搜索设备">
    //搜索指定物理口
    @Override
    public IDevice[] SearchDevice(AbstractIO io) {
        //尝试打开IO口
        ArrayList<IDevice> devlist = new ArrayList();

        for (int i = 1; i < 0x20; i++) {
            try {
                //搜索设备基本信息，根据基本信息创建虚拟设备
                IDevice newdev = SearchOneDev(io, (byte) i);
                if (newdev != null) {
                    devlist.add(newdev);
                    TimeUnit.MILLISECONDS.sleep(50);
                }
            } catch (Exception ex) {
                //超时无所谓
            }
        }
        return devlist.toArray(new IDevice[0]);
    }

    IMEG VDEVTYPE = new IMEG(new VPA(0x00, 2), "设备类型");
    IMEG VDOATOKEN = new IMEG(new VPA(0x14, 2), "溶氧A版本标志");
    AbstractIO lastio;
    MIGP_CmdSend base;

    //搜索一个设备
    @Override
    public IDevice SearchOneDev(AbstractIO io, byte addr) throws Exception {
        if (lastio == io) {
            base.SetDstAddr(addr);
        } else {
            //创建一个基础协议包
            base = new MIGP_CmdSend((io), (byte) 0xF0, addr);
            lastio = io;
        }

        //搜索设备基本信息，根据基本信息创建虚拟设备
//        return null;
//        VPA VPA00 = new VPA(0x00, 2);//设备类型地址
//        VPA VPA20 = new VPA(0x14, 2);//溶氧A版本标志
        try {
            base.ReadMEG(1, 200, VDEVTYPE);
            //创建一个基础协议包
            if (VDEVTYPE.GetValue() == 0x110 || VDEVTYPE.GetValue() == 0x210) {
                base.ReadMEG(1, 200, VDOATOKEN);
                if (VDOATOKEN.GetValue() > 0) {
                    VDEVTYPE.SetValue(VDEVTYPE.GetValue() + 0xA000);
                }
            }
            return this.BuildDevice(io, (byte) addr, VDEVTYPE.GetValue());
        } catch (Exception ex) {
//            System.out.println(ex);
            return null;
        }
    }

    //创建设备
    @Override
    public IDevice BuildDevice(AbstractIO io, byte addr, int DevType) throws Exception {
        //根据设备类型创建设备类
        String class_name = class_map.get(DevType);
        if (class_name != null) {
            //反射获取对应类
            Class stu = Class.forName(class_name);
            Constructor constructor = stu.getConstructor(SDevInfo.class);
            SDevInfo devinfo = new SDevInfo();
            devinfo.io = io;
            devinfo.dev_addr = addr;
            devinfo.dev_type = DevType;
            devinfo.protype = SDevInfo.ProType.MIGP;
            devinfo.serial_num = "";
            return (IDevice) constructor.newInstance(devinfo);
        } else {
            //没有找到设备类，返回空
            if (DevType != -1) {
                System.out.println(String.format("无法识别设备类型:0x%04X", DevType));
                Logger.getLogger(MIGPDevFactory.class.getName()).log(Level.SEVERE, null, String.format("无法识别设备类型:0x%04X", DevType));
            }
            //System.out.println(String.format("0x%04X", DevType));
            return null;
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="协议信息">
    @Override
    public String ProType() {
        return SDevInfo.ProType.MIGP.toString();
    }

    public static int getDefTimeout() {
        return AbsDevice.DEF_TIMEOUT;
    }

    public static void setDefTimeout(int timeout) {
        if (timeout < 100) {
            timeout = 100;
        } else if (timeout > 1000) {
            timeout = 1000;
        }
        AbsDevice.DEF_TIMEOUT = timeout;
    }
    // </editor-fold> 

    public static void main(String... args) {
        MockDev dev = new MockDev((byte) 0x00);
        MIGPEia eiainfo = new MIGPEia(null);
        try {
            eiainfo.EBUILDSER.SetValue("NAQ0309120337776");
            eiainfo.EDEVNAME.SetValue("AVVOR9000");
            eiainfo.EBUILDDATE.SetValue("20200911");
        } catch (Exception ex) {
            Logger.getLogger(MIGPDevFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

//        dev.GETMEG(eiainfo.ESWVER);
//        dev.GETMEG(eiainfo.EBUILDDATE);
        dev.GETMEG(eiainfo.EDEVNAME);
        dev.SETMEG(eiainfo.EDEVNAME,eiainfo.EBUILDSER,eiainfo.EBUILDDATE);

    }
}
