/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.config;

/**
 *
 * @author chejf
 */
public class SConfigItem {

    public enum ItemType {
        R, W, S, B //只读，读写，选择, boolean
    }
    public String data_name;
    public String value;
    public String unit;
    public ItemType inputtype;
    public String[] range; //只在选择模式下有效

    private SConfigItem(String data_name,
            String value,
            String unit,
            ItemType inputtype,
            String[] range) {
        this.data_name = data_name;
        this.value = value;
        this.inputtype = inputtype;
        this.range = range;
        this.unit = unit;
    }

    public SConfigItem(SConfigItem other) {
        this.data_name = other.data_name;
        this.value = other.value;
        this.inputtype = other.inputtype;
        this.range = other.range;
        this.unit = other.unit;
    }

    public boolean IsKey(String key) {
        return data_name.contentEquals(key);
    }

    private boolean ischanged = false;

    public boolean IsChanged() {
        return this.ischanged;
    }

    //已更新
    public void Updated() {
        this.ischanged = false;
    }

    //设置
    public void SetValue(String newvalue) {
        this.ischanged = !this.value.contentEquals(newvalue);
        this.value = newvalue;
    }

    public static SConfigItem CreateInfoItem(String name) {
        return new SConfigItem(name, "", "",
                ItemType.R,
                null);
    }

    public static SConfigItem CreateRItem(String data_name,
            String value,
            String unit) {
        return new SConfigItem(data_name, value, unit,
                ItemType.R, null);
    }

    public static SConfigItem CreateRWItem(String data_name,
            String value,
            String unit) {
        return new SConfigItem(data_name, value, unit,
                ItemType.W, null);
    }

    public static SConfigItem CreateSItem(String data_name,
            String value,
            String unit, String[] range) {
        return new SConfigItem(data_name, value, unit,
                ItemType.S, range);
    }

    public static SConfigItem CreateBItem(String data_name,
            boolean value) {
        return new SConfigItem(data_name, String.valueOf(value), "",
                ItemType.B, null);
    }
}
