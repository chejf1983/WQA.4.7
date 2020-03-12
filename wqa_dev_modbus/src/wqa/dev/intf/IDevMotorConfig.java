/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.dev.intf;

import wqa.dev.data.SMotorParameter;


/**
 *
 * @author chejf
 */
public interface IDevMotorConfig  extends IDevice{

    public SMotorParameter GetMotoPara();

    public void SetMotoPara(SMotorParameter par) throws Exception;

    public void StartManual() throws Exception;
}
