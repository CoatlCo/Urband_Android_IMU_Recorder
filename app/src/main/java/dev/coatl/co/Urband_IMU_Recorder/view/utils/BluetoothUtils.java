/**
 * Filename:        BluetoothUtils.java
 * Created:         Thu, 11 Aug 2016
 * Author:          Erick Rivera
 * Description:
 *
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
package dev.coatl.co.Urband_IMU_Recorder.view.utils;

import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import dev.coatl.co.Urband_IMU_Recorder.presenter.services.BluetoothService;
import dev.coatl.co.Urband_IMU_Recorder.model.objects.BluetoothGattAttributes;
import dev.coatl.co.Urband_IMU_Recorder.presenter.activities.ActivityHomePanel;

public class BluetoothUtils  {

    private final static String TAG = BluetoothUtils.class.getSimpleName();

    private static Context context;
    private static boolean connected = false;
    private static ActivityHomePanel home;
    public static BluetoothService bluetoothService;
    private static Runnable run_batteryService;

    private static ArrayList<ArrayList<BluetoothGattCharacteristic>> gattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private static BluetoothGattCharacteristic mNotifyCharacteristic;
    private static BluetoothGattCharacteristic mNotifyCharacteristicDATA_acc;
    private static BluetoothGattCharacteristic mNotifyCharacteristicDATA_gyro;

    public BluetoothUtils(Context context)
        {
            Log.i(TAG, "On BluetoothUtils(Context context)");
            this.context = context;
            home = new ActivityHomePanel();
        }

    public static ServiceConnection serviceConnection = new ServiceConnection()
        {
            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service)
                {
                    Log.i(TAG, "On onServiceConnected()");
                    bluetoothService = ((BluetoothService.LocalBinder) service).getService();
                    if (!bluetoothService.initialize())
                        {
                            Log.i(TAG, "Unable to initialize Bluetooth");
                            ((ActivityHomePanel) context).finish();
                        }
                    bluetoothService.connect(BluetoothService.deviceAddress);
                }

            @Override
            public void onServiceDisconnected(ComponentName componentName)
                {
                    Log.i(TAG, "On onServiceDisconnected()");
                    bluetoothService = null;
                }
        };

    public ServiceConnection getServiceConnection()
        {
            Log.i(TAG, "On getServiceConnection()");
            return serviceConnection;
        }

    public static final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
                {
                    final String action = intent.getAction();
                    if (BluetoothService.ACTION_GATT_CONNECTED.equals(action))
                        {
                            connected = true;
                            BluetoothService.statusUrband = BluetoothService.STATUS_URBAND_CONNECT;
                            Log.i(TAG, "ACTION_GATT_CONNECTED --> status of Urband: " + BluetoothService.statusUrband);
                        }
                    else if (BluetoothService.ACTION_GATT_DISCONNECTED.equals(action))
                        {
                            connected = false;
                            BluetoothService.statusUrband = BluetoothService.STATUS_URBAND_DISCONNECT;
                            ((ActivityHomePanel) context).invalidateOptionsMenu();
                            Log.i(TAG, "ACTION_GATT_DISCONNECTED --> status of Urband: " + BluetoothService.statusUrband);
                        }
                    else if (BluetoothService.ACTION_MTU_CHANGED.equals(action))
                    {
                        Log.i(TAG, "ACTION_MTU_CHANGED ---------");

                    }
                    else if (BluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
                        {
                            Log.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED ---------");
                            enabledDATA_IMU_FIFO();
//                            if(!BluetoothService.enabledSuscription)
//                                {
                                    //enabledDATA_acc();
                                    //Log.i(TAG, "enabledDATA_acc --- complete");

/////////////////////////////////////////////////////////////

/*
                            BluetoothGattService dataService = BluetoothService.mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.GESTURE_SERVICE));
                            if(dataService == null)
                            {
                                Log.i(TAG, "GESTURE_SERVICE not found!");
                                return;
                            }

                            BluetoothGattCharacteristic dataChar = dataService.getCharacteristic(UUID.fromString(BluetoothGattAttributes.DEV1_CHARACTERISTIC));
                            if(dataChar == null)
                            {
                                Log.i(TAG, "DEV1_CHARACTERISTIC not found!");
                                return;
                            }

                            //bluetoothService.readCharacteristic(dataChar);
*/






         /////////////////////////////////////////////////////////////
//                                    try
//                                        {
//                                            Log.i(TAG, "SLEEPING....");
//                                            Thread.sleep(900);
//                                        }
//                                    catch (InterruptedException e)
//                                        {
//                                            e.printStackTrace();
//                                        }
//                                    enabledDATA_gyro();
//                                    Log.i(TAG, "enabledDATA_gyro --- complete");
//                                }
//                            else
//                                {
//                                    Log.w(TAG, "Suscription was enabled");
//                                }
                        }
                    else if (BluetoothService.ACTION_DATA_AVAILABLE.equals(action))
                        {
                            //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                            //Log.i(TAG, "ACTION_DATA_AVAILABLE, length: " + intent.getStringExtra(BluetoothService.EXTRA_DATA).length());
                            //Log.i(TAG, "ACTION_DATA_AVAILABLE, data: " + intent.getStringExtra(BluetoothService.EXTRA_DATA));
                        }
                    else if (BluetoothService.ACTION_DATA_BATTERY.equals(action))
                        {
                            Log.i(TAG, "ACTION_DATA_BATTERY: " + intent.getStringExtra(BluetoothService.EXTRA_DATA));
                            ActivityHomePanel.battery_percentage = intent.getStringExtra(BluetoothService.EXTRA_DATA);
                            ((ActivityHomePanel) context).invalidateOptionsMenu();
                        }
                    else if(BluetoothDevice.ACTION_UUID.equals(action))
                        {
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                            if (uuidExtra != null)
                                {
                                    for (Parcelable uuid : uuidExtra)
                                        {
                                            Log.i(TAG, "Device: " + device.getName() + " ( " + device.getAddress() + " ) - Service: " + uuid.toString());
                                        }
                                }
                        }
                }
        };


    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothService.ACTION_MTU_CHANGED);
        intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothService.ACTION_DATA_BATTERY);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }

//    public static void enabledBT()
//    {
//        Log.i(TAG, "Enter into enableBT() function");
//        try
//            {
//                BluetoothGattService serviceGatt = BluetoothService.mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.GESTURE_SERVICE));
//                BluetoothGattCharacteristic characteristic = serviceGatt.getCharacteristic(UUID.fromString(BluetoothGattAttributes.GESTURE_DEBUG_CHAR));
//                if (gattCharacteristics != null)
//                    {
//                        final int charaProp = characteristic.getProperties();
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0)
//                            {
//                                Log.i(TAG, "Characteristic can be read");
//                                if (mNotifyCharacteristicDATA_acc != null)
//                                    {
//                                        Log.i(TAG, "Reset notification subscription");
//                                        bluetoothService.setCharacteristicIndication(characteristic, false);
//                                        mNotifyCharacteristicDATA_acc = null;
//                                    }
//                                bluetoothService.readCharacteristic(characteristic);
//                            }
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)
//                            {
//                                Log.i(TAG, "Characteristic is notifiable");
//                                mNotifyCharacteristicDATA_acc = characteristic;
//                                Log.i(TAG, "Set notification subscription");
//                                bluetoothService.setCharacteristicNotification(mNotifyCharacteristicDATA_acc, true);
//                                Log.i(TAG, "Notification subscription was set");
//                                boolean btService = false;
////                                do
////                                    {
////                                        BluetoothGattService serviceGattHaptics = BluetoothService.mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.GESTURE_LED_SERVICE));
////                                        characteristic = serviceGattHaptics.getCharacteristic(UUID.fromString(BluetoothGattAttributes.GESTURE_LED_CHARAC_HAPTICS));
////                                        final byte[] SEND = { (byte) 0x02};
////                                        characteristic.setValue(SEND);
////                                        btService = BluetoothService.mBluetoothGatt.writeCharacteristic(characteristic);
////                                        Log.w(TAG,"not writing");
////                                    } while (!btService);
//                            }
//                    }
//                else
//                    {
//                        Log.i(TAG, "***debug: charac empty");
//                    }
//            }
//        catch (Exception e)
//            {
//                e.printStackTrace();
//                Log.e(TAG, " ..... error on gattCharacteristics");
//            }
//    }

    public static void enabledDATA_IMU_FIFO()
    {
        Log.i(TAG, "--- enabledDATA_IMU_FIFO()");
        if (BluetoothService.mBluetoothGatt == null)
        {
            Log.e(TAG, "lost connection IMU");
        }

        BluetoothGattService dataService = BluetoothService.mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.GESTURE_SERVICE));
        if(dataService == null)
        {
            Log.i(TAG, "DATA IMU service not found!");
            return;
        }

        BluetoothGattCharacteristic dataChar = dataService.getCharacteristic(UUID.fromString(BluetoothGattAttributes.GESTURE_DEBUG_CHAR09));
        if(dataChar == null)
        {
            Log.i(TAG, "DATA IMU characteristic not found!");
            return;
        }

        try
        {
            final int charaProp = dataChar.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0)
            {
                Log.i(TAG, "***debug: read DATA IMU");
                bluetoothService.setCharacteristicNotification(dataChar, false);
                if (mNotifyCharacteristic != null)
                {
                    Log.i(TAG, "***debug: clean DATA IMU");
                    bluetoothService.setCharacteristicNotification(dataChar, false);
                    mNotifyCharacteristic = null;
                }
                bluetoothService.readCharacteristic(dataChar);
            }

            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)
            {
                Log.i(TAG, "***debug: notify DATA IMU");
                mNotifyCharacteristic = dataChar;
                bluetoothService.setCharacteristicNotification(mNotifyCharacteristic, true);
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, " ..... error on gattCharacteristics IMU");
        }
    }

    public static void enabledDATA_acc()
        {
            if (BluetoothService.mBluetoothGatt == null)
                {
                    Log.e(TAG, "lost connection");
                }

            BluetoothGattService dataService =
                    BluetoothService.mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.GESTURE_SERVICE));
            if(dataService == null)
                {
                    Log.i(TAG, "DATA acc service not found!");
                    return;
                }

            BluetoothGattCharacteristic dataChar =
                    dataService.getCharacteristic(UUID.fromString(BluetoothGattAttributes.GESTURE_DEBUG_CHAR09));
            if(dataChar == null)
                {
                    Log.i(TAG, "DATA acc characteristic not found!");
                    return;
                }

            try
                {
                    final int charaProp = dataChar.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0)
                        {
                            Log.i(TAG, "***debug: read DATA acc");
                            if (mNotifyCharacteristicDATA_acc != null)
                                {
                                    Log.i(TAG, "***debug: clean DATA acc");
//                                    bluetoothService.setCharacteristicNotification(dataChar, false);
                                    mNotifyCharacteristicDATA_acc = null;
                                }
                        }

                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)
                        {
                            Log.i(TAG, "***debug: notify DATA acc");
                            mNotifyCharacteristicDATA_acc = dataChar;
                            bluetoothService.setCharacteristicNotification(mNotifyCharacteristicDATA_acc, true);
                        }
                }
            catch (Exception e)
                {
                    e.printStackTrace();
                    Log.e(TAG, " ..... error on gattCharacteristics");
                }
        }



    public static void enabledDATA_gyro()
        {

            if (BluetoothService.mBluetoothGatt == null)
                {
                    Log.e(TAG, "lost connection");
                }

            BluetoothGattService dataService =
                    BluetoothService.mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.GESTURE_SERVICE));
            if(dataService == null)
                {
                    Log.i(TAG, "DATA gyro service not found!");
                    return;
                }

            BluetoothGattCharacteristic dataChar =
                    dataService.getCharacteristic(UUID.fromString(BluetoothGattAttributes.GESTURE_DEBUG_CHARA));
            if(dataChar == null)
                {
                    Log.i(TAG, "DATA gyro characteristic not found!");
                    return;
                }

            try
                {
                    final int charaProp = dataChar.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0)
                        {
                            Log.i(TAG, "***debug: read DATA gyro");
                            if (mNotifyCharacteristicDATA_gyro != null)
                                {
                                    Log.i(TAG, "***debug: clean DATA gyro");
//                                    bluetoothService.setCharacteristicNotification(dataChar, false);
                                    mNotifyCharacteristicDATA_gyro = null;
                                }
//                            bluetoothService.readCharacteristic(dataChar);
                        }

                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)
                        {
                            Log.i(TAG, "***debug: notify DATA gyro");
                            mNotifyCharacteristicDATA_gyro = dataChar;
                            bluetoothService.setCharacteristicNotification(mNotifyCharacteristicDATA_gyro, true);
                        }
                }
            catch (Exception e)
                {
                    e.printStackTrace();
                    Log.e(TAG, " ..... error on gattCharacteristics");
                }
        }




    @SuppressWarnings("deprecation")
    public static boolean isServiceRunning(String activityClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningTaskInfo task : manager.getRunningTasks(Integer.MAX_VALUE)) {
            if (activityClass.equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }
        return false;
    }
}
