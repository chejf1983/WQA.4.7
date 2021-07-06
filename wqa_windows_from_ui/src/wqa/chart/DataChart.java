/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.chart;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import nahon.comm.event.NEventCenter;
import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.*;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author chejf
 */
public class DataChart extends javax.swing.JPanel {

    public CrossHairPaint crosshair;
    public ChartRangePaint chart_range;

    /* Init temperature chart */
    public JFreeChart dataChart;
    public ChartPanel dataChartPane;
    public XYPlot xyplot;

    /**
     * Creates new form DataChart
     */
    public DataChart() {
        initComponents();

        this.InitChart();

        this.CreateMainLine();

        this.initSnapShot();
        
        //初始化图标大小调节
        this.chart_range = new ChartRangePaint(this);
        this.crosshair = new CrossHairPaint(this);
        this.crosshair.EnableCrossHairPaint(true);
        this.crosshair.Event = (value -> {
            this.linerender.UpdateSelect(value, new TimeSeries[]{this.main_line});
            this.linerender2.UpdateSelect(value, snapshot);
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Init Chart Component">  
    /**
     * Init data chart
     */
    private void InitChart() {
        /* 创建线性图表 */
        dataChart = ChartFactory.createTimeSeriesChart(
                "",
                "",
                "",
                null,
                true,
                false,
                false);
        xyplot = (XYPlot) this.dataChart.getPlot();
        this.dataChartPane = new ChartPanel(dataChart);

        //背景渐变颜色
        GradientPaint gradientpaint
                = new GradientPaint(0.0F, 0.0F, new Color(197, 199, 207), 0.0F, 0.0F, new Color(241, 244, 247));

        //chart背景
        dataChart.setBackgroundPaint(new Color(241, 244, 247));

        //xyplot背景
        this.xyplot.setBackgroundPaint(gradientpaint);

        //把曲线名称去掉
        dataChart.getLegend().setVisible(false);
        dataChart.getLegend().setItemFont(new Font("Microsoft YaHei", 0, 10));

        //设置网格线颜色和样式
        xyplot.setDomainGridlinePaint(new Color(188, 192, 201));
        xyplot.setDomainGridlineStroke(new BasicStroke(30));
        xyplot.setRangeGridlinePaint(new Color(159, 170, 186));
        //设置上下左右留白
        dataChart.setPadding(new RectangleInsets(5d, 5d, 5d, 10d));

        //设置Y轴显示模式
//        ((NumberAxis) xyplot.getRangeAxis()).setNumberFormatOverride(new DecimalFormat("0.00"));
        //((NumberAxis) xyplot.getRangeAxis()).setAutoRangeIncludesZero(true);//是否从0点开始
//        xyplot.getRangeAxis().setAutoRangeMinimumSize(0.01);
        xyplot.getRangeAxis().setAutoRangeMinimumSize(1);
        //让X轴最右数据没有留白
        //xyplot.getDomainAxis().setUpperMargin(0);
//        ((DateAxis) (xyplot.getDomainAxis()))
        ((DateAxis) (xyplot.getDomainAxis())).setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //xyplot.getDomainAxis().setTickLabelFont(new Font("Microsoft YaHei", 0, 15));
        //((NumberAxis) xyplot.getDomainAxis()).setNumberFormatOverride(new DecimalFormat("0s"));

        this.InitPopMenu();
        //设置点标签可见
        this.Panel_chart.setLayout(new CardLayout());
        this.Panel_chart.add(dataChartPane);
    }

    public javax.swing.JMenuItem MenuItem_Del = new javax.swing.JMenuItem("删除");

    private void InitPopMenu() {

        //关闭右键菜单
        dataChartPane.setPopupMenu(null);

        MenuItem_Del.addActionListener((java.awt.event.ActionEvent evt) -> {
            crosshair.ClearAllHair();
            main_line.clear();
            PaintMainLine(main_line);
        });

        javax.swing.JPopupMenu PopupMenu = new javax.swing.JPopupMenu();
        PopupMenu.add(MenuItem_Del);
        this.dataChartPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON3 && me.getClickCount() == 1) {
                    if (crosshair.GetNearestCrossHair(me) == null) {
                        PopupMenu.show(dataChartPane, me.getX(), me.getY());
                    }
                }
            }
        });
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Create Line"> 
    private int series_index = -1;

    public int ApplyIndex() {
        series_index++;
//        System.out.println(series_index);
        return series_index;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="MainData Line"> 
    public int main_line_index;
    TimeSeries main_line = new TimeSeries("");
    XYTimeLabelRender linerender;

    private void CreateMainLine() {
        main_line_index = this.ApplyIndex();

        main_line = new TimeSeries("");
        TimeSeriesCollection xySeriesCollection = new TimeSeriesCollection();
        xySeriesCollection.addSeries(main_line);
        xyplot.setDataset(main_line_index, xySeriesCollection);

        linerender = new XYTimeLabelRender(this.xyplot.getRangeAxis(), this.xyplot.getDomainAxis(), false, true);
        linerender.setSeriesShape(0, new java.awt.geom.Rectangle2D.Float(-1, -1, 2, 2));
        linerender.setSeriesPaint(0, new Color(19, 140, 228));//new Color(0x7C, 0xFC, 0x00, 150);
        this.xyplot.setRenderer(series_index, linerender);
    }

    public TimeSeries GetMainLine() {
        return main_line;
    }

    public NEventCenter<TimeSeries> UpdateMainDataEvent = new NEventCenter();

    public void PaintMainLine(TimeSeries main_line) {
        this.main_line = main_line;
        TimeSeriesCollection data_set = (TimeSeriesCollection) xyplot.getDataset(main_line_index);
        data_set.removeAllSeries();
        data_set.addSeries(main_line);

//        this.xyplot.mapDatasetToRangeAxis(main_line_index, 0);
//        ValueAxisPlot vap = (ValueAxisPlot)this.xyplot.getRangeAxis().getPlot();
////        this.xyplot.getRangeAxis().getPlot()
//        XYPlot plt = (XYPlot)vap;
//        
//        
//        System.out.println(this.xyplot.getDatasetCount());
//        XYDataset d1 = this.xyplot.getDataset(0);
//        XYDataset d2 = this.xyplot.getDataset(1);
////        XYDataset d3 = this.xyplot.getDataset(2);
//        System.out.println(this.xyplot.getRendererForDataset(d1).findRangeBounds(d1));
//        System.out.println(this.xyplot.getRendererForDataset(d2).findRangeBounds(d2));
////        System.out.println(this.xyplot.getRendererForDataset(d3).findRangeBounds(d3));
//        System.out.println(vap.getDataRange(this.xyplot.getRangeAxis()));
        //        TimeSeriesCollection xySeriesCollection = new TimeSeriesCollection();
        //        xySeriesCollection.addSeries(main_line);
        //        xyplot.setDataset(main_line_index, xySeriesCollection);
        UpdateMainDataEvent.CreateEvent(main_line);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SnapShot Line"> 
    public final Color[] line_color = new Color[]{Color.GREEN, Color.RED, Color.ORANGE, Color.YELLOW, Color.BLACK};
    private int snapshot_index = -1;
    private XYTimeLabelRender linerender2;

    private void initSnapShot() {
        snapshot_index = this.ApplyIndex();
        linerender2 = new XYTimeLabelRender(this.xyplot.getRangeAxis(), this.xyplot.getDomainAxis(), false, true);
        for (int i = 0; i < line_color.length; i++) {
            linerender2.setSeriesShape(i, new java.awt.geom.Rectangle2D.Float(-1, -1, 2, 2));
            linerender2.setSeriesPaint(i, line_color[i]);//new Color(0x7C, 0xFC, 0x00, 150);
        }
        this.xyplot.setRenderer(series_index, linerender2);

        TimeSeriesCollection xySeriesCollection = new TimeSeriesCollection();
        xyplot.setDataset(series_index, xySeriesCollection);
    }

    TimeSeries[] snapshot = new TimeSeries[0];

    public void PaintSnapShot(TimeSeries[] lines) {
//        if (this.snapshot_index < 0) {
//        }
        TimeSeriesCollection data_set = (TimeSeriesCollection) this.xyplot.getDataset(series_index);
        data_set.removeAllSeries();
        for (TimeSeries line : lines) {
            data_set.addSeries(line);
        }
        snapshot = lines;
        UpdateSnapShotDataEvent.CreateEvent(lines);
    }

    public NEventCenter<TimeSeries[]> UpdateSnapShotDataEvent = new NEventCenter();
    // </editor-fold>

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Panel_chart = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(100, 100));

        Panel_chart.setPreferredSize(new java.awt.Dimension(100, 100));

        javax.swing.GroupLayout Panel_chartLayout = new javax.swing.GroupLayout(Panel_chart);
        Panel_chart.setLayout(Panel_chartLayout);
        Panel_chartLayout.setHorizontalGroup(
            Panel_chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 229, Short.MAX_VALUE)
        );
        Panel_chartLayout.setVerticalGroup(
            Panel_chartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 115, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel_chart, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel_chart, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Panel_chart;
    // End of variables declaration//GEN-END:variables
}
