/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.DB;

import java.text.SimpleDateFormat;
import java.util.Date;

//报警记录
public class AlarmRecord {

    public int alarm;
    public String alarm_info;
    public Date time;

    public String[] GetColumnName() {
        return new String[]{"时间", "报警码", "报警信息"};
    }

    public Object[] GetValue() {
        return new Object[]{new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.time), this.alarm, this.alarm_info};
    }
}
