package com.tmoreno.androrun.beans;

public class Posicion {

	private double latitud;
	private double longitud;
	
	public double getLatitud() {
		return latitud;
	}
	
	public int getLatitud1E6(){
		return (int)(latitud * 1E6);
	}
	
	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}
	
	public double getLongitud() {
		return longitud;
	}
	
	public int getLongitud1E6(){
		return (int)(longitud * 1E6);
	}
	
	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}
}
