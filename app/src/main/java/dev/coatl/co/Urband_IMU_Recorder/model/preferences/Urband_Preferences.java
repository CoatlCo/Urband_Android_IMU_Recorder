/**
 * Filename:        Urband_Preferences.java
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

package dev.coatl.co.Urband_IMU_Recorder.model.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class Urband_Preferences {
	
	//Configs
	private static String NAME = "Urband";
	public static String ADDRESS = "address";
	public static String USER_NUMBER = "user_num";
	public static String ACTIVITY_NUMBER = "activity_num";
	public static String GESTURE_NUMBER = "gesture_num";
	public static String isRecordingSession = "is_recording_session";

	public static void setLastDevice(Context context, String address){
		SharedPreferences sharedPref = context.getSharedPreferences("Urband", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(ADDRESS, address);
		editor.commit();
	}
	
	public static String getLastDevice(Context context){
		SharedPreferences sharedPref = context.getSharedPreferences("Urband", Context.MODE_PRIVATE);
		return sharedPref.getString(ADDRESS, "nodevice");
	}
	
	public static void setConfig(Context context, String key, boolean value){
		SharedPreferences sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static boolean getConfig(Context context, String key){
		SharedPreferences sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return sharedPref.getBoolean(key, false);
	}

	public static void setActualUser(Context context, String key, Integer value){
		SharedPreferences sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public static Integer getActualUser(Context context, String key){
		SharedPreferences sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return sharedPref.getInt(key, 1);
	}

	public static void setActualSession(Context context, String key, Integer value){
		SharedPreferences sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public static Integer getActualSession(Context context, String key){
		SharedPreferences sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return sharedPref.getInt(key, 1);
	}

	public static void setColor(Context context, String key, int value){
		SharedPreferences sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(key+"_color", value);
		editor.commit();
	}
	
	public static int getColor(Context context, String key){
		SharedPreferences sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return sharedPref.getInt(key+"_color", Color.parseColor("#A8B555"));
	}
	
	public static class Constants{
		public static String appId = "NA";
		public static String SLDeviceID = "908734IUHEF";
		public static String deviceName = "Urband";
	}
}
