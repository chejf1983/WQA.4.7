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
import wqa.bill.io.IIOFactory;
import wqa.dev.data.SIOInfo;
import wqa.dev.intf.IAbstractIO;

/**
 *
 * @author chejf
 */
public class IOFactory implements IIOFactory {

    public IOFactory() {
        WindowsIOFactory.CreateIO(null);
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
            public void SetConnectInfo(SIOInfo info) throws Exception {
                io.SetConnectInfo(new WIOInfo(info.iotype, info.par));
            }
        };
    }

    @Override
    public IAbstractIO CreateIO(String type, String[] pars) {
        WAbstractIO wio = WindowsIOFactory.CreateIO(new WIOInfo(type, pars));
        if (wio == null) {
            return null;
        }
        return Convert(wio);
    }

    @Override
    public String[] ListAllCom() {
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
}
