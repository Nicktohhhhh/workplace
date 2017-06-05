package com.dji.FPVDemo.service.impl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lenovo on 2016/11/19.
 */

public class DistanceTimeChart extends AbstractDemoChart {

    private Timer timer = new Timer();
    private TimerTask task;
    private GraphicalView mGraphicalView;
    private XYMultipleSeriesRenderer multipleSeriesRenderer;
    private XYMultipleSeriesDataset multipleSeriesDataset;
    private XYSeries mSeries;
    private XYSeriesRenderer mRenderer;
    private Context context;
    private TimeSeries series;
    private XYSeries mSeries_danger;

    //   private double addX=-1;
    //   private double addY;

    private double[] data_array_x;
    private double[] data_array_y;

    private double[] data_array_x_danger;
    private double[] data_array_y_danger;

    private double[] data_array_x_show;
    private double[] data_array_y_show;

    public DistanceTimeChart(Context context) {
        this.context = context;
        data_array_x = new double[6888];
        data_array_y = new double[6888];
        data_array_x_danger = new double[6888];
        data_array_y_danger = new double[6888];
        data_array_x_show = new double[6888];
        data_array_y_show = new double[6888];

        for (int i = 0; i < 6887; i++) {
            data_array_x[i] = i;
            data_array_y[i] = 0.0;
        }
        for (int i = 0; i < 6887; i++) {
            data_array_x_show[i] = i;
            data_array_y_show[i] = 0.0;
        }
        for (int i = 0; i < 6887; i++) {
            data_array_x_danger[i] = i;
            data_array_y_danger[i] = 7.0;
        }
    }

    @Override
    public String getName() {
        return "DistanceTimeChart";
    }

    @Override
    public String getDesc() {
        return "Hugo_dis_chart";
    }

    @Override
    public Intent execute(Context context) {
        return null;
    }

    public void setXYMultipleSeriesDataset(String curveTitle) {
        multipleSeriesDataset = new XYMultipleSeriesDataset();
        mSeries = new XYSeries(curveTitle);
        mSeries_danger = new XYSeries(curveTitle);
        for (int i = 0; i < 6887; i++) {
            mSeries.add(data_array_x[i], data_array_y[i]);
        }
        for (int i = 0; i < 6887; i++) {
            mSeries_danger.add(data_array_x_danger[i], data_array_y_danger[i]);
        }
        multipleSeriesDataset.addSeries(mSeries_danger);
        multipleSeriesDataset.addSeries(mSeries);
    }

    public void setXYMultipleSeriesDataset_show(String curveTitle) {
        multipleSeriesDataset = new XYMultipleSeriesDataset();
        mSeries = new XYSeries(curveTitle);
        mSeries_danger = new XYSeries(curveTitle);
        for (int i = 0; i < 6887; i++) {
            mSeries.add(data_array_x[i], data_array_y_show[i]);
        }
        for (int i = 0; i < 6887; i++) {
            mSeries_danger.add(data_array_x_danger[i], data_array_y_danger[i]);
        }
        multipleSeriesDataset.addSeries(mSeries_danger);
        multipleSeriesDataset.addSeries(mSeries);
    }


    public void setXYMultipleSeriesRenderer() {
        multipleSeriesRenderer = new XYMultipleSeriesRenderer();
        setChartSettings(multipleSeriesRenderer, "实时最短距离图", "", "", 0, 60, 0, 20, Color.LTGRAY, Color.LTGRAY);
        multipleSeriesRenderer.setXLabels(15);
        multipleSeriesRenderer.setYLabels(15);
        multipleSeriesRenderer.setShowGrid(true);
        multipleSeriesRenderer.setXLabelsAlign(Paint.Align.CENTER);
        multipleSeriesRenderer.setYLabelsAlign(Paint.Align.LEFT);

        //Hugo 20170215 begin
        multipleSeriesRenderer.setZoomEnabled(false, false);//设置缩放,这边是横向,竖向
        // multipleSeriesRenderer.setZoomLimits(new double[]{0, 3600, 0, 0});
        // multipleSeriesRenderer.setExternalZoomEnabled(false);//设置是否可以缩放
        multipleSeriesRenderer.setPanEnabled(true, false);//设置滑动,这边是横向,竖向
        multipleSeriesRenderer.setPanLimits(new double[]{0, 6888, 0, 20});   //设置滑动的范围
        //Hugo 20170215 end

//        multipleSeriesRenderer.setPointSize(7);
        multipleSeriesRenderer.setDisplayValues(false);
        multipleSeriesRenderer.setGridLineWidth(1.0f);
        multipleSeriesRenderer.setLabelsTextSize(35);
        multipleSeriesRenderer.setAxisTitleTextSize(100);
        multipleSeriesRenderer.setChartTitleTextSize(0);
        multipleSeriesRenderer.setLegendTextSize(0);
        multipleSeriesRenderer.setBarWidth(0);
        // multipleSeriesRenderer.setInScroll(true);
        // multipleSeriesRenderer.setClickEnabled(true);

        XYSeriesRenderer r = new XYSeriesRenderer();//(类似于一条线对象)
        r.setColor(Color.RED);//设置颜色
        r.setPointStyle(PointStyle.POINT);//设置点的样式
        r.setDisplayBoundingPoints(false);
        r.setFillPoints(true);//填充点（显示的点是空心还是实心）
        r.setDisplayChartValues(false);//将点的值显示出来
        r.setChartValuesSpacing(10);//显示的点的值与图的距离
        r.setChartValuesTextSize(25);//点的值的文字大小

        //  r.setFillBelowLine(true);//是否填充折线图的下方
        //  r.setFillBelowLineColor(Color.GREEN);//填充的颜色，如果不设置就默认与线的颜色一致
        r.setLineWidth(3);//设置线宽
        multipleSeriesRenderer.addSeriesRenderer(r);

        XYSeriesRenderer rTwo = new XYSeriesRenderer();//(类似于一条线对象)
        rTwo.setColor(Color.BLUE);//设置颜色
//        rTwo.setPointStyle(PointStyle.CIRCLE);//设置点的样式
        rTwo.setPointStyle(PointStyle.POINT);//设置点的样式
        rTwo.setDisplayBoundingPoints(false);
        rTwo.setFillPoints(true);//填充点（显示的点是空心还是实心）
        rTwo.setDisplayChartValues(false);//将点的值显示出来
        rTwo.setChartValuesSpacing(10);//显示的点的值与图的距离
        rTwo.setChartValuesTextSize(25);//点的值的文字大小

        //  rTwo.setFillBelowLine(true);//是否填充折线图的下方
        //  rTwo.setFillBelowLineColor(Color.GREEN);//填充的颜色，如果不设置就默认与线的颜色一致
        rTwo.setLineWidth(3);//设置线宽

        multipleSeriesRenderer.addSeriesRenderer(rTwo);
}

    public void setXYMultipleSeriesRenderer_show() {
        multipleSeriesRenderer = new XYMultipleSeriesRenderer();
        setChartSettings(multipleSeriesRenderer, "实时最短距离图", "", "", 0, 60, 0, 20, Color.LTGRAY, Color.LTGRAY);
        multipleSeriesRenderer.setXLabels(15);
        multipleSeriesRenderer.setYLabels(15);
        multipleSeriesRenderer.setShowGrid(true);
        multipleSeriesRenderer.setXLabelsAlign(Paint.Align.CENTER);
        multipleSeriesRenderer.setYLabelsAlign(Paint.Align.LEFT);

        //Hugo 20170215 begin
        multipleSeriesRenderer.setZoomEnabled(true, true);//设置缩放,这边是横向,竖向
        multipleSeriesRenderer.setZoomLimits(new double[]{0, 6888, 0, 20});
        // multipleSeriesRenderer.setExternalZoomEnabled(false);//设置是否可以缩放
        multipleSeriesRenderer.setPanEnabled(true, true);//设置滑动,这边是横向,竖向
        multipleSeriesRenderer.setPanLimits(new double[]{0, 6888, 0, 20});   //设置滑动的范围
        //Hugo 20170215 end

        //multipleSeriesRenderer.setPointSize(7);
        multipleSeriesRenderer.setDisplayValues(false);
        multipleSeriesRenderer.setGridLineWidth(1.0f);
        multipleSeriesRenderer.setLabelsTextSize(35);
        multipleSeriesRenderer.setAxisTitleTextSize(100);
        multipleSeriesRenderer.setChartTitleTextSize(0);
        multipleSeriesRenderer.setLegendTextSize(0);
        multipleSeriesRenderer.setBarWidth(0);
        // multipleSeriesRenderer.setInScroll(true);
        // multipleSeriesRenderer.setClickEnabled(true);


        XYSeriesRenderer r = new XYSeriesRenderer();//(类似于一条线对象)
        r.setColor(Color.RED);//设置颜色
        r.setPointStyle(PointStyle.POINT);//设置点的样式
        r.setDisplayBoundingPoints(false);
        r.setFillPoints(true);//填充点（显示的点是空心还是实心）
        r.setDisplayChartValues(false);//将点的值显示出来
        r.setChartValuesSpacing(10);//显示的点的值与图的距离
        r.setChartValuesTextSize(25);//点的值的文字大小

        //  r.setFillBelowLine(true);//是否填充折线图的下方
        //  r.setFillBelowLineColor(Color.GREEN);//填充的颜色，如果不设置就默认与线的颜色一致
        r.setLineWidth(3);//设置线宽
        multipleSeriesRenderer.addSeriesRenderer(r);

        XYSeriesRenderer rTwo = new XYSeriesRenderer();//(类似于一条线对象)
        rTwo.setColor(Color.BLUE);//设置颜色
//        rTwo.setPointStyle(PointStyle.CIRCLE);//设置点的样式
        rTwo.setPointStyle(PointStyle.POINT);//设置点的样式
        rTwo.setDisplayBoundingPoints(false);
        rTwo.setFillPoints(true);//填充点（显示的点是空心还是实心）
        rTwo.setDisplayChartValues(false);//将点的值显示出来
        rTwo.setChartValuesSpacing(10);//显示的点的值与图的距离
        rTwo.setChartValuesTextSize(25);//点的值的文字大小

        //  rTwo.setFillBelowLine(true);//是否填充折线图的下方
        //  rTwo.setFillBelowLineColor(Color.GREEN);//填充的颜色，如果不设置就默认与线的颜色一致
        rTwo.setLineWidth(3);//设置线宽

        multipleSeriesRenderer.addSeriesRenderer(rTwo);

    }

    private boolean check_slip=true;

    public boolean getCheck_slip(){
        return check_slip;
    }

    public void setCheck_slip(boolean check){
        check_slip=check;
    }

    public void setXAxisMinMax(double xMin, double xMax) {
        if (check_slip) {
            setXAxisRange(multipleSeriesRenderer, xMin, xMax);
        }
    }
    public void reSetXAxisMinMax(){
        setXAxisRange(multipleSeriesRenderer, 0, 60);
    }



    public GraphicalView getmGraphicalView() {
        mGraphicalView = ChartFactory.getLineChartView(context, multipleSeriesDataset, multipleSeriesRenderer);
        return mGraphicalView;
    }

    public void updateChart(double data, int size_x_series, int cou) {
        //addX = (double) (addX + 1);
        // addX=cou;
        addDataToArray(data);
        // addY=data;
        multipleSeriesDataset.removeSeries(mSeries);
        mSeries.clear();

        int size = cou;
        if (size > size_x_series)
            size = size_x_series;
        for (int i = 0; i <= size; i++) {
            mSeries.add(data_array_x[i], data_array_y[i]);
        }
        multipleSeriesDataset.addSeries(mSeries);
        mGraphicalView.repaint();//此处也可以调用invalidate()
    }

    public void updateChart(double data, int size_x_series) {
        //addX = (double) (addX + 1);
        // addX=cou;
        addDataToArray_show(data);
        // addY=data;
        multipleSeriesDataset.removeSeries(mSeries);
        mSeries.clear();

        if (size_x_series > 6888) {
            size_x_series = 6887;
        }

        for (int i = 0; i < size_x_series; i++) {
            mSeries.add(data_array_x_show[i], data_array_y_show[i]);
        }
        multipleSeriesDataset.addSeries(mSeries);

        mGraphicalView.repaint();//此处也可以调用invalidate()
    }

    public void showChart(int size_x_series){
        multipleSeriesDataset.removeSeries(mSeries);
        mSeries.clear();

        if (size_x_series > 6888) {
            size_x_series = 6887;
        }

        for (int i = 0; i < size_x_series; i++) {
            mSeries.add(data_array_x_show[i], data_array_y_show[i]);
        }
        multipleSeriesDataset.addSeries(mSeries);

        mGraphicalView.repaint();//此处也可以调用invalidate()
    }

    public void draw_Pic_all_data(double[] data, int size) {
        multipleSeriesDataset.removeSeries(mSeries);
        mSeries.clear();

        for (int i = 0; i < size; i++) {
            mSeries.add(i, data[i]);
        }
        multipleSeriesDataset.addSeries(mSeries);
        mGraphicalView.repaint();//此处也可以调用invalidate()
    }

    public void clearChart() {
        mSeries.clear();
        mSeries.clearAnnotations();
        mSeries.clearSeriesValues();
        mGraphicalView.repaint();
    }

    public void updateChart(List<Double> xList, List<Double> yList) {
        for (int i = 0; i < xList.size(); i++) {
            mSeries.add(xList.get(i), yList.get(i));
        }
        mGraphicalView.repaint();
    }

    private void addDataToArray(double data) {
        if (data_array_y[6887] == 0.0) {
            int i = 0;
            while (data_array_y[i] != 0.0) {
                i++;
            }
            data_array_y[i] = data;
        } else {
            for (int i = 0; i < data_array_x.length - 1; i++) {
                data_array_y[i] = data_array_y[i + 1];
            }
            data_array_y[data_array_y.length - 1] = data;
        }
    }

    public void addDataToArray_show(double data) {
        if (data_array_y_show[6887] == 0.0) {
            int i = 0;
            while (data_array_y_show[i] != 0.0) {
                i++;
            }
            data_array_y_show[i] = data;
        } else {
            for (int i = 0; i < data_array_x.length - 1; i++) {
                data_array_y_show[i] = data_array_y_show[i + 1];
            }
            data_array_y_show[data_array_y_show.length - 1] = data;
        }
    }

    public void reSetArray() {
        for (int i = 0; i < 6887; i++) {
            data_array_x[i] = i;
            data_array_y[i] = 0.0;
        }
    }
}
