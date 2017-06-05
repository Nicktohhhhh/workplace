package com.dji.FPVDemo.utils;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.Exchanger;

public class OTG_ConnectService extends Service {

    CommandReceiver cmdReceiver;//继承自BroadcastReceiver对象，用于得到Activity发送过来的命令
    private final static int REQUEST_CONNECT_DEVICE = 1;    //宏定义查询设备句

//    private BluetoothAdapter mBluetoothAdapter = null;
//    private BluetoothSocket btSocket = null;
//    private OutputStream outStream = null;
//    private InputStream inStream = null;
//    BluetoothDevice device;
    // public  boolean bluetoothFlag  = true;
    private String smsg = "";    //显示用数据缓存
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "98:D3:32:10:7D:A2"; // <==要连接的蓝牙设备MAC地址

    static final int CMD_SEND_DATA = 0x02;
    static final int CMD_SEND_ADDRESS = 0x03;
    static final int CMD_SEND_TO_ACTIVITY = 0x04;
    static final int CMD_SEND_TO_ACTIVITY_SET = 0x06;
    static final int CMD_SHOW_TOAST = 0x05;
    private boolean setok = false;
    private boolean readok = false;


    private float mid_dis = 0;
    private float mid_dis1 = 0;
    private String smsg1 = "";     //显示线的距离
//    private boolean Start_off = false;
    int upangle;
    int downangle;
    int upangle_s;
    int downangle_s;
    int distance;

    public OTG_ConnectService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        start_thread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cmdReceiver = new CommandReceiver();
        IntentFilter filter = new IntentFilter();//创建IntentFilter对象
        //注册一个广播，用于接收Activity传送过来的命令，控制Service的行为，如：发送数据，停止服务等
        filter.addAction("android.intent.action.cmd");
        //注册Broadcast Receiver
        registerReceiver(cmdReceiver, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    //串口发送数据
    public void sendCmd_w(byte senddata[]) {
        byte msgBuffer[] = senddata;
        try {
            int count = 0;
            while (setok == false) {
                long time = System.currentTimeMillis();
                while ((System.currentTimeMillis() - time) < 300) ;
                if (setok == false) {
                    MyApp.driver.WriteData(msgBuffer, msgBuffer.length);//写数据，第一个参数为需要发送的字节数组，第二个参数为需要发送的字节长度，返回实际发送的字节长度
                    count++;
                    if (count > 10) {
                        setok = true;
                        smsg = "设置失败";
                        showToast(smsg);
                        // Toast.makeText(this, "设置失败！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            setok = false;

            Log.d("Bluetooth_try", "设置参数成功传到服务中（服务输出流）");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendcmd_r(byte senddata[]) {

        byte msgBuffer[] = senddata;
        try {
            int count2 = 0;
            while (readok == false) {
                long time = System.currentTimeMillis();
                while ((System.currentTimeMillis() - time) < 300) ;
                if (readok == false) {
                    MyApp.driver.WriteData(msgBuffer, msgBuffer.length);
                }
                count2++;
                if (count2 > 10) {
                    readok = true;
                    smsg = "读取失败";
                    showToast(smsg);
                    //Toast.makeText(this, "读取失败！", Toast.LENGTH_SHORT).show();
                }
            }
            readok = false;
            Log.d("Bluetooth_try", "获取参数函数启动（函数）");
        } catch (Exception e) {

        }
    }

    private void return_setdata() {

        String string_u = String.valueOf(upangle);
        String string_d = String.valueOf(downangle);
        String string_dis = String.valueOf(distance);
        String string_s_u = String.valueOf(upangle_s);
        String string_s_d = String.valueOf(downangle_s);

        Intent intent = new Intent();
        intent.putExtra("cmd_activity_set", CMD_SEND_TO_ACTIVITY_SET);
        intent.putExtra("string_u", string_u);
        intent.putExtra("string_d", string_d);
        intent.putExtra("string_dis", string_dis);
        intent.putExtra("string_s_u", string_s_u);
        intent.putExtra("string_s_d", string_s_d);
        intent.setAction("android.intent.action.lxx");
        sendBroadcast(intent);
        Log.d("Bluetooth_try", "sendtoActivity启动成功（服务）");
    }


    public void showToast(String str_show) {//显示提示信息
        Intent intent = new Intent();
        intent.putExtra("cmd_show_toast", CMD_SHOW_TOAST);
        intent.putExtra("str_show", str_show);
        intent.setAction("android.intent.action.lxx");
        sendBroadcast(intent);
    }

    //发送线树的数据给activity
    public void sendtoActivity(float str, String str_x) {

        Intent intent = new Intent();
        intent.putExtra("cmd_send_activity", CMD_SEND_TO_ACTIVITY);
        intent.putExtra("str", str);
        intent.putExtra("str_x", str_x);
        intent.setAction("android.intent.action.lxx");

        sendBroadcast(intent);
        Log.d("Bluetooth_try", "sendtoActivity启动成功（服务）");
    }

    //接收Activity传送过来的命令
    private class CommandReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.cmd")) {
                int cmd_send_activity_set = intent.getIntExtra("cmd_send_activity_set", -1);//获取Extra信息
                int cmd_send_data = intent.getIntExtra("cmd_send_data", -1);
                int cmd_send_address = intent.getIntExtra("cmd_send_address", -1);


                if (cmd_send_activity_set == CMD_SEND_TO_ACTIVITY_SET) {
                    byte senddata_read[] = intent.getByteArrayExtra("Send_data_read");
                    sendcmd_r(senddata_read);
                    return_setdata();
                    Log.d("Bluetooth_try", "成功传数据到服务中（服务）");
                    // readByte();
                    // sendtoActivity(smsg);
                }
                if (cmd_send_data == CMD_SEND_DATA) {
                    byte senddata[] = intent.getByteArrayExtra("Send_data");
                    sendCmd_w(senddata);
                    Log.d("Bluetooth_try", "设置参数成功传到服务中（服务）");
                }
                if (cmd_send_address == CMD_SEND_ADDRESS) {
                    Log.d("Bluetooth_try", "成功传输蓝牙地址到服务中（服务）");
                    address = intent.getStringExtra("string_address");
                }
            }
        }
    }

    public void start_thread() {
        ReadThread.start();
    }

    //接收数据线程
    Thread ReadThread = new Thread() {
        public void run() {
            int num = 0;
            byte[] buffer = new byte[1024];
            byte[] revdata = new byte[100];
            int j = 0;
            int n = 0;
            // bRun = true;
            while (true) {
                try {
                  /*  while(inStream.available()==0){
                        while(bRun == false){}
                    }  */

                    while (true) {
                        num = MyApp.driver.ReadData(buffer, 64);         //读入数据
                        for (j = 0; j < num; j++) {
                            if ((buffer[j] & 0xff) == 0x11) {
                                n = 0;
                            }
                            revdata[n] = buffer[j];
                            n++;
                        }
                        if (n == 7 && revdata[6] == 0x22) {
                            n = 0;
                            if (revdata[1] == 0x02) {
                                int mxian = ((revdata[2] & 0xff) << 8) | (revdata[3] & 0xff);
                                int mxianshu = ((revdata[4] & 0xff) << 8) | (revdata[5] & 0xff);


                                mid_dis = (float) (mxianshu / 1000.0f);
                                mid_dis1 = (float) (mxian / 1000.0f);
                                smsg1 = String.valueOf(mid_dis1);  //线的距离

                                sendtoActivity(mid_dis, smsg1);

                            }
                            if (revdata[1] == 0x00) {
                                setok = true;
                                smsg = "设置成功";
                                showToast(smsg);
                            }
                            if ((revdata[1] & 0xF0) == 0xA0) {
                                readok = true;
                                upangle = revdata[2] & 0xff;   //10
                                downangle = revdata[3] & 0xff;   //30
                                upangle_s = revdata[4] & 0xff;   //90
                                downangle_s = revdata[5] & 0xff;   //90
                                distance = (revdata[1] & 0x0f);
                                smsg = "读取成功";
                                showToast(smsg);
                            }
                        }
                        Log.d("Bluetooth_try", "线程已启动（服务）");
                    }
                    //发送显示消息，进行显示刷新
                    // handler.sendMessage(handler.obtainMessage());

                } catch (Exception e) {
                }
            }

        }

    };
}
