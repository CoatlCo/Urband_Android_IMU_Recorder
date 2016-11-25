/**
 * Filename:        FragmentMenuPanel.java
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
package dev.coatl.co.Urband_IMU_Recorder.presenter.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import dev.coatl.co.Urband_IMU_Recorder.model.preferences.Urband_Preferences;
import dev.coatl.co.Urband_IMU_Recorder.presenter.services.BluetoothService;
import dev.coatl.co.Urband_IMU_Recorder.R;
import dev.coatl.co.Urband_IMU_Recorder.model.objects.BluetoothGattAttributes;
import dev.coatl.co.Urband_IMU_Recorder.presenter.activities.ActivityHomePanel;

public class FragmentMenuPanel extends Fragment implements OnClickListener{

	private final static String TAG = FragmentMenuPanel.class.getSimpleName();

	private View button_plus_actividad;
	private View button_minus_actividad;

	private View button_plus_user;
	private View button_minus_user;
	public View button_recording;

	
	public static Boolean recordNow = false;
	
	private String miActualUser;
	private String miActivitySession;
	private String miGestureSession;
	
	private Integer cuenta_actividades = 0;
	private Integer cuenta_usuarios = 0;
	
	private TextView Actividades;
	
	private TextView Usuarios;
	public  TextView sessionDescription;

	public Activity mActivity;

	static File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	public static File dir = new File (sdCard.getAbsolutePath() + "/DataSessions");
	public static File file = new File(dir, "tsxt.txt");
	public static FileWriter miFile = null;

	/*
 * ************************************************************************
 * Estas son las funciones para la lectura y escritura de datos en archivos
 * en la memoria externa (SD card)
 *
 * ************************************************************************
 */
	public static void writeDataToFile(String data, int sensor_sel) throws IOException
	{
		//System.out.println(data);
		Log.d("writeDataToFile", "Entro");
		try
		{
			if(sensor_sel==0)
			{
				miFile.write(data);
			}
			else
			{
				//miFile_gyro.write(data);
			}
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable()
	{
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable()
	{
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
		{
			return true;
		}
		return false;
	}



		public static String ActivitySessionsDescription[] = 
		{
			"Session A01: ",
			"Session A02: ",
			"Session A03: ", 
			"Session A04: ", 
			"Session A05: ", 
			"Session A06: ", 
			"Session A07: ", 
			"Session A08: ", 
			"Session A09: ", 
			"Session A10: ", 
			"Session A11: ",
			"Session A12: ", 
			"Session A14: ", 
			"Session A15: ", 
			"Session A16: ", 
			"Session A17: ",
			"Session A18: ", 
			"Session A19: ", 
			"Session A20: ", 
			"Session A21: ",
			"Session A22: ", 
			"Session A24: ", 
			"Session A25: ", 
			"Session A26: ", 
			"Session A27: ",
			"Session A28: ", 
			"Session A29: ", 
			"Session A30: ", 
			"Session A31: ", 
			"Session A34: ", 
			"Session A35: ", 
			"Session A36: ", 
			"Session A39: ", 
			"Session A40: ", 
		};
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/* Initialize layout */
		View rootView = inflater.inflate(R.layout.fragment_menu_panel, null);
		
		/* Initialize buttons */
		button_plus_actividad = rootView.findViewById(R.id.button_plus_actividad);
		button_minus_actividad = rootView.findViewById(R.id.button_minus_actividad);
		button_plus_user = rootView.findViewById(R.id.button_plus_user);
		button_minus_user = rootView.findViewById(R.id.button_minus_user);
		button_recording = rootView.findViewById(R.id.button_recording);
		
		/* Initialize TextViews */
		Actividades = (TextView) rootView.findViewById(R.id.contador_actividades);
		Usuarios = (TextView) rootView.findViewById(R.id.contador_usuario);

		
		/* Set on click listeners on buttons */
		button_plus_actividad.setOnClickListener(this);
		button_minus_actividad.setOnClickListener(this);
		button_plus_user.setOnClickListener(this);
		button_minus_user.setOnClickListener(this);
		button_recording.setOnClickListener(this);

		button_recording.setVisibility(View.INVISIBLE);
		
		/* set Text on TextViews from preferences */
		Actividades.setText((String.format(Locale.US, "%d", (Urband_Preferences.getActualSession( getActivity(), Urband_Preferences.ACTIVITY_NUMBER))) + " de 40"));
		Usuarios.setText(String.format(Locale.US, "%d",(Urband_Preferences.getActualUser(getActivity(), Urband_Preferences.USER_NUMBER)) ));
		
		/* Record button gets its status from preferences */
		recordingMovementSession();
		
		/* Initialize TextView for session description */
		sessionDescription = (TextView) rootView.findViewById(R.id.sessionDescription);
		
		/* Initialize TextView sessionDescription based on the actual session */
		int indexActivity = Urband_Preferences.getActualSession(getActivity(), Urband_Preferences.ACTIVITY_NUMBER)-1;
		sessionDescription.setText(ActivitySessionsDescription[indexActivity]);

		mActivity = getActivity();

		/*Returns generates view */
		return rootView;
	}

	@Override
	public void onClick(View v) 
		{
			/* Gets the ID of the clicked object */
			int id = v.getId();
			Log.i(TAG,Integer.toString(id));
			/* Gets the activity of running */
			ActivityHomePanel activity = (ActivityHomePanel)getActivity();
			
			/* Determines what to do according to the ID */
            final String actualUser_str = Urband_Preferences.getActualUser(getActivity(), Urband_Preferences.USER_NUMBER).toString();
            final String actualSession_actNum_str = Urband_Preferences.getActualSession(getActivity(), Urband_Preferences.ACTIVITY_NUMBER).toString();
            if(id == R.id.button_recording)
				{
					/* Gets USER_NUM, ACTIVITY_NUM and GESTURE_NUM from preferences and store them as strings for file creation */
					miActualUser = actualUser_str;
					miActivitySession = actualSession_actNum_str;
					miGestureSession = Urband_Preferences.getActualSession(getActivity(), Urband_Preferences.GESTURE_NUMBER).toString();
					
					/* Generates a vibrator object to control the device vibration */
					Vibrator vib = (Vibrator) activity.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
					
					/* Determines if starting or ending recording */
					if( !Urband_Preferences.getConfig(getActivity(), Urband_Preferences.isRecordingSession) )
						{
							/* Generates a file name according to session type selected */
							Toast.makeText(activity, "Se graba una actividad", Toast.LENGTH_LONG).show();
							file = new File(dir, "Urband_U" + miActualUser + "_A" + miActivitySession + ".txt" );

							/* Creates the file acording to actual user and type of session */
							try 
								{
									miFile	 = new FileWriter(file, true);
								} 
							catch (IOException e) 
								{
									e.printStackTrace();
								}
							
							/* Generates a vibration pattern before start recording to notify the user */
							try
								{
									vib.vibrate(100);
									Thread.sleep(900);
									vib.vibrate(100);
									Thread.sleep(900);
									vib.vibrate(300);
								} 
							catch (InterruptedException e) 
								{
									e.printStackTrace();
								}
							
							/* Enable recordNow flag */
							recordNow = true;
						}
					else /* ending recording */
						{
							/* Disable recordNow flag */
							recordNow = false;
							
							/* Vibrates to notify the user */
							vib.vibrate(300);
							
							/* Writes last data and closes the file */
							try 
								{
									if(miFile != null)
										{
											miFile.flush();
											miFile.close();
										}
								} 
							catch (IOException e) 
								{
									e.printStackTrace();
								}
							
						}
					Urband_Preferences.setConfig(getActivity(), Urband_Preferences.isRecordingSession, !Urband_Preferences.getConfig(getActivity(), Urband_Preferences.isRecordingSession)); 
					Log.v("Esta grabando?: ", Urband_Preferences.getConfig(getActivity(), Urband_Preferences.isRecordingSession)?"Si":"No");
					recordingMovementSession();
				}
			else if(id == R.id.button_plus_actividad) /* Button adds one to the count of activities */
				{
					/* Gets ACTIVITY_NUM from preferences and adds one to the result */
					BluetoothGattService batteryService = BluetoothService.mBluetoothGatt.getService(UUID.fromString(BluetoothGattAttributes.GESTURE_SERVICE));
					if(batteryService == null)
					{
						Log.i("TAG", "fifo service not found! FRAGMENTMENUPANEL");
						return;
					}
					BluetoothGattCharacteristic FIFO = batteryService.getCharacteristic(UUID.fromString(BluetoothGattAttributes.GESTURE_DEBUG_CHAR09));
					if(FIFO == null)
					{
						Log.i("TAG", "fifo level not found! FRAGMENTMENUPANEL");
						return;
					}
					BluetoothService.mBluetoothGatt.readCharacteristic(FIFO);
					cuenta_actividades = Urband_Preferences.getActualSession(getActivity(), Urband_Preferences.ACTIVITY_NUMBER) + 1;

					if (cuenta_actividades > 40) 
					{
						cuenta_actividades = 40;
					}
					/* Updates ACTIVITY_NUM into preferences */
					Urband_Preferences.setActualSession(getActivity(), Urband_Preferences.ACTIVITY_NUMBER, cuenta_actividades);
					/* Set textView with the new value */
					Actividades.setText(actualSession_actNum_str + " de 40");
					Log.i("Session de Actividad: ", actualSession_actNum_str);
				}
			
			else if(id == R.id.button_minus_actividad)
				{
					cuenta_actividades = Urband_Preferences.getActualUser(getActivity(), Urband_Preferences.ACTIVITY_NUMBER) - 1;
					if (cuenta_actividades <= 0) 
					{
						cuenta_actividades = 1;
					}
					Urband_Preferences.setActualUser(getActivity(), Urband_Preferences.ACTIVITY_NUMBER, cuenta_actividades);
                    final String actualUser_actnum = Urband_Preferences.getActualUser(getActivity(), Urband_Preferences.ACTIVITY_NUMBER).toString();
                    Actividades.setText(actualUser_actnum + " de 40");
					Log.v("Session de Actividad: ", actualUser_actnum);
				}

			else if(id == R.id.button_plus_user)
				{
					cuenta_usuarios = Urband_Preferences.getActualUser(getActivity(), Urband_Preferences.USER_NUMBER) + 1;
					Urband_Preferences.setActualUser(getActivity(), Urband_Preferences.USER_NUMBER, cuenta_usuarios);
					Usuarios.setText(actualUser_str);
					Log.v("Usuario: ", actualUser_str);
				}
			
			else if(id == R.id.button_minus_user)
				{
					cuenta_usuarios = Urband_Preferences.getActualUser(getActivity(), Urband_Preferences.USER_NUMBER) - 1;
					if (cuenta_usuarios == 0) 
						{
							cuenta_usuarios = 1;
						}
					Urband_Preferences.setActualUser(getActivity(), Urband_Preferences.USER_NUMBER, cuenta_usuarios);
					Usuarios.setText(actualUser_str);
					Log.v("Usuario: ", actualUser_str);
				}
			
			/* Determines which is the actual session and display description on TextView sessionDescription */
			int indexActivity = Urband_Preferences.getActualSession(getActivity(), Urband_Preferences.ACTIVITY_NUMBER)-1;
			sessionDescription.setText(ActivitySessionsDescription[indexActivity]);
		}
	
	public void recordingMovementSession(){
		if(!Urband_Preferences.getConfig(getActivity(), Urband_Preferences.isRecordingSession))
			{
				button_recording.setBackgroundResource(R.drawable.ic_icono_record_up);
			}
		else
			{
				button_recording.setBackgroundResource(R.drawable.ic_icono_record_down);
			}
	}
}
