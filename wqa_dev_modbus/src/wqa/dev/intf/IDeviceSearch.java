/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.dev.intf;

/**
 *
 * @author chejf
 */
public interface IDeviceSearch {
    public IDevice[] SearchDevice(IMAbstractIO io);
    
    public IDevice SearchOneDev(IMAbstractIO io, byte addr) throws Exception;

    public IDevice BuildDevice(IMAbstractIO io, byte addr, int DevType) throws Exception;
}
