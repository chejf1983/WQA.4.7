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
import java.util.Date;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author chejf
 */
public class XYTimeLabelRender extends XYLineAndShapeRenderer {

    private final ValueAxis range;
    private final ValueAxis domain;

    public XYTimeLabelRender(ValueAxis range, ValueAxis domain, boolean lines, boolean shapes) {
        super(lines, shapes);
        this.range = range;
        this.domain = domain;

        //显示标签
        setBaseItemLabelsVisible(true);
        //标签字体颜色
        setBaseItemLabelPaint(Color.WHITE);
        //设置字体
        setBaseItemLabelFont(new Font("Microsoft YaHei", 0, 12));//峰值点值显示
        //设置标签返回内容
        setBaseItemLabelGenerator((XYDataset xyd, int s_index, int p_index) -> {
            double x = xyd.getXValue(s_index, p_index);
            double y = xyd.getYValue(s_index, p_index);
            Date time = new Date((long) x);
            String label = String.format("(%s ,  %.4f)", new SimpleDateFormat("HH:mm:ss").format(time), y);
            return label;
        });
        //设置标签点颜色
        setSeriesPaint(0, def_color);
    }

    @Override
    protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, XYDataset dataset, int series, int item, double x, double y, boolean negative) {
        XYItemLabelGenerator generator = getItemLabelGenerator(series, item);
        boolean hit = false;
        if (selects.length <= series) {
            return;
        }
        
        for (int i = 0; i < selects[series].length; i++) {
            hit = hit || item == selects[series][i];
        }

        if (!hit) {
            return;
        }

        if (generator != null) {
            Font labelFont = getItemLabelFont(series, item);
            Paint paint = getItemLabelPaint(series, item);
            String label = generator.generateLabel(dataset, series, item);
            ItemLabelPosition position = getPositiveItemLabelPosition(series, item);
            //计算点坐标
            Point2D anchorPoint = calculateLabelAnchorPoint(position
                    .getItemLabelAnchor(), x, y, orientation);

            //画点
            g2.setPaint(def_color);
            g2.fillRect((int) anchorPoint.getX() - 4, (int) anchorPoint.getY(), 8, 8);

            //分割字符串
            String[] labels = label.split("<br>");
            //找到多行最大宽度
            int stringWidth = 0;
            for (String label1 : labels) {
                int tmp = g2.getFontMetrics().stringWidth(label1);
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
            //检查x坐标
            max_value = this.domain.getRange().getUpperBound() - this.domain.getRange().getLength() * 0.2;
            Number x1 = dataset.getX(series, item);
            if (x1.doubleValue() > max_value) {
                anchorPoint.setLocation(anchorPoint.getX() - stringWidth - 20, anchorPoint.getY());
            }
            //区域左上角起点坐标
            double rect_x = anchorPoint.getX() + 15;
            double rect_y = anchorPoint.getY() - (height * labels.length);
            //画阴影
            g2.setPaint(Color.GRAY);
            g2.fillRect((int) rect_x - 2, (int) rect_y, stringWidth + 4, height * labels.length);
            //画背景
            g2.setPaint(def_color);
            g2.fillRect((int) rect_x - 4, (int) rect_y - 2, stringWidth + 4, height * labels.length);
            g2.setPaint(paint);

            for (int i = 0; i < labels.length; i++) {
//                        int tmp = g2.getFontMetrics().stringWidth(labels[i]);
                g2.drawString(labels[i], (float) anchorPoint.getX() + 18, (float) (rect_y + (i + 1) * height - 7));
            }

        }
    }

//    private int[] select = new int[0];
    private int[][] selects = new int[0][];

    //寻找离标线最近的点坐标
    public void UpdateSelect(long[] hair_values, TimeSeries[] data_line) {
        selects = new int[data_line.length][];
        for (int i = 0; i < data_line.length; i++) {
            updateOne(hair_values, i, data_line[i]);
        }
    }

    private void updateOne(long[] hair_values, int series, TimeSeries data_line) {
        if (hair_values.length == 0) {
            selects[series] = new int[0];
            data_line.fireSeriesChanged();
            return;
        } else {
            selects[series] = new int[hair_values.length];
        }

        for (int j = 0; j < hair_values.length; j++) {
            //离标线最近的点序号（都比标线小则就是最后一个离标线最近）
            int new_index = data_line.getItemCount() - 1;
            long hair_value = hair_values[j];
            //遍历所有点
            for (int i = 0; i < data_line.getItemCount(); i++) {
                //找到第一个比标线大的值
                long r_p = ((Second) data_line.getDataItem(i).getPeriod()).getMiddleMillisecond();
                if (r_p > hair_value) {
                    if (i == 0) {
                        new_index = 0;//第一个点比标线大，最近的就是第一个点
                    } else {
                        long r_value = r_p - hair_value;
                        long l_p = ((Second) data_line.getDataItem(i - 1).getPeriod()).getMiddleMillisecond();
                        long l_value = hair_value - l_p;
                        new_index = r_value < l_value ? i : i - 1;
                    }
                    break;
                }
            }
            selects[series][j] = new_index;
        }
        data_line.fireSeriesChanged();
    }

    private Paint def_color = Color.BLUE;

    public void setDefColor(Paint color) {
        def_color = color;
    }
}
