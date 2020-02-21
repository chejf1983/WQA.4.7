/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.adapter.model;

/**
 *
 * @author chejf
 */
public class PrintLog {
    public static boolean PintSwitch = false;

    public static void println(String info) {
        if (PintSwitch) {
            System.out.println(info);
        }
    }

    public static void print(String info) {
        if (PintSwitch) {
            System.out.print(info);
        }
    }

//    public static void PrintLog(String info) {
//        
//    }
}
