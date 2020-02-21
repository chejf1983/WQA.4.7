/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.common;

import wqa.adapter.io.ShareIO;

/**
 *
 * @author chejf
 */
public interface IDeviceSearch {
    public IDevice[] SearchDevice(ShareIO io);
    
    public IDevice SearchOneDev(ShareIO io, byte addr) throws Exception;

}
