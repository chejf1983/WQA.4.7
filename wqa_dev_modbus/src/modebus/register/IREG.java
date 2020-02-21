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
public class IREG extends REG<Integer> {

    private int value = 0;
    public int min = 0;
    public int max = 0;

    public IREG(int reg_add, int reg_num, String info) {
        super(reg_add, reg_num, info);
        switch (this.RegNum()) {
            case 1:
                max = 65535;
                break;
            case 2:
                max = Integer.MAX_VALUE;
                break;
            default:
                max = Integer.MAX_VALUE;
        }
        min = -max;
    }

    public IREG(int reg_add, int reg_num, String info, int min, int max) {
        super(reg_add, reg_num, info);
        this.min = min;
        this.max = max;
    }

    // <editor-fold defaultstate="collapsed" desc="属性"> 
    @Override
    public Integer GetValue() {
        return value;
    }

    @Override
    public void SetValue(Integer value) throws Exception {
        if (value < this.min || value > this.max) {
            throw new Exception("超出量程:" + this.min + "-" + this.max);
        }
        this.value = value;
    }

    @Override
    public byte[] ToBytes() throws Exception {
        switch (this.RegNum()) {
            case 1:
                return NahonConvert.UShortToByteArray(value);
            case 2:
                return NahonConvert.IntegerToByteArray(value);
            default:
                throw new Exception("无法识别的整数寄存器");
        }
    }

    @Override
    public void LoadBytes(byte[] mem, int pos) throws Exception {
        if (mem.length < pos + this.RegNum() * 2) {
            throw new Exception("内存长度不足，无法初始化数据");
        }
        switch (this.RegNum()) {
            case 1:
                this.value = NahonConvert.ByteArrayToUShort(mem, pos);
                break;
            case 2:
                this.value = NahonConvert.ByteArrayToInteger(mem, pos);
                break;
            default:
                throw new Exception("无法识别的整数寄存器");
        }
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="公共接口"> 
    @Override
    public Integer Convert(String value) throws Exception {
        return Integer.valueOf(value);
    }

    @Override
    public boolean ConmpareTo(Integer value) {
        return value.compareTo(this.value) == 0;
    }
    // </editor-fold>  

}
