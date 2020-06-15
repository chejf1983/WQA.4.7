/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.model;

import base.migp.node.MIGPCodec;
import base.migp.node.MIGPPacket;
import base.migp.node.MIGP_CmdSend;
import base.migp.reg.MEG;
import base.pro.convert.NahonConvert;
import java.util.ArrayList;
import java.util.Arrays;
import migp.adapter.factory.AbsDevice;

/**
 *
 * @author chejf
 */
public class MigpClient {

    private byte[] mem_eia = new byte[1024];
    private byte[] mem_mda = new byte[1024];
    private byte[] mem_sra = new byte[1024];
    private byte[] mem_nvpa = new byte[1024];
    private byte[] mem_vpa = new byte[1024];
    public int addr = 2;
    public int bandrate = 1;

    public MigpClient() {

    }

    private byte GetAddr() throws Exception {
        return (byte) addr;
    }

    // <editor-fold defaultstate="collapsed" desc="外部命令"> 
    private byte[] reply = new byte[0];

    public void ReceiveCmd(byte[] input) throws Exception {
        MIGPPacket packet = MIGPCodec.DecodeBuffer(input, input.length);
        if(packet.dstAddress != this.addr){
            return;
        }
        int last_addr = this.addr;
        byte[] rec_buffer = new byte[]{MIGP_CmdSend.FALSE};
                
        switch (packet.CMD) {
            case MIGP_CmdSend.SET_DEVNUM:
                this.addr = packet.data[0];
                rec_buffer = new byte[]{MIGP_CmdSend.TRUE};                
                break;
            case AbsDevice.MIGP_CMD_GET_COMM_PARA:                
                rec_buffer = new byte[]{(byte)this.bandrate};    
                break;
            case AbsDevice.MIGP_CMD_SET_COMM_PARA:
                this.bandrate = NahonConvert.ByteArrayToUShort(packet.data, 0);
                rec_buffer = new byte[]{MIGP_CmdSend.TRUE};    
                break;
            case MIGP_CmdSend.GETEIA:
            case MIGP_CmdSend.GETSRA:
            case MIGP_CmdSend.GETMDA:
            case MIGP_CmdSend.GETVPA:
            case MIGP_CmdSend.GETNVPA:
                rec_buffer = this.read_mem(this.GetMeory(packet.CMD), NahonConvert.ByteArrayToInteger(packet.data, 0), NahonConvert.ByteArrayToInteger(packet.data, 4));
                break;
            case MIGP_CmdSend.SETEIA:
            case MIGP_CmdSend.SETSRA:
            case MIGP_CmdSend.SETMDA:
            case MIGP_CmdSend.SETVPA:
            case MIGP_CmdSend.SETNVPA:
                this.write_mem(this.GetMeory(packet.CMD), NahonConvert.ByteArrayToInteger(packet.data, 0), NahonConvert.ByteArrayToInteger(packet.data, 4), packet.data);
                rec_buffer = new byte[]{MIGP_CmdSend.TRUE};
                break;
            default:
                throw new Exception(String.format("异常命令: 0x%0X", packet.CMD));
        }
        
        MIGPPacket rec = new MIGPPacket(packet.srcAddress, (byte)last_addr, (byte)(packet.CMD | 0x80), rec_buffer);
        this.reply = MIGPCodec.EncodeBuffer(rec);
    }

    public byte[] Reply() {
        return reply;
    }

    private void write_mem(byte[] memory, int addr, int num, byte[] buffer) {
        System.arraycopy(buffer, 8, memory, addr, num);
    }

    private byte[] read_mem(byte[] memory, int addr, int num) throws Exception {
        byte[] buffer = new byte[num + 4];
        System.arraycopy(NahonConvert.IntegerToByteArray(addr), 0, buffer, 0, 4);
        System.arraycopy(memory, addr, buffer, 4, num);
        return buffer;
    }

    private byte[] GetMeory(int cmd) {
        switch (cmd) {
            case MIGP_CmdSend.GETEIA:
            case MIGP_CmdSend.SETEIA:
                return this.mem_eia;
            case MIGP_CmdSend.GETSRA:
            case MIGP_CmdSend.SETSRA:
                return this.mem_sra;
            case MIGP_CmdSend.GETMDA:
            case MIGP_CmdSend.SETMDA:
                return this.mem_mda;
            case MIGP_CmdSend.GETNVPA:
            case MIGP_CmdSend.SETNVPA:
                return this.mem_nvpa;
            case MIGP_CmdSend.GETVPA:
            case MIGP_CmdSend.SETVPA:
                return this.mem_vpa;
            default:
                return this.mem_eia;
        }
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="寄存器更新"> 
    private void ReadREG(MEG... regs) throws Exception {
        for (MEG reg : regs) {
            reg.LoadBytes(this.GetMeory(reg.GetMEM().getMEM), reg.GetMEM().addr);
        }
    }

    private void SetREG(MEG... regs) throws Exception {
        for (MEG reg : regs) {
            byte[] mem = reg.ToBytes();
            System.arraycopy(mem, 0, this.GetMeory(reg.GetMEM().setMEM), reg.GetMEM().addr, reg.GetMEM().length);
        }
    }

    private ArrayList<MEG> local_reg = new ArrayList();

    public void RegisterREGS(MEG... regs) {
        this.local_reg.addAll(Arrays.asList(regs));
    }

    public void DowloadRegs() throws Exception {
        this.SetREG(local_reg.toArray(new MEG[0]));
    }

    public void Refresh() throws Exception {
        this.ReadREG(local_reg.toArray(new MEG[0]));
    }
    // </editor-fold>  
}
