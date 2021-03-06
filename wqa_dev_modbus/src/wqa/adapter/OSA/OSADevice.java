/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.OSA;

import modebus.register.IREG;
import wqa.dev.data.SDevInfo;
import wqa.dev.data.SMotorParameter;
import wqa.dev.data.SMotorParameter.CleanMode;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class OSADevice extends MOSADevice implements IDevMotorConfig {

    protected final IREG CMODE = new IREG(0x08, 1, "清扫模式", 0, 2);//R/W
    protected final IREG CTIME = new IREG(0x09, 1, "清扫次数", 1, 100);//R/W
    protected final IREG CINTVAL = new IREG(0x0A, 1, "清扫间隔", 10, 60 * 24);//R/W

    public OSADevice(SDevInfo info) {
        super(info);
    }

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice();

        //初始化寄存器
        this.ReadREG(CMODE, CTIME, CINTVAL);
    }

    // <editor-fold defaultstate="collapsed" desc="电机控制"> 
    @Override
    public SMotorParameter GetMotoPara() {
        SMotorParameter par = new SMotorParameter(
                this.CMODE.GetValue() == 00 ? CleanMode.Auto : CleanMode.Manu,
                new SConfigItem[]{
                    SConfigItem.CreateRWItem(this.CTIME.toString(), CTIME.GetValue().toString(), CTIME.min + "-" + CTIME.max),
                    SConfigItem.CreateRWItem(this.CINTVAL.toString(), CINTVAL.GetValue().toString(), CINTVAL.min + "-" + CINTVAL.max + "(min)")},
                new SConfigItem[0]);
        return par;
    }

    @Override
    public void SetMotoPara(SMotorParameter par) throws Exception {
        //设置参数
        for (SConfigItem item : par.auto_config) {
            if (item.IsKey(CTIME.toString())) {
                this.CTIME.SetValue(Integer.valueOf(item.GetValue()));

            }
            if (item.IsKey(CINTVAL.toString())) {
                CINTVAL.SetValue(Integer.valueOf(item.GetValue()));
            }
        }

        //设置模式
        this.CMODE.SetValue(par.mode == CleanMode.Auto ? 0 : 1);
        this.SetREG(CMODE, CTIME, CINTVAL);
    }

    @Override
    public void StartManual() throws Exception {
        int ora = this.CMODE.GetValue();
        //切到手动
        this.SetConfigREG(CMODE, "1");
        //清扫一次
        this.SetConfigREG(CMODE, "2");
        //恢复之前模式
        this.SetConfigREG(CMODE, String.valueOf(ora));
    }
    // </editor-fold>  
}
