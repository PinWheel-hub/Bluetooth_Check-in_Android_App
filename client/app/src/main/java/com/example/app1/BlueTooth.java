package com.example.app1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class BlueTooth {


    private BluetoothAdapter mAdapter;
    public BlueTooth(){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    //  是否支持蓝牙
    public boolean isSupportBlueTooth(){
        if(mAdapter != null){
            return true;
        }
        else {
            return  false;
        }
    }

    //判断当前蓝牙状态
    public boolean getBlueThoothStatus(){
        assert(mAdapter != null);
        return mAdapter.isEnabled();
    }

    //打开蓝牙
    public void turnOnBlueTooth(Activity activity, int requestCode){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    //关闭蓝牙
    public void  turnOffBlueTooth(){
        mAdapter.disable();
    }

    //打开蓝牙可见性
    public void enableVisibily(Context context){
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        context.startActivity(discoverableIntent);
    }

    //查找设备
    public void findDevice() {
        assert (mAdapter != null);
        mAdapter.startDiscovery();
    }

    // 获取已绑定设备
    public List<BluetoothDevice> getBondedDeviceList(){
        return new ArrayList<>(mAdapter.getBondedDevices());
    }
}
