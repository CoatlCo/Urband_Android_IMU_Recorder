/**
 * Filename:        BluetoothService.java
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
package dev.coatl.co.Urband_IMU_Recorder.presenter.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import dev.coatl.co.Urband_IMU_Recorder.model.objects.BluetoothGattAttributes;
import dev.coatl.co.Urband_IMU_Recorder.model.preferences.Urband_Preferences;
import dev.coatl.co.Urband_IMU_Recorder.presenter.activities.ActivityHomePanel;
import dev.coatl.co.Urband_IMU_Recorder.presenter.fragments.FragmentMenuPanel;

public class BluetoothService extends Service
    {
        private final static String TAG = BluetoothService.class.getSimpleName();
        private BluetoothManager mBluetoothManager;
        private static BluetoothAdapter mBluetoothAdapter;
        private String mBluetoothDeviceAddress;
        public static BluetoothGatt mBluetoothGatt;

        public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
        public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
        public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
        public final static String ACTION_MTU_CHANGED = "ACTION_MTU_CHANGED";

        public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
        public final static String ACTION_DATA_BATTERY = "ACTION_DATA_BATTERY";
        public final static String EXTRA_DATA = "EXTRA_DATA";

        static ArrayList<String> listServiceBT = null;
        static ArrayList<String> listCharacteristicBT = null;
        static ArrayList<Integer> listBytesBT = null;

        public static boolean enabledSuscription = false;
        private Context context;
        private Handler handler;

        public static String deviceAddress;
        public static boolean statusWriteList = false;
        public static int flagWriteList = 0;
        public static boolean statusBluetooth = false;

        public static int contador = 0;

        public static String STATUS_URBAND_DEFAULT = "urbandDefault";
        public static String STATUS_URBAND_CONNECT = "urbandConnect";
        public static String STATUS_URBAND_DISCONNECT = "urbandDisconnect";
        public static String statusUrband = STATUS_URBAND_DEFAULT;


        @Override
        public void onCreate()
            {
                Log.i(TAG, "on onCreate()");
                super.onCreate();
                context = getBaseContext();
                handler = new Handler();
            }

        // Implements callback methods for GATT events that the app cares about. For
        // example,
        // connection change and services discovered.
        private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
            {
                @Override
                public void onConnectionStateChange(android.bluetooth.BluetoothGatt gatt, int status, int newState)
                    {
                        Log.i(TAG, "On onConnectionStateChange(...,...,...)");
                        String intentAction;
                        if (newState == BluetoothProfile.STATE_CONNECTED)
                            {
                                gatt.requestMtu(98);
                                intentAction = ACTION_GATT_CONNECTED;
                                broadcastUpdate(intentAction);
                                Log.i(TAG, "Connected to GATT server.");

                            }
                        else if (newState == BluetoothProfile.STATE_DISCONNECTED)
                            {
                                intentAction = ACTION_GATT_DISCONNECTED;
                                Log.i(TAG, "Disconnected from GATT server.");
                                broadcastUpdate(intentAction);
                                contador = 0;
                                handler.post(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                            {
                                                final FragmentMenuPanel fg_menu = ((FragmentMenuPanel) ActivityHomePanel.getActivityHomePanel().getSupportFragmentManager().findFragmentByTag("fg_menu"));
                                                fg_menu.button_recording.setVisibility(View.INVISIBLE);
                                                /* Disable recordNow flag */
                                                FragmentMenuPanel.recordNow = false;

                                                /* Vibrates to notify the user */
                                                Vibrator vib = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                                vib.vibrate(900);

                                                /* Writes last data and closes the file */
                                                try
                                                    {
                                                        if(FragmentMenuPanel.miFile != null)
                                                            {
                                                                FragmentMenuPanel.miFile.flush();
                                                                FragmentMenuPanel.miFile.close();
                                                            }
                                                    }
                                                catch (IOException e)
                                                    {
                                                        e.printStackTrace();
                                                    }
                                                Urband_Preferences.setConfig(fg_menu.mActivity, Urband_Preferences.isRecordingSession, false);
                                            }
                                    });
                            }
                    }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
                    {
                        Log.i(TAG, "Enter into onCharacteristicWrite");
                        super.onCharacteristicWrite(gatt, characteristic, status);
//                        listenerWriteFirstConnect.onWriteFirstConnect();

                        if(statusWriteList)
                            {
                                if(flagWriteList < listServiceBT.size())
                                    {
                                        WriteAsyncTask writeOnList = new WriteAsyncTask(new OnWriteCompleted()
                                            {
                                                @Override
                                                public void processFinish()
                                                    {
                                                        Log.i(TAG, "writeOnList -> processFinish()");
                                                        flagWriteList++;
                                                    }
                                            },
                                                listServiceBT.get(flagWriteList),
                                                listCharacteristicBT.get(flagWriteList),
                                                listBytesBT.get(flagWriteList)
                                        );
                                        writeOnList.execute();
                                    }
                            }
                    }

                @Override
                public void onServicesDiscovered(android.bluetooth.BluetoothGatt gatt, int status)
                    {
                        Log.i(TAG, "Enter into onServicesDiscovered");
                        if (status == android.bluetooth.BluetoothGatt.GATT_SUCCESS)
                            {
                                Log.i(TAG, "onServicesDiscovered SUCCESS");
                                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                            }
                        else
                            {
                                Log.i(TAG, "onServicesDiscovered received status = " + status);
                            }
                        //getFIFO();
                        //Log.i(TAG, "getFIFO() called");
                    }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
                    {
                        Log.i(TAG, "Enter into onCharacteristicRead with status: " + status);
                        if (status == android.bluetooth.BluetoothGatt.GATT_SUCCESS)
                            {
                                Log.i(TAG, "onCharacteristicRead SUCCESS");
                                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                            }
                    }


                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
                    {
                        // Log.i(TAG, "Enter into onCharacteristicChanged ACTION_DATA_AVAILABLE");
                        enabledSuscription = true;
                        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    }

                @Override
                public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                    super.onMtuChanged(gatt, mtu, status);
                    Log.i(TAG, "This happens in response to requestMtu() function: " + Integer.toString(status) + ", mtu = " + Integer.toString(mtu));

                    // Attempts to discover services after successful connection.
                    if(mBluetoothGatt.discoverServices())
                    {
                        Log.i(TAG, "Service discovery resulted TRUE");
                    }
                    else
                    {
                        Log.i(TAG, "Service discovery resulted FALSE");
                    }

                    //BluetoothGattService serviceGatt = BluetoothService.mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.GESTURE_SERVICE));
                   // BluetoothGattCharacteristic dataChar = serviceGatt.getCharacteristic(UUID.fromString(BluetoothGattAttributes.GESTURE_DEBUG_CHAR));

                    //BluetoothUtils.bluetoothService.readCharacteristic(dataChar);
                    //setCharacteristicNotification(dataChar, true);
                    enabledSuscription = true;
                    broadcastUpdate(ACTION_MTU_CHANGED);


                }
            };

        private void broadcastUpdate(final String action)
            {
                // Log.i(TAG, "Enter into broadcastUpdate (one param)");
                final Intent intent = new Intent(action);
                sendBroadcast(intent);
            }

        private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic)
            {
                //Log.i(TAG, "Enter into broadcastUpdate (2 param)");
                final Intent intent = new Intent(action);
                StringBuilder stringBuilder = null;

                if (UUID.fromString(BluetoothGattAttributes.GESTURE_DEBUG_CHAR09).equals(characteristic.getUuid()))
                    {
                        Log.i(TAG, "Enter into broadcastUpdate (2 param)------ no UUIID: " + characteristic.getUuid().toString());
                        final byte[] data = characteristic.getValue();
                        //Log.i(TAG, "-------------------------------------------------------------data length: " + Integer.toString(data.length));
                        if (data != null && data.length > 0)
                        {
                            stringBuilder = new StringBuilder(data.length);
                            for (byte byteChar : data)
                            {
                                stringBuilder.append(String.format("%02X", byteChar));
                            }
                            intent.putExtra(EXTRA_DATA, new String(data));
                            // Log.i(TAG, stringBuilder.toString() + " UUID: " + characteristic.getUuid().toString());
                            contador++;
                            if(contador >= 10)
                            {
                                handler.post(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        final FragmentMenuPanel fg_menu = ((FragmentMenuPanel) ActivityHomePanel.getActivityHomePanel().getSupportFragmentManager().findFragmentByTag("fg_menu"));
                                        fg_menu.button_recording.setVisibility(View.VISIBLE);
                                    }
                                });
                            }

                            /* Determina si los datos se graban */
                            if(Urband_Preferences.getConfig(context, Urband_Preferences.isRecordingSession) && FragmentMenuPanel.recordNow)
                            {
                                try
                                {
                                    String stampedData = stringBuilder.toString() + "\r\n";
                                    FragmentMenuPanel.writeDataToFile(stampedData,0);
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                sendBroadcast(intent);
                //writeCharacteristic(characteristic, 0x00);
            }

        private void showToast(final String msg)
            {
                new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                            {
                                handler.post(new Runnable()
                                             {
                                                 @Override
                                                 public void run()
                                                    {
                                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                                    }
                                             }
                                );
                            }
                    }
                ).start();
            }


        public class LocalBinder extends Binder
            {
                public BluetoothService getService()
                    {
                        Log.i(TAG, "On getService()");
                        return BluetoothService.this;
                    }
            }

        @Override
        public IBinder onBind(Intent intent)
            {
                Log.i(TAG, "On onBind(Intent intent)");
                return mBinder;
            }

        @Override
        public boolean onUnbind(Intent intent)
            {
                // After using a given device, you should make sure that
                // BluetoothGatt.close() is called
                // such that resources are cleaned up properly. In this particular
                // example, close() is
                // invoked when the UI is disconnected from the Service.
                Log.i(TAG, "On onUnbind()");
                close();
                return super.onUnbind(intent);
            }

        private final IBinder mBinder = new LocalBinder();

        /**
         * Initializes a reference to the local Bluetooth adapter.
         *
         * @return Return true if the initialization is successful.
         */
        public boolean initialize()
            {
                // For API level 18 and above, get a reference to BluetoothAdapter
                // through BluetoothManager.
                Log.i(TAG, "On initialize()");
                if (mBluetoothManager == null)
                    {
                        Log.i(TAG, "BluetoothManager == null");
                        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                        if (mBluetoothManager == null)
                            {
                                Log.e(TAG, "Unable to initialize BluetoothManager.");
                                return false;
                            }
                    }
                mBluetoothAdapter = mBluetoothManager.getAdapter();
                if (mBluetoothAdapter == null)
                    {
                        Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
                        return false;
                    }
                return true;
            }

        /**
         * Connects to the GATT server hosted on the Bluetooth LE device.
         *
         * @param address
         *            The device address of the destination device.
         *
         * @return Return true if the connection is initiated successfully. The
         *         connection result is reported asynchronously through the
         *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
         *         callback.
         */
        public boolean connect(final String address)
            {
                Log.i(TAG, "On connect(final String address)");
                statusBluetooth = true;
                if (mBluetoothAdapter == null || address == null)
                    {
                        Log.i(TAG, "BluetoothAdapter not initialized or unspecified address.");
                        return false;
                    }
                // Previously connected device. Try to reconnect.
                if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null)
                    {
                        Log.i(TAG, "Trying to use an existing mBluetoothGatt for connection.");
                        if (mBluetoothGatt.connect())
                            {
                                return true;
                            }
                        else
                            {
                                return false;
                            }
                    }
                final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                if (device == null)
                    {
                        Log.i(TAG, "Device not found.  Unable to connect.");
                        return false;
                    }
                // We want to directly connect to the device, so we are setting the
                // autoConnect
                // parameter to false.
                mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
                Log.i(TAG, "Trying to create a new connection.");
                mBluetoothDeviceAddress = address;
                return true;
            }

        /**
         * Disconnects an existing connection or cancel a pending connection. The
         * disconnection result is reported asynchronously through the
         * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
         * callback.
         */
        public void disconnect()
            {
                Log.i(TAG, "On disconnect()");
                statusBluetooth = false;
                if (mBluetoothAdapter == null || mBluetoothGatt == null)
                    {
                        Log.i(TAG, "BluetoothAdapter not initialized");
                        return;
                    }
                mBluetoothGatt.disconnect();
            }

        /**
         * After using a given BLE device, the app must call this method to ensure
         * resources are released properly.
         */
        public void close()
            {
                Log.i(TAG, "On close()");
                if (mBluetoothGatt == null)
                    {
                        return;
                    }
                mBluetoothGatt.close();
                mBluetoothGatt = null;
            }

        /**
         * Request a read on a given {@code BluetoothGattCharacteristic}. The read
         * result is reported asynchronously through the
         * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
         * callback.
         *
         * @param characteristic
         *            The characteristic to read from.
         */

        public void readCharacteristic(BluetoothGattCharacteristic characteristic)
            {
                Log.i(TAG, "On readCharacteristic(BluetoothGattCharacteristic characteristic)");
                if (mBluetoothAdapter == null || mBluetoothGatt == null)
                    {
                        Log.w(TAG, "BluetoothAdapter not initialized");
                        return;
                    }
                mBluetoothGatt.readCharacteristic(characteristic);
            }

        /**
         * Enables or disables notification on a give characteristic.
         *
         * @param characteristic
         *            Characteristic to act on.
         * @param enabled
         *            If true, enable notification. False otherwise.
         */

        public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled)
            {
                Log.i(TAG, "On setCharacteristicNotification()");
                if (mBluetoothAdapter == null || mBluetoothGatt == null)
                    {
                        Log.w(TAG, "BluetoothAdapter not initialized");
                        return;
                    }
                mBluetoothGatt.setCharacteristicNotification(characteristic, false);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(BluetoothGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue((enabled) ? BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE:BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
                mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
/*
                UUID uuid = UUID.fromString(BluetoothGattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuid);
                if(enabled)
                    {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    }
                else
                    {
                        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                    }
                mBluetoothGatt.writeDescriptor(descriptor);
*/
            }

        public void setCharacteristicIndication(BluetoothGattCharacteristic characteristic, boolean enabled)
            {
                Log.i(TAG, "On setCharacteristicIndication()");
                if (mBluetoothAdapter == null || mBluetoothGatt == null)
                    {
                        Log.w(TAG, "BluetoothAdapter not initialized");
                        return;
                    }
                Log.i(TAG, "setCharacteristicIndication");
                UUID uuid = UUID.fromString(BluetoothGattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(uuid);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
                mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            }

        /**
         * Retrieves a list of supported GATT services on the connected device. This
         * should be invoked only after {@code BluetoothGatt#discoverServices()}
         * completes successfully.
         *
         * @return A {@code List} of supported services.
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public List<BluetoothGattService> getSupportedGattServices()
            {
                if (mBluetoothGatt == null)
                    {
                        return null;
                    }
                return mBluetoothGatt.getServices();
            }

        public static void writeCharacteristic(BluetoothGattCharacteristic characteristic, int command)
            {
                if (mBluetoothAdapter == null || mBluetoothGatt == null)
                    {
                        Log.w(TAG, "BluetoothAdapter not initialized");
                        return;
                    }
                if (characteristic == null)
                    {
                        Log.w(TAG, "Send characteristic not found");
                    }
                final byte[] SEND = { (byte) command};
                characteristic.setValue(SEND);
                boolean status = mBluetoothGatt.writeCharacteristic(characteristic);
            }

        public static synchronized boolean writeCharacteristic(String service, String characteristicBT, int command)
            {
                Log.i(TAG, "Enter into synchronized boolean writeCharacteristic");
                BluetoothGattService serviceGatt = BluetoothService.mBluetoothGatt.getService(UUID.fromString(service));
                BluetoothGattCharacteristic characteristic = serviceGatt.getCharacteristic(UUID.fromString(characteristicBT));
                if (mBluetoothAdapter == null || mBluetoothGatt == null)
                    {
                        Log.w(TAG, "BluetoothAdapter not initialized");
                        return false;
                    }
                if (characteristic == null)
                    {
                        Log.w(TAG, "Send characteristic not found");
                        return false;
                    }
                final byte[] SEND = { (byte) command};
                characteristic.setValue(SEND);
                boolean status = mBluetoothGatt.writeCharacteristic(characteristic);
                Log.d(TAG, "On synchronized boolean writeCharacteristic status = " + status);
                return status;
            }

        public static synchronized void writeCharacteristicList(String[] listService, String[] listCharacteristic, Integer[] listBytes)
            {
                Log.i(TAG, "Enter into synchronized void writeCharacteristicList");
                statusWriteList = true;
                if(listServiceBT != null)
                    {
                        Log.i(TAG, "listServiceBT not equal NULL, so clear arrays. listServiceBT.size() = " + listServiceBT.size() + ", listService.length = " + listService.length);
                        flagWriteList = 0;
                        listServiceBT.clear();
                        listCharacteristicBT.clear();
                        listBytesBT.clear();
                    }
                listServiceBT = new ArrayList( Arrays.asList(listService));
                listCharacteristicBT = new ArrayList( Arrays.asList(listCharacteristic));
                listBytesBT = new ArrayList( Arrays.asList(listBytes));
                WriteAsyncTask writeOnList = new WriteAsyncTask(new OnWriteCompleted()
                    {
                        @Override
                        public void processFinish()
                            {
                                Log.i(TAG, "synchronized void writeCharacteristicList --> writeOnList --> processFinish()");
                                flagWriteList++;
                            }
                    },
                        listService[0],
                        listCharacteristic[0],
                        listBytes[0]
                );
                writeOnList.execute();
            }

        public interface OnWriteCompleted
            {
                void processFinish();
            }

        private static class WriteAsyncTask extends AsyncTask<String, Void, String>
            {
                public OnWriteCompleted listener = null;
                public String service;
                public String characteristic;
                public int byteSend;
                public WriteAsyncTask(OnWriteCompleted listener, String service, String characteristic, int byteSend)
                    {
                        this.listener = listener;
                        this.service = service;
                        this.characteristic = characteristic;
                        this.byteSend = byteSend;
                    }

                @Override
                protected String doInBackground(String... params)
                    {
                        return null;
                    }

                @Override
                protected void onPostExecute(String result)
                    {
                        try
                            {
                                writeCharacteristic(service, characteristic, byteSend);
                                listener.processFinish();
                            }
                        catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                    }
            }

        public void getBattery()
            {
                if (mBluetoothGatt == null)
                    {
                        Log.e(TAG, "lost connection");
                    }
                BluetoothGattService batteryService = mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.BATTERY_SERVICE));
                if(batteryService == null)
                    {
                        Log.i(TAG, "Battery service not found!");
                        return;
                    }
                BluetoothGattCharacteristic batteryLevel = batteryService.getCharacteristic(UUID.fromString(BluetoothGattAttributes.BATTERY_CHARACTERISTIC_CHARGE));
                if(batteryLevel == null)
                    {
                        Log.i(TAG, "Battery level not found!");
                        return;
                    }
                mBluetoothGatt.readCharacteristic(batteryLevel);
                mBluetoothGatt.setCharacteristicNotification(batteryLevel, true);
            }

        public void getFIFO()
        {
            if (mBluetoothGatt == null)
            {
                Log.e(TAG, "lost connection");
            }
            BluetoothGattService batteryService = mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.GESTURE_SERVICE));
            if(batteryService == null)
            {
                Log.i(TAG, "fifo service not found!");
                return;
            }
            BluetoothGattCharacteristic FIFO = batteryService.getCharacteristic(UUID.fromString(BluetoothGattAttributes.GESTURE_DEBUG_CHAR09));
            if(FIFO == null)
            {
                Log.i(TAG, "fifo level not found!");
                return;
            }
            mBluetoothGatt.readCharacteristic(FIFO);
            mBluetoothGatt.setCharacteristicNotification(FIFO, true);
        }
    }
