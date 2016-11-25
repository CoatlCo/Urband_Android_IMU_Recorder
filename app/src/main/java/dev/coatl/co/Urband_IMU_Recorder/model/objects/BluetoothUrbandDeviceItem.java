/**
 * Filename:        BluetoothUrbandDeviceItem.java
 * Created:         Thu, 11 Aug 2016
 * Author:          Erick Rivera
 * Description:
 * Revisions:       Thu, 11 Aug 2016 by Erick Rivera
 * 					Mon, 14 Nov 2016 by Erick Rivera
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

package dev.coatl.co.Urband_IMU_Recorder.model.objects;
import android.bluetooth.BluetoothDevice;
public class BluetoothUrbandDeviceItem {

    BluetoothDevice device;
    String deviceName;
    String deviceAddress;

    public BluetoothUrbandDeviceItem(BluetoothDevice device, String deviceName, String deviceAddress) {
        this.device = device;
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
    }

    public BluetoothDevice getDevice() {
        return device;
    }
    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }
    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }
}
