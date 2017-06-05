package com.dji.FPVDemo.service.impl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;

import com.dji.FPVDemo.model.BluetoothModel;
import com.dji.FPVDemo.service.BluetoothService;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by lenovo on 2016/12/12.
 */

public class BluetoothImpl implements BluetoothService {
    private BluetoothModel bluetoothModel;

    public BluetoothImpl(){
        bluetoothModel=new BluetoothModel();
    }

    @Override
    public InputStream getBlueToothStream(){
        return BluetoothModel.getBlueToothStream();
    }

    @Override
    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothModel.getBluetoothAdapter();
    }

    @Override
    public BluetoothDevice getBlueToothDevice() {
        return bluetoothModel.get_device();
    }

    @Override
    public BluetoothSocket getBlueToothSocket() {
        return bluetoothModel.get_socket();
    }

    @Override
    public void close_socket() {
        try {
            bluetoothModel.get_socket().close();
            bluetoothModel.set_socket(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close_stream() {
        try {
            BluetoothModel.getBlueToothStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect_bluetooth(Context context,String address){
        bluetoothModel.set_device(this.getBluetoothAdapter().getRemoteDevice(address));
        BluetoothDevice _device=bluetoothModel.get_device();

        // 用服务号得到socket
        try{
            bluetoothModel.set_socket(_device.createRfcommSocketToServiceRecord(UUID.fromString(BluetoothModel.MY_UUID)));
            BluetoothSocket _socket=bluetoothModel.get_socket();
            _socket.connect();
            Toast.makeText(context, "连接"+_device.getName()+"成功！", Toast.LENGTH_SHORT).show();
        }catch(IOException e){
                Toast.makeText(context, "蓝牙连接失败！", Toast.LENGTH_SHORT).show();
                this.close_socket();
            return;
        }

        //打开接收线程
        try{
            BluetoothModel.setBlueToothStream(bluetoothModel.get_socket().getInputStream());  //得到蓝牙数据输入流
            Toast.makeText(context, "蓝牙数据输入流开启！", Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            Toast.makeText(context, "接收数据失败！", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
