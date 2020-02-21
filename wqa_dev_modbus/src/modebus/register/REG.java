/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modebus.register;

/**
 *
 * @author chejf
 */
public abstract class REG<T> {

    public REG(int reg_add, int reg_num, String info) {
        this.reg_add = reg_add;
        this.reg_num = reg_num;
        this.info = info;
    }

    // <editor-fold defaultstate="collapsed" desc="属性"> 
    private int reg_add;
    private int reg_num;
    private String info = "";

    //寄存器地址
    public int RegAddr() {
        return this.reg_add;
    }

    //寄存器个数
    public int RegNum() {
        return this.reg_num;
    }

    public abstract void SetValue(T value) throws Exception;

    //获取值
    public abstract T GetValue();

    //获取byte值
    public abstract byte[] ToBytes() throws Exception;

    //加载byte值
    public abstract void LoadBytes(byte[] mem, int pos) throws Exception;

    //获取描述信息
    @Override
    public String toString() {
        return info;
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="公共接口"> 
    public abstract T Convert(String value) throws Exception;

    public abstract boolean ConmpareTo(T value);
    // </editor-fold>  
}
