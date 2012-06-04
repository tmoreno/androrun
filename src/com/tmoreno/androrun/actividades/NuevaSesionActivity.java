package com.tmoreno.androrun.actividades;

import java.text.SimpleDateFormat;

import com.tmoreno.androrun.R;
import com.tmoreno.androrun.listeners.AndroRunLocationListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class NuevaSesionActivity extends Activity {

	private static final int HORAS_INICIAL_DEFECTO = 0;
	private static final int MINUTOS_INICIAL_DEFECTO = 30;
	private static final int TIEMPO_REFRESCO_GPS = 15;
	private static final int INTERVALO_CONTADOR = 1000;
	private static final int FIN_SESION_NOTIFICACION = 1;
	
	private boolean sesionIniciada;
	
	private LocationManager locationManager;
	private AndroRunLocationListener locationListener;
	
	private Button iniciarSesionButton;
	private Button cancelarSesionButton;
	private Button pausarSesionButton;
	private Button reanudarSesionButton;
	private View cancelarPausaView;
	
	private TimePicker timePicker;
	
	private TextView contadorTextView;
	private CountDownTimer contador;
	private long tiempoRestanteContador;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevasesion);
        
        sesionIniciada = false;
        
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new AndroRunLocationListener(this);
        
        cancelarPausaView = findViewById(R.id.cancelarPausaView);
        contadorTextView = (TextView)findViewById(R.id.contadorTextView);
        
        iniciarSesionButton = (Button)findViewById(R.id.iniciarSesionButton);
        iniciarSesionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
		   	     	showDialog(0);
		        }
				else {
					timePicker.setVisibility(View.GONE);
					contadorTextView.setVisibility(View.VISIBLE);
					iniciarSesionButton.setVisibility(View.GONE);
					cancelarPausaView.setVisibility(View.VISIBLE);
					pausarSesionButton.setVisibility(View.VISIBLE);
					reanudarSesionButton.setVisibility(View.GONE);
					
					long duracionSesion = timePicker.getCurrentHour() * 3600000 + timePicker.getCurrentMinute() * 60000;
					
					locationListener.iniciarSesion(duracionSesion);
					
					contador = crearContador(duracionSesion);
					contador.start();
					
					registrarGps();
					
					sesionIniciada = true;
				}
			}
		});
        
        cancelarSesionButton = (Button)findViewById(R.id.cancelarSesionButton);
        cancelarSesionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				timePicker.setVisibility(View.VISIBLE);
				contadorTextView.setVisibility(View.GONE);
				iniciarSesionButton.setVisibility(View.VISIBLE);
				cancelarPausaView.setVisibility(View.GONE);
				
				liberarGps();
				
				contador.cancel();
				
				locationListener.borrarSesion();
				
				sesionIniciada = false;
			}
		});
        
        pausarSesionButton = (Button)findViewById(R.id.pausarSesionButton);
        pausarSesionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pausarSesionButton.setVisibility(View.GONE);
				reanudarSesionButton.setVisibility(View.VISIBLE);
				
				liberarGps();
				
				contador.cancel();
			}
		});
        
        reanudarSesionButton = (Button)findViewById(R.id.reanudarSesionButton);
        reanudarSesionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pausarSesionButton.setVisibility(View.VISIBLE);
				reanudarSesionButton.setVisibility(View.GONE);
				
				registrarGps();
				
				contador = crearContador(tiempoRestanteContador);
				contador.start();
			}
		});
        
        timePicker = (TimePicker)findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(HORAS_INICIAL_DEFECTO);
        timePicker.setCurrentMinute(MINUTOS_INICIAL_DEFECTO);
        timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				if(hourOfDay == 0 && minute == 0){
					iniciarSesionButton.setEnabled(false);
				}
				else{
					iniciarSesionButton.setEnabled(true);
				}
			}
		});
    }
	
	@Override
	public Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
	    builder.setMessage(getString(R.string.gpsDesactivado));
	    builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
	    });
	 
	    return builder.create();
	}
	
	@Override
	public void onBackPressed() {
		if(!sesionIniciada){
			super.onBackPressed();
		}
	}
	
	private void registrarGps(){
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIEMPO_REFRESCO_GPS, 0, locationListener);
	}
	
	private void liberarGps(){
		locationManager.removeUpdates(locationListener);
	}

	private CountDownTimer crearContador(long duracion){
		final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		
		CountDownTimer contador = new CountDownTimer(duracion, INTERVALO_CONTADOR) {
			@Override
			public void onTick(long millisUntilFinished) {
				tiempoRestanteContador = millisUntilFinished;
				contadorTextView.setText(sdf.format(millisUntilFinished));
			}

			@Override
			public void onFinish() {
				liberarGps();
				
				contadorTextView.setText("00:00:00");
				
				pausarSesionButton.setEnabled(false);
				cancelarSesionButton.setEnabled(false);
				
				notificarFinSesion();
				
				sesionIniciada = false;
			}
		};
		
		return contador;
	}
	
	private void notificarFinSesion() {
		Context contexto = getApplicationContext();
		Intent intent = new Intent(contexto, HistorialActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(contexto, 0, intent, 0);

		Notification notificacion = new Notification(
									android.R.drawable.stat_sys_warning, 
									getString(R.string.finSesionAlerta), 
									System.currentTimeMillis());
		
		notificacion.setLatestEventInfo(contexto, getString(R.string.app_name), 
										getString(R.string.finSesion), pendingIntent);
		notificacion.flags |= Notification.FLAG_AUTO_CANCEL;
		notificacion.defaults |= Notification.DEFAULT_SOUND;
		notificacion.defaults |= Notification.DEFAULT_VIBRATE;
		
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(FIN_SESION_NOTIFICACION, notificacion);
	}
}
