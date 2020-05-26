/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.winio.adapter;

import comm.absractio.WAbstractIO;
import comm.absractio.WIOInfo;
import comm.win.io.WindowsIOFactory;
import gnu.io.CommPortIdentifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;
import wqa.bill.io.IAbstractIO;
import wqa.bill.io.SIOInfo;
import wqa.bill.io.ShareIO;
import wqa.system.WQAPlatform;

/**
 *
 * @author chejf
 */
public class WComManager {

    private static WComManager instance;

    private WComManager() {
        String is_init = WQAPlatform.GetInstance().GetConfig().getProperty("FST", "false");
        try {
            if (is_init.contentEquals("true")) {
                WindowsIOFactory.InitWindowsIODriver(true);
            } else {
                WindowsIOFactory.InitWindowsIODriver();
            }
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, ex);
        }
    }

    public static WComManager GetInstance() {
        if (instance == null) {
            instance = new WComManager();
        }
        return instance;
    }

    // <editor-fold defaultstate="collapsed" desc="IO初始化"> 
    private ShareIO[] system_coms = new ShareIO[0];

    public void InitAllCom() {
        //列举所有系统支持串口
        String[] ComList = this.ListAllCom();
        system_coms = new ShareIO[ComList.length];
        for (int i = 0; i < system_coms.length; i++) {
            //查找配置文件当中的信息
            SIOInfo ioinfo = WQAPlatform.GetInstance().GetIOManager().GetIOConfig(ComList[i]);
            if (ioinfo == null) {
                //如果没有找到配置文件，新建一个9600的串口信息
                ioinfo = new SIOInfo(SIOInfo.COM, ComList[i], "9600");
                //如果不存在，保存配置信息
                WQAPlatform.GetInstance().GetIOManager().SaveIOConfig(ComList[i], ioinfo);
            }
            //查找IO库中是否已经存在该串口实体
            system_coms[i] = FindIO(ioinfo);
            if (system_coms[i] == null) {
                //创建IO
                system_coms[i] = WQAPlatform.GetInstance().GetIOManager().ADDShareIO(CreateIO(ioinfo));
            }
        }
    }

    //只根据串口号来查找IO
    private ShareIO FindIO(SIOInfo info) {
        for (ShareIO io : WQAPlatform.GetInstance().GetIOManager().GetAllIO()) {
            if (io.GetConnectInfo().par[0].contentEquals(info.par[0])) {
                return io;
            }
        }
        return null;
    }

    private String[] ListAllCom() {
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();

        ArrayList<String> ionames = new ArrayList();
        /* Foud Comm port */
        while (portList.hasMoreElements()) {
            CommPortIdentifier comportId = (CommPortIdentifier) portList.nextElement();
            ionames.add(comportId.getName());
        }
        //WAbstractIO wio = WindowsIOFactory.CreateIO(new WIOInfo("COM", new String[]{"COM5", baundrate}));

        //iolist.add(Convert(wio));
        return ionames.toArray(new String[0]);
    }

    private IAbstractIO Convert(WAbstractIO io) {
        return new IAbstractIO() {
            @Override
            public boolean IsClosed() {
                return io.IsClosed();
            }

            @Override
            public void Open() throws Exception {
                io.Open();
            }

            @Override
            public void Close() {
                io.Close();
            }

            @Override
            public void SendData(byte[] data) throws Exception {
                io.SendData(data);
            }

            @Override
            public int ReceiveData(byte[] data, int timeout) throws Exception {
                return io.ReceiveData(data, timeout);
            }

            @Override
            public SIOInfo GetConnectInfo() {
                WIOInfo info = io.GetConnectInfo();
                return new SIOInfo(info.iotype, info.par);
            }

            @Override
            public int MaxBuffersize() {
                return io.MaxBuffersize();
            }

            @Override
            public void SetConnectInfo(SIOInfo info) {
                io.SetConnectInfo(new WIOInfo(info.iotype, info.par));
            }
        };
    }

    public IAbstractIO CreateIO(SIOInfo ioinfo) {
        WAbstractIO wio = WindowsIOFactory.CreateIO(new WIOInfo(ioinfo.iotype, ioinfo.par));
        if (wio == null) {
            return null;
        }
        return Convert(wio);
    }
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="手动增加串口"> 
    private ArrayList<ShareIO> added_coms = new ArrayList<>();

    public boolean AddCom(String com) {
        for (ShareIO tio : this.GetAllCom()) {
            if (tio.GetConnectInfo().par[0].contentEquals(com)) {
                return false;
            }
        }

        SIOInfo ioinfo = new SIOInfo(SIOInfo.COM, com, "9600");
        //查找配置文件当中的信息
        ShareIO io = FindIO(ioinfo);
        if (io == null) {
            //创建IO
            io = WQAPlatform.GetInstance().GetIOManager().ADDShareIO(CreateIO(ioinfo));
        }
        added_coms.add(io);
        return true;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="修改波特率"> 
    public void ChangeBandrate(ShareIO ioinstance, String bandrate) throws Exception {
        SIOInfo ioinfo = ioinstance.GetConnectInfo();
        ioinfo.par[1] = bandrate;
        ioinstance.SetConnectInfo(ioinfo);
        WQAPlatform.GetInstance().GetIOManager().SaveIOConfig(ioinfo.par[0], ioinfo);
    }
    // </editor-fold> 

    //获取打开的串口
    public ShareIO[] GetAllOpenCom() {
        ArrayList<ShareIO> open_coms = new ArrayList();
        for (ShareIO io : GetAllCom()) {
            if (!io.IsClosed()) {
                open_coms.add(io);
            }
        }
        return open_coms.toArray(new ShareIO[0]);
    }

    //获取所有串口
    public ShareIO[] GetAllCom() {
        ShareIO[] ret = new ShareIO[added_coms.size() + system_coms.length];
        System.arraycopy(system_coms, 0, ret, 0, system_coms.length);
        System.arraycopy(added_coms.toArray(new ShareIO[0]), 0, ret, system_coms.length, added_coms.size());
        return ret;
    }

}
