/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.naqing.mockdev;

/**
 *
 * @author chejf
 */
public class PrintLog {
    public static int NOPRINT = 0x00;
    public static int IOLOG = 0x01;
    public static int PRINTLOG = 0x02;
    
    private static int PRINTTAG = 0;

    public static void SetPrintlevel(int level){
        PRINTTAG = level;
    }
    
    public static void println(String info) {
        if ((PRINTTAG & PRINTLOG) > 0) {
            System.out.println(info);
        }
    }

    public static void print(String info) {
        if((PRINTTAG & PRINTLOG) > 0) {
            System.out.print(info);
        }
    }

    public static void printIO(String info) {
        if ((PRINTTAG & IOLOG) > 0) {
            System.out.print(info);
        }
    }
    public static void printlnIO(String info) {
        if ((PRINTTAG & IOLOG) > 0) {
            System.out.println(info);
        }
    }
}
