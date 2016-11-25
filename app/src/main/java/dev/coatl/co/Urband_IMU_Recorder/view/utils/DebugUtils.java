/**
 * Filename:        DebugUtils.java
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

import android.util.Log;

public class DebugUtils {

	private static final boolean DEBUG = true;
	private static final boolean INFO = true;
	private static final boolean ERROR = true;

	private static final boolean LOG_TO_FILE = false;

	public static void logInfo(String texto) {
		if (INFO)
			Log.i("DebugUtils", String.valueOf(texto));
	}

	public static void logInfo(String method, String texto) {
		if (INFO)
			Log.i(String.valueOf(method), String.valueOf(texto));
	}
	
	public static void logInfo(String clase, String method, String texto) {
		if (INFO)
			Log.i(clase+" :: "+String.valueOf(method), String.valueOf(texto));
	}
	
	public static void logError(String texto) {
		if (ERROR)
			Log.e("DebugUtils", String.valueOf(texto));
	}

	public static void logError(String method, String texto) {
		if (ERROR)
			Log.e(String.valueOf(method), String.valueOf(texto));
	}
	
	public static void logError(String clase, String method, String texto) {
		if (ERROR)
			Log.e(clase+" :: "+String.valueOf(method), String.valueOf(texto));
	}
	
	public static void logDebug(String texto) {
		if (DEBUG)
			Log.d("DebugUtils", String.valueOf(texto));
	}

	public static void logDebug(String method, String texto) {
		if (DEBUG)
			Log.d(String.valueOf(method), String.valueOf(texto));
	}
	
	public static void logDebug(String clase, String method, String texto) {
		if (DEBUG)
			Log.d(clase+" :: "+String.valueOf(method), String.valueOf(texto));
	}
	
	public static void logThrowable(Throwable e) {
		if (DEBUG) {
			Log.e("LogUtil", e.toString());
			e.printStackTrace();
			if (LOG_TO_FILE) {
				logToFile(e);
			}
		}
	}

	public static void logToFile(Throwable e) {
		try {
			StringBuffer sb = new StringBuffer(e.toString() + "\n");
			StackTraceElement[] stElements = e.getStackTrace();
			String newLine = "";

			for (StackTraceElement stElement : stElements) {
				sb.append(newLine);
				sb.append("\tat ");
				sb.append(stElement.toString());
				newLine = "\n";
			}
		} catch (Exception ee) {
			e.printStackTrace();
		}
	}
	
}