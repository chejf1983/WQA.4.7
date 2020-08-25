/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modebus.pro;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import modebus.register.REG;
import nahon.comm.io.AbstractIO;

/**
 *
 * @author chejf
 */
public class ModeBusClient {

    public static byte READCMD = 0x03;
    public static byte WRITECMD = 0x10;

    public static int MINLEN = 4;

    public static int OK = 0;//无错误
    public static int ERR1 = 1;//内存范围错误
    public static int ERR2 = 2;//非法波特率或校验
    public static int ERR3 = 3;//非法从属地址
    public static int ERR4 = 4;//非法Modbus参数值
    public static int ERR5 = 5; //保持寄存器与Modbus从属符号重叠
    public static int ERR6 = 6;//收到校验错误
    public static int ERR7 = 7;//收到CRC错误
    public static int ERR8 = 8;//非法功能请求／功能不受支持
    public static int ERR9 = 9;//请求中的非法内存地址
    public static int ERR10 = 10; //从属功能未启用

    private byte[] memory = new byte[1024];
    private byte address;
    private AbstractIO io_instance;
    private MCRC16 crc16 = new MCRC16();

    public ModeBusClient(AbstractIO io, byte addr) {
        io_instance = io;
        this.address = addr;
    }

    // <editor-fold defaultstate="collapsed" desc="设备地址"> 
    public byte GetAddr() {
        return this.address;
    }

    public void SetAddr(byte addr) {
        this.address = addr;
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="监听端口"> 
    private boolean is_start = false;
    private int def_timeout = 100;
    private byte[] re_buffer = new byte[255];
    public final Lock dbLock = new ReentrantLock(true);

    public void StartListen() {
        if (this.is_start) {
            return;
        }
        this.is_start = true;
        while (this.is_start) {
            dbLock.lock();
            try {
                int rec_len = this.io_instance.ReceiveData(re_buffer, def_timeout);
                if (rec_len > 0) {
                    byte[] rec_data = this.ReceiveCmd(re_buffer, rec_len);
                    if (rec_data.length > 0) {
                        this.io_instance.SendData(rec_data);
                    }
                }
            } catch (Exception ex) {
            } finally {
                dbLock.unlock();
            }

            try {
                //休息10ms
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (Exception ex2) {
//                Logger.getLogger(ModeBusClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void StopListen() {
        dbLock.lock();
        try {
            is_start = false;
        } finally {
            dbLock.unlock();
        }
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="外部命令"> 
    public byte[] ReceiveCmd(byte[] input, int buffer_len) throws Exception {
        //预先检查
        if (!this.precheck(input, buffer_len)) {
            //非本地包，丢弃
            return new byte[0];
        }

        //获取功能码
        int cmd = input[1];
        //检查CRC
        int ret = this.checkCRC(input, buffer_len);
        if (ret != OK) {
            //检查错误，返回错误码 地址+功能码（最高位置d1）+错误码+校验
            return buildPacket(0x80 | cmd, new byte[]{(byte) ret});
        }

        //处理读命令
        if (cmd == READCMD || cmd == WRITECMD) {
            //获取地址
            int reg_addr = NahonConvert.ByteArrayToUShort(input, 2);
            //检查地址
            if (reg_addr < 0 && reg_addr * 2 > this.memory.length) {
                return this.buildPacket(0x80 | cmd, new byte[]{(byte) ERR3});
            }
            //获取寄存器个数
            int reg_num = NahonConvert.ByteArrayToUShort(input, 4);
            //检查长度
            if (reg_num < 0 && (reg_addr + reg_num) * 2 > this.memory.length) {
                return this.buildPacket(0x80 | cmd, new byte[]{(byte) ERR1});
            }

            if (cmd == READCMD) {
                //创建缓存，长度（1）+数据
                byte[] rec_buffer = new byte[reg_num * 2 + 1];
                //赋值长度
                rec_buffer[0] = (byte) (reg_num * 2); //长度
                //读内存
                System.arraycopy(this.memory, reg_addr * 2, rec_buffer, 1, rec_buffer[0]);
                //返回数据
                return this.buildPacket(cmd, rec_buffer);
            } else {
                //字节计数器
                int len = input[6];
                //字节计数器长度等于寄存器个数*2
                if (len != reg_num * 2) {
                    return this.buildPacket(0x80 | cmd, new byte[]{(byte) ERR4});
                }

                byte[] mem = new byte[len];
                System.arraycopy(input, 7, mem, 0, len);

                //设置内存
                System.arraycopy(input, 7, this.memory, reg_addr * 2, reg_num * 2);

                //返回寄存器地址+个数
                return this.buildPacket(cmd, NahonConvert.IntegerToByteArray(reg_addr << 16 | reg_num));
            }
        } else {
            return this.buildPacket(0x80 | cmd, new byte[]{(byte) ERR8});
        }
    }

    //预检
    private boolean precheck(byte[] buffer, int buffer_len) {
        //小于最小包，丢弃
        if (buffer_len <= MINLEN) {
            return false;
        }

        //地址不正确
        if (buffer[0] != 0 && buffer[0] != this.GetAddr()) {
            return false;
        }

        return true;
    }

    //检查crc
    private int checkCRC(byte[] buffer, int buffer_len) throws Exception {
//        MCRC16 crc16 = new MCRC16();
        int crc = crc16.getCrc(buffer, buffer_len - 2);
        byte[] a_crc = NahonConvert.UShortToByteArray(crc);
        if (a_crc[0] == buffer[buffer_len - 2] && a_crc[1] == buffer[buffer_len - 1]) {
            return OK;
        } else {
            return ERR7;
        }
    }

    //组包
    private byte[] buildPacket(int cmd, byte[] buffer) throws Exception {
        byte[] ret = new byte[buffer.length + 3];

        //赋值地址
        ret[0] = this.GetAddr(); //地址
        ret[1] = (byte) cmd;//命令字

        //赋值内容
        System.arraycopy(buffer, 0, ret, 2, ret.length);

        //计算校验
        MCRC16 crc16 = new MCRC16();
        int crc = crc16.getCrc(ret, ret.length - 2);
        //赋值CRC校验码
        System.arraycopy(NahonConvert.UShortToByteArray(crc), 0, ret, ret.length - 2, 2);//CRC检验
        return ret;
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="寄存器更新"> 
    private ArrayList<REG> local_reg = new ArrayList();

    public void RegisterREGS(REG... regs) {
        for (REG reg : regs) {
            this.local_reg.add(reg);
        }
    }

    public void DowloadRegs() throws Exception {
        for (REG reg : local_reg) {
            byte[] mem = reg.ToBytes();
            System.arraycopy(mem, 0, this.memory, reg.RegAddr() * 2, reg.RegNum() * 2);
        }
    }

    public void Refresh() throws Exception {
        for (REG reg : local_reg) {
            reg.LoadBytes(this.memory, reg.RegAddr() * 2);
        }
    }
    // </editor-fold>  
}
