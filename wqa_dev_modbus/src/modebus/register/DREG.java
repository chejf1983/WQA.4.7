/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modebus.register;

import modebus.pro.NahonConvert;

/**
 *
 * @author chejf
 */
public class DREG extends REG<Double> {


    public DREG(int reg_add, int reg_num, String info) {
        super(reg_add, reg_num, info);
        min = -Double.MAX_VALUE;
        max = Double.MAX_VALUE;
    }

    public DREG(int reg_add, int reg_num, String info, double min, double max) {
        super(reg_add, reg_num, info);
        this.min = min;
        this.max = max;
    }

    // <editor-fold defaultstate="collapsed" desc="属性"> 
    private double value = 0;
    public double min = 0;
    public double max = 0;
    @Override
    public Double GetValue() {
        return value;
    }

    @Override
    public void SetValue(Double value) throws Exception {
        if (value < this.min || value > this.max) {
            throw new Exception("超出量程:" + this.min + "-" + this.max);
        }
        this.value = value;
    }

    @Override
    public byte[] ToBytes() throws Exception {
        return NahonConvert.DoubleToByteArray(value);
    }

    @Override
    public void LoadBytes(byte[] mem, int pos) throws Exception {
        if (mem.length < pos + this.RegNum() * 2) {
            throw new Exception("内存长度不足，无法初始化数据");
        }
        this.value = NahonConvert.ByteArrayToDouble(mem, pos);
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="公共接口"> 

    @Override
    public Double Convert(String value) throws Exception{
        return Double.valueOf(value);
    }

    @Override
    public boolean ConmpareTo(Double value) {
        return value.compareTo(this.value) == 0;
    }
    // </editor-fold> 

}
