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
public class SDataElement {

    public String name = "";
    public float mainData = 0;
    public String unit = "";
    public String range_info = "";
    public int team;

    public SDataElement() {
    }

    public SDataElement(SDataElement other) {
        this.name = other.name;
        this.mainData = other.mainData;
        this.unit = other.unit;
        this.range_info = other.range_info;
        this.team = other.team;
    }
}
