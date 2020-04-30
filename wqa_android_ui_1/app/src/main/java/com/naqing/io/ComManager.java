/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.naqing.io;

import com.naqing.mockdev.MOCKIOFactory;

import java.util.ArrayList;
import java.util.EventListener;

import wqa.bill.io.SIOInfo;
import wqa.bill.io.ShareIO;
import wqa.system.WQAPlatform;

/**
 * @author chejf
 */
public class ComManager implements EventListener {
    private ArrayList<ShareIO> localio = new ArrayList();

    //刷新IO口
    public void InitIO() throws Exception {
        this.localio.clear();

        for (String com : MOCKIOFactory.ListAllCom()) {
            SIOInfo sioInfo = WQAPlatform.GetInstance().GetIOManager().GetIOConfig(com);
            if (sioInfo == null) {
                sioInfo = new SIOInfo("COM", com, "9600");
                WQAPlatform.GetInstance().GetIOManager().SaveIOConfig(com, sioInfo);
            }

            ShareIO io = WQAPlatform.GetInstance().GetIOManager().FindIO(sioInfo);
            if (io == null) {
                io = WQAPlatform.GetInstance().GetIOManager().ADDShareIO(MOCKIOFactory.BuildCom(sioInfo));
            }
            this.localio.add(io);
        }
    }

    public ShareIO FindIO(SIOInfo ioinfo) {
        for (ShareIO io : localio) {
            if (io.GetConnectInfo().equalto(ioinfo)) {
                return io;
            }
        }
        return null;
    }

    public ShareIO[] GetAllCOM() {
        return localio.toArray(new ShareIO[0]);
    }

    public String[] GetAllCOMName(){
        String[] coms = new String[GetAllCOM().length];
        for(int i = 0; i < coms.length; i++){
            coms[i] = GetAllCOM()[i].GetConnectInfo().par[0];
        }
        return coms;
    }

    public void ChangeBandRate(ShareIO io, String bandrate) {
        SIOInfo info = io.GetConnectInfo();
        if (info.iotype.contentEquals(SIOInfo.COM)) {
            info.par[1] = bandrate;
            io.SetConnectInfo(info);
            WQAPlatform.GetInstance().GetIOManager().SaveIOConfig(info.par[0], info);
        }
    }

    public String GetKey(ShareIO io) {
        if (io.GetConnectInfo().iotype.contentEquals(SIOInfo.COM))
            return io.GetConnectInfo().par[0];
        else
            return "";
    }

    public static String[] BandRate = new String[]{"4800", "9600", "19200", "38400", "57600", "115200"};
}
