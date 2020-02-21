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
public class SREG extends REG<String> {

    private String value = "";

    public SREG(int reg_add, int reg_num, String info) {
        super(reg_add, reg_num, info);
    }

    // <editor-fold defaultstate="collapsed" desc="公共接口"> 
    @Override
    public String GetValue() {
        return value;
    }

    @Override
    public void SetValue(String value) throws Exception {
        if (value.length() > this.RegNum() * 2) {
            throw new Exception("字符串长度过长:" + value.length());
        }
        this.value = value;
    }

    @Override
    public void LoadBytes(byte[] mem, int pos) throws Exception {
        if (mem.length < pos + this.RegNum() * 2) {
            throw new Exception("内存长度不足，无法初始化数据");
        }
        this.value = NahonConvert.ByteArrayToString(mem, pos, this.RegNum() * 2);
    }

    @Override
    public byte[] ToBytes() throws Exception {
        return NahonConvert.StringToByte(value, this.RegNum() * 2);
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="公共接口"> 
    @Override
    public String Convert(String value) throws Exception {
        if (value.length() > this.RegNum() * 2) {
            throw new Exception("字符串长度过长:" + value.length());
        }
        return value;
    }

    @Override
    public boolean ConmpareTo(String value) {
        return this.value.contentEquals(value);
    }
    // </editor-fold>  

}
