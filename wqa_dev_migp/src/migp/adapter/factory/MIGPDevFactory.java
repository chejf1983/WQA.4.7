/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.factory;

import base.migp.mem.VPA;
import base.migp.node.MIGP_CmdSend;
import base.pro.absractio.AbstractIO;
import base.pro.absractio.IOInfo;
import base.pro.convert.NahonConvert;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import migp.adapter.ESA.*;
import migp.adapter.ISA.ISA_X;
import migp.adapter.OSA.OSA_X;
import static migp.adapter.factory.AbsDevice.DEF_RETRY;
import static migp.adapter.factory.AbsDevice.DEF_TIMEOUT;
import wqa.dev.data.MIOInfo;
import wqa.dev.data.SDevInfo;
import wqa.dev.intf.IDevice;
import wqa.dev.intf.IDeviceSearch;
import wqa.dev.intf.IMAbstractIO;

/**
 *
 * @author chejf
 */
public class MIGPDevFactory implements IDeviceSearch {

    // <editor-fold defaultstate="collapsed" desc="设备类目录">
    private final HashMap<Integer, String> class_map = new HashMap<>();

    public MIGPDevFactory() {
        class_map.put(0x0200, ESA_PH.class.getName());
        class_map.put(0x0220, ESA_PH.class.getName());
        class_map.put(0x0201, EOSA_DO.class.getName());
        class_map.put(0x0202, ESA_EC.class.getName());
        class_map.put(0x0221, ESA_EC.class.getName());
        class_map.put(0x0203, ESA_EC.class.getName());
        class_map.put(0x0208, ESA_ORP.class.getName());
        class_map.put(0x0209, ESA_AMMO.class.getName());
        class_map.put(0x0210, EOSA_DO.class.getName());
        class_map.put(0xA210, EOSA_DO.class.getName());

        class_map.put(0x1200, ESA_PH.class.getName());
        class_map.put(0x1201, ESA_ORP.class.getName());
        class_map.put(0x1202, ESA_EC.class.getName());
        class_map.put(0x1203, EOSA_DO.class.getName());
        
        class_map.put(0x0100, OSA_X.class.getName());
        class_map.put(0x0102, OSA_X.class.getName());
        class_map.put(0x0104, OSA_X.class.getName());
        class_map.put(0x0106, OSA_X.class.getName());
        class_map.put(0x0108, OSA_X.class.getName());
        class_map.put(0x010A, OSA_X.class.getName());
        class_map.put(0x010E, OSA_X.class.getName());
        class_map.put(0x0110, EOSA_DO.class.getName());
        class_map.put(0xA110, EOSA_DO.class.getName());

        class_map.put(0x0300, ISA_X.class.getName());
        class_map.put(0x0301, ISA_X.class.getName());
        class_map.put(0x0308, ISA_X.class.getName());
        class_map.put(0x0309, ISA_X.class.getName());
        class_map.put(0x0310, ISA_X.class.getName());
        class_map.put(0x0311, ISA_X.class.getName());
        class_map.put(0x0312, ISA_X.class.getName());
        class_map.put(0x0320, ISA_X.class.getName());
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="搜索设备">
    //搜索指定物理口
    @Override
    public IDevice[] SearchDevice(IMAbstractIO io) {
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

    //搜索一个设备
    @Override
    public IDevice SearchOneDev(IMAbstractIO io, byte addr) throws Exception {
        //创建一个基础协议包
        MIGP_CmdSend base = new MIGP_CmdSend(Convert(io), (byte) 0xF0, addr);
        //搜索设备基本信息，根据基本信息创建虚拟设备
//        return null;
        return this.BuildDevice(io, (byte) addr, SearchDevType(base));
    }

    //创建设备
    @Override
    public IDevice BuildDevice(IMAbstractIO io, byte addr, int DevType) throws Exception {
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
            devinfo.protype = SDevInfo.ProType.MODEBUS;
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

    //获取设备类型函数
    public static int SearchDevType(MIGP_CmdSend sender) {
        VPA VPA00 = new VPA(0x00, 2);//设备类型地址
        VPA VPA20 = new VPA(0x14, 2);//溶氧A版本标志
        try {
            //创建一个基础协议包
            byte[] ret = sender.GetMEM(VPA00, VPA00.length, 1, 200);

            //搜索设备基本信息，根据基本信息创建虚拟设备
            int devtype = NahonConvert.ByteArrayToUShort(ret, 0);
            //检查是否是A版本的溶解氧
            if (devtype == 0x110 || devtype == 0x210) {
                ret = sender.GetMEM(VPA20, VPA20.length, DEF_RETRY, DEF_TIMEOUT);
                if (NahonConvert.ByteArrayToUShort(ret, 0) > 0) {
                    devtype = devtype + 0xA000;
                }
            }
            return devtype;
        } catch (Exception ex) {
            System.out.println(ex);
            return -1;
        }
    }

    public static AbstractIO Convert(IMAbstractIO io) {
        return new AbstractIO() {
            private final IMAbstractIO instance = io;

            @Override
            public boolean IsClosed() {
                return instance.IsClosed();
//                return false;
            }

            @Override
            public void Open() throws Exception {
//                this.instance.Open();
            }

            @Override
            public void Close() {
//                this.instance.Close();
            }

            @Override
            public void SendData(byte[] data) throws Exception {
                this.instance.SendData(data);
            }

            @Override
            public int ReceiveData(byte[] data, int timeout) throws Exception {

                int len = this.instance.ReceiveData(data, timeout);
//                System.out.println("收到" + len);
                return len;
            }

            @Override
            public IOInfo GetConnectInfo() {
                MIOInfo ioinfo = this.instance.GetIOInfo();
                return new IOInfo(ioinfo.iotype, ioinfo.par);
            }

            @Override
            public int MaxBuffersize() {
                return this.instance.MaxBuffersize();
//                return 65535;
            }
        };
    }
    // </editor-fold> 

}
