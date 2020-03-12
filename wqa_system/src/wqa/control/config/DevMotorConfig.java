/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.config;

import wqa.dev.data.SMotorParameter;
import java.util.logging.Level;
import nahon.comm.faultsystem.LogCenter;
import wqa.adapter.io.ShareIO;
import wqa.dev.intf.IDevMotorConfig;

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
            ((ShareIO) motorbean.GetIO()).Lock();
            if (par == null) {
                LogCenter.Instance().SendFaultReport(Level.SEVERE, "入参不能为空");
                return;
            }
            this.motorbean.SetMotoPara(par);
            this.msg_instance.SetMessage("设置成功");
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "设置参数失败: ", ex);
        } finally {
            ((ShareIO) motorbean.GetIO()).UnLock();
        }
    }

    public void StartManual() {
        try {
            ((ShareIO) motorbean.GetIO()).Lock();
            this.motorbean.StartManual();
            this.msg_instance.SetMessage("启动成功");
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.SEVERE, "启动失败: ", ex);
        } finally {
            ((ShareIO) motorbean.GetIO()).UnLock();
        }
    }
}
