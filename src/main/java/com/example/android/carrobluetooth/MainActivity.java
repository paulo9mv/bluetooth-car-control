package com.example.android.carrobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;


import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.BluetoothCallback;
import me.aflak.bluetooth.DeviceCallback;
import me.aflak.bluetooth.DiscoveryCallback;



public class MainActivity extends AppCompatActivity implements JoystickView.JoystickListener{

    private JoystickView mJoystickView;

    Bluetooth bluetooth = new Bluetooth(this);
    SimplesAdapter simplesAdapter = new SimplesAdapter(this);

    private static final String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetooth.onStart();
        bluetooth.enable();

        mJoystickView = new JoystickView(this);
        setContentView(R.layout.activity_main);

        bluetooth.setBluetoothCallback(new BluetoothCallback() {
            @Override
            public void onBluetoothTurningOn() {
                Log.d(TAG, "onBluetoothTurningOn()");
            }

            @Override
            public void onBluetoothOn() {
                Log.d(TAG, "onBluetoothOn()");
            }

            @Override
            public void onBluetoothTurningOff() {
                Log.d(TAG, "onBluetoothTurningOff()");
            }

            @Override
            public void onBluetoothOff() {
                Log.d(TAG, "onBluetoothOff()");
            }

            @Override
            public void onUserDeniedActivation() {
                Log.d(TAG, "onUserDeniedActivation()");
                // when using bluetooth.showEnableDialog()
                // you will also have to call bluetooth.onActivityResult()
            }
        });
        bluetooth.setDiscoveryCallback(new DiscoveryCallback() {
            @Override public void onDiscoveryStarted() {
                Log.d(TAG, "Discovering");
            }
            @Override public void onDiscoveryFinished() {
                Log.d(TAG, "Discovering finish");

            }
            @Override public void onDeviceFound(BluetoothDevice device) {
                Log.d(TAG, "Nome: " +  device.getName() + " " + device.getAddress());
                simplesAdapter.addDevice(device);
            }
            @Override public void onDevicePaired(BluetoothDevice device) {
                Log.d(TAG, "onDevicePaired() " + device.getName());
                showToast("Pareado com " + device.getName());
                bluetooth.connectToDevice(device);
            }
            @Override public void onDeviceUnpaired(BluetoothDevice device) {
                Log.d(TAG, "onDeviceUnpaired() " + device.getName());
            }
            @Override public void onError(String message) {
                Log.d(TAG, "onError() " + message);
            }
        });
        bluetooth.setDeviceCallback(new DeviceCallback() {
            @Override public void onDeviceConnected(final BluetoothDevice device) {
                showToast("Conectado a " + device.getName());
            }
            @Override public void onDeviceDisconnected(BluetoothDevice device, String message) {
                Log.d(TAG, "onDeviceDisconnected() " + device.getName() + " " + message);
                showToast("Desconectado de " + device.getName());
            }
            @Override public void onMessage(String message) {
                Log.d(TAG, "onMessage() " + message);
            }
            @Override public void onError(String message) {
                Log.d(TAG, "onError() " + message);
            }
            @Override public void onConnectError(BluetoothDevice device, String message) {
                Log.d(TAG, "onConnectError() " + device.getName() + " " + message);
            }
        });
    }

    public Context getContext(){
        return this;
    }

    public void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        simplesAdapter.cleanAdapter();
        simplesAdapter.addDevice(bluetooth.getPairedDevices());
        builder.setTitle("Buscando dispositivos...");
        builder.setAdapter(simplesAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                BluetoothDevice device = (BluetoothDevice) simplesAdapter.getItem(which);

                if(!bluetooth.isConnected()){
                    if(bluetooth.getPairedDevices().contains(device))
                        bluetooth.connectToDevice(device);
                    else
                        bluetooth.pair(device);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onJoystickMoved(float x, float y, int source) {
        Log.d(TAG, "X percent: " + x + " Y percent: " + y);
        if(bluetooth.isConnected()) {
            if(x < 0)
                bluetooth.send("Y");
            else
                bluetooth.send("N");
            Log.d(TAG, "Sent");
        }
    }

    public void showToast(final String message){
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, message);
                Toast.makeText(MainActivity.this,message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //Infla o menu_main com o que está no XML
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_scan){
            if(!bluetooth.isEnabled()) {
                bluetooth.showEnableDialog(this);
                bluetooth.onStart();
                bluetooth.enable();
            }
            checkLocationPermission();
            createDialog();
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    proceedDiscovery(); // --->
                } else {
                    //TODO re-request
                }
                break;
            }
        }
    }

    public void proceedDiscovery(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);

        bluetooth.startScanning();
    }

    protected void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        else
            proceedDiscovery();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bluetooth.onStart();
        bluetooth.enable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetooth.onStop();
    }

}
