/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.config;

import wqa.control.data.SMotorParameter;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;

/**
 *
 * @author chejf
 */
public class DevMotorConfig {

    private final IDevMotorConfig motorbean;
    private DevConfigBean msg_instance;

    public DevMotorConfig(IDevMotorConfig motorbean, DevConfigBean msg_instance) {
        this.motorbean = motorbean;
        this.msg_instance = msg_instance;
    }

    public SMotorParameter GetMotoPara() {
        return this.motorbean.GetMotoPara();
    }

    public void SetMotoPara(SMotorParameter par) {
        try {
            this.motorbean.LockDev();
            if (par == null) {
                LogCenter.Instance().SendFaultReport(Level.SEVERE, "入参不能为空");
                return;
            }
            this.motorbean.SetMotoPara(par);
            this.msg_instance.SetMessage("设置成功");
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "设置参数失败: ", ex);
        } finally {
            this.motorbean.UnLockDev();
        }
    }

    public void StartManual() {
        try {
            this.motorbean.LockDev();
            this.motorbean.StartManual();
            this.msg_instance.SetMessage("启动成功");
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "启动失败: ", ex);
        } finally {
            this.motorbean.UnLockDev();
        }
    }
}
