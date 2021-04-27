/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package migp.adapter.OSA;

import base.migp.mem.*;
import base.migp.reg.*;
import base.pro.convert.NahonConvert;
import java.util.ArrayList;
import java.util.Arrays;
import migp.adapter.factory.AbsDevice;
import static migp.adapter.factory.AbsDevice.DMask;
import wqa.adapter.factory.CErrorTable;
import wqa.dev.data.*;
import wqa.dev.intf.SConfigItem;

/**
 *
 * @author chejf
 */
public class MOSAII_X extends AbsDevice {

    public MOSAII_X(SDevInfo devinfo) {
        super(devinfo);
    }

    // <editor-fold defaultstate="collapsed" desc="寄存器"> 
    // <editor-fold defaultstate="collapsed" desc="VPA"> 
    FMEG[] VDRANGE_MIN = new FMEG[]{new FMEG(new VPA(0x02, 4), "主参数1量程下限"), new FMEG(new VPA(0x0A, 4), "主参数2量程下限"),
        new FMEG(new VPA(0x12, 4), "主参数3量程下限"), new FMEG(new VPA(0x1A, 4), "主参数4量程下限")};
    FMEG[] VDRANGE_MAX = new FMEG[]{new FMEG(new VPA(0x06, 4), "主参数1量程上限"), new FMEG(new VPA(0x0E, 4), "主参数2量程上限"),
        new FMEG(new VPA(0x16, 4), "主参数3量程上限"), new FMEG(new VPA(0x1E, 4), "主参数4量程上限")};
    FMEG[] VDRANGE_MIN2 = new FMEG[]{new FMEG(new VPA(0x22, 4), "附参数1量程下限"), new FMEG(new VPA(0x2A, 4), "附参数2量程下限"),
        new FMEG(new VPA(0x32, 4), "附参数3量程下限"), new FMEG(new VPA(0x3A, 4), "附参数4量程下限")};
    FMEG[] VDRANGE_MAX2 = new FMEG[]{new FMEG(new VPA(0x26, 4), "附参数1量程上限"), new FMEG(new VPA(0x2E, 4), "附参数2量程上限"),
        new FMEG(new VPA(0x36, 4), "附参数3量程上限"), new FMEG(new VPA(0x3E, 4), "附参数4量程上限")};
    FMEG VTRANGE_MIN = new FMEG(new VPA(0x42, 4), "温度参数量程下限");
    FMEG VTRANGE_MAX = new FMEG(new VPA(0x46, 4), "温度参数量程上限");
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="MDA"> 
    IMEG MALARM = new IMEG(new MDA(0x00, 2), "报警码");  // OSA_TURB | OSA_TSS | OSA_SS | OSA_CHLA | OSA_CYANO_I | OSA_OIL_I | OSA_MLSS | OSA_FDO    |MOSA_CHlA pro | **-Cyano /I/II | **-TA /I/II|
    FMEG MPAR1 = new FMEG(new MDA(0x02, 4), "参数1");    //   浊度      悬浮物    悬浮物     叶绿素      蓝绿藻        水中油     污泥浓度   溶解氧mg/L     叶绿素           蓝绿藻          叶绿素
    FMEG MPAR2 = new FMEG(new MDA(0x06, 4), "参数2");    //   温度       温度      温度      温度         温度         温度        温度     溶解氧%         浊度             浊度          蓝绿藻     
    FMEG MPAR3 = new FMEG(new MDA(0x0A, 4), "参数3");    //    --         --        --       --           --           --          --     温度            温度             温度           温度
    // </editor-fold>     

    // <editor-fold defaultstate="collapsed" desc="SRA"> 
//    FMEG SR1 = new FMEG(new SRA(0x00, 4), "2.5V基准电压");
//    FMEG SR2 = new FMEG(new SRA(0x04, 4), "4.096V基准电压");
    IMEG SR3 = new IMEG(new SRA(0x0C, 2), "原始光强信号(高电平)");
    IMEG SR4 = new IMEG(new SRA(0x0E, 2), "原始光强信号(低电平)");
    FMEG SR5 = new FMEG(new SRA(0x10, 4), "温度原始信号");

    IMEG SCLTYPE = new IMEG(new SRA(22, 2), "定标类型");
    IMEG SCLRANG = new IMEG(new SRA(24, 2), "定标量程");
    FMEG SCLODATA[] = new FMEG[]{new FMEG(new SRA(26, 4), "原始信号1"), new FMEG(new SRA(34, 4), "原始信号2"), new FMEG(new SRA(42, 4), "原始信号3")};
    FMEG SCLTDATA[] = new FMEG[]{new FMEG(new SRA(30, 4), "定标数据1"), new FMEG(new SRA(38, 4), "定标数据2"), new FMEG(new SRA(46, 4), "定标数据3")};
    IMEG SCLSTART = new IMEG(new SRA(50, 2), "启动定标"); //W
    FMEG SCLTEMPER = new FMEG(new SRA(52, 4), "温度定标数据");    //R/W
    IMEG SCLTEMPERSTART = new IMEG(new SRA(56, 2), "温度启动定标");//R/W
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="NVPA"> 
    IMEG NRANGE = new IMEG(new NVPA(0, 2), "量程档位", 0, 3);
    IMEG NAVR = new IMEG(new NVPA(2, 2), "平均次数", 1, 100);

    FMEG[] NCLTEMPER = new FMEG[]{new FMEG(new NVPA(12, 4), "定标温度1"), new FMEG(new NVPA(40, 4), "定标温度2"), new FMEG(new NVPA(68, 4), "定标温度3"), new FMEG(new NVPA(96, 4), "定标温度4")};
    DMEG[] NCLPARA = new DMEG[]{new DMEG(new NVPA(16, 8), "定标系数A1"), new DMEG(new NVPA(44, 8), "定标系数A2"), new DMEG(new NVPA(72, 8), "定标系数A3"), new DMEG(new NVPA(100, 8), "定标系数A4")};
    DMEG[] NCLPARB = new DMEG[]{new DMEG(new NVPA(24, 8), "定标系数B1"), new DMEG(new NVPA(52, 8), "定标系数B2"), new DMEG(new NVPA(80, 8), "定标系数B3"), new DMEG(new NVPA(108, 8), "定标系数B4")};
    DMEG[] NCLPARC = new DMEG[]{new DMEG(new NVPA(32, 8), "定标系数C1"), new DMEG(new NVPA(60, 8), "定标系数C2"), new DMEG(new NVPA(88, 8), "定标系数C3"), new DMEG(new NVPA(116, 8), "定标系数C4")};

    FMEG NTEMPER_COMP = new FMEG(new NVPA(124, 4), "温度补偿系数");
    IMEG[] NAMPLIFY = new IMEG[]{new IMEG(new NVPA(133, 2), "放大倍数1"), new IMEG(new NVPA(135, 2), "放大倍数2"), new IMEG(new NVPA(137, 2), "放大倍数3"), new IMEG(new NVPA(139, 2), "放大倍数4")};
    IMEG NRANGE_NUM = new IMEG(new NVPA(132, 1), "量程数量", 0, 3);
    FMEG[] NRANGE_MAX = new FMEG[]{new FMEG(new NVPA(141, 4), "量程上限1"), new FMEG(new NVPA(145, 4), "量程上限2"), new FMEG(new NVPA(149, 4), "量程上限3"), new FMEG(new NVPA(153, 4), "量程上限4")};
    //****************************************************************************************************************
    IMEG NRANGE2 = new IMEG(new NVPA(4, 2), "量程档位", 0, 3);
    IMEG NAVR2 = new IMEG(new NVPA(6, 2), "平均次数", 1, 100);

    FMEG[] NCLTEMPER2 = new FMEG[]{new FMEG(new NVPA(160, 4), "定标温度1"), new FMEG(new NVPA(188, 4), "定标温度2"), new FMEG(new NVPA(216, 4), "定标温度3"), new FMEG(new NVPA(244, 4), "定标温度4")};
    DMEG[] NCLPARA2 = new DMEG[]{new DMEG(new NVPA(164, 8), "定标系数A1"), new DMEG(new NVPA(192, 8), "定标系数A2"), new DMEG(new NVPA(220, 8), "定标系数A3"), new DMEG(new NVPA(248, 8), "定标系数A4")};
    DMEG[] NCLPARB2 = new DMEG[]{new DMEG(new NVPA(172, 8), "定标系数B1"), new DMEG(new NVPA(200, 8), "定标系数B2"), new DMEG(new NVPA(228, 8), "定标系数B3"), new DMEG(new NVPA(256, 8), "定标系数B4")};
    DMEG[] NCLPARC2 = new DMEG[]{new DMEG(new NVPA(180, 8), "定标系数C1"), new DMEG(new NVPA(208, 8), "定标系数C2"), new DMEG(new NVPA(236, 8), "定标系数C3"), new DMEG(new NVPA(264, 8), "定标系数C4")};

    FMEG NTEMPER_COMP2 = new FMEG(new NVPA(272, 4), "温度补偿系数");
    IMEG[] NAMPLIFY2 = new IMEG[]{new IMEG(new NVPA(277, 2), "放大倍数1"), new IMEG(new NVPA(279, 2), "放大倍数2"), new IMEG(new NVPA(281, 2), "放大倍数3"), new IMEG(new NVPA(283, 2), "放大倍数4")};
    IMEG NRANGE_NUM2 = new IMEG(new NVPA(276, 1), "量程数量", 0, 3);
    FMEG[] NRANGE_MAX2 = new FMEG[]{new FMEG(new NVPA(285, 4), "量程上限1"), new FMEG(new NVPA(289, 4), "量程上限2"), new FMEG(new NVPA(293, 4), "量程上限3"), new FMEG(new NVPA(297, 4), "量程上限4")};
    //****************************************************************************************************************
    FMEG NTEMPER_PAR = new FMEG(new NVPA(128, 4), "温度定标系数");
    FMEG NDOCOM_PAR = new FMEG(new NVPA(8, 4), "浊度补偿系数");
    // </editor-fold> 
    // </editor-fold> 

    @Override
    public void InitDevice() throws Exception {
        super.InitDevice(); //To change body of generated methods, choose Tools | Templates.

        //VPA初始化
        this.ReadMEG(VDRANGE_MIN[0], VDRANGE_MIN[1], VDRANGE_MIN[2], VDRANGE_MIN[3],
                VDRANGE_MAX[0], VDRANGE_MAX[1], VDRANGE_MAX[2], VDRANGE_MAX[3],
                VDRANGE_MIN2[0], VDRANGE_MIN2[1], VDRANGE_MIN2[2], VDRANGE_MIN2[3],
                VDRANGE_MAX2[0], VDRANGE_MAX2[1], VDRANGE_MAX2[2], VDRANGE_MAX2[3],
                VTRANGE_MIN, VTRANGE_MAX);
        //NVPA初始化
        this.ReadMEG(NRANGE, NAVR, NRANGE2, NAVR2, NDOCOM_PAR,
                NCLTEMPER[0], NCLTEMPER[1], NCLTEMPER[2], NCLTEMPER[3],
                NCLPARA[0], NCLPARA[1], NCLPARA[2], NCLPARA[3],
                NCLPARB[0], NCLPARB[1], NCLPARB[2], NCLPARB[3],
                NCLPARC[0], NCLPARC[1], NCLPARC[2], NCLPARC[3],
                NTEMPER_COMP, NTEMPER_PAR, NRANGE_NUM,
                NAMPLIFY[0], NAMPLIFY[1], NAMPLIFY[2], NAMPLIFY[3],
                NRANGE_MAX[0], NRANGE_MAX[1], NRANGE_MAX[2], NRANGE_MAX[3],
                NCLTEMPER2[0], NCLTEMPER2[1], NCLTEMPER2[2], NCLTEMPER2[3],
                NCLPARA2[0], NCLPARA2[1], NCLPARA2[2], NCLPARA2[3],
                NCLPARB2[0], NCLPARB2[1], NCLPARB2[2], NCLPARB2[3],
                NCLPARC2[0], NCLPARC2[1], NCLPARC2[2], NCLPARC2[3],
                NRANGE_NUM2, NTEMPER_COMP2,
                NAMPLIFY2[0], NAMPLIFY2[1], NAMPLIFY2[2], NAMPLIFY2[3],
                NRANGE_MAX2[0], NRANGE_MAX2[1], NRANGE_MAX2[2], NRANGE_MAX2[3]);
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

    private String get_range_string2(int index) {
        if (index < 0 || index >= VDRANGE_MIN2.length) {
            return "未知量程" + index;
        }
        return "(" + VDRANGE_MIN2[index].GetValue() + "-" + NRANGE_MAX2[index].GetValue() + ")";
    }

    //获取量程字符串描述
    private String[] get_range_string2() {
        String[] tmp = new String[this.NRANGE_NUM2.GetValue() + 1];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = get_range_string2(i);
        }

        return tmp;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="配置接口"> 
    @Override
    public ArrayList<SConfigItem> GetConfigList() {
        ArrayList<SConfigItem> item = super.GetConfigList();
        String[] data_names = this.GetDataNames();
        item.add(SConfigItem.CreateSItem(data_names[0] + NRANGE.toString(), this.get_range_string(NRANGE.GetValue()), "", this.get_range_string()));
        item.add(SConfigItem.CreateRWItem(data_names[0] + NAVR.toString(), NAVR.GetValue().toString(), NAVR.min + "-" + NAVR.max));
        item.add(SConfigItem.CreateRWItem(data_names[0] + NTEMPER_COMP.toString(), NTEMPER_COMP.GetValue().toString(), ""));
        item.add(SConfigItem.CreateSItem(data_names[1] + NRANGE2.toString(), this.get_range_string2(NRANGE2.GetValue()), "", this.get_range_string2()));
        item.add(SConfigItem.CreateRWItem(data_names[1] + NAVR2.toString(), NAVR2.GetValue().toString(), NAVR2.min + "-" + NAVR2.max));
        item.add(SConfigItem.CreateRWItem(data_names[1] + NTEMPER_COMP2.toString(), NTEMPER_COMP2.GetValue().toString(), ""));
        if (this.GetDevInfo().dev_type == 0x1110 || this.GetDevInfo().dev_type == 0x1111 || this.GetDevInfo().dev_type == 0x1112) {
            item.add(SConfigItem.CreateRWItem(this.NDOCOM_PAR.toString(), NDOCOM_PAR.GetValue().toString(), ""));
        }
        return item;
    }

    @Override
    public void SetConfigList(ArrayList<SConfigItem> list) throws Exception {
        super.SetConfigList(list);
        String[] data_names = this.GetDataNames();

        for (SConfigItem item : list) {
            if (item.IsKey(data_names[0] + NRANGE.toString())) {
                String[] _range_string = this.get_range_string();
                for (int i = 0; i < _range_string.length; i++) {
                    if (item.GetValue().contentEquals(_range_string[i])) {
                        this.SetConfigREG(NRANGE, String.valueOf(i));
                        break;
                    }
                }
            }
            if (item.IsKey(data_names[1] + NRANGE2.toString())) {
                String[] _range_string = this.get_range_string2();
                for (int i = 0; i < _range_string.length; i++) {
                    if (item.GetValue().contentEquals(_range_string[i])) {
                        this.SetConfigREG(NRANGE2, String.valueOf(i));
                        break;
                    }
                }
            }
            if (item.IsKey(NDOCOM_PAR.toString())) {
                this.SetConfigREG(NDOCOM_PAR, item.GetValue());
            }
            if (item.IsKey(data_names[0] + NAVR.toString())) {
                this.SetConfigREG(NAVR, item.GetValue());
            }
            if (item.IsKey(data_names[1] + NAVR2.toString())) {
                this.SetConfigREG(NAVR2, item.GetValue());
            }
            if (item.IsKey(data_names[0] + NTEMPER_COMP.toString())) {
                this.SetConfigREG(NTEMPER_COMP, item.GetValue());
            }
            if (item.IsKey(data_names[1] + NTEMPER_COMP2.toString())) {
                this.SetConfigREG(NTEMPER_COMP2, item.GetValue());
            }
        }

    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="量程系数"> 
    @Override
    public ArrayList<SConfigItem> GetCalParList() {
        ArrayList<SConfigItem> item = super.GetCalParList(); //To change body of generated methods, choose Tools | Templates.
        String[] GetDataNames = this.GetDataNames();
        item.add(SConfigItem.CreateRWItem(NTEMPER_PAR.toString(), NTEMPER_PAR.GetValue().toString(), ""));

        //****************************************************************************************************************
        item.add(SConfigItem.CreateInfoItem(GetDataNames[0] + "校准参数列表:"));
        item.add(SConfigItem.CreateRWItem(GetDataNames[0] + NRANGE_NUM.toString(), (NRANGE_NUM.GetValue() + 1) + "", (NRANGE_NUM.min + 1) + "-" + (NRANGE_NUM.max + 1)));
        item.add(SConfigItem.CreateInfoItem(""));

        for (int i = 0; i < this.NAMPLIFY.length; i++) {
            item.add(SConfigItem.CreateRWItem(GetDataNames[0] + NRANGE_MAX[i].toString(), NRANGE_MAX[i].GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(GetDataNames[0] + NCLTEMPER[i].toString(), NCLTEMPER[i].GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(GetDataNames[0] + NCLPARA[i].toString(), NCLPARA[i].GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(GetDataNames[0] + NCLPARB[i].toString(), NCLPARB[i].GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(GetDataNames[0] + NCLPARC[i].toString(), NCLPARC[i].GetValue().toString(), ""));
//            this.getAmplfyItem(SR3)
            item.add(SConfigItem.CreateRWItem(GetDataNames[0] + NAMPLIFY[i].toString(), this.getAmplyfy(NAMPLIFY[i].GetValue()) + "", ""));
//            if (NAMPLIFY[i].GetValue() == 0) {
//                item.add(SConfigItem.CreateRWItem(GetDataNames[0] + NAMPLIFY[i].toString(), (int) (AMPPAR) + "", ""));
//            } else {
//                item.add(SConfigItem.CreateRWItem(GetDataNames[0] + NAMPLIFY[i].toString(), NahonConvert.TimData((float) AMPPAR / NAMPLIFY[i].GetValue(), 2) + "", ""));
//            }
            item.add(SConfigItem.CreateInfoItem(""));
        }
        //****************************************************************************************************************
        item.add(SConfigItem.CreateInfoItem(GetDataNames[1] + "校准参数列表:"));
        item.add(SConfigItem.CreateRWItem(GetDataNames[1] + NRANGE_NUM2.toString(), (NRANGE_NUM2.GetValue() + 1) + "", (NRANGE_NUM2.min + 1) + "-" + (NRANGE_NUM2.max + 1)));
        item.add(SConfigItem.CreateInfoItem(""));

        for (int i = 0; i < this.NAMPLIFY2.length; i++) {
            item.add(SConfigItem.CreateRWItem(GetDataNames[1] + NRANGE_MAX2[i].toString(), NRANGE_MAX2[i].GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(GetDataNames[1] + NCLTEMPER2[i].toString(), NCLTEMPER2[i].GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(GetDataNames[1] + NCLPARA2[i].toString(), NCLPARA2[i].GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(GetDataNames[1] + NCLPARB2[i].toString(), NCLPARB2[i].GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(GetDataNames[1] + NCLPARC2[i].toString(), NCLPARC2[i].GetValue().toString(), ""));
            item.add(SConfigItem.CreateRWItem(GetDataNames[1] + NAMPLIFY2[i].toString(), this.getAmplyfy(NAMPLIFY2[i].GetValue()) + "", ""));
//            if (NAMPLIFY2[i].GetValue() == 0) {
//                item.add(SConfigItem.CreateRWItem(GetDataNames[1] + NAMPLIFY2[i].toString(), (int) (AMPPAR) + "", ""));
//            } else {
//                item.add(SConfigItem.CreateRWItem(GetDataNames[1] + NAMPLIFY2[i].toString(), NahonConvert.TimData((float) AMPPAR / NAMPLIFY2[i].GetValue(), 2) + "", ""));
//            }
            item.add(SConfigItem.CreateInfoItem(""));
        }
        return item;
    }

    @Override
    public void SetCalParList(ArrayList<SConfigItem> list) throws Exception {
        super.SetConfigList(list);
        String[] GetDataNames = this.GetDataNames();
        for (SConfigItem item : list) {
            if (item.IsKey(NTEMPER_PAR.toString())) {
                this.SetConfigREG(NTEMPER_PAR, item.GetValue());
            }
            //*************************************************************************
            if (item.IsKey(GetDataNames[0] + NRANGE_NUM.toString())) {
                int num = Integer.valueOf(item.GetValue());
                if (num > NRANGE_NUM.min && num <= NRANGE_NUM.max + 1) {
                    this.SetConfigREG(NRANGE_NUM, (num - 1) + "");
                } else {
                    throw new Exception("输入范围在" + (NRANGE_NUM.min + 1) + "-" + (NRANGE_NUM.max + 1));
                }
            }

            for (int i = 0; i < this.NAMPLIFY.length; i++) {
                if (item.IsKey(GetDataNames[0] + NRANGE_MAX[i].toString())) {
                    this.SetConfigREG(NRANGE_MAX[i], item.GetValue());
                }
                if (item.IsKey(GetDataNames[0] + NCLTEMPER[i].toString())) {
                    this.SetConfigREG(NCLTEMPER[i], item.GetValue());
                }
                if (item.IsKey(GetDataNames[0] + NCLPARA[i].toString())) {
                    this.SetConfigREG(NCLPARA[i], item.GetValue());
                }
                if (item.IsKey(GetDataNames[0] + NCLPARB[i].toString())) {
                    this.SetConfigREG(NCLPARB[i], item.GetValue());
                }
                if (item.IsKey(GetDataNames[0] + NCLPARC[i].toString())) {
                    this.SetConfigREG(NCLPARC[i], item.GetValue());
                }
                if (item.IsKey(GetDataNames[0] + NAMPLIFY[i].toString())) {
//                    float tmp = Float.valueOf(item.GetValue());
//                    float famply = AMPPAR;
//                    if (tmp != 0) {
//                        famply = AMPPAR / Float.valueOf(item.GetValue());
//                    }
//                    int amply = (int) (famply + 0.5);
//                    amply = amply > AMPPAR ? AMPPAR : amply;
                    this.SetConfigREG(NAMPLIFY[i], String.valueOf(this.setAmplyfy(Float.valueOf(item.GetValue()))));
                }
            }

            //*************************************************************************
            if (item.IsKey(GetDataNames[1] + NRANGE_NUM2.toString())) {
                int num = Integer.valueOf(item.GetValue());
                if (num > NRANGE_NUM2.min && num <= NRANGE_NUM2.max + 1) {
                    this.SetConfigREG(NRANGE_NUM2, (num - 1) + "");
                } else {
                    throw new Exception("输入范围在" + (NRANGE_NUM2.min + 1) + "-" + (NRANGE_NUM2.max + 1));
                }
            }

            for (int i = 0; i < this.NAMPLIFY2.length; i++) {
                if (item.IsKey(GetDataNames[1] + NRANGE_MAX2[i].toString())) {
                    this.SetConfigREG(NRANGE_MAX2[i], item.GetValue());
                }
                if (item.IsKey(GetDataNames[1] + NCLTEMPER2[i].toString())) {
                    this.SetConfigREG(NCLTEMPER2[i], item.GetValue());
                }
                if (item.IsKey(GetDataNames[1] + NCLPARA2[i].toString())) {
                    this.SetConfigREG(NCLPARA2[i], item.GetValue());
                }
                if (item.IsKey(GetDataNames[1] + NCLPARB2[i].toString())) {
                    this.SetConfigREG(NCLPARB2[i], item.GetValue());
                }
                if (item.IsKey(GetDataNames[1] + NCLPARC2[i].toString())) {
                    this.SetConfigREG(NCLPARC2[i], item.GetValue());
                }
                if (item.IsKey(GetDataNames[1] + NAMPLIFY2[i].toString())) {
//                    float tmp = Float.valueOf(item.GetValue());
//                    float famply = AMPPAR;
//                    if (tmp != 0) {
//                        famply = AMPPAR / Float.valueOf(item.GetValue());
//                    }
//                    int amply = (int) (famply + 0.5);
//                    amply = amply > AMPPAR ? AMPPAR : amply;
//                    this.SetConfigREG(NAMPLIFY2[i], String.valueOf(amply));
                    this.SetConfigREG(NAMPLIFY2[i], String.valueOf(this.setAmplyfy(Float.valueOf(item.GetValue()))));
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
        this.ReadMEG(MALARM, MPAR1, MPAR2, MPAR3);
        //原始数据
        this.ReadMEG(SR3, SR4, SR5);

        disdata.datas[0].mainData = NahonConvert.TimData(MPAR1.GetValue(), 2);   //OSA值1
        disdata.datas[0].range_info = get_range_string(NRANGE.GetValue());       //量程1
        disdata.datas[1].mainData = this.SR3.GetValue(); //OSA原始值1
        if (this.GetDevInfo().dev_type == 0x1111 || this.GetDevInfo().dev_type == 0x1112) {
            if (disdata.datas[0].range_info.length() > "(0-20000)".length()) {
                disdata.datas[0].unit = "细胞/ml";
            }
        }

        disdata.datas[2].mainData = NahonConvert.TimData(MPAR2.GetValue(), 2);   //OSA值2
        disdata.datas[2].range_info = get_range_string2(NRANGE2.GetValue());       //量程2
        disdata.datas[3].mainData = this.SR4.GetValue(); //OSA原始值2
        if (this.GetDevInfo().dev_type == 0x1113 || this.GetDevInfo().dev_type == 0x1114) {
            if (disdata.datas[2].range_info.length() > "(0-20000)".length()) {
                disdata.datas[2].unit = "细胞/ml";
            }
        }

        disdata.datas[4].mainData = NahonConvert.TimData(MPAR3.GetValue(), 2);   //温度值
        disdata.datas[4].range_info = "(" + this.VTRANGE_MIN.GetValue() + "-" + this.VTRANGE_MAX.GetValue() + ")"; //量程
        disdata.datas[5].mainData = NahonConvert.TimData(SR5.GetValue(), 2); //温度原始值

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
            ret.children.addAll(Arrays.asList(CalTemer(testdata[0])));
        } else {
            ret.children.addAll(Arrays.asList(CalDevice(type, oradata, testdata)));
        }
        return ret;
    }

    private LogNode[] CalDevice(String type, float[] oradata, float[] caldata) throws Exception {
        if (SCLODATA.length < oradata.length) {
            throw new Exception("定标个数异常");
        }

        String[] GetDataNames = this.GetDataNames();
        if (type.contains(GetDataNames[0])) {
            this.SCLTYPE.SetValue(0);
            SCLRANG.SetValue(this.NRANGE.GetValue());
        } else {
            this.SCLTYPE.SetValue(1);
            SCLRANG.SetValue(this.NRANGE2.GetValue());
        }

        for (int i = 0; i < oradata.length; i++) {
            SCLODATA[i].SetValue(oradata[i]);
            SCLTDATA[i].SetValue(caldata[i]);
        }
        SCLSTART.SetValue(oradata.length);
        this.SetMEG(SCLTYPE, SCLRANG, SCLODATA[0], SCLODATA[1], SCLODATA[2], SCLTDATA[0], SCLTDATA[1], SCLTDATA[2], SCLSTART);

        if (type.contains(GetDataNames[0])) {
            //NVPA初始化
            this.ReadMEG(NCLTEMPER[0], NCLTEMPER[1], NCLTEMPER[2], NCLTEMPER[3],
                    NCLPARA[0], NCLPARA[1], NCLPARA[2], NCLPARA[3],
                    NCLPARB[0], NCLPARB[1], NCLPARB[2], NCLPARB[3],
                    NCLPARC[0], NCLPARC[1], NCLPARC[2], NCLPARC[3]);

            return new LogNode[]{new LogNode("定标参数", type),
                new LogNode("当前量程", get_range_string(NRANGE.GetValue())),
                new LogNode(NCLTEMPER[NRANGE.GetValue()].toString(), NCLTEMPER[NRANGE.GetValue()]),
                new LogNode(NCLPARA[NRANGE.GetValue()].toString(), NCLPARA[NRANGE.GetValue()]),
                new LogNode(NCLPARB[NRANGE.GetValue()].toString(), NCLPARB[NRANGE.GetValue()]),
                new LogNode(NCLPARC[NRANGE.GetValue()].toString(), NCLPARC[NRANGE.GetValue()])};
        } else {
            //NVPA初始化
            this.ReadMEG(NCLTEMPER2[0], NCLTEMPER2[1], NCLTEMPER2[2], NCLTEMPER2[3],
                    NCLPARA2[0], NCLPARA2[1], NCLPARA2[2], NCLPARA2[3],
                    NCLPARB2[0], NCLPARB2[1], NCLPARB2[2], NCLPARB2[3],
                    NCLPARC2[0], NCLPARC2[1], NCLPARC2[2], NCLPARC2[3]);

            return new LogNode[]{new LogNode("定标参数", type),
                new LogNode("当前量程", get_range_string2(NRANGE2.GetValue())),
                new LogNode(NCLTEMPER2[NRANGE.GetValue()].toString(), NCLTEMPER2[NRANGE.GetValue()]),
                new LogNode(NCLPARA2[NRANGE.GetValue()].toString(), NCLPARA2[NRANGE.GetValue()]),
                new LogNode(NCLPARB2[NRANGE.GetValue()].toString(), NCLPARB2[NRANGE.GetValue()]),
                new LogNode(NCLPARC2[NRANGE.GetValue()].toString(), NCLPARC2[NRANGE.GetValue()])};
        }
    }

    private LogNode[] CalTemer(float caltemper) throws Exception {
        this.SCLTEMPER.SetValue(caltemper);
        this.SCLTEMPERSTART.SetValue(0x01);
        this.SetMEG(SCLTEMPER, SCLTEMPERSTART);

        //NVPA初始化
        this.ReadMEG(NTEMPER_PAR);
        return new LogNode[]{new LogNode(NTEMPER_PAR.toString(), NTEMPER_PAR.GetValue())};
    }
    // </editor-fold> 

}
