/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.OSA;

import wqa.dev.data.*;
import wqa.dev.intf.*;

/**
 *
 * @author chejf
 */
public class OSA_X extends MOSA_X implements IDevMotorConfig {

    public OSA_X(SDevInfo devinfo) {
        super(devinfo);
    }

    // <editor-fold defaultstate="collapsed" desc="电机控制"> 
    //获取电机设置
    @Override
    public SMotorParameter GetMotoPara() {
        SMotorParameter par = new SMotorParameter(
                this.NCMODE.GetValue() == 00 ? SMotorParameter.CleanMode.Auto : SMotorParameter.CleanMode.Manu,
                new SConfigItem[]{
                    SConfigItem.CreateRWItem(NCTIME.toString(), NCTIME.GetValue().toString(), NCTIME.min + "-" + NCTIME.max),
                    SConfigItem.CreateRWItem(NCINTERVAL.toString(), NCINTERVAL.GetValue().toString(), NCINTERVAL.min + "-" + NCINTERVAL.max)},
                new SConfigItem[]{
                    SConfigItem.CreateRWItem(NCTIME.toString(), NCTIME.GetValue().toString(), NCTIME.min + "-" + NCTIME.max),
                    SConfigItem.CreateRWItem(NCINTERVAL.toString(), NCINTERVAL.GetValue().toString(), NCINTERVAL.min + "-" + NCINTERVAL.max),
                    SConfigItem.CreateRWItem(NCBRUSH.toString(), NCBRUSH.GetValue().toString(), NCBRUSH.min + "-" + NCBRUSH.max)});
        return par;
    }

    //设置电机设置
    @Override
    public void SetMotoPara(SMotorParameter par) throws Exception {
        SConfigItem[] list = par.mode == SMotorParameter.CleanMode.Auto ? par.auto_config : par.manu_config;
        for (SConfigItem item : list) {
            if (item.IsKey(NCTIME.toString())) {
                this.SetConfigREG(NCTIME, item.GetValue());
            }
            if (item.IsKey(NCINTERVAL.toString())) {
                this.SetConfigREG(NCINTERVAL, item.GetValue());
            }
            if (item.IsKey(NCBRUSH.toString())) {
                this.SetConfigREG(NCBRUSH, item.GetValue());
            }
        }

        //设置模式
        int tmode = par.mode == SMotorParameter.CleanMode.Auto ? 0 : 1;
        this.SetConfigREG(NCMODE, String.valueOf(tmode));
    }

    //手动清扫一次
    @Override
    public void StartManual() throws Exception {
        int ora = this.NCMODE.GetValue();
        //切到手动
        this.SetConfigREG(NCMODE, "1");
        //清扫一次
        this.SetConfigREG(NCMODE, "2");
        //恢复之前模式
        this.SetConfigREG(NCMODE, String.valueOf(ora));
    }
    // </editor-fold>  
}
