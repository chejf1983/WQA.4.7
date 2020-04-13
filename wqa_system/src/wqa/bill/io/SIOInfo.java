/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.bill.io;

/**
 *
 * @author Administrator
 */
public class SIOInfo {

    public static String COM = "COM";
    public static String TCP = "TCP";
    public static String USB = "USB";

    public String iotype;
    public String[] par;

    public SIOInfo(String iotype, String... pars) {
        this.iotype = iotype;
        this.par = pars;
    }

    public SIOInfo(SIOInfo info) {
        this.iotype = info.iotype;
        this.par = info.par;
    }

    public boolean equalto(SIOInfo info) {
        if (info == null) {
            return false;
        }

        if ((this.iotype == null ? info.iotype == null : this.iotype.equals(info.iotype))
                && this.par.length == info.par.length) {
            for (int i = 0; i < this.par.length; i++) {
                if (!this.par[i].equals(info.par[i])) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    public String GetString() {
        String ret = "类型[" + this.iotype + "], 参数";
        for (String par : this.par) {
            ret += par + " ";
        }
        return ret;
    }
}
