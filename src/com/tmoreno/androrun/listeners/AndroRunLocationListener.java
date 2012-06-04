package com.tmoreno.androrun.listeners;

import com.tmoreno.androrun.basedatos.AndroRunDataBaseHelper;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class AndroRunLocationListener implements LocationListener {
	
	private static final int MAX_TIEMPO_DIF = 120000;
	private static final int MIN_PRECISION = 200;
	
	private Activity activity;
	private AndroRunDataBaseHelper ardbh;
	
	private int idSesion;
	private Location ultimaMejorLocation;
	
	public AndroRunLocationListener(Activity activity) {
		this.activity = activity;
		this.ardbh = new AndroRunDataBaseHelper(activity);
	}
	
	public void iniciarSesion(long duracionSesion){
		idSesion = ardbh.insertarSesion(duracionSesion);
	}
	
	public void borrarSesion() {
		ardbh.borrarSesion(idSesion);
	}

	@Override
	public void onLocationChanged(Location location) {
		if(esMejorLocation(location)){
			ardbh.actualizaDistancia(idSesion, distancia(location));
			ardbh.insertarPosicion(idSesion, location.getLatitude(), location.getLongitude());
			
			ultimaMejorLocation = location;
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		activity.showDialog(0);
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	
	private float distancia(Location location) {
		if(ultimaMejorLocation == null){
			return 0;
		}
		else {
			return location.distanceTo(ultimaMejorLocation);
		}
	}
	
	/**
	 * Método que comprueba si la Location leida es mejor que la última mejor leida
	 * @param location
	 * @param currentBestLocation
	 * @return
	 */
	private boolean esMejorLocation(Location location) {
		// Si no hay última mejor Location entonces la que hemos leido es mejor
		if (ultimaMejorLocation == null) {
	        return true;
	    }
		
		if(location.getLatitude() == ultimaMejorLocation.getLatitude() &&
		   location.getLongitude() == ultimaMejorLocation.getLongitude()){
			return false;
		}

	    // Comprobamos si la Location leida es vieja o nueva
	    long tiempoDif = location.getTime() - ultimaMejorLocation.getTime();
	    boolean esMuyNueva = tiempoDif > MAX_TIEMPO_DIF;
	    boolean esMuyVieja = tiempoDif < -MAX_TIEMPO_DIF;
	    boolean esNueva = tiempoDif > 0;

	    if (esMuyNueva) {
	        return true;
	    } 
	    else if (esMuyVieja) {
	        return false;
	    }

	    // Comprobamos la precision de la Location leida
	    int precisionDif = (int) (location.getAccuracy() - ultimaMejorLocation.getAccuracy());
	    boolean esMenosPrecisa = precisionDif > 0;
	    boolean esMasPrecisa = precisionDif < 0;
	    boolean esMuyPocoPrecisa = precisionDif > MIN_PRECISION;
	    // Comprobamos si el proveedor del Location leido es el mismo que el último mejor Location
	    boolean esMismoProveedor = isSameProvider(location.getProvider(), ultimaMejorLocation.getProvider());

	    if (esMasPrecisa) {
	        return true;
	    } 
	    else if (esNueva && !esMenosPrecisa) {
	        return true;
	    } 
	    else if (esNueva && !esMuyPocoPrecisa && esMismoProveedor) {
	        return true;
	    }
	    
	    return false;
	}

	/**
	 * 
	 * @param provider1
	 * @param provider2
	 * @return
	 */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	    	return provider2 == null;
	    }
	    
	    return provider1.equals(provider2);
	}
}
