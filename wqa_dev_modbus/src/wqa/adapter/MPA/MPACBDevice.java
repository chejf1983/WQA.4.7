/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.MPA;

import modebus.register.IREG;
import wqa.adapter.factory.AbsDevice;
import wqa.adapter.factory.CErrorTable;
import wqa.dev.data.CollectData;
import wqa.dev.data.LogNode;
import wqa.dev.data.SDevInfo;
import wqa.dev.data.SMotorParameter;
import wqa.dev.intf.IDevMotorConfig;
import wqa.dev.intf.SConfigItem;

/**
 *
 * @author chejf
 */
public class MPACBDevice extends AbsDevice implements IDevMotorConfig {

    private final IREG ALARM = new IREG(0x00, 1, "报警码");//R

    protected final IREG CMODE = new IREG(0x08, 1, "清扫模式", 0, 2);//R/W
    protected final IREG CTIME = new IREG(0x09, 1, "清扫次数", 1, 100);//R/W
    protected final IREG CINTVAL = new IREG(0x0A, 1, "清扫间隔", 10, 60 * 24);//R/W
    protected final IREG CBRUSH = new IREG(0x0B, 1, "清扫刷偏移量", 0, 360);//R/W

    public MPACBDevice(SDevInfo info) {
        super(info);
    }

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice();

        //初始化寄存器
        this.ReadREG(CMODE, CTIME, CINTVAL, CBRUSH);
    }

    @Override
    public wqa.dev.data.CollectData CollectData() throws Exception {
        CollectData disdata = this.BuildDisplayData();
        this.ReadREG(ALARM);
        disdata.alarm = ALARM.GetValue();
        String info = CErrorTable.GetInstance().GetErrorString(CErrorTable.MPA_E | disdata.alarm);
        disdata.alram_info = info == null ? "" : info;
        return disdata;
    }

    @Override
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception {
        throw new Exception("不能定标"); //To change body of generated methods, choose Tools | Templates.
    }

    // <editor-fold defaultstate="collapsed" desc="电机控制"> 
    //获取电机设置
    @Override
    public SMotorParameter GetMotoPara() {
        SMotorParameter par = new SMotorParameter(
                this.CMODE.GetValue() == 00 ? SMotorParameter.CleanMode.Auto : SMotorParameter.CleanMode.Manu,
                new SConfigItem[]{
                    SConfigItem.CreateRWItem(this.CTIME.toString(), CTIME.GetValue().toString(), CTIME.min + "-" + CTIME.max),
                    SConfigItem.CreateRWItem(this.CINTVAL.toString(), CINTVAL.GetValue().toString(), CINTVAL.min + "-" + CINTVAL.max + "(min)")},
                new SConfigItem[]{
                    SConfigItem.CreateRWItem(this.CTIME.toString(), CTIME.GetValue().toString(), CTIME.min + "-" + CTIME.max),
                    SConfigItem.CreateRWItem(this.CINTVAL.toString(), CINTVAL.GetValue().toString(), CINTVAL.min + "-" + CINTVAL.max + "(min)"),
                    SConfigItem.CreateRWItem(this.CBRUSH.toString(), CBRUSH.GetValue().toString(), CBRUSH.min + "-" + CBRUSH.max + " 度")});
        return par;
    }

    //设置电机设置
    @Override
    public void SetMotoPara(SMotorParameter par) throws Exception {
        //设置参数
        SConfigItem[] list = par.mode == SMotorParameter.CleanMode.Auto ? par.auto_config : par.manu_config;
        for (SConfigItem item : list) {
            if (item.IsKey(CTIME.toString())) {
                this.SetConfigREG(CTIME, item.GetValue());
            }
            if (item.IsKey(CINTVAL.toString())) {
                this.SetConfigREG(CINTVAL, item.GetValue());
            }
            if (item.IsKey(CBRUSH.toString())) {
                this.SetConfigREG(CBRUSH, item.GetValue());
            }
        }

        //设置模式
        int tmode = par.mode == SMotorParameter.CleanMode.Auto ? 0 : 1;
        this.SetConfigREG(CBRUSH, tmode + "");
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
