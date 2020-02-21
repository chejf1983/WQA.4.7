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
public class BREG extends REG<Boolean> {


    public BREG(int reg_add, int reg_num, String info) {
        super(reg_add, reg_num, info);
    }

    // <editor-fold defaultstate="collapsed" desc="属性"> 
    private boolean value = false;
    @Override
    public Boolean GetValue() {
        return value;
    }

    @Override
    public void SetValue(Boolean value) throws Exception {
        this.value = value;
    }

    @Override
    public byte[] ToBytes() throws Exception {
        return NahonConvert.UShortToByteArray(this.value ? 1 : 0);
    }

    @Override
    public void LoadBytes(byte[] mem, int pos) throws Exception {
        if (mem.length < pos + this.RegNum() * 2) {
            throw new Exception("内存长度不足，无法初始化数据");
        }
        int ib = NahonConvert.ByteArrayToUShort(mem, pos);

        this.value = ib > 0;
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="公共接口"> 
    @Override
    public Boolean Convert(String value) throws Exception{
        return Boolean.valueOf(value);
    }

    @Override
    public boolean ConmpareTo(Boolean value) {
        return this.value == value;
    }
    // </editor-fold>  

}
