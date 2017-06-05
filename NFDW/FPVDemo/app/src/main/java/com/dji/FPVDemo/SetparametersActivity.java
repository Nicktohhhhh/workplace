package com.dji.FPVDemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dji.FPVDemo.model.BluetoothModel;
import com.dji.FPVDemo.service.BluetoothService;
import com.dji.FPVDemo.service.impl.BluetoothImpl;
import com.dji.FPVDemo.utils.ChangeOrientationHandler;
import com.dji.FPVDemo.utils.OrientationSensorListener;

/**
 * Created by xiaoT on 2017/1/18.
 */

public class SetparametersActivity extends Activity implements View.OnClickListener {

    private Button button_obtain,button_cancle1,button_sure;
    private EditText editText_dis1;
    private EditText editText_dis;
    private EditText editText_safe_dis_now,editText_safe_dis_set;
    private EditText editText_x_u1,editText_x_u2,editText_x_d1,editText_x_d2;
    private EditText editText_s_u1,editText_s_u2,editText_s_d1,editText_s_d2;

    private BluetoothService bluetoothService;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothModel bluetoothModel;

    static final int CMD_SEND_DATA = 0x02;
    //static final int CMD_SEND_ADDRESS =0x03;
    static final int CMD_SEND_TO_ACTIVITY =0x04;
    static final int CMD_SHOW_TOAST =0x05;
    static final int CMD_SEND_TO_ACTIVITY_SET =0x06;
    static final int CMD_SAFE_DIS_SET =0x07;
    static final int CMD_SAFE_DIS_SET_NOW=0x08;
    static final int CMD_SAFE_DIS_SET_NOW_TO_SET=0x09;

    private SensorManager sensorManager;
    private Sensor sensor;
    private Handler handler;
    private OrientationSensorListener listener;

    MyReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.layout_set_change);

        button_cancle1=(Button)findViewById(R.id.button_cancel);
        button_obtain=(Button)findViewById(R.id.button_obtain);
        button_sure=(Button)findViewById(R.id.button_sure);

        editText_dis1=(EditText)findViewById(R.id.editText_dis1);
        editText_safe_dis_now=(EditText)findViewById(R.id.safe_dis_now);
        editText_safe_dis_set=(EditText)findViewById(R.id.safe_dis_set);
        editText_dis=(EditText)findViewById(R.id.editText_dis);

        editText_s_d1=(EditText)findViewById(R.id.editText_s_d1);
        editText_s_d2=(EditText)findViewById(R.id.editText_s_d2);
        editText_s_u1=(EditText)findViewById(R.id.editText_s_u1);
        editText_s_u2=(EditText)findViewById(R.id.editText_s_u2);
        editText_x_d1=(EditText)findViewById(R.id.editText_x_d1);
        editText_x_d2=(EditText)findViewById(R.id.editText_x_d2);
        editText_x_u1=(EditText)findViewById(R.id.editText_x_u1);
        editText_x_u2=(EditText)findViewById(R.id.editText_x_u2);

        bluetoothService=new BluetoothImpl();
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        editText_x_u2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                try{
                    if(editable.length()!=0){
                        int tempnum=Integer.parseInt(editable.toString());
                        if (tempnum>2000||tempnum<0){
                            Toast.makeText(SetparametersActivity.this,"该数范围为0~2000",Toast.LENGTH_SHORT).show();
                            editable.replace(0,editable.length(),"1000");
                        }else{
                            return;
                        }
                    }else{
                        return;
                    }
                }catch (Exception ex){
                    Log.d("Hugo","There is something wrong.");
                }
            }
        });
        editText_x_d2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                try{
                    if(editable.length()!=0){
                        int tempnum=Integer.parseInt(editable.toString());
                        if (tempnum>2000||tempnum<0){
                            Toast.makeText(SetparametersActivity.this,"该数范围为0~2000",Toast.LENGTH_SHORT).show();
                            editable.replace(0,editable.length(),"1000");
                        }else{
                            return;
                        }
                    }else{
                        return;
                    }
                }catch (Exception ex){
                    Log.d("Hugo","There is something wrong.");
                }


            }
        });
        editText_dis1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    if(editable.length()!=0){
                        int tempnum=Integer.parseInt(editable.toString());
                        if (tempnum>10||tempnum<0){
                            Toast.makeText(SetparametersActivity.this,"该数范围为0~10",Toast.LENGTH_SHORT).show();
                            editable.replace(0,editable.length(),"5");
                        }else{
                            return;
                        }
                    }else{
                        return;
                    }
                }catch (Exception ex){
                    Log.d("Hugo","There is something wrong.");
                }
            }
        });
        editText_safe_dis_set.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try{
                    if(s.length()!=0){
                        int tempnum=Integer.parseInt(s.toString());
                        if (tempnum>90||tempnum<0){
                            Toast.makeText(SetparametersActivity.this,"该数范围为0~90",Toast.LENGTH_SHORT).show();
                            s.replace(0,s.length(),"30");
                        }else{
                            return;
                        }
                    }else{
                        return;
                    }
                }catch (Exception ex){
                    Log.d("Hugo","There is something wrong.");
                }

            }
        });
        editText_s_u2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    if(s.length()!=0){
                        int tempnum=Integer.parseInt(s.toString());
                        if (tempnum>180||tempnum<0){
                            Toast.makeText(SetparametersActivity.this,"该数范围为0~180",Toast.LENGTH_SHORT).show();
                            s.replace(0,s.length(),"90");
                        }else{
                            return;
                        }
                    }else{
                        return;
                    }
                }catch (Exception ex){
                    Log.d("Hugo","There is something wrong.");
                }

            }
        });
        editText_s_d2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    if(s.length()!=0){
                        int tempnum=Integer.parseInt(s.toString());
                        if (tempnum>180||tempnum<0){
                            Toast.makeText(SetparametersActivity.this,"该数范围为0~180",Toast.LENGTH_SHORT).show();
                            s.replace(0,s.length(),"90");
                        }else{
                            return;
                        }
                    }else{
                        return;
                    }
                }catch (Exception ex){
                    Log.d("Hugo","There is something wrong.");
                }
            }
        });


        button_sure.setOnClickListener(this);
        button_obtain.setOnClickListener(this);
        button_cancle1.setOnClickListener(this);

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        handler = new ChangeOrientationHandler(this);
        listener = new OrientationSensorListener(handler);
        sensorManager.registerListener(listener,sensor,sensorManager.SENSOR_DELAY_UI);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null){
            SetparametersActivity.this.unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        sensorManager.registerListener(listener,sensor,sensorManager.SENSOR_DELAY_UI);
        super.onResume();
        receiver = new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.intent.action.lxx");
        SetparametersActivity.this.registerReceiver(receiver,filter);
    }

    @Override
    public void onPause(){
//        sensorManager.unregisterListener(sensorEventListener);
        sensorManager.unregisterListener(listener);
        super.onPause();
    }


    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    public void onClick(View view){
        switch (view.getId()){
            case R.id.button_obtain:           //获取当前参数按钮

                byte[] Send_data_read=new byte[11];
                Send_data_read[0] = 0x11;
                Send_data_read[1]=0x02;
                Send_data_read[2] = 0;
                Send_data_read[3] =0;
                Send_data_read[4] = 0;
                Send_data_read[5] = 0;
                Send_data_read[6] = 0;
                Send_data_read[7] = 0;
                Send_data_read[8] = 0;
                Send_data_read[9] = 0;
                Send_data_read[10] = 0x22;
                sendCmd_read(Send_data_read);
                send_safe_dis_now();

                Log.d("Bluetooth_try","获取参数传输给服务启动（活动）");

                break;
            case R.id.button_cancel:          //取消键按钮

                // text_s.setText("你好"+mid_dis);
//                Intent intent=new Intent(this,ChartActivity.class);
//                startActivity(intent);
                finish();
                break;
            case R.id.button_sure:        //确定键按钮
                String bos0 = editText_x_u2.getText().toString();
                String bos1 = editText_x_d2.getText().toString();
                String u_s=editText_s_u2.getText().toString();
                String d_s=editText_s_d2.getText().toString();
                String bos2 = editText_dis1.getText().toString();
                String safe_dis=editText_safe_dis_set.getText().toString();

                short num_u=(short)Integer.valueOf(u_s).intValue();
                short num_d=(short)Integer.valueOf(d_s).intValue();
                short num0=(short)Integer.valueOf(bos0).intValue();
                short num1=(short)Integer.valueOf(bos1).intValue();
                short num2=(short)Integer.valueOf(bos2).intValue();
                short num3=(short)Integer.valueOf(safe_dis).intValue();

                byte xian_d=(byte)num2;

                if (num1>num_u){
                    num_u=num1;
                    String string_u1=String.valueOf(num_u);
                    editText_s_u2.setText(string_u1);
                }

                if (num_d<=num_u){
                    num_d=180;
                    if (num_d<180){
                        String string_u1=String.valueOf(num_d);
                        editText_s_d2.setText(string_u1);
                    }else {
                        num_d=180;
                        String string_u1=String.valueOf(num_d);
                        editText_s_d2.setText(string_u1);
//                        editText_s_d2.setText(num_d);
                    }
                }


                byte[] Send_data=new byte[11];
                Send_data[0] = 0x11;
                Send_data[1]=(byte) 0xA0;
                Send_data[2] = (byte)((num0>>8)&0xff);
                Send_data[3] =(byte)(num0&0xff);
                Send_data[4] = (byte)((num1>>8)&0xff);
                Send_data[5] =(byte)(num1&0xff);
                Send_data[6] = (byte)(num_u&0xff);
                Send_data[7] = (byte)(num_d&0xff);
                Send_data[8] =(byte)(num2&0xff);
                Send_data[9] = (byte) (num3&0xff);
                Send_data[10] = (byte)0x22;
                sendCmd(Send_data);
                send_safe_dis(safe_dis);
                Log.d("Bluetooth_try","发送参数给服务启动（活动）");
                break;
            default:
                break;
        }
    }

    public void sendCmd_read(byte Send_data_read[]){   //用该命令来把蓝牙数据从Service中传到Activity中
        Intent intent = new Intent();//创建Intent对象
        intent.setAction("android.intent.action.cmd");
        intent.putExtra("cmd_send_activity_set", CMD_SEND_TO_ACTIVITY_SET);
        intent.putExtra("Send_data_read",Send_data_read);
        sendBroadcast(intent);//发送广播
        Log.d("Bluetooth_try","发送将蓝牙数据由传输到活动的命令成功");
    }

    public void sendCmd(byte Send_data[]){///发送设置参数
        Intent intent = new Intent();//创建Intent对象
        intent.setAction("android.intent.action.cmd");
        intent.putExtra("cmd_send_data", CMD_SEND_DATA);
        intent.putExtra("Send_data",Send_data);
        sendBroadcast(intent);//发送广播
        Log.d("Bluetooth_try","发送设置参数到服务中成功");
    }
    public void send_safe_dis(String safedis){
        Intent intent = new Intent();
        intent.putExtra("cmd_safe_dis_set", CMD_SAFE_DIS_SET);
        intent.putExtra("safedis",safedis);
        intent.setAction("android.intent.action.lxx");
        sendBroadcast(intent);
        Log.d("Bluetooth_try","sendtoActivity启动成功（服务）");
    }
    public void send_safe_dis_now(){
        Intent intent = new Intent();
        intent.putExtra("cmd_safe_set_now", CMD_SAFE_DIS_SET_NOW);
        intent.setAction("android.intent.action.lxx");
        sendBroadcast(intent);
        Log.d("Bluetooth_try","sendtoActivity启动成功（服务）");
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if(intent.getAction().equals("android.intent.action.lxx")){
                Bundle bundle = intent.getExtras();
                int cmd_show_toast = bundle.getInt("cmd_show_toast");
                int cmd_activity_set=bundle.getInt("cmd_activity_set");
                int cmd_safe_set_now=bundle.getInt("cmd_safe_set_now");

                if(cmd_activity_set == CMD_SEND_TO_ACTIVITY_SET){
                    Log.d("Bluetooth_try","传输命令成功");
                    String string_u = bundle.getString("string_u");
                    String string_d = bundle.getString("string_d");
                    String string_dis = bundle.getString("string_dis");
                    String string_s_d= bundle.getString("string_s_d");
                    String string_s_u= bundle.getString("string_s_u");

//                    editText_x_u1.setText(string_u);
//                    editText_x_d1.setText(string_d);
                    editText_dis.setText(string_dis);
                    editText_s_u1.setText(string_s_u);
                    editText_s_d1.setText(string_s_d);
                    Log.d("Bluetooth_try","显示数据成功");
                }
                if(cmd_show_toast == CMD_SHOW_TOAST){
                    String str = bundle.getString("str_show");
                    Toast.makeText(SetparametersActivity.this,str,Toast.LENGTH_SHORT).show();
                }
                if (cmd_safe_set_now==CMD_SAFE_DIS_SET_NOW_TO_SET){
                    double dis_set_show=bundle.getDouble("dis_set");
                    editText_safe_dis_now.setText(dis_set_show+"");
                }
            }
        }
    }
}
