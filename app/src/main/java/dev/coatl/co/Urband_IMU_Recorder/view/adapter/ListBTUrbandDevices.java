/**
 * Filename:        ListBTUrbandDevices.java
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
package dev.coatl.co.Urband_IMU_Recorder.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dev.coatl.co.Urband_IMU_Recorder.model.objects.BluetoothUrbandDeviceItem;
import dev.coatl.co.Urband_IMU_Recorder.R;

public class ListBTUrbandDevices extends ArrayAdapter<BluetoothUrbandDeviceItem> {

    private Context context;
    private int resource;
    private ArrayList<BluetoothUrbandDeviceItem> data;
    private Holder holder;

    public ListBTUrbandDevices(Context context, int resource, ArrayList<BluetoothUrbandDeviceItem> data) {
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(resource, parent, false);

            holder = new Holder();
            holder.deviceName = (TextView) v.findViewById(R.id.urband_device_row_text_name);
            holder.deviceAddress = (TextView) v.findViewById(R.id.urband_device_row_text_address);

            v.setTag(holder);
        }
        else{
            holder = (Holder) v.getTag();
        }

        BluetoothUrbandDeviceItem item = data.get(position);
        if(item.getDeviceName() == null){
            holder.deviceName.setText("Dispositivo Desconocido");
        }
        else{
            holder.deviceName.setText(item.getDeviceName());
        }
        holder.deviceAddress.setText(item.getDeviceAddress());

        return v;
    }

    public static class Holder{
        TextView deviceName;
        TextView deviceAddress;
        TextView title;
    }
}
