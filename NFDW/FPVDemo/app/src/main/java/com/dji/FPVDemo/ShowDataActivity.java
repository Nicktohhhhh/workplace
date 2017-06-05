package com.dji.FPVDemo;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dji.FPVDemo.model.BluetoothModel;
import com.dji.FPVDemo.model.NewTest;
import com.dji.FPVDemo.model.Test;
import com.dji.FPVDemo.service.BluetoothService;
import com.dji.FPVDemo.service.impl.BluetoothImpl;
import com.dji.FPVDemo.service.impl.DistanceTimeChart;
import com.dji.FPVDemo.utils.ChangeOrientationHandler;
import com.dji.FPVDemo.utils.Dialog_whether_close;
import com.dji.FPVDemo.utils.MyOpenHelper;
import com.dji.FPVDemo.utils.OrientationSensorListener;

import org.achartengine.GraphicalView;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.dji.FPVDemo.ConnectionActivity.CMD_SEND_TO_ACTIVITY;

public class ShowDataActivity extends AppCompatActivity {

    private TextView sign_view,shortest_dis_show;
    private LinearLayout linearLayout;
    private GraphicalView mView;
    private DistanceTimeChart chart;
    double temp_data;
    private int count1 = 0;
    private int size_x_series = 6888;

    private SensorManager sensorManager;
    private Sensor sensor;
    private Handler handler;
    private OrientationSensorListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hugo set full screen 20161028
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //end 20161028
        setContentView(R.layout.activity_show_data);
        initDataActivityUI();

//        String nameString=getIntent().getStringExtra("sign_csg");
        Intent intent_temp=getIntent();
        String sign=intent_temp.getStringExtra("sign_csg");

//        String sign = "3";
//        sign_view.setText(sign);

        show_data(sign);

//        Log.d("show_data", "图表数据显示完成");

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        handler = new ChangeOrientationHandler(this);
        listener = new OrientationSensorListener(handler);
        sensorManager.registerListener(listener,sensor,sensorManager.SENSOR_DELAY_UI);
    }

    private void initDataActivityUI() {
        sign_view = (TextView) findViewById(R.id.text_dis_data);
        shortest_dis_show=(TextView)findViewById(R.id.shortest_dis_show);
        linearLayout = (LinearLayout) findViewById(R.id.distance_time_curve_data);
        chart = new DistanceTimeChart(this);
        chart.setXYMultipleSeriesDataset_show("实时最短距离监测");
        chart.setXYMultipleSeriesRenderer_show();
        mView = chart.getmGraphicalView();
        linearLayout.addView(mView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private void show_data(String sign){
        MyOpenHelper myOpenHelper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase db_read_show = myOpenHelper.getReadableDatabase();
        String temp_sign=sign;
        Cursor cursor = db_read_show.query("info", new String[]{"_id", "time", "dist1", "dist2", "place","sign"},"sign=?", new String[]{temp_sign},null,null, null);
        Cursor cursor_1=db_read_show.query("catalog",new String[]{"_id", "time", "dist1", "dist2", "place","sign","shortest_dis"},"sign=?", new String[]{temp_sign},null,null, null);
        cursor_1.moveToNext();
        String shortest_dis=cursor_1.getString(6);

        cursor.moveToLast();
        int length=cursor.getCount();
        cursor.moveToFirst();

        double[] data=new double[length];
        int count=0;

        String title_place=cursor.getString(4);

        sign_view.setText(title_place);
        DecimalFormat df = new DecimalFormat("#.###");
        double twoSign_shortest_dis = Double.parseDouble(df.format(Double.valueOf(shortest_dis)));


        shortest_dis_show.setText("线树最短距离为："+String.valueOf(twoSign_shortest_dis)+"米");

        while(cursor.moveToNext()){
            String temp=cursor.getString(3);
            chart.addDataToArray_show((Double.valueOf(temp)));
//            chart.updateChart((Double.valueOf(temp)), length-1);
            count++;
        }
        chart.showChart(length-1);
        db_read_show.close();
    }

    @Override
    public void onPause(){
//        sensorManager.unregisterListener(sensorEventListener);
        sensorManager.unregisterListener(listener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        sensorManager.registerListener(listener,sensor,sensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }
}

