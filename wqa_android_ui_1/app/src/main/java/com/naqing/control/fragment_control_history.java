package com.naqing.control;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.naqing.common.ErrorExecutor;
import com.naqing.common.InputDialog;
import com.naqing.common.NQProcessDialog;
import com.naqing.common.TableElement;
import com.naqing.wqa_android_ui_1.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;

import nahon.comm.faultsystem.LogCenter;
import wqa.control.DB.DataRecord;
import wqa.control.DB.SDataRecordResult;
import wqa.control.data.DevID;
import wqa.control.data.IMainProcess;
import wqa.system.WQAPlatform;

public class fragment_control_history extends Fragment {
    private View root;
    private Activity parent;
    private String TIMEFORMATE = "yyyy-MM-dd HH:mm:ss";

    public fragment_control_history() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.parent = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_control_history, container, false);

        initTime();

        initDevList();

        root.findViewById(R.id.mchart_search).setOnClickListener((View view) -> {
            search_history();
        });

        /**初始化图表*/
        ScatterChart chart = root.findViewById(R.id.mLineChar);
        initChart(chart);
        return root;
    }

    // <editor-fold desc="初始化时间">
    private Date start_time, stop_time;

    private void initTime() {
        /** 初始化搜索时间*/
        stop_time = new Date();
        start_time = new Date();
        start_time.setTime(stop_time.getTime() - 3600 * 24 * 1000 * 365);

        /** 设置起止时间*/
        TextView start_text = root.findViewById(R.id.mchart_start_time);
        start_text.setText(new SimpleDateFormat(TIMEFORMATE).format(start_time));
        start_text.setOnClickListener((View view) -> {
            InputDialog.ShowDateDialog(parent, "开始时间", start_text.getText().toString(), (String value) -> {
                start_text.setText(value);
            });
        });
        TextView stop_text = root.findViewById(R.id.mchart_end_time);
        stop_text.setText(new SimpleDateFormat(TIMEFORMATE).format(stop_time));
        stop_text.setOnClickListener((View view) -> {
            InputDialog.ShowDateDialog(parent, "截至时间", stop_text.getText().toString(), (String value) -> {
                stop_text.setText(value);
            });
        });
    }
    // </editor-fold>

    // <editor-fold desc="初始化设备参数列表">
    private DevID[] dev_lists = new DevID[0];
    private int select_dev = 0;
    private String select_data = "";

    private void initDevList() {
        /** 设置设备列表*/
        if (dev_lists.length == 0)
            dev_lists = WQAPlatform.GetInstance().GetDBHelperFactory().GetDataDB().ListAllDevice();

        /** 如果没有设备列表，直接返回*/
        TextView dev_view = root.findViewById(R.id.mchart_devs);
        if (dev_lists.length > select_dev) {
            dev_view.setText(dev_lists[select_dev].ToChineseString());
        } else {
            dev_view.setText("");
        }

        /** 点击时刷新数据库列表*/
        dev_view.setOnClickListener((View view) -> {
            dev_lists = WQAPlatform.GetInstance().GetDBHelperFactory().GetDataDB().ListAllDevice();
            String[] range = new String[dev_lists.length];
            for (int i = 0; i < range.length; i++) {
                range[i] = dev_lists[i].ToChineseString();
            }

            /** 如果有数据*/
            if (range.length > 0) {
                InputDialog.ShowListDialog(parent, range, (View dv) -> {
                    dev_view.setText(((TextView) dv).getText());
                    for (int i = 0; i < range.length; i++) {
                        if (range[i].contentEquals(dev_view.getText().toString()))
                            select_dev = i;
                    }
                });
            }
        });

        /** 初始化数据列表*/
        initDataList();
    }

    /*** 初始化对应的数据列表*/
    private void initDataList() {
        TextView dev_data_view = root.findViewById(R.id.mchart_data);
        //如果有数据
        if (data_result != null && data_result.data.size() > 0) {
            select_data = data_result.data.get(0).names[0];
            TableElement.initSelectView(dev_data_view, parent, select_data, data_result.data.get(0).names, (View view) -> {
                //计算曲线耗时比较长，放在线程中，然后触发绘画
                select_data = dev_data_view.getText().toString();
                ShowLine(data_result.data.get(0).GetIndex(select_data));
            });
            //默认画0号数据
            ShowLine(data_result.data.get(0).GetIndex(select_data));
        } else {
            ShowLine(-1);
        }
    }

    /** 刷新数据*/
    private void ShowLine(int index) {
        //如果不是搜索进程，发出一个刷新数据的进度条框
        if (mProgressDialog == null && index >= 0 && data_result != null && data_result.data.size() > 0) {
            mProgressDialog = NQProcessDialog.ShowProcessDialog(getContext(), "刷新数据...");
        }

        WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
            //绘制曲线
            DrawLine(data_result, index);
            //显示曲线
            messagehandler.sendEmptyMessage(DRAWLINE);
        });
    }
    // </editor-fold>

    // <editor-fold desc="搜索设备">
    NQProcessDialog mProgressDialog;

    private SDataRecordResult data_result = null;

    private void search_history() {
        if (mProgressDialog != null && !mProgressDialog.isFinished()) {
            return;
        }
        //获取设置的时间段
        try {
            start_time = new SimpleDateFormat(TIMEFORMATE).parse(((TextView) root.findViewById(R.id.mchart_start_time)).getText().toString());
            stop_time = new SimpleDateFormat(TIMEFORMATE).parse(((TextView) root.findViewById(R.id.mchart_end_time)).getText().toString());
        } catch (Exception ex) {
            LogCenter.Instance().SendFaultReport(Level.WARNING, ex.getMessage());
            return;
        }

        //检查选择的设备是否有效
        if (select_dev < 0 || dev_lists.length == 0) {
            return;
        }

        mProgressDialog = NQProcessDialog.ShowProcessDialog(getContext(), "搜索数据...");

        Future<?> submit = WQAPlatform.GetInstance().GetThreadPool().submit(() -> {
            WQAPlatform.GetInstance().GetDBHelperFactory().GetDataDB().SearchLimitData(dev_lists[select_dev], start_time, stop_time, MaxPointNum, new IMainProcess<SDataRecordResult>() {
                @Override
                public void SetValue(float pecent) {
                    mProgressDialog.SetPecent((int) pecent + 10);
                }

                @Override
                public void Finish(SDataRecordResult result) {
                    data_result = result;
                    /** 刷新数据列表，在数据列表里画曲线*/
                    messagehandler.sendEmptyMessage(INITDATA);
                }
            });
        });

        if (!submit.isDone()) {
            if (mProgressDialog == null)
                return;
            mProgressDialog.SetTimout(60000, () -> {
                if (!submit.isDone()) {
                    if (mProgressDialog != null) {
                        mProgressDialog.Finish();
                        mProgressDialog = null;
                    }
                    submit.cancel(true);
                    ErrorExecutor.PrintErrorInfo("搜索设备超时");
                }
            });
        } else {
            mProgressDialog.Finish();
        }
    }
    // </editor-fold>

    // <editor-fold desc="图表">
    private ScatterChart lineChart;
    private XAxis xAxis;                //X轴
    private YAxis leftYAxis;            //左侧Y轴
    private YAxis rightYaxis;           //右侧Y轴
    private Legend legend;              //图例
    private LimitLine limitLine;        //限制线
    private ArrayList<IncomeBean> dataList = new ArrayList<>();

    /**
     * 初始化图表
     */
    private void initChart(ScatterChart lineChart) {
        this.lineChart = lineChart;
        /***图表设置***/
        //是否展示网格线
        lineChart.setDrawGridBackground(false);
        //是否显示边界
        lineChart.setDrawBorders(true);
        //是否可以拖动
        lineChart.setDragEnabled(true);
        //是否有触摸事件
        lineChart.setTouchEnabled(true);
        //不放大
        lineChart.setPinchZoom(false);
        //设置XY轴动画效果
//        lineChart.animateY(1000);
//        lineChart.animateX(1000);

        leftYAxis = lineChart.getAxisLeft();
        //设置X Y轴网格线为虚线（实体线长度、间隔距离、偏移量：通常使用 0）
        leftYAxis.enableGridDashedLine(10f, 10f, 0f);
        leftYAxis.setTextColor(Color.WHITE);
        leftYAxis.setTextSize(18f);
        //保证Y轴从0开始，不然会上移一点
        leftYAxis.setAxisMinimum(0f);

        rightYaxis = lineChart.getAxisRight();
        rightYaxis.setEnabled(false);

        /***XY轴的设置***/
        xAxis = lineChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setTextSize(18f);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(0f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                String tradeDate = dataList.get((int) value % dataList.size()).tradeDate;
                return tradeDate;
            }
        });
        lineChart.setExtraBottomOffset(2 * xAxis.getTextSize());
        lineChart.setXAxisRenderer(new XAxisRenderer(lineChart.getViewPortHandler(), xAxis, lineChart.getTransformer(leftYAxis.getAxisDependency())) {
            @Override
            protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
                String[] lines = formattedLabel.split(" ");
                for (int i = 0; i < lines.length; i++) {
                    super.drawLabel(c, lines[i], x, y + i * xAxis.getTextSize(), anchor, angleDegrees);
                }
            }
        });

        /***折线图例 标签 设置***/
        legend = lineChart.getLegend();
        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(22f);
        legend.setTextColor(Color.WHITE);
        //显示位置 左下方
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        legend.setDrawInside(true);

        this.showLineChart(this.dataList, "数据", Color.GREEN);
    }

    /**
     * 曲线初始化设置 一个LineDataSet 代表一条曲线
     *
     * @param lineDataSet 线条
     * @param color       线条颜色
     */
    private void initLineDataSet(ScatterDataSet lineDataSet, int color) {
        lineDataSet.setColor(color);
        lineDataSet.setScatterShapeSize(3f);
//        lineDataSet.setCircleColor(color);
//        lineDataSet.setLineWidth(1f);
//        lineDataSet.setCircleRadius(1f);
//        //设置曲线值的圆点是实心还是空心
//        lineDataSet.setDrawCircleHole(false);
//        lineDataSet.setValueTextSize(10f);
//        //设置折线图填充
////        lineDataSet.setDrawFilled(true);
//        lineDataSet.setFormLineWidth(1f);
//        lineDataSet.setFormSize(15.f);
//        if (mode == null) {
//            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
//            lineDataSet.setMode(LineDataSet.Mode.LINEAR);
//        } else {
//            lineDataSet.setMode(mode);
//        }
    }

    /**
     * 展示曲线
     *
     * @param dataList 数据集合
     * @param name     曲线名称
     * @param color    曲线颜色
     */
    public void showLineChart(ArrayList<IncomeBean> dataList, String name, int color) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            IncomeBean data = dataList.get(i);
            /**
             * 在此可查看 Entry构造方法，可发现 可传入数值 Entry(float x, float y)
             * 也可传入Drawable， Entry(float x, float y, Drawable icon) 可在XY轴交点 设置Drawable图像展示
             */
            Entry entry = new Entry(i, (float) data.value);
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        ScatterDataSet lineDataSet = new ScatterDataSet(entries, name);
        initLineDataSet(lineDataSet, color);
        ScatterData lineData = new ScatterData(lineDataSet);
        lineChart.setData(lineData);
    }


    /**
     * 我的收益
     */
    class IncomeBean {
        /**
         * tradeDate : 20180502
         * value : 0.03676598
         */
        public String tradeDate;
        public double value;

        public IncomeBean(Date time, double value) {
            this.tradeDate = new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(time);
            this.value = value;
        }
    }

    private int MaxPointNum = 2048;

    private void DrawLine(SDataRecordResult result, int index) {
        //计算曲线耗时比较长，放在线程中，然后触发绘画
        dataList.clear();
        if (index >= 0 && result.data.size() > 0) {
            //获取起止时间
            Date start = new Date();
            start.setTime(result.data.get(0).time.getTime());
            Date end = new Date();
            end.setTime(result.data.get(result.data.size() - 1).time.getTime());
            //窗口宽度
            long timespane = (end.getTime() - start.getTime()) / MaxPointNum;

            //窗口前移半个周期
            start.setTime(start.getTime() - timespane / 2);
            //数据指针
            int result_point = 0;
            for (int i = 0; i <= MaxPointNum * 2; i++) {
                //数据采样完毕，退出
                if (result_point >= result.data.size()) {
                    break;
                }
                //获取一个数据
                DataRecord record = result.data.get(result_point);
                //落在窗口内
                if (record.time.getTime() > start.getTime() &&
                        record.time.getTime() <= start.getTime() + timespane) {
                    //添加数据
                    dataList.add(new IncomeBean(record.time, record.values[index]));
                    //数据指针加长
                    result_point++;
                    //窗口平移
                    start.setTime(start.getTime() + timespane);
                } else if (record.time.getTime() > start.getTime() + timespane) {
                    //落在窗口后面，增加空数据，窗口平移
                    dataList.add(new IncomeBean(start, Float.NaN));
                    start.setTime(start.getTime() + timespane);
                } else {
                    //落在窗口前，数据追加，窗口等待
                    result_point++;
                }
            }
        }

        //曲线名称
        String name = "";
        if (index >= 0 && result.data.size() > 0) {
            name = data_result.data.get(0).names[index] + data_result.search_num;
        }
        this.showLineChart(dataList, name, Color.GREEN);
    }
    // </editor-fold>

    // <editor-fold desc="消息中心">
    //activity 消息
    private int DRAWLINE = 0x01;
    private int INITDATA = 0x02;
    private Handler messagehandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == DRAWLINE) {
                lineChart.invalidate();
                if (mProgressDialog != null) {
                    mProgressDialog.Finish();
                    mProgressDialog = null;
                }
            }
            if (msg.what == INITDATA) {
                initDataList();
            }
        }
    };

    // </editor-fold>
}

