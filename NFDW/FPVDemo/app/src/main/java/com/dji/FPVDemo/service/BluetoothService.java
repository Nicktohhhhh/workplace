package com.dji.FPVDemo.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.dji.FPVDemo.model.BluetoothModel;

import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/12.
 */

public interface BluetoothService {
    public BluetoothAdapter getBluetoothAdapter();
    public InputStream getBlueToothStream();
    public BluetoothDevice getBlueToothDevice();
    public BluetoothSocket getBlueToothSocket();
    public void close_socket();
    public void close_stream();

    //蓝牙连接
    public void connect_bluetooth(Context context, String address);
}
