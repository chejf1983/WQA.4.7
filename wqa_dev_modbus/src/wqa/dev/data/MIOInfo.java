/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.dev.data;

/**
 *
 * @author chejf
 */
public class MIOInfo {
    public static String COM = "COM";
    public static String TCP = "TCP";
    public static String USB = "USB";
    
    public String iotype;
    public String[] par;

    public MIOInfo(String iotype, String... pars) {
        this.iotype = iotype;
        this.par = pars;
    }

    public MIOInfo(MIOInfo info) {
        this.iotype = info.iotype;
        this.par = info.par;
    }
}
