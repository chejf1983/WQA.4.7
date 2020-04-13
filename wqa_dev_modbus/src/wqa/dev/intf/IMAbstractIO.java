/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.dev.intf;

import wqa.dev.data.MIOInfo;

/**
 *
 * @author chejf
 */
public interface IMAbstractIO {
    /**
     * io 是否关闭
     *
     * @return
     */
    public boolean IsClosed();

    /**
     * 发送数据
     *
     * @param data
     * @throws Exception
     */
    public void SendData(byte[] data) throws Exception;

    /**
     * 接收数据，如果没有数据，data = {0};
     *
     * @param data
     * @param timeout
     * @return
     * @throws Exception
     */
    public int ReceiveData(byte[] data, int timeout) throws Exception;

    /**
     * 获取IO信息
     *
     * @return
     */
    public MIOInfo GetIOInfo();    

    //最大包长度
    public int MaxBuffersize();
}
