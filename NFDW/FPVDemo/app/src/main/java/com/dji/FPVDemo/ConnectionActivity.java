package com.dji.FPVDemo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dji.FPVDemo.model.BluetoothModel;
import com.dji.FPVDemo.model.MenuDialogFragment;

import com.dji.FPVDemo.service.impl.BluetoothImpl;
import com.dji.FPVDemo.utils.BitmapDecode;
import com.dji.FPVDemo.utils.OTG_ConnectService;
import com.dji.FPVDemo.utils.MyApp;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;

public class ConnectionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ConnectionActivity.class.getName();
    private Button blutooth1, blutooth, menu;
    private ImageView imageView;
    private ImageView menu_image;
    private Bitmap tempBitmap;
    private BluetoothAdapter bluetoothAdapter;
    static final int CMD_SEND_DATA = 0x02;
    static final int CMD_SEND_ADDRESS = 0x03;
    static final int CMD_SEND_TO_ACTIVITY = 0x04;
    private int baudRate;
    private byte  dataBit, stopBit,parity,flowControl;
    private int retval;
    private static final String ACTION_USB_PERMISSION = "cn.wch.wchusbdriver.USB_PERMISSION";
    MenuDialogFragment mMenuDialogFragment;
    FragmentTransaction transaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.READ_PHONE_STATE,
                    }
                    , 1);
        }

        //Hugo set full screen 20161028
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //end 20161028

        setContentView(R.layout.activity_connection);

        MyApp.driver = new CH34xUARTDriver(
                (UsbManager) getSystemService(Context.USB_SERVICE), this,
                ACTION_USB_PERMISSION);


        if (!MyApp.driver.UsbFeatureSupported())// 判断系统是否支持USB HOST
        {
            Dialog dialog = new android.app.AlertDialog.Builder(ConnectionActivity.this)
                    .setTitle("提示")
                    .setMessage("您的手机不支持USB HOST，请更换其他手机再试！")
                    .setPositiveButton("确认",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    System.exit(0);
                                }
                            }).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        initUI();

        CloseActivity.add(this);
    }

    private void initUI() {
        blutooth1 = (Button) findViewById(R.id.btn_bluetooth1);
        blutooth = (Button) findViewById(R.id.btn_bluetooth);
        menu = (Button) findViewById(R.id.menu);
        blutooth1.setOnClickListener(this);
        blutooth.setOnClickListener(this);
        menu.setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.connection_image);
        tempBitmap = BitmapDecode.decodeSampledBitmapFromResource(this.getResources(), R.raw.index, 500, 500);
        imageView.setImageBitmap(tempBitmap);
        menu_image = (ImageView) findViewById(R.id.menu_image);
        tempBitmap = BitmapDecode.decodeSampledBitmapFromResource(this.getResources(), R.raw.menu, 200, 200);
        menu_image.setImageBitmap(tempBitmap);
    }

    private void set_OTG_config(){
        //配置串口
        baudRate=115200;
        dataBit=(byte) Integer.parseInt("8");
        stopBit=(byte) Integer.parseInt("1");
        parity=(byte) Integer.parseInt("0");
        flowControl=(byte) Integer.parseInt("0");
        if (MyApp.driver.SetConfig(baudRate, dataBit, stopBit,parity,flowControl)) {
            Toast.makeText(ConnectionActivity.this, "串口设置成功!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ConnectionActivity.this, "串口设置失败!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bluetooth1:
            case R.id.btn_bluetooth: {

                retval = MyApp.driver.ResumeUsbList();
                if (retval == -1)// ResumeUsbList方法用于枚举CH34X设备以及打开相关设备
                {
                    Toast.makeText(ConnectionActivity.this, "打开设备失败!",
                            Toast.LENGTH_SHORT).show();
                    MyApp.driver.CloseDevice();
                } else if (retval == 0) {
                    if (!MyApp.driver.UartInit()) {//对串口设备进行初始化操作
                        Toast.makeText(ConnectionActivity.this, "设备初始化失败!",
                                Toast.LENGTH_SHORT).show();
                        Toast.makeText(ConnectionActivity.this, "打开" +
                                        "设备失败!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(ConnectionActivity.this, "打开设备成功!",
                            Toast.LENGTH_SHORT).show();

                    //配置串口
                    set_OTG_config();

                    Intent intent = new Intent(this, OTG_ConnectService.class);//OTG连接成功立即启动服务
                    startService(intent);

                    String address = "CSG";
                    send_address(address);
                    Intent intent1 = new Intent(this, ChartActivity.class);
                    startActivity(intent1);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionActivity.this);
                    builder.setTitle("未授权限");
                    builder.setMessage("确认退出吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
//								MainFragmentActivity.this.finish();
                            System.exit(0);
                        }
                    });
                    builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    });
                    builder.show();
                }
                break;
            }
            case R.id.menu:
                showmenudialog();
            default:
                break;
        }
    }

    public void send_address(String string_address) {
        Intent intent = new Intent();//创建Intent对象
        intent.setAction("android.intent.action.cmd");
        intent.putExtra("cmd_send_address", CMD_SEND_ADDRESS);
        intent.putExtra("string_address", string_address);
        sendBroadcast(intent);//发送广播
    }

    public void showmenudialog() {
        transaction = getSupportFragmentManager().beginTransaction();
        mMenuDialogFragment = MenuDialogFragment.newInstance();
        // // 指定一个过渡动画
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        // 作为全屏显示,使用“content”作为fragment容器的基本视图,这始终是Activity的基本视图
        transaction.replace(android.R.id.content, mMenuDialogFragment).commit();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");

        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");

        System.gc();
        super.onStop();
    }

    public void onReturn(View view) {
        Log.e(TAG, "onReturn");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        // unregisterReceiver(mReceiver);
        super.onDestroy();
        if (tempBitmap != null && !tempBitmap.isRecycled()) {
            tempBitmap.recycle();
            tempBitmap = null;
        }
        //CloseActivity.close();
    }
}
