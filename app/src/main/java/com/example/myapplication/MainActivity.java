package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Set;


public class MainActivity extends AppCompatActivity {

    BluetoothAdapter btAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    TextView textStatus;
    Button btnParied, btnSearch, btnRSSI;
    ListView listView;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> btArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        ActivityCompat.requestPermissions(MainActivity.this, permission_list,  1);

        // Enable bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        textStatus = (TextView) findViewById(R.id.text_status);
        btnParied = (Button) findViewById(R.id.btn_paired);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnRSSI = (Button) findViewById(R.id.btn_rssi);
        listView = (ListView) findViewById(R.id.listview);

        // show paired devices
        btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(btArrayAdapter);

    }

    public void onClickButtonPaired(View view){
        btArrayAdapter.clear();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    public void onClickButtonSearch(View view){
        // Check if the device is already discovering
        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),getString(R.string.DisStop),Toast.LENGTH_SHORT).show();
        }
        else {
            if (btAdapter.isEnabled()) {
                btArrayAdapter.clear();//clear
                btAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), getString(R.string.DisStart), Toast.LENGTH_SHORT).show();
                registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else {
                Toast.makeText(getApplicationContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

    public void onClickButtonRSSI(View view) {
        Toast.makeText(getApplicationContext(), "개발중", Toast.LENGTH_SHORT).show();
    }
}
