/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.MPA;

import base.migp.mem.MDA;
import base.migp.mem.NVPA;
import base.migp.reg.IMEG;
import migp.adapter.factory.AbsDevice;
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

    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    IMEG NCMODE = new IMEG(new NVPA(4, 2), "清扫模式", 0, 2);
    IMEG NCTIME = new IMEG(new NVPA(6, 2), "清扫次数", 1, 100);
    IMEG NCINTERVAL = new IMEG(new NVPA(8, 2), "清扫间隔(分钟)", 1, 24 * 60);
    IMEG NCBRUSH = new IMEG(new NVPA(10, 2), "清扫刷偏移量", 0, 360);
    // </editor-fold>  

    IMEG MALARM = new IMEG(new MDA(0x00, 2), "报警码");  // OSA_TURB | OSA_TSS | OSA_SS | OSA_CHLA | OSA_CYANO_I | OSA_OIL_I | OSA_MLSS | OSA_FDO

    public MPACBDevice(SDevInfo devinfo) {
        super(devinfo);
    }

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice(); //To change body of generated methods, choose Tools | Templates.

        //点击刷，初始化
        this.ReadMEG(NCMODE, NCTIME, NCINTERVAL, NCBRUSH);
    }
    

    @Override
    public wqa.dev.data.CollectData CollectData() throws Exception {
        CollectData disdata = this.BuildDisplayData();
        //读取数据
        this.ReadMEG(MALARM);
        disdata.alarm = MALARM.GetValue(); //报警信息
        String info = CErrorTable.GetInstance().GetErrorString(wqa.adapter.factory.CErrorTable.MPA_E | disdata.alarm);
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
                this.NCMODE.GetValue() == 00 ? SMotorParameter.CleanMode.Auto : SMotorParameter.CleanMode.Manu,
                new SConfigItem[]{
                    SConfigItem.CreateRWItem(NCTIME.toString(), NCTIME.GetValue().toString(), NCTIME.min + "-" + NCTIME.max),
                    SConfigItem.CreateRWItem(NCINTERVAL.toString(), NCINTERVAL.GetValue().toString(), NCINTERVAL.min + "-" + NCINTERVAL.max)},
                new SConfigItem[]{
                    SConfigItem.CreateRWItem(NCTIME.toString(), NCTIME.GetValue().toString(), NCTIME.min + "-" + NCTIME.max),
                    SConfigItem.CreateRWItem(NCINTERVAL.toString(), NCINTERVAL.GetValue().toString(), NCINTERVAL.min + "-" + NCINTERVAL.max),
                    SConfigItem.CreateRWItem(NCBRUSH.toString(), NCBRUSH.GetValue().toString(), NCBRUSH.min + "-" + NCBRUSH.max + " 度")});
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
