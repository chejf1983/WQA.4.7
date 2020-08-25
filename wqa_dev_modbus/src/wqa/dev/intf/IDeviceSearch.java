/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.dev.intf;

import nahon.comm.io.AbstractIO;

/**
 *
 * @author chejf
 */
public interface IDeviceSearch {
    public IDevice[] SearchDevice(AbstractIO io);
    
    public IDevice SearchOneDev(AbstractIO io, byte addr) throws Exception;

    public IDevice BuildDevice(AbstractIO io, byte addr, int DevType) throws Exception;
    
    public String ProType();
}
