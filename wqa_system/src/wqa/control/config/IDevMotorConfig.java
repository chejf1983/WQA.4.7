/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.config;

import wqa.control.data.SMotorParameter;
import wqa.control.common.IDevice;

/**
 *
 * @author chejf
 */
public interface IDevMotorConfig  extends IDevice{

    public SMotorParameter GetMotoPara();

    public void SetMotoPara(SMotorParameter par) throws Exception;

    public void StartManual() throws Exception;
}
