/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.factory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import modebus.pro.ModeBusNode;
import modebus.register.IREG;
import modebus.register.SREG;
import wqa.adapter.ESA.DODevice;
import wqa.adapter.ESA.EAMMODevice;
import wqa.adapter.ESA.ECDevice;
import wqa.adapter.ESA.PHDevice;
import wqa.adapter.ISA.AMMODevice;
import wqa.adapter.OSA.OSADevice;
import wqa.dev.data.SDevInfo;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class ModBusDevFactory implements IDeviceSearch {
    //搜索指定物理口
    @Override
    public IDevice[] SearchDevice(IMAbstractIO io) {
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
        ModeBusNode base = new ModeBusNode(io, addr);
        IREG DEVTYPE = new IREG(0x25, 1, "设备类型", 1, 32);//R
        SREG SERIANUM = new SREG(0x18, 8, "序列号");//R
        base.ReadREG(1, 250, DEVTYPE);
        base.ReadREG(1, 250, SERIANUM);
        //搜索设备基本信息，根据基本信息创建虚拟设备
        return this.BuildDevice(io, (byte) addr, DEVTYPE.GetValue(), SERIANUM.GetValue());
    }

    //创建设备
    @Override
    public IDevice BuildDevice(IMAbstractIO io, byte addr, int DevType, String SerialNum) throws Exception {
        //根据设备类型创建设备类
        String class_name = class_map.get(DevType);
        if (class_name != null) {
            Class stu = Class.forName(class_name);
            Constructor constructor = stu.getConstructor(SDevInfo.class);
            SDevInfo devinfo = new SDevInfo();
            devinfo.io = io;
            devinfo.dev_addr = addr;
            devinfo.dev_type = DevType;
            devinfo.protype = SDevInfo.ProType.MODEBUS;
            devinfo.serial_num = SerialNum;
            return (IDevice) constructor.newInstance(devinfo);
        } else {
            if (DevType != -1) {
                System.out.println(String.format("无法识别设备类型:0x%04X", DevType));
                Logger.getLogger(ModBusDevFactory.class.getName()).log(Level.SEVERE, null, String.format("无法识别设备类型:0x%04X", DevType));
            }
            //System.out.println(String.format("0x%04X", DevType));
            return null;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="设备类目录">
    private final HashMap<Integer, String> class_map = new HashMap<>();
    
    public ModBusDevFactory() {
        class_map.put(0x0200, PHDevice.class.getName());
        class_map.put(0x0201, DODevice.class.getName());
        class_map.put(0x0202, ECDevice.class.getName());
        class_map.put(0x0203, ECDevice.class.getName());
        class_map.put(0x0208, PHDevice.class.getName());
        class_map.put(0x0209, EAMMODevice.class.getName());
        class_map.put(0x0210, DODevice.class.getName());

        
        class_map.put(0x1200, PHDevice.class.getName());
        class_map.put(0x1201, PHDevice.class.getName());
        class_map.put(0x1202, ECDevice.class.getName());
        class_map.put(0x1203, DODevice.class.getName());
        
        class_map.put(0x0100, OSADevice.class.getName());
        class_map.put(0x0102, OSADevice.class.getName());
        class_map.put(0x0104, OSADevice.class.getName());
        class_map.put(0x0106, OSADevice.class.getName());
        class_map.put(0x0108, OSADevice.class.getName());
        class_map.put(0x010A, OSADevice.class.getName());
        class_map.put(0x010E, OSADevice.class.getName());
        class_map.put(0x0110, DODevice.class.getName());

        class_map.put(0x0300, AMMODevice.class.getName());
        class_map.put(0x0301, AMMODevice.class.getName());
    }
    // </editor-fold> 
}
