package com.dji.FPVDemo;

/**
 * Created by lenovo on 2016/11/19.
 */

import android.app.Activity;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.dji.FPVDemo.ConnectionActivity.CMD_SEND_TO_ACTIVITY;

public class ChartActivity extends Activity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private LinearLayout linearLayout;
    private GraphicalView mView;
    private DistanceTimeChart chart;
    private TextView text_dis, state, textView_xs,realtime_dis;

    private boolean Start_off = false;
    double temp_data;
    private int count1 = 0;
    private double num_min = 0;
    private BluetoothAdapter bluetoothAdapter;
    MyReceiver receiver;

    ToggleButton toggleButton;
    private Button button_clear;
    private Button button_Save;
    private Button button_History;
    private Button button_Set;
    private Button button_tail;
    private ImageView imageView_on_off;
    private int size_x_series = 6888;

    private SoundPool soundPool;    //声音报警的实现
    private HashMap<Integer, Integer> newHashMap;
    private HashMap<Integer, Integer> soundmap = new HashMap<Integer, Integer>();
    private boolean isExit = false;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private MyOpenHelper myOpenHelper;
    private List<Test> lists;
    private List<NewTest> newlists;
    private long insert1, insert2;
    private String place;

    private int j;
    private String time;

    static final int CMD_SAFE_DIS_SET = 0x07;
    static final int CMD_SAFE_DIS_SET_NOW = 0x08;
    static final int CMD_SAFE_DIS_SET_NOW_TO_SET = 0x09;

    double safedis_set = 7;

    private Timer timer;
    private TimerTask timerTask;

    private SensorManager sensorManager;
    private Sensor sensor;
    private Handler handler;
    private OrientationSensorListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.distance_time_chart);

        linearLayout = (LinearLayout) findViewById(R.id.distance_time_curve);
        chart = new DistanceTimeChart(this);
        chart.setXYMultipleSeriesDataset("实时最短距离监测");
        chart.setXYMultipleSeriesRenderer();

        mView = chart.getmGraphicalView();

        linearLayout.addView(mView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        toggleButton = (ToggleButton) findViewById(R.id.toggle);
        button_clear = (Button) findViewById(R.id.button_clear);
        button_History = (Button) findViewById(R.id.button_History);
        button_Save = (Button) findViewById(R.id.button_Save);
        button_Set = (Button) findViewById(R.id.button_Set);
        button_tail = (Button) findViewById(R.id.button_tail);
        text_dis = (TextView) findViewById(R.id.text_dis);
        realtime_dis=(TextView)findViewById(R.id.realtime_dis);
        textView_xs = (TextView) findViewById(R.id.textview_xs);
        state = (TextView) findViewById(R.id.state);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        imageView_on_off = (ImageView) findViewById(R.id.toggle_on_off);

        soundPool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 0);    //加载报警声
        soundmap.put(1, soundPool.load(this, R.raw.warning_bgm, 1));

        toggleButton.setOnCheckedChangeListener(this);
        button_clear.setOnClickListener(this);
        button_Set.setOnClickListener(this);
        button_tail.setOnClickListener(this);
        button_Save.setOnClickListener(this);
        button_History.setOnClickListener(this);

        CloseActivity.add(this);
        myOpenHelper = new MyOpenHelper(getApplicationContext());
        lists = new ArrayList<Test>();
        newlists = new ArrayList<NewTest>();



        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        handler = new ChangeOrientationHandler(this);
        listener = new OrientationSensorListener(handler);
        sensorManager.registerListener(listener,sensor,sensorManager.SENSOR_DELAY_UI);
    }

//    delete
    private int count_rolling = 0;
    private boolean check_slip = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            ChartActivity.this.unregisterReceiver(receiver);
        }
        bluetoothAdapter.disable();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_clear:
                count1 = 0;
                chart.reSetArray();
                sign_check = 0;
                record = 0;

                count_rolling=0;
                chart.reSetXAxisMinMax();

                values.clear();
                chart.updateChart(temp_data, size_x_series, count1);
                //将之前缓存的数据也一起清除掉
                newlists.clear();
                text_dis.setText("线：检测中...");
                textView_xs.setText("线树最小距离：检测中...");
                realtime_dis.setText("线树距离：检测中...");
                state.setText("状态：初始化中...");

                Toast.makeText(this, "数据清除成功", Toast.LENGTH_LONG).show();
                break;
            case R.id.button_Set:
                toggleButton.setChecked(false);
                Intent intent = new Intent(this, SetparametersActivity.class);
                startActivity(intent);


                //Toast.makeText(this,"button_Set",Toast.LENGTH_LONG).show();
                break;
            case R.id.button_History:
                toggleButton.setChecked(false);
                Intent intent2 = new Intent(this, TestActivity.class);
                startActivity(intent2);

//                Toast.makeText(this, "button_History", Toast.LENGTH_LONG).show();
                break;
            case R.id.button_Save:
                toggleButton.setChecked(false);
                save_dialog();

                break;
            case R.id.button_tail:
                if(check_slip==true){
                    check_slip=false;
                    chart.setCheck_slip(false);
                    button_tail.setText("手动模式");
                }else{
                    check_slip=true;
                    chart.setCheck_slip(true);
                    button_tail.setText("自动模式");
                }
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        sensorManager.registerListener(listener,sensor,sensorManager.SENSOR_DELAY_UI);
        super.onResume();
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.lxx");
        ChartActivity.this.registerReceiver(receiver, filter);
    }

    @Override
    public void onPause(){
//        sensorManager.unregisterListener(sensorEventListener);
        sensorManager.unregisterListener(listener);
        super.onPause();
    }


    private ContentValues values = new ContentValues();
    private static int record = 0;
    private int sign_check = 0;


    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("android.intent.action.lxx")) {
                Bundle bundle = intent.getExtras();
                int cmd_send_activity = bundle.getInt("cmd_send_activity");
                int cmd_safe_dis_set = bundle.getInt("cmd_safe_dis_set");
                int cmd_safe_set_now = bundle.getInt("cmd_safe_set_now");

                if (cmd_send_activity == CMD_SEND_TO_ACTIVITY) {
                    DecimalFormat df = new DecimalFormat("#.###");

                    Log.d("Bluetooth_try", "传输命令成功");
                    // String str = bundle.getString("str");
                    String str1 = bundle.getString("str_x");
                    double twoSign_str1 = Double.parseDouble(df.format(Double.parseDouble(str1)));
                    str1=String.valueOf(twoSign_str1);

                    double dis_x = Double.valueOf(str1);
                    if (dis_x < 2) {
                        text_dis.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    } else {
                        text_dis.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    }
                    float str = bundle.getFloat("str");
                    if (Double.valueOf(str1) == 15) {
                        text_dis.setText("线: 检测中... ");
                        state.setText("状态：检测不到线");
                    } else {
                        if (Start_off == true) {
                            try {
                                temp_data = str;
                                if (Double.valueOf(str) == 30) {

                                    text_dis.setText("线: " + str1);
                                    state.setText("状态：超出检测范围");
                                } else if (temp_data > 0) {
                                    if (sign_check == 0) {
                                        num_min = temp_data;
                                        sign_check = 1;
                                    }
                                    if (temp_data < num_min)
                                        num_min = temp_data;

                                    count_rolling++;
                                    if (count_rolling > 60 && check_slip == true) {
                                        chart.setXAxisMinMax(count_rolling - 59, count_rolling);
                                    }
                                    chart.updateChart(temp_data, size_x_series, count1);
//                                    count1++;

//                                    chart.updateChart(temp_data, size_x_series, count1);

                                    text_dis.setText("线:" + str1 + "米");

                                    double twoSign_num_min = Double.parseDouble(df.format(num_min));
                                    double twoSign_temp_data=Double.parseDouble(df.format(temp_data));

                                    textView_xs.setText("线树最小距离: " + String.valueOf(twoSign_num_min) + "米");

                                    realtime_dis.setText("线树距离："+String.valueOf(twoSign_temp_data)+ "米");
                                    state.setText("状态：正常");
                                    // t+=1;
                                    count1++;
                                    Log.d("Bluetooth_try", "图表数据更新完成");

                                    //缓存数据
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
                                    Date curDate = new Date(System.currentTimeMillis());
                                    time = formatter.format(curDate);
                                    if (record == 0) {
                                        NewTest newTest = new NewTest();
                                        newTest.setTime(time);
                                        newTest.setDist1(str1);
                                        newTest.setDist2(String.valueOf(temp_data));
                                        newlists.add(newTest);
                                        values.put("time", time);
                                        values.put("dist1", str1);
                                        values.put("dist2", String.valueOf(temp_data));
                                    } else {
                                        NewTest newTest = new NewTest();
                                        newTest.setTime(time);
                                        newTest.setDist1(str1);
                                        newTest.setDist2(String.valueOf(temp_data));
                                        newlists.add(newTest);
                                    }

                                    if (temp_data <= safedis_set) {
                                        soundPool.play(soundmap.get(1), 1, 1, 0, 0, 1);
                                        textView_xs.setTextColor(context.getResources().getColor(R.color.colorAccent));
                                    } else {
                                        textView_xs.setTextColor(context.getResources().getColor(R.color.colorWhite));

                                    }


                                }
                            } catch (Exception ex) {
                                Log.v("debug", "----------------bugbubgbugbubgubugbugbugbubug-------------------");
                                Log.v("debug", "----------------bugbubgbugbubgubugbugbugbubug-------------------");
                                Log.v("debug", "----------------bugbubgbugbubgubugbugbugbubug-------------------");
                                Log.v("debug", ex.toString());

                            }
                        } else {
//                        Toast.makeText(ChartActivity.this, "不接收数据", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if (cmd_safe_dis_set == CMD_SAFE_DIS_SET) {
                    String safe_dis = bundle.getString("safedis");
                    safedis_set = Double.parseDouble(safe_dis);
                }
                if (cmd_safe_set_now == CMD_SAFE_DIS_SET_NOW) {
                    send_dis_set(safedis_set);
                }
            }
        }
    }

    public void send_dis_set(double dis_set) {
        Intent intent = new Intent();
        intent.putExtra("cmd_safe_set_now", CMD_SAFE_DIS_SET_NOW_TO_SET);
        intent.putExtra("dis_set", dis_set);
        intent.setAction("android.intent.action.lxx");
        sendBroadcast(intent);
        Log.d("Bluetooth_try", "send_dis_set启动成功（Chartactivity）");
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            Start_off = true;
            // sendCmd_START();
            Toast.makeText(this, "开始接收数据", Toast.LENGTH_SHORT).show();
        } else {
            //sendCmd_STOP();
            Start_off = false;
            Toast.makeText(this, "暂停接收数据", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();
                mhandler.sendEmptyMessageDelayed(0, 3000);

            } else {
                //finish();
                // System.exit(0);
                // Process.killProcess(Process.myPid());
                CloseActivity.close();
            }
        }
        return false;
    }


    public void save_dialog() {

        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.layout_save_dialog, (ViewGroup) findViewById(R.id.save_dialog));
        final View tittle_view = factory.inflate(R.layout.layout_tittle, null);

        AlertDialog.Builder mbuilder = new AlertDialog.Builder(ChartActivity.this);

        mbuilder.setCustomTitle(tittle_view);

        mbuilder.setView(textEntryView);


        final TextView textView_sys_time = (TextView) textEntryView.findViewById(R.id.sys_time);
        final EditText edit_place = (EditText) textEntryView.findViewById(R.id.place);
        SimpleDateFormat formatter_show = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate_show = new Date(System.currentTimeMillis());
        String time2 = formatter_show.format(curDate_show);
        textView_sys_time.setText(time2);
        myOpenHelper = new MyOpenHelper(getApplicationContext());
//        lists = new ArrayList<Test>();
//        newlists = new ArrayList<NewTest>();

        mbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Dialog_whether_close dialog_whether_close = new Dialog_whether_close();
                if (edit_place.getText() == null) {
                    Toast.makeText(getApplicationContext(), "请输入测试地点", Toast.LENGTH_SHORT).show();
                    dialog_whether_close.keepDialog(dialogInterface);
                } else {
                    //拿到sign_number
                    SQLiteDatabase db_read = myOpenHelper.getReadableDatabase();
                    Cursor cursor = db_read.query("catalog", new String[]{"_id", "time", "dist1", "dist2", "place", "sign", "shortest_dis"}, null, null, null, null, null);
//                    cursor.move(cursor.getCount());
//                    cursor.moveToPosition(cursor.getCount()-1);
                    cursor.moveToLast();
                    int sign_number = Integer.valueOf(cursor.getString(5));

                    sign_number += 1;
                    String string_sign_number = String.valueOf(sign_number);

//                    Toast.makeText(getApplicationContext(), string_sign_number, Toast.LENGTH_SHORT).show();
                    db_read.close();

                    //拿到用户数的place
                    String place = edit_place.getText().toString();
                    String shortest_dis = String.valueOf(num_min).substring(0, 3);

                    //将信息写入数据库表catalog
                    SQLiteDatabase db = myOpenHelper.getWritableDatabase();
                    values.put("place", place);
                    values.put("sign", string_sign_number);
                    values.put("shortest_dis", shortest_dis);

                    insert2 = db.insert("catalog", null, values);

                    //将信息写入数据库表info
                    for (int count = 0; count < newlists.size(); count++) {
                        newlists.get(count).setPlace(place);
                        newlists.get(count).setSign(string_sign_number);
                    }

                    db.beginTransaction();
                    try {
                        for (NewTest nt : newlists) {
                            ContentValues values = new ContentValues();
                            values.put("time", nt.getTime());
                            values.put("dist1", nt.getDist1());
                            values.put("dist2", nt.getDist2());
                            values.put("place", nt.getPlace());
                            values.put("sign", nt.getSign());
                            insert1 = db.insert("info", null, values);

//                            Toast.makeText(getApplicationContext(), nt.getSign() , Toast.LENGTH_SHORT).show();

                        }
                        db.setTransactionSuccessful();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();

                    } finally {
                        db.endTransaction();
                    }
                    db.close();

                    if (insert2 > 0) {
                        Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Dialog_whether_close dialog_whether_close = new Dialog_whether_close();
                dialog_whether_close.distoryDialog(dialogInterface);
            }
        });
        mbuilder.create().show();
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;


            if (convertView == null) {
                //创建新的view 对象
                view = View.inflate(getApplicationContext(), R.layout.item, null);

            } else {
                view = convertView;

            }

            TextView tv_id = (TextView) view.findViewById(R.id.tv_id);
            TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
            TextView tv_dist1 = (TextView) view.findViewById(R.id.tv_dist1);
            TextView tv_dist2 = (TextView) view.findViewById(R.id.tv_dist2);
            TextView tv_place = (TextView) view.findViewById(R.id.tv_place);
            Test test = lists.get(position);
            tv_id.setText(test.get_id());
            tv_time.setText(test.getTime());
            tv_dist1.setText(test.getDist1());
            tv_dist2.setText(test.getDist2());
            tv_place.setText(test.getPlace());
            view.setBackgroundColor(Color.BLACK);
            return view;
        }
    }

}