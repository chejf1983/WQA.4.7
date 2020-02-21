/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.chart;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;

/**
 * 标线绘制
 *
 * @author jiche
 */
public class CrossHairPaint {

    //鼠标点
    private class MousePoint {

        public double x, y;

        public MousePoint(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    //标线涂层
    private CrosshairOverlay crosshairoverlay = new CrosshairOverlay();
    //数据表
    private DataChart parent;

    //初始化
    public CrossHairPaint(DataChart dataChartPane) {
        this.parent = dataChartPane;
        //添加标线图层
        this.parent.dataChartPane.addOverlay(crosshairoverlay);

        //初始化标线
        initLable();

        this.parent.dataChartPane.setZoomTriggerDistance(20);
                
        //鼠标移动事件
        this.parent.dataChartPane.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (IsCrossHairPaintEnable()) {
                    //有选中的标线，移动到鼠标移动的位置
                    if (selectedhair != null) {
                        MoveSelectCrossHairTo(GetMouseSlectedDomainValue(e));
                        //重新计算选中标线对应最近的点
                        lable_cal.Update();
//                        XYPlot xyplot = (XYPlot) parent.dataChart.getPlot();
//                        XYItemRenderer linerender = xyplot.getRenderer();
//                        linerender.setBaseItemLabelsVisible(Lable_enable);
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });

        //鼠标点击事件
        this.parent.dataChartPane.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (IsCrossHairPaintEnable()) {
                    //双击左键，在鼠标选择的坐标，创建新标线
                    if ((e.getButton() == MouseEvent.BUTTON1)) {
                        if (e.getClickCount() == 2) {
                            CreateCrossHair(GetMouseSlectedDomainValue(e));
                        }

                    } else if (e.getButton() == MouseEvent.BUTTON3) {
                        //鼠标右键双击，删除离鼠标最近的一条标线
                        if (e.getClickCount() == 2) {
                            DeleteCrossHair(GetNearestCrossHair(GetMouseSlectedDomainValue(e)));
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (IsCrossHairPaintEnable()) {
                    //鼠标左键单击，寻找最近的标线
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        selectedhair = GetNearestCrossHair(GetMouseSlectedDomainValue(e));
                        if (selectedhair != null) {
                            //选中的表面，颜色变成橘黄色
                            selectedhair.setPaint(Color.ORANGE);
                            //关闭放大缩小功能
                            parent.chart_range.SetZommable(false);
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (IsCrossHairPaintEnable()) {
                    //鼠标左键释放
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if (selectedhair != null) {
                            //恢复选中标线颜色
                            selectedhair.setPaint(Color.RED);

                            //恢复放大缩小功能
                            parent.chart_range.SetZommable(true);
                        }
                        selectedhair = null;
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="显示点值">
    public PointLine lable_cal;

    private void initLable() {
        lable_cal = new PointLine(this.parent, this.x_crosshairs);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="XY轴切换">
    private boolean is_x_enable = true;

    public boolean IsXModel() {
        return this.is_x_enable;
    }

    public void SetXModel(boolean value) {
        this.is_x_enable = value;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="标线是否开启">
    //标线是否开启
    private boolean iscorssHairPaintEnable = false;

    public boolean IsCrossHairPaintEnable() {
        return this.iscorssHairPaintEnable;
    }

    public void EnableCrossHairPaint(boolean value) {
        this.iscorssHairPaintEnable = value;

        for (Crosshair crosshair : this.x_crosshairs) {
            crosshair.setVisible(value);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="标线创建删除">
    private ArrayList<Crosshair> x_crosshairs = new ArrayList(); //标线组
    private ArrayList<Crosshair> y_crosshairs = new ArrayList(); //标线组
    //创建标线

    //创建标线
    private void CreateCrossHair(MousePoint p) {
        Crosshair crosshair;
        if (this.is_x_enable) {
            crosshair = new Crosshair(p.x);
        } else {
            crosshair = new Crosshair(p.y);
        }

        /* Init corsshair */
        //标线颜色
        crosshair.setPaint(Color.red);

        //标线旗帜设置
//        crosshair.setLabelVisible(true);
        //标线旗帜位置
        crosshair.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        //旗帜背景色
        crosshair.setLabelBackgroundPaint(new Color(255, 255, 0, 100));

        //是否显示标线
        //crosshair.setVisible(this.iscorssHairPaintEnable);
        if (this.is_x_enable) {
            //设置标线上的标签
//            crosshair.setLabelGenerator((Crosshair crshr) -> {
//                Date time = new Date();
//                time.setTime((long) crshr.getValue());
//                return new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(time);
//            });

            //添加标线
            this.crosshairoverlay.addDomainCrosshair(crosshair);
            this.x_crosshairs.add(crosshair);
            //标线点搜索添加标线
            this.lable_cal.Update();
        } else {
//            //设置标线上的标签
//            crosshair.setLabelGenerator((Crosshair crshr) -> {
//                return String.format("%.2f", crshr.getValue());
//            });
            //添加标线
            this.crosshairoverlay.addRangeCrosshair(crosshair);
            this.y_crosshairs.add(crosshair);
        }
    }

    //删除标线(选中的标线)
    private void DeleteCrossHair(Crosshair selecthair) {
        if (this.is_x_enable) {
            if (this.x_crosshairs.size() > 0) {
                if (this.x_crosshairs.remove(selecthair)) {
                    this.crosshairoverlay.removeDomainCrosshair(selecthair);
                    //标线点搜索删除标线
                    this.lable_cal.Update();
                }
            }
        } else {
            if (this.y_crosshairs.size() > 0) {
                if (this.y_crosshairs.remove(selecthair)) {
                    this.crosshairoverlay.removeRangeCrosshair(selecthair);
                }
            }
        }
    }

    //清除所有标线
    public void ClearAllHair() {
        for (Crosshair hair : this.x_crosshairs) {
            this.crosshairoverlay.removeDomainCrosshair(hair);
        }

        for (Crosshair hair : this.y_crosshairs) {
            this.crosshairoverlay.removeRangeCrosshair(hair);
        }

        this.x_crosshairs.clear();
        this.y_crosshairs.clear();
        //标线点搜索删除标线
        this.lable_cal.Update();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="选中的标线控制">
    private Crosshair selectedhair;  //选中的标线
    //选择最近的一个标线，做选中的标线

    //计算当前鼠标对应的X轴的值
    private MousePoint GetMouseSlectedDomainValue(java.awt.event.MouseEvent evt) {
        int xPos = evt.getX();
        int yPos = evt.getY();
        XYPlot xyplot = (XYPlot) this.parent.xyplot;
        Point2D point2D = this.parent.dataChartPane.translateScreenToJava2D(new Point(xPos, yPos));
//        XYPlot xyPlot = (XYPlot)this.dataChart.getPlot();
        ChartRenderingInfo chartRenderingInfo = this.parent.dataChartPane.getChartRenderingInfo();
        Rectangle2D rectangle2D = chartRenderingInfo.getPlotInfo().getDataArea();
        ValueAxis valueAxis1 = xyplot.getDomainAxis();
        RectangleEdge rectangleEdge1 = xyplot.getDomainAxisEdge();
        ValueAxis valueAxis2 = xyplot.getRangeAxis();
        RectangleEdge rectangleEdge2 = xyplot.getRangeAxisEdge();
        double d1 = valueAxis1.java2DToValue(point2D.getX(), rectangle2D, rectangleEdge1);
        double d2 = valueAxis2.java2DToValue(point2D.getY(), rectangle2D, rectangleEdge2);

        if (d1 < xyplot.getDomainAxis().getLowerBound()) {
            d1 = xyplot.getDomainAxis().getLowerBound();
        } else if (d1 > xyplot.getDomainAxis().getUpperBound()) {
            d1 = xyplot.getDomainAxis().getUpperBound();
        }

        return new MousePoint(d1, d2);
    }

    public Crosshair GetNearestCrossHair(java.awt.event.MouseEvent evt) {
        return GetNearestCrossHair(GetMouseSlectedDomainValue(evt));
    }
    
    //根据坐标点，获取最近的标线值
    private Crosshair GetNearestCrossHair(MousePoint p) {
        ArrayList<Crosshair> crosshairs;
        ValueAxis axis;//坐标轴
        double value;

        if (this.is_x_enable) {
            crosshairs = this.x_crosshairs;
            axis = ((XYPlot) this.parent.xyplot).getDomainAxis();
            value = p.x;
        } else {
            crosshairs = this.y_crosshairs;
            axis = ((XYPlot) this.parent.xyplot).getRangeAxis();
            value = p.y;
        }

        Crosshair ret = null;
        //坐标轴的50分之一作为有效距离
        double distance = (axis.getUpperBound() - axis.getLowerBound()) / 40;
        for (Crosshair crosshair : crosshairs) {
            //当前位置与标线的绝对距离
            double tmpdistance = Math.abs(crosshair.getValue() - value);
            //绝对距离小于有效距离且绝对距离最小的标线有效
            if (tmpdistance < distance) {
                ret = crosshair;
                distance = tmpdistance;
            }
        }
        return ret;
    }

    //移动标线
    private void MoveSelectCrossHairTo(MousePoint p) {
        if (this.selectedhair != null) {
            if (this.is_x_enable) {
                selectedhair.setValue(p.x);
            } else {
                selectedhair.setValue(p.y);
            }
        }
    }
    // </editor-fold>
}
