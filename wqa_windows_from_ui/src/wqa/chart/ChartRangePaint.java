/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.chart;

import org.jfree.data.Range;

/**
 *
 * @author jiche
 */
public class ChartRangePaint {

    private DataChart parent;
    public ChartRangePaint(DataChart parent) {
        this.parent = parent;

        //设置当前为自动调节
        this.ChangeType(RangeType.AutoRange);        
    }

    // <editor-fold defaultstate="collapsed" desc="放大使能">
    public boolean IsZoomable() {
        return this.parent.dataChartPane.isDomainZoomable() && this.parent.dataChartPane.isRangeZoomable();
    }

    public void SetZommable(boolean value) {
        this.parent.dataChartPane.setMouseZoomable(value);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="标尺类型">
    public enum RangeType {
        AutoRange,
        Manual
    }

    private RangeType rangetype;

    //修改chart范围类型
    public void ChangeType(RangeType type) {
        this.rangetype = type;

        switch (this.rangetype) {
            case AutoRange:
                //自动调节状态
                this.parent.xyplot.getRangeAxis().setAutoRange(true);//y
                this.parent.xyplot.getDomainAxis().setAutoRange(true);//x
                break;
            case Manual:
                //手动调节
                this.parent.xyplot.getRangeAxis().setAutoRange(false);//y
                this.parent.xyplot.getDomainAxis().setAutoRange(false);//x
                this.parent.xyplot.getRangeAxis().setRange(new Range(this.ManualYMin, this.ManualYMax));
                break;
            default:
                throw new AssertionError(this.rangetype.name());
        }
    }

    /**
     * 获取当前range模式
     *
     * @return
     */
    public RangeType GetRangeType() {
        return this.rangetype;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="手动设置上下限">
    public double ManualYMax = 65535;
    public double ManualYMin = -1000;

    /**
     * 设置手动量程范围
     *
     * @param Ystart
     * @param Yend
     */
    public void SetManualRange(double Ystart, double Yend) {
        this.ManualYMax = Yend;
        this.ManualYMin = Ystart;

        ChangeType(RangeType.Manual);
    }
    // </editor-fold>
}
