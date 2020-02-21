/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.model;

import java.util.ArrayList;
import modebus.pro.CRC16;
import modebus.pro.NahonConvert;
import modebus.register.IREG;
import modebus.register.REG;

/**
 *
 * @author chejf
 */
public class ModbusClient {

    public static byte READCMD = 0x03;
    public static byte WRITECMD = 0x10;
    private byte[] memory = new byte[1024];
    protected final IREG DEVADDR = new IREG(0x23, 1, "设备地址", 1, 32);//R/W

    public ModbusClient() {

    }

    private byte GetAddr() throws Exception {
        this.ReadREG(DEVADDR);
        return DEVADDR.GetValue().byteValue();
    }

    // <editor-fold defaultstate="collapsed" desc="外部命令"> 
    private byte[] reply = new byte[0];

    public void ReceiveCmd(byte[] input) throws Exception {
        //检查地址
        if (input[0] != this.GetAddr()) {
            reply = new byte[0];
            return;
        }

        int cmd = input[1];
        int reg_addr = NahonConvert.ByteArrayToUShort(input, 2);
        int reg_num = NahonConvert.ByteArrayToUShort(input, 4);

        byte[] rec_buffer = new byte[100];
        int rec_buffer_len;
        rec_buffer[0] = this.GetAddr(); //地址
        rec_buffer[1] = (byte) cmd;//命令字

        if (cmd == READCMD) {
            rec_buffer_len = 5 + reg_num * 2;
            rec_buffer[2] = (byte) (reg_num * 2); //长度
            //读内存
            System.arraycopy(this.ReadReg(reg_addr, reg_num), 0, rec_buffer, 3, rec_buffer[2]);
        } else {
            int len = input[6];
            byte[] mem = new byte[len];
            System.arraycopy(input, 7, mem, 0, len);

            //设置内存
            this.SetREG(reg_addr, reg_num, mem);

            rec_buffer_len = 8;
            System.arraycopy(NahonConvert.UShortToByteArray(reg_addr), 0, rec_buffer, 2, 2); //寄存器
            System.arraycopy(NahonConvert.UShortToByteArray(reg_num), 0, rec_buffer, 4, 2);  //个数
        }
        CRC16 crc16 = new CRC16();
        int crc = crc16.getCrc(rec_buffer, rec_buffer_len - 2);
        System.arraycopy(NahonConvert.UShortToByteArray(crc), 0, rec_buffer, rec_buffer_len - 2, 2);//CRC检验

        //返回命令
        byte[] ret = new byte[rec_buffer_len];
        System.arraycopy(rec_buffer, 0, ret, 0, ret.length);
        reply = ret;
    }

    public byte[] Reply() {
        return reply;
    }

    private void SetREG(int addr, int num, byte[] buffer) {
        System.arraycopy(buffer, 0, this.memory, addr * 2, num * 2);
    }

    private byte[] ReadReg(int addr, int num) {
        byte[] buffer = new byte[num * 2];
        System.arraycopy(this.memory, addr * 2, buffer, 0, buffer.length);
        return buffer;
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="寄存器更新"> 
    private void ReadREG(REG... regs) throws Exception {
        for (REG reg : regs) {
            reg.LoadBytes(this.memory, reg.RegAddr() * 2);
        }
    }

    private void SetREG(REG... regs) throws Exception {
        for (REG reg : regs) {
            byte[] mem = reg.ToBytes();
            System.arraycopy(mem, 0, this.memory, reg.RegAddr() * 2, reg.RegNum() * 2);
        }
    }

    private ArrayList<REG> local_reg = new ArrayList();

    public void RegisterREGS(REG... regs) {
        for (REG reg : regs) {
            this.local_reg.add(reg);
        }
    }

    public void DowloadRegs() throws Exception {
        this.SetREG(local_reg.toArray(new REG[0]));
    }

    public void Refresh() throws Exception {
        this.ReadREG(local_reg.toArray(new REG[0]));
    }
    // </editor-fold>  
}
