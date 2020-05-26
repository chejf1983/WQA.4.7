package android_serialport_api;

import wqa.bill.io.IAbstractIO;
import wqa.bill.io.SIOInfo;

public class SerialPortFactory {
    public static IAbstractIO BuildCom(SIOInfo info) {
        return new SerialPortIO(info);
    }

    public static String[] ListAllCom(){
        SerialPortFinder finder = new SerialPortFinder();
        return finder.getAllDevices();
    }
}
