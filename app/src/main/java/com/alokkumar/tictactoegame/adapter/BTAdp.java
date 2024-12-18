package com.alokkumar.tictactoegame.adapter;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class BTAdp {
    boolean supportBT;

    BTAdp(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter==null){
            supportBT=false;
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        }
    }
}
