/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.OSA;

import base.migp.mem.SRA;
import base.migp.reg.FMEG;
import base.migp.reg.IMEG;
import java.util.Arrays;
import wqa.dev.data.LogNode;
import wqa.dev.data.SDevInfo;

/**
 *
 * @author chejf
 */
public class MOSA_FDO extends OSA_FDOII {
    // <editor-fold defaultstate="collapsed" desc="SRA"> 
    public FMEG SR42 = new FMEG(new SRA(42, 4), "零点信号数据");
    public FMEG SR46 = new FMEG(new SRA(46, 4), "饱和氧信号数据");
    public IMEG SR50 = new IMEG(new SRA(50, 2), "定标使能");
    public FMEG SR52 = new FMEG(new SRA(52, 4), "温度定标数据");
    public IMEG SR56 = new IMEG(new SRA(56, 2), "温度定标使能");

    // </editor-fold> 
    public MOSA_FDO(SDevInfo devinfo) {
        super(devinfo);
    }

    // <editor-fold defaultstate="collapsed" desc="定标接口">     
    private LogNode[] CalTemer(float caltemper) throws Exception {
        this.SR52.SetValue(caltemper);
        this.SR56.SetValue(0x01);
        this.SetMEG(SR52, SR56);

        //NVPA初始化
        this.ReadMEG(NPTEMPER);
        return new LogNode[]{new LogNode(NPTEMPER.toString(), NPTEMPER.GetValue())};
    }

    @Override
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception {
        LogNode ret = LogNode.CALOK();
        if (type.contentEquals("温度")) {
            //温度定标
            ret.children.addAll(Arrays.asList(CalTemer(testdata[0])));
        } else {
            if (oradata.length == 1) {
                //一点就是饱和氧
                SR46.SetValue(oradata[0]);
                SR50.SetValue(0x01);
                this.SetMEG(SR46, SR50);
//                this.do_single_cal(oradata[0], temp);
            } else {
                //界面输入是 {饱和氧,无氧}的顺序,需要交换顺序
                SR46.SetValue(oradata[0]);
                SR42.SetValue(oradata[1]);
                SR50.SetValue(0x02);
                this.SetMEG(SR46, SR42, SR50);
//                this.do_double_cal(oradata[1], oradata[0], temp);
            }
            this.ReadMEG(NCLTEMPER, NA, NB);
            ret.children.add(new LogNode(this.NCLTEMPER.toString(), this.NCLTEMPER.GetValue()));
            ret.children.add(new LogNode(this.NA.toString(), this.NA.GetValue()));
            ret.children.add(new LogNode(this.NB.toString(), this.NB.GetValue()));
        }
        return ret;
    }
    // </editor-fold> 
}
