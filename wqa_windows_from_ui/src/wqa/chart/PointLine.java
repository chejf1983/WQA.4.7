/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import nahon.comm.event.NEvent;
import nahon.comm.event.NEventListener;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author chejf
 */
public class PointLine {

    private final DataChart parent;
    //标线数组
    private final ArrayList<Crosshair> x_crosshairs;
    //标签点曲线序号
    private final int point_index;
    //标签曲线
    private TimeSeries point_line;

    private ArrayList<String> descibre = new ArrayList();

    public PointLine(DataChart dataChartPane, ArrayList<Crosshair> cross_hair) {
        this.parent = dataChartPane;
        this.x_crosshairs = cross_hair;
        //申请标线曲线
        this.point_index = this.parent.ApplyIndex();

        if (point_line == null) {
            BuildPointLine();
        }

        //注册主曲线刷新响应
        this.parent.UpdateMainDataEvent.RegeditListener(new NEventListener() {
            @Override
            public void recevieEvent(NEvent event) {
                Update();
            }
        });
    }

    //初始化标签曲线
    private void BuildPointLine() {
        //创建标签点背景颜色
        XYLineAndShapeRenderer render = new XYLineAndShapeRenderer(false, true) {
            private final ValueAxis range;

            {
                range = parent.xyplot.getRangeAxis();
            }

            @Override
            protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, XYDataset dataset, int series, int item, double x, double y, boolean negative) {
                XYItemLabelGenerator generator = getItemLabelGenerator(series, item);
                if (generator != null) {
                    Font labelFont = getItemLabelFont(series, item);
                    Paint paint = getItemLabelPaint(series, item);
                    String label = generator.generateLabel(dataset, series, item);
                    ItemLabelPosition position = getPositiveItemLabelPosition(series, item);
                    //计算点坐标
                    Point2D anchorPoint = calculateLabelAnchorPoint(position
                            .getItemLabelAnchor(), x, y, orientation);
                    //分割字符串
                    String[] labels = label.split("<br>");
                    //找到多行最大宽度
                    int stringWidth = 0;
                    for (int i = 0; i < labels.length; i++) {
                        int tmp = g2.getFontMetrics().stringWidth(labels[i]);
                        if (stringWidth < tmp) {
                            stringWidth = tmp;
                        }
                    }
                    //宽度加宽
                    stringWidth += 8;
                    //找到单行字体高度
//                    FontDesignMetrics metrics = FontDesignMetrics.getMetrics(labelFont);
//                    int height = metrics.getHeight();
                    //正常流程
                    g2.setFont(labelFont);
                    int height = g2.getFontMetrics().getHeight();

                    //判断量程是否超标
                    double max_value = this.range.getRange().getUpperBound() - this.range.getRange().getLength() * 0.1;
                    Number y1 = dataset.getY(series, item);
                    if (y1.doubleValue() > max_value) {
                        anchorPoint.setLocation(anchorPoint.getX(), anchorPoint.getY() + height * labels.length + 15);
                    }

                    //区域左上角起点坐标
                    double rect_x = anchorPoint.getX() - stringWidth / 2;
                    double rect_y = anchorPoint.getY() - (height * labels.length);
                    //画阴影
                    g2.setPaint(Color.GRAY);
                    g2.fillRect((int) rect_x - 2, (int) rect_y, stringWidth + 4, height * labels.length);
                    //画背景
                    g2.setPaint(Color.BLUE);
                    g2.fillRect((int) rect_x - 4, (int) rect_y - 2, stringWidth + 4, height * labels.length);
                    g2.setPaint(paint);

                    for (int i = 0; i < labels.length; i++) {
                        int tmp = g2.getFontMetrics().stringWidth(labels[i]);
                        g2.drawString(labels[i], (float) anchorPoint.getX() - tmp / 2, (float) (rect_y + (i + 1) * height - 7));
                    }
//                    TextUtilities.drawRotatedString(label, g2,
//                            (float) anchorPoint.getX(),
//                            (float) anchorPoint.getY() - 2, position
//                            .getTextAnchor(), position.getAngle(), position
//                            .getRotationAnchor());
                }
            }
        };

        //设置标签点形状
        render.setSeriesShape(0, new java.awt.geom.Rectangle2D.Float(-2, -2, 5, 5));
        //显示标签
        render.setBaseItemLabelsVisible(true);
        //标签字体颜色
        render.setBaseItemLabelPaint(Color.WHITE);
        //设置字体
        render.setBaseItemLabelFont(new Font("Microsoft YaHei", 0, 12));//峰值点值显示
        //设置标签返回内容
        render.setBaseItemLabelGenerator((XYDataset xyd, int s_index, int p_index) -> {
            double x = xyd.getXValue(s_index, p_index);
            double y = xyd.getYValue(s_index, p_index);
            Date time = new Date((long) x);
            String label = String.format("(%s ,  %.4f)", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time), y);
            if (descibre != null && descibre.size() > p_index) {
                label += "<br>" + descibre.get(p_index);
            }
            return label;
        });
        //设置标签点颜色
        render.setSeriesPaint(0, new Color(19, 35, 67));
        //设置标签曲线Render
        this.parent.xyplot.setRenderer(point_index, render);

        //创建标签曲线
        point_line = new TimeSeries("");
        TimeSeriesCollection xySeriesCollection = new TimeSeriesCollection();
        xySeriesCollection.addSeries(point_line);
        this.parent.xyplot.setDataset(point_index, xySeriesCollection);
    }

    //刷新数据
    public void Update() {
        TimeSeries main_data = this.parent.GetMainLine();
        CalCrossLabel(main_data, this.parent.GetMainLineDescribe());
    }

    //寻找离标线最近的点坐标
    private void CalCrossLabel(TimeSeries data_line, String[] describe) {
        TimeSeries lable_line = point_line;
        //清除数据
        lable_line.clear();
        this.descibre.clear();
        //循环每个hair
        for (Crosshair hair : this.x_crosshairs) {
            //标线值
            double hair_value = hair.getValue();
            //离标线最近的点序号（都比标线小则就是最后一个离标线最近）
            int new_index = data_line.getItemCount() - 1;
            //遍历所有点
            for (int i = 0; i < data_line.getItemCount(); i++) {
                //找到第一个比标线大的值
                long t_value = ((Second) data_line.getDataItem(i).getPeriod()).getMiddleMillisecond();
                if (t_value > hair_value) {
                    if (i == 0) {
                        new_index = 0;//第一个点比标线大，最近的就是第一个点
                    } else {
                        double r_value = t_value - hair_value;
                        double l_value = hair_value - ((Second) data_line.getDataItem(i - 1).getPeriod()).getMiddleMillisecond();
                        new_index = r_value < l_value ? i : i - 1;
                    }
                    break;
                }
            }

            //添加找到的标签点
            if (new_index >= 0) {
                lable_line.addOrUpdate(data_line.getDataItem(new_index));
                if (describe != null && describe.length > new_index) {
                    this.descibre.add(describe[new_index]);
                }
//                lable_line.addOrUpdate(data_line.getDataItem(new_index).getPeriod(), data_line.getDataItem(new_index).getValue().floatValue() - 20);
            }
        }
    }
}
