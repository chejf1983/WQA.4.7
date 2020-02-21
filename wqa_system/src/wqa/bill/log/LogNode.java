/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.bill.log;

import java.util.ArrayList;

/**
 *
 * @author chejf
 */
public class LogNode {

    public String name;
    public Object[] value;
    public ArrayList<LogNode> children = new ArrayList();

    public LogNode(String name, Object... value) {
        this.name = name;
        this.value = value;
    }

    public static LogNode CALOK() {
        return new LogNode("效准结果", "效准成功");
    }

    public static LogNode CALFAIL() {
        return new LogNode("效准结果", "效准失败");
    }
}
