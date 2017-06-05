package com.dji.FPVDemo.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.InputStream;

/**
 * Created by lenovo on 2016/11/22.
 */

public class BluetoothModel {
    public final static int REQUEST_CONNECT_DEVICE = 1;    //宏定义查询设备句柄
    public final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号
    private static InputStream blueToothStream=null;    //输入流，用来接收蓝牙数据
    private BluetoothDevice _device;     //蓝牙设备
    private BluetoothSocket _socket;      //蓝牙通信socket
    private BluetoothAdapter _bluetooth ;    //获取本地蓝牙适配器，即蓝牙设备

    public BluetoothModel(){
        _device=null;
        _socket=null;
        _bluetooth= BluetoothAdapter.getDefaultAdapter();
    }

    public static InputStream getBlueToothStream(){
        if(blueToothStream!=null){
            return blueToothStream;
        }else{
            return null;
        }
    }

    //注意stream是static的
    public static void setBlueToothStream(InputStream blueToothStream) {
        BluetoothModel.blueToothStream = blueToothStream;
    }

    public BluetoothAdapter getBluetoothAdapter(){
        if(_bluetooth!=null){
            return _bluetooth;
        }else{
            return null;
        }
    }

    public BluetoothDevice get_device(){
        return _device;
    }

    public void set_device(BluetoothDevice _device) {
        this._device = _device;
    }

    public BluetoothSocket get_socket(){
        return _socket;
    }

    public void set_socket(BluetoothSocket _socket) {
        this._socket = _socket;
    }
}
