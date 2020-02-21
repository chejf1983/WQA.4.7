/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.data;

import wqa.control.config.SConfigItem;

/**
 *
 * @author chejf
 */
public class SMotorParameter {

    public enum CleanMode {
        Auto,
        Manu
    }

    public SMotorParameter(CleanMode mode,
            SConfigItem[] auto_config,
            SConfigItem[] manu_config) {
        this.mode = mode;
        this.auto_config = auto_config;
        this.manu_config = manu_config;
    }

    public SMotorParameter(SMotorParameter other) {
        this.mode = other.mode;
        this.auto_config = other.auto_config;
        this.manu_config = other.manu_config;
    }

    public CleanMode mode;
//    public int cleantime;     //(1-100)
//    public int clean_interval;//(10min-24h)
    public SConfigItem[] auto_config;
    public SConfigItem[] manu_config;
}
