/**
 * Filename:        StartSearchDevice.java
 * Created:         Thu, 11 Aug 2016
 * Author:          Erick Rivera
 * Description:     This file contains implementation for searching and initiate connection with
 *                  URBAND devices over Bluetooth LE protocol on Android devices.
 * Revisions:       Thu, 11 Aug 2016 by Erick Rivera
 *
 <!--
 Copyright 2014 - 2016 Centro de Investigacion y Desarrollo COATL S.A. de C.V.
 All rights reserved.

 IMPORTANT: Your use of this Software is limited to those specific rights
 granted under the terms of a software license agreement (SLA) or
 non-disclosure agreement (NDA) between the user
 who got the software (the "Licensee") and the "Centro de Investigacion
 y Desarrollo COATL S.A. de C.V." (the "Licensor").

 You may not use this Software unless you agree to abide by the terms of the
 License (SLA and/or NDA). The License limits your use, and you acknowledge,
 that the Software may not be modified, copied or distributed.
 Other than for the foregoing purpose, you may not use, reproduce, copy,
 prepare derivative works of, modify, distribute, perform, display or sell this
 Software and/or its documentation for any purpose.

 YOU FURTHER ACKNOWLEDGE AND AGREE THAT THE SOFTWARE AND DOCUMENTATION ARE
 PROVIDED ``AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY, TITLE,
 NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT SHALL
 Centro de Investigacion y Desarrollo COATL S.A. de C.V. OR ITS LICENSORS BE
 LIABLE OR OBLIGATED UNDER CONTRACT, NEGLIGENCE, STRICT LIABILITY,
 CONTRIBUTION, BREACH OF WARRANTY, OR OTHER LEGAL EQUITABLE THEORY ANY DIRECT
 OR INDIRECT DAMAGES OR EXPENSES INCLUDING BUT NOT LIMITED TO ANY INCIDENTAL,
 SPECIAL, INDIRECT, PUNITIVE OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST
 DATA, COST OF PROCUREMENT OF SUBSTITUTE GOODS, TECHNOLOGY, SERVICES, OR ANY
 CLAIMS BY THIRD PARTIES (INCLUDING BUT NOT LIMITED TO ANY DEFENSE THEREOF),
 OR OTHER SIMILAR COSTS.

 Should you have any questions regarding your right to use this Software,
 contact Centro de Investigacion y Desarrollo COATL S.A. de C.V. at
 http://coatl.co/
 -->
 */
package dev.coatl.co.Urband_IMU_Recorder.presenter.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import dev.coatl.co.Urband_IMU_Recorder.R;
import dev.coatl.co.Urband_IMU_Recorder.model.objects.BluetoothUrbandDeviceItem;
import dev.coatl.co.Urband_IMU_Recorder.view.adapter.ListBTUrbandDevices;

public class StartSearchDevice extends AppCompatActivity {

    private final static String TAG = StartSearchDevice.class.getSimpleName();

    private Intent intent;
    private ListView listDevices;
    private AlertDialog show = null;

    private ListBTUrbandDevices adapterDevices;
    private ArrayList<BluetoothUrbandDeviceItem> arrayDevices = new ArrayList<>();
    private ArrayList<BluetoothDevice> arrayBT = new ArrayList<>();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private BluetoothAdapter bluetoothAdapter;
    private boolean scanning;
    private Handler handler;
    private Runnable searchingBT;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 6000;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onPause()
        {
            super.onPause();
            scanLeDevice(false);
            handler.removeCallbacks(searchingBT);
            arrayDevices.clear();
            arrayBT.clear();
        }

    @Override
    protected void onResume()
        {
            super.onResume();
            showAlert();
            setBluetooth();
        }

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search_device);
            setResources();
            setListAdapter();
        }

    private void setResources()
        {
            handler = new Handler();
            listDevices = (ListView) findViewById(R.id.urband_device_list);
            listDevices.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            final BluetoothDevice device = ((BluetoothUrbandDeviceItem) parent.getItemAtPosition(position)).getDevice();
                            if (device == null)
                                {
                                    return;
                                }
                            intent = new Intent(getApplicationContext(), ActivityHomePanel.class);
                            intent.putExtra(EXTRAS_DEVICE_NAME, device.getName());
                            intent.putExtra(EXTRAS_DEVICE_ADDRESS, device.getAddress());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (scanning)
                                {
                                    bluetoothAdapter.stopLeScan(scanDevices);
                                    scanning = false;
                                }
                            startActivity(intent);
                        }
                });
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
                {
                    Toast.makeText(this, "Este dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
                    finish();
                }
            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

    private void setListAdapter()
        {
            adapterDevices = new ListBTUrbandDevices(this, R.layout.activity_search_device_row, arrayDevices);
            listDevices.setAdapter(adapterDevices);
        }

    private void setBluetooth()
        {
            /* Verify that Android device has Bluetooth LE feature*/
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
                {
                    finish();
                }
            /* Check if Bluetooth is enabled (ON)*/
            if (!bluetoothAdapter.isEnabled())
                {
                    /* Start system activity for request to turn on the bluetooth */
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            scanLeDevice(true);
        }

    private void scanLeDevice(final boolean enable)
        {
            if (enable)
                {
                    searchingBT = new Runnable()
                        {
                            @Override
                            public void run()
                                {
                                    scanning = false;
                                    bluetoothAdapter.stopLeScan(scanDevices);
                                    try
                                        {
                                            show.dismiss();
                                        }
                                    catch (Exception e)
                                        {
                                            Log.w("Home: ", "dialog not starting...");
                                        }
                                    invalidateOptionsMenu();
                                }
                        };
                    handler.postDelayed(searchingBT, SCAN_PERIOD);
                    scanning = true;
                    bluetoothAdapter.startLeScan(scanDevices);
                }
            else
                {
                    scanning = false;
                    bluetoothAdapter.stopLeScan(scanDevices);
                    try
                        {
                            show.dismiss();
                        }
                    catch (Exception e)
                        {
                            Log.w("Home: ", "dialog not starting...");
                        }
                }
            invalidateOptionsMenu();
        }

    private void showAlert()
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View alertView = inflater.inflate(R.layout.alert_search_device, null);
            alertDialog.setView(alertView);
            alertDialog.setCancelable(true);
            show = alertDialog.show();
        }

    private BluetoothAdapter.LeScanCallback scanDevices = new BluetoothAdapter.LeScanCallback()
        {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
                {
                    runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                                {
                                    if (!arrayBT.contains(device))
                                        {
                                            if (device.getName() != null)
                                                {
                                                    if ((device.getName()).contains("Urband"))
                                                        {
                                                            arrayDevices.add(new BluetoothUrbandDeviceItem(device, device.getName(), device.getAddress()));
                                                        }
                                                }
                                            arrayBT.add(device);
                                        }
                                    adapterDevices.notifyDataSetChanged();
                                }
                        });
                }
        };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
        {
            getMenuInflater().inflate(R.menu.menu_scan, menu);
            if (!scanning)
                {
                    menu.findItem(R.id.menu_stop).setVisible(false);
                    menu.findItem(R.id.menu_scan).setVisible(true);
                }
            else
                {
                    menu.findItem(R.id.menu_stop).setVisible(true);
                    menu.findItem(R.id.menu_scan).setVisible(false);
                }

            return true;
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
        {
            switch (item.getItemId())
                {
                    case R.id.menu_scan:
                        arrayDevices.clear();
                        arrayBT.clear();
                        scanLeDevice(true);
                        showAlert();
                        break;
                    case R.id.menu_stop:
                        scanLeDevice(false);
                        break;
                }
            return super.onOptionsItemSelected(item);
        }
}