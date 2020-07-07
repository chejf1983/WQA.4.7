/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modebus.pro;

import modebus.register.REG;
import wqa.dev.intf.IMAbstractIO;

/**
 *
 * @author chejf
 */
public class ModeBusNode {

    protected IMAbstractIO io;
    public byte addr = 0x00;
    public final int def_timeout = 400;
    public final int max_pack_len = 1024;
    public static byte READCMD = 0x03;
    public static byte WRITECMD = 0x10;

    public ModeBusNode(IMAbstractIO io, byte addr) {
        this.io = io;
        this.addr = addr;
    }

    // <editor-fold defaultstate="collapsed" desc="公共接口"> 
    public IMAbstractIO GetIO() {
        return this.io;
    }

    public byte GetCurrentAddr() {
        return this.addr;
    }

    private byte[] ReadPacket(byte devaddr, int memaddr, int mem_num) throws Exception {
        byte[] tmp = new byte[1 + 1 + 2 + 2 + 2];
        tmp[0] = devaddr;
        tmp[1] = READCMD; // 读寄存器命令字
        System.arraycopy(NahonConvert.UShortToByteArray(memaddr), 0, tmp, 2, 2);
        System.arraycopy(NahonConvert.UShortToByteArray(mem_num), 0, tmp, 4, 2);
        MCRC16 crc16 = new MCRC16();
        int crc = crc16.getCrc(tmp, tmp.length - 2);
        System.arraycopy(NahonConvert.UShortToByteArray(crc), 0, tmp, 6, 2);
        return tmp;
    }

    private byte[] WriterPacket(byte devaddr, int memaddr, int mem_num, byte[] par) throws Exception {
        byte[] tmp = new byte[1 + 1 + 2 + 2 + 1 + par.length + 2];
        tmp[0] = devaddr;
        tmp[1] = WRITECMD; // 写寄存器命令字
        System.arraycopy(NahonConvert.UShortToByteArray(memaddr), 0, tmp, 2, 2);
        System.arraycopy(NahonConvert.UShortToByteArray(mem_num), 0, tmp, 4, 2);
        tmp[6] = (byte) (par.length);
        System.arraycopy(par, 0, tmp, 7, par.length);
        MCRC16 crc16 = new MCRC16();
        int crc = crc16.getCrc(tmp, tmp.length - 2);
        System.arraycopy(NahonConvert.UShortToByteArray(crc), 0, tmp, tmp.length - 2, 2);
        return tmp;
    }

    private boolean CheckCRC(byte[] buffer, int buffer_len) throws Exception {
        if (buffer_len < 2) {
            return false;
        }

        MCRC16 crc16 = new MCRC16();
        int crc = crc16.getCrc(buffer, buffer_len - 2);
        byte[] a_crc = NahonConvert.UShortToByteArray(crc);
        return a_crc[0] == buffer[buffer_len - 2] && a_crc[1] == buffer[buffer_len - 1];
    }

    public static void main(String... args) throws Exception {
        ModeBusNode node = new ModeBusNode(null, (byte) 0);
        byte[] ret = node.WriterPacket((byte) 0x02, 0x30, 01, new byte[]{(byte) 2});
        for (int i = 0; i < ret.length; i++) {
            System.out.print(String.format("%02x ", ret[i]));
        }
        System.out.println();
        ret = node.ReadPacket((byte) 0x2, 0x30, 01);
        for (int i = 0; i < ret.length; i++) {
            System.out.print(String.format("%02x ", ret[i]));
        }

        //读取设备地址 00 03 00 23 00 01 74 11
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="内存读写"> 
    //临时缓存
    byte[] tmpbuffer = new byte[100];

    private int RecieveData(byte[] rcbuffer, int timeout) throws Exception {
        //时间戳
        long start_time = System.currentTimeMillis();
        //接收到的数据个数
        int ret_len = 0;
        while (System.currentTimeMillis() - start_time < timeout) {
            int tmp_len = this.io.ReceiveData(tmpbuffer, timeout);

            //检查内存大小
            if (ret_len + tmp_len > rcbuffer.length) {
                throw new Exception("数据缓存太大,收到数据:" + ret_len);
            }
            System.arraycopy(tmpbuffer, 0, rcbuffer, ret_len, tmp_len);
            //拼接接收的数据
            ret_len += tmp_len;

            //检查是否是完整包
            if (CheckCRC(rcbuffer, ret_len)) {
                break;
            }
        }

        //检查是否是完整包
        if (!CheckCRC(rcbuffer, ret_len)) {
            return 0;
        }

        //检查地址
        if (rcbuffer[0] != this.addr) {
            return 0;
        }

        return ret_len;
    }

    //ModeBus一包最大255个数据长度，所以接收缓存只需要1K就足够了
    private byte[] rcbuffer_area = new byte[max_pack_len];

    //读内存
    private byte[] readmemory(int memaddr, int mem_num, int timeout) throws Exception {
        //发送读取命令
        this.io.SendData(ReadPacket(this.addr, memaddr, mem_num));

        //收数据
        int ret_len = this.RecieveData(rcbuffer_area, timeout);

        //检查长度，长度至少包含1个地址+ 1个命令字+1长度字+2个CRC
        if (ret_len < 5) {
            return new byte[0];
        }

        //检查返回命令
        if (rcbuffer_area[1] == READCMD) {
            //获取返回内容长度
            int len = rcbuffer_area[2];
            byte[] ret = new byte[len];

            //将内容复制出来
            System.arraycopy(rcbuffer_area, 3, ret, 0, len);
            return ret;
        } else {
            //String packet = "";
            //for (int i = 0; i < ret_len; i++) {
            //   packet += String.format("%02X ", tmpbuffer[i]);
            //}
            return new byte[0];
        }
    }

    //读内存，三次机制
    public byte[] ReadMemory(int memaddr, int mem_num, int retry_time, int timeout) throws Exception {
        int retry = 0;
        if (retry > 10) {
            throw new Exception("重试过多");
        }
        while (retry < retry_time) {
            //变长读取，重试后，改变读取寄存器的个数，避免485对固定数据包内容有异常
            byte[] ret = this.readmemory(memaddr, mem_num + retry, timeout);
            if (ret.length > 0) {
                return ret;
            }
            retry++;
        }
        throw new Exception("超时");
    }

    //批量读寄存器
    public void ReadREG(int retry_time, int timeout, REG... reg) throws Exception {
        REG min_reg = reg[0];
        REG max_reg = reg[0];

        //找到最小最大寄存器位置
        for (REG treg : reg) {
            if (min_reg.RegAddr() > treg.RegAddr()) {
                min_reg = treg;
            }
            if (max_reg.RegAddr() < treg.RegAddr()) {
                max_reg = treg;
            }
        }
        //读取最小最大寄存器对应的内容
        byte[] memory = this.ReadMemory(min_reg.RegAddr(), max_reg.RegAddr() + max_reg.RegNum() - min_reg.RegAddr(), retry_time, timeout);
        for (REG treg : reg) {
            //每个寄存器初始化内存，地址和内存有2倍关系
            treg.LoadBytes(memory, (treg.RegAddr() - min_reg.RegAddr()) * 2);
        }
    }

    //写内存
    private boolean writermemory(int memaddr, int mem_num, byte[] memorys, int timeout) throws Exception {
        //发送读取命令
        this.io.SendData(WriterPacket(this.addr, memaddr, mem_num, memorys));
        //ModeBus一包最大255个数据长度，所以接收缓存只需要1K就足够了
        byte[] rcbuffer = new byte[max_pack_len];
        //收数据
        int ret_len = this.RecieveData(rcbuffer, timeout);

        //检查长度，长度至少包含1个地址+ 1个命令字+1长度字+2个CRC
        if (ret_len < 5) {
            return false;
        }

        //检查返回命令
        if (rcbuffer[1] == WRITECMD) {
            //暂时不判断写内存返回值，有返回表示成功            
            return CheckCRC(rcbuffer, ret_len);
        } else {
            // String packet = "";
            // for (int i = 0; i < ret_len; i++) {
            //     packet += String.format("%02X ", tmpbuffer[i]);
            // }
            return false;
        }
    }

    //写内存，三次机制
    public void WriterMemory(int memaddr, int mem_num, byte[] memorys, int retry_time, int timeout) throws Exception {
        int retry = 0;
        if (retry > 10) {
            throw new Exception("重试过多");
        }
        while (retry++ < retry_time) {
            if (this.writermemory(memaddr, mem_num, memorys, timeout)) {
                return;
            }
        }
        throw new Exception("超时");
    }

    //批量读寄存器
    public void SetREG(int retry_time, int timeout, REG... reg) throws Exception {
        REG min_reg = reg[0];
        REG max_reg = reg[0];

        //找到最小最大寄存器位置
        for (REG treg : reg) {
            if (min_reg.RegAddr() > treg.RegAddr()) {
                min_reg = treg;
            }
            if (max_reg.RegAddr() < treg.RegAddr()) {
                max_reg = treg;
            }
        }
        //开辟写内存大小，等于最大最小寄存器地址差+最大寄存器长度 * 2
        byte[] memory = new byte[(max_reg.RegAddr() + max_reg.RegNum() - min_reg.RegAddr()) * 2];
        for (REG treg : reg) {
            //复制出内存
            System.arraycopy(treg.ToBytes(), 0, memory, (treg.RegAddr() - min_reg.RegAddr()) * 2, treg.RegNum() * 2);
        }
        this.WriterMemory(min_reg.RegAddr(), memory.length / 2, memory, retry_time, timeout);
    }
    // </editor-fold>  
}
