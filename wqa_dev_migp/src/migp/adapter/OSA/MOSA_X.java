/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.OSA;

import base.migp.mem.MDA;
import base.migp.mem.NVPA;
import base.migp.mem.SRA;
import base.migp.mem.VPA;
import base.migp.reg.DMEG;
import base.migp.reg.FMEG;
import base.migp.reg.IMEG;
import base.pro.convert.NahonConvert;
import java.util.ArrayList;
import java.util.Arrays;
import migp.adapter.factory.AbsDevice;
import static migp.adapter.factory.AbsDevice.DMask;
import migp.adapter.factory.TemperCalibrateCalculate;
import wqa.adapter.factory.CErrorTable;
import wqa.dev.data.CollectData;
import wqa.dev.data.LogNode;
import wqa.dev.data.SDevInfo;
import wqa.dev.intf.SConfigItem;

/**
 *
 * @author chejf
 */
public class MOSA_X extends AbsDevice{
     public MOSA_X(SDevInfo devinfo) {
        super(devinfo);
    }

    // <editor-fold defaultstate="collapsed" desc="寄存器"> 
    // <editor-fold defaultstate="collapsed" desc="VPA"> 
    FMEG[] VDRANGE_MIN = new FMEG[]{new FMEG(new VPA(0x02, 4), "主参数1量程下限"), new FMEG(new VPA(0x0A, 4), "主参数2量程下限"),
        new FMEG(new VPA(0x12, 4), "主参数3量程下限"), new FMEG(new VPA(0x1A, 4), "主参数4量程下限")};
    FMEG[] VDRANGE_MAX = new FMEG[]{new FMEG(new VPA(0x06, 4), "主参数1量程上限"), new FMEG(new VPA(0x0E, 4), "主参数2量程上限"),
        new FMEG(new VPA(0x16, 4), "主参数3量程上限"), new FMEG(new VPA(0x1E, 4), "主参数4量程上限")};
    FMEG VTRANGE_MIN = new FMEG(new VPA(0x22, 4), "温度参数量程下限");
    FMEG VTRANGE_MAX = new FMEG(new VPA(0x26, 4), "温度参数量程上限");
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="MDA"> 
    IMEG MALARM = new IMEG(new MDA(0x00, 2), "报警码");  // OSA_TURB | OSA_TSS | OSA_SS | OSA_CHLA | OSA_CYANO_I | OSA_OIL_I | OSA_MLSS | OSA_FDO
    FMEG MPAR1 = new FMEG(new MDA(0x02, 4), "参数1");    //   浊度      悬浮物    悬浮物     叶绿素      蓝绿藻        水中油     污泥浓度   溶解氧mg/L
    FMEG MPAR2 = new FMEG(new MDA(0x06, 4), "参数2");    //   温度       温度      温度      温度         温度         温度        温度     溶解氧%
    FMEG MPAR3 = new FMEG(new MDA(0x0A, 4), "参数3");    //    --         --        --       --           --           --          --     温度
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="SRA"> 
    FMEG SR1 = new FMEG(new SRA(0x00, 4), "2.5V基准电压");
    FMEG SR2 = new FMEG(new SRA(0x04, 4), "4.096V基准电压");
    IMEG SR3 = new IMEG(new SRA(0x0C, 2), "原始光强信号(高电平)");
    IMEG SR4 = new IMEG(new SRA(0x0E, 2), "原始光强信号(低电平)");
    FMEG SR5 = new FMEG(new SRA(0x10, 4), "温度原始信号");
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    IMEG NRANGE = new IMEG(new NVPA(0, 2), "量程档位", 0, 3);
    IMEG NAVR = new IMEG(new NVPA(2, 2), "平均次数", 1, 100);

    IMEG NCMODE = new IMEG(new NVPA(4, 2), "清扫模式", 0, 2);
    IMEG NCTIME = new IMEG(new NVPA(6, 2), "清扫次数", 1, 100);
    IMEG NCINTERVAL = new IMEG(new NVPA(8, 2), "清扫间隔(分钟)", 1, 24 * 60);
    IMEG NCBRUSH = new IMEG(new NVPA(10, 2), "清扫刷偏移量", 0, 1000);

    FMEG[] NCLTEMPER = new FMEG[]{new FMEG(new NVPA(12, 4), "定标温度1"), new FMEG(new NVPA(40, 4), "定标温度2"), new FMEG(new NVPA(68, 4), "定标温度3"), new FMEG(new NVPA(96, 4), "定标温度4")};
    DMEG[] NCLPARA = new DMEG[]{new DMEG(new NVPA(16, 8), "定标系数A1"), new DMEG(new NVPA(44, 8), "定标系数A2"), new DMEG(new NVPA(72, 8), "定标系数A3"), new DMEG(new NVPA(100, 8), "定标系数A4")};
    DMEG[] NCLPARB = new DMEG[]{new DMEG(new NVPA(24, 8), "定标系数B1"), new DMEG(new NVPA(52, 8), "定标系数B2"), new DMEG(new NVPA(80, 8), "定标系数B3"), new DMEG(new NVPA(108, 8), "定标系数B4")};
    DMEG[] NCLPARC = new DMEG[]{new DMEG(new NVPA(32, 8), "定标系数C1"), new DMEG(new NVPA(60, 8), "定标系数C2"), new DMEG(new NVPA(88, 8), "定标系数C3"), new DMEG(new NVPA(116, 8), "定标系数C4")};

    FMEG NTEMPER_COMP = new FMEG(new NVPA(124, 4), "温度补偿系数");
    FMEG NTEMPER_PAR = new FMEG(new NVPA(128, 4), "温度定标系数");

    public static int AMPPAR = 4096;
    IMEG[] NAMPLIFY = new IMEG[]{new IMEG(new NVPA(133, 2), "放大倍数1"), new IMEG(new NVPA(135, 2), "放大倍数2"),
        new IMEG(new NVPA(137, 2), "放大倍数3"), new IMEG(new NVPA(139, 2), "放大倍数4")};

    IMEG NRANGE_NUM = new IMEG(new NVPA(132, 1), "量程数量", 0, 3);
    FMEG[] NRANGE_MAX = new FMEG[]{new FMEG(new NVPA(141, 4), "主参数1量程上限"), new FMEG(new NVPA(145, 4), "主参数2量程上限"),
        new FMEG(new NVPA(149, 4), "主参数3量程上限"), new FMEG(new NVPA(153, 4), "主参数4量程上限")};
    // </editor-fold> 
    // </editor-fold> 

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice(); //To change body of generated methods, choose Tools | Templates.

        //VPA初始化
        this.ReadMEG(VDRANGE_MIN[0], VDRANGE_MIN[1], VDRANGE_MIN[2], VDRANGE_MIN[3],
                VDRANGE_MAX[0], VDRANGE_MAX[1], VDRANGE_MAX[2], VDRANGE_MAX[3], VTRANGE_MIN, VTRANGE_MAX);
        //NVPA初始化
        this.ReadMEG(NRANGE, NAVR, NCMODE, NCTIME, NCINTERVAL, NCBRUSH,
                NCLTEMPER[0], NCLTEMPER[1], NCLTEMPER[2], NCLTEMPER[3],
                NCLPARA[0], NCLPARA[1], NCLPARA[2], NCLPARA[3],
                NCLPARB[0], NCLPARB[1], NCLPARB[2], NCLPARB[3],
                NCLPARC[0], NCLPARC[1], NCLPARC[2], NCLPARC[3],
                NTEMPER_COMP, NTEMPER_PAR, NRANGE_NUM,
                NAMPLIFY[0], NAMPLIFY[1], NAMPLIFY[2], NAMPLIFY[3],
                NRANGE_MAX[0], NRANGE_MAX[1], NRANGE_MAX[2], NRANGE_MAX[3]);
    }

    // <editor-fold defaultstate="collapsed" desc="量程数据"> 
    //获取量程字符串描述（量程档位）
    private String get_range_string(int index) {
        if (index < 0 || index >= VDRANGE_MIN.length) {
            return "未知量程" + index;
        }
        return "(" + VDRANGE_MIN[index].GetValue() + "-" + NRANGE_MAX[index].GetValue() + ")";
    }

    //获取量程字符串描述
    private String[] get_range_string() {
        String[] tmp = new String[this.NRANGE_NUM.GetValue() + 1];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = get_range_string(i);
        }

        return tmp;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="配置接口"> 
    @Override
    public ArrayList<SConfigItem> GetConfigList() {
        ArrayList<SConfigItem> item = super.GetConfigList(); //To change body of generated methods, choose Tools | Templates.
        item.add(SConfigItem.CreateSItem(NRANGE.toString(), this.get_range_string(NRANGE.GetValue()), "", this.get_range_string()));
        item.add(SConfigItem.CreateRWItem(NAVR.toString(), NAVR.GetValue().toString(), NAVR.min + "-" + NAVR.max));
        item.add(SConfigItem.CreateRWItem(NTEMPER_COMP.toString(), NTEMPER_COMP.GetValue().toString(), ""));
        return item;
    }

    @Override
    public void SetConfigList(ArrayList<SConfigItem> list) throws Exception {
        super.SetConfigList(list);

        for (SConfigItem item : list) {
            if (item.IsKey(NRANGE.toString())) {
                String[] _range_string = this.get_range_string();
                for (int i = 0; i < _range_string.length; i++) {
                    if (item.GetValue().contentEquals(_range_string[i])) {
                        this.SetConfigREG(NRANGE, String.valueOf(i));
                        break;
                    }
                }
            }
            if (item.IsKey(NAVR.toString())) {
                this.SetConfigREG(NAVR, item.GetValue());
            }
            if (item.IsKey(NTEMPER_COMP.toString())) {
                this.SetConfigREG(NTEMPER_COMP, item.GetValue());
            }
        }

    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="量程系数"> 
    @Override
    public ArrayList<SConfigItem> GetCalParList() {
        ArrayList<SConfigItem> item = super.GetCalParList(); //To change body of generated methods, choose Tools | Templates.

        item.add(SConfigItem.CreateRWItem(NRANGE_NUM.toString(), (NRANGE_NUM.GetValue() + 1) + "", (NRANGE_NUM.min + 1) + "-" + (NRANGE_NUM.max + 1)));
        item.add(SConfigItem.CreateRWItem(NTEMPER_PAR.toString(), NTEMPER_PAR.GetValue().toString(), ""));
        item.add(SConfigItem.CreateInfoItem(""));

        for (int i = 0; i < this.NAMPLIFY.length; i++) {
            item.add(SConfigItem.CreateRWItem(NRANGE_MAX[i].toString(), NRANGE_MAX[i].GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(NCLTEMPER[i].toString(), NCLTEMPER[i].GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(NCLPARA[i].toString(), NCLPARA[i].GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(NCLPARB[i].toString(), NCLPARB[i].GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(NCLPARC[i].toString(), NCLPARC[i].GetValue().toString(), ""));
            if (NAMPLIFY[i].GetValue() == 0) {
                item.add(SConfigItem.CreateRWItem(NAMPLIFY[i].toString(), (int) (AMPPAR) + "", ""));
            } else {
                item.add(SConfigItem.CreateRWItem(NAMPLIFY[i].toString(), (int) (AMPPAR / NAMPLIFY[i].GetValue()) + "", ""));
            }
            item.add(SConfigItem.CreateInfoItem(""));
        }
        return item;
    }

    @Override
    public void SetCalParList(ArrayList<SConfigItem> list) throws Exception {
        super.SetConfigList(list);

        for (SConfigItem item : list) {
            if (item.IsKey(NRANGE_NUM.toString())) {
                int num = Integer.valueOf(item.GetValue());
                if (num > NRANGE_NUM.min && num <= NRANGE_NUM.max + 1) {
                    this.SetConfigREG(NRANGE_NUM, (num - 1) + "");
                } else {
                    throw new Exception("输入范围在" + (NRANGE_NUM.min + 1) + "-" + (NRANGE_NUM.max + 1));
                }
            }
            if (item.IsKey(NTEMPER_PAR.toString())) {
                this.SetConfigREG(NTEMPER_PAR, item.GetValue());
            }

            for (int i = 0; i < this.NAMPLIFY.length; i++) {
                if (item.IsKey(NRANGE_MAX[i].toString())) {
                    this.SetConfigREG(NRANGE_MAX[i], item.GetValue());
                }
                if (item.IsKey(NCLTEMPER[i].toString())) {
                    this.SetConfigREG(NCLTEMPER[i], item.GetValue());
                }
                if (item.IsKey(NCLPARA[i].toString())) {
                    this.SetConfigREG(NCLPARA[i], item.GetValue());
                }
                if (item.IsKey(NCLPARB[i].toString())) {
                    this.SetConfigREG(NCLPARB[i], item.GetValue());
                }
                if (item.IsKey(NCLPARC[i].toString())) {
                    this.SetConfigREG(NCLPARC[i], item.GetValue());
                }
                if (item.IsKey(NAMPLIFY[i].toString())) {
                    float tmp = Float.valueOf(item.GetValue());
                    float famply = AMPPAR;
                    if (tmp != 0) {
                        famply = AMPPAR / Float.valueOf(item.GetValue());
                    }
                    int amply = (int) (famply + 0.5);
                    this.SetConfigREG(NAMPLIFY[i], String.valueOf(amply));
                }
            }
        }
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="采集接口"> 
    @Override
    public CollectData CollectData() throws Exception {
        CollectData disdata = this.BuildDisplayData();
        //读取数据
        this.ReadMEG(MALARM, MPAR1, MPAR2);
        //原始数据
        this.ReadMEG(SR1, SR2, SR3, SR4, SR5);

        disdata.datas[0].mainData = NahonConvert.TimData(MPAR1.GetValue(), 2);   //OSA值
        disdata.datas[0].range_info = get_range_string(NRANGE.GetValue());         //量程

        disdata.datas[1].mainData = NahonConvert.TimData(this.SR3.GetValue() - SR4.GetValue(), 2); //OSA原始值(光强高电平-低电平)

        disdata.datas[2].mainData = NahonConvert.TimData(MPAR2.GetValue(), 2);   //温度值
        disdata.datas[2].range_info = "(" + this.VTRANGE_MIN.GetValue() + "-" + this.VTRANGE_MAX.GetValue() + ")"; //量程

        disdata.datas[3].mainData = NahonConvert.TimData(SR5.GetValue(), 2); //温度原始值

        disdata.alarm = MALARM.GetValue(); //报警信息
        String info = CErrorTable.GetInstance().GetErrorString(((this.GetDevInfo().dev_type & DMask) << 8) | disdata.alarm);
        disdata.alram_info = info == null ? "" : info;
        return disdata;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="定标接口"> 
    @Override
    public LogNode CalParameter(String type, float[] oradata, float[] testdata) throws Exception {
        LogNode ret = LogNode.CALOK();
        if (type.contentEquals("温度")) {
            //温度定标
            float cal_par = new TemperCalibrateCalculate().Calculate(testdata, oradata);
            this.SetConfigREG(this.NTEMPER_PAR, String.valueOf(cal_par));
            ret.children.add(new LogNode(NRANGE_NUM.toString(), cal_par));
        } else {
            ret.children.addAll(Arrays.asList(cal_osa(oradata, testdata)));
        }
        return ret;
    }

    private LogNode[] cal_osa(float[] oradata, float[] testdata) throws Exception {
        //原始数据
        this.ReadMEG(MPAR2);
        float cal_tmp = NahonConvert.TimData(MPAR2.GetValue(), 2);   //温度值
        switch (oradata.length) {
            case 1:
                this.single_cal(oradata, testdata, cal_tmp);
                break;
            case 2:
                this.double_cal(oradata, testdata, cal_tmp);
                break;
            default:
                this.triple_cal(oradata, testdata, cal_tmp);
                break;
        }

        return new LogNode[]{new LogNode("当前量程", get_range_string(NRANGE.GetValue())),
            new LogNode("当前温度", cal_tmp),
            new LogNode(NCLPARA[NRANGE.GetValue()].toString(), NCLPARA[NRANGE.GetValue()]),
            new LogNode(NCLPARB[NRANGE.GetValue()].toString(), NCLPARB[NRANGE.GetValue()]),
            new LogNode(NCLPARC[NRANGE.GetValue()].toString(), NCLPARC[NRANGE.GetValue()])};
    }

    private void single_cal(float[] oradata, float[] testdata, float temper) throws Exception {
        double current = 0;
        double A = this.NCLPARA[this.NRANGE.GetValue()].GetValue();
        double B = this.NCLPARB[this.NRANGE.GetValue()].GetValue();
        double C = this.NCLPARC[this.NRANGE.GetValue()].GetValue();
        if (B == 0) {
            current = A * oradata[0] + C;
        } else {
            current = A / (B - oradata[0]) + C;
        }

        double newC = testdata[0] - current + C;

        this.SetConfigREG(this.NCLTEMPER[this.NRANGE.GetValue()], String.valueOf(temper));
        this.SetConfigREG(this.NCLPARC[this.NRANGE.GetValue()], String.valueOf(newC));
//        this.set_calpar(temper, this.A[this.drange_index], this.B[this.drange_index], newC);
    }

    private void double_cal(float[] oradata, float[] testdata, float temper) throws Exception {
        if (oradata[0] - oradata[1] == 0) {
            throw new Exception("原始值不能相同");
        }

        double newA = (testdata[0] - testdata[1]) / (oradata[0] - oradata[1]);
        double newB = 0;
        double newC = testdata[0] - newA * oradata[0];
//        this.set_calpar(temper, newA, newB, newC);

        this.SetConfigREG(this.NCLTEMPER[this.NRANGE.GetValue()], String.valueOf(temper));
        this.SetConfigREG(this.NCLPARA[this.NRANGE.GetValue()], String.valueOf(newA));
        this.SetConfigREG(this.NCLPARB[this.NRANGE.GetValue()], String.valueOf(newB));
        this.SetConfigREG(this.NCLPARC[this.NRANGE.GetValue()], String.valueOf(newC));
//        this.set_cal_temp(this.NRANGE.GetValue(), temper);
//        this.set_A(this.NRANGE.GetValue(), newA);
//        this.set_B(this.NRANGE.GetValue(), newB);
//        this.set_C(this.NRANGE.GetValue(), newC);
    }

    private void triple_cal(float[] oradata, float[] testdata, float temper) throws Exception {
        if (oradata[0] - oradata[1] == 0 || oradata[1] - oradata[2] == 0) {
            throw new Exception("原始值不能相同");
        }

        double temp = (testdata[0] - testdata[1]) * (oradata[1] - oradata[2])
                / ((testdata[1] - testdata[2]) * (oradata[0] - oradata[1]));

        double newB = (temp * oradata[0] - oradata[2]) / (temp - 1.0);
        double newA = (testdata[0] - testdata[1]) * (newB - oradata[0])
                * (newB - oradata[1]) / (oradata[0] - oradata[1]);
        double newC = testdata[1] - newA / (newB - oradata[1]);

//        this.set_calpar(temper, newA, newB, newC);
        this.SetConfigREG(this.NCLTEMPER[this.NRANGE.GetValue()], String.valueOf(temper));
        this.SetConfigREG(this.NCLPARA[this.NRANGE.GetValue()], String.valueOf(newA));
        this.SetConfigREG(this.NCLPARB[this.NRANGE.GetValue()], String.valueOf(newB));
        this.SetConfigREG(this.NCLPARC[this.NRANGE.GetValue()], String.valueOf(newC));
    }
    // </editor-fold> 

}
