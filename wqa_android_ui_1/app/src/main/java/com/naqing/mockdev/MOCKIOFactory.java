package com.naqing.mockdev;


import wqa.bill.io.IAbstractIO;
import wqa.bill.io.SIOInfo;
public class MOCKIOFactory {

    public static IAbstractIO BuildCom(SIOInfo info) {

            OSADevMock dev_mock = new OSADevMock();
            try {
                dev_mock.ResetREGS();
                return new MOCKIO(dev_mock.client,info);
            } catch (Exception ex) {
                return null;
            }
    }

    public static String[] ListAllCom(){
        return new String[]{"ACOM9"};
    }
}
