package com.tmoreno.androrun.basedatos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.tmoreno.androrun.beans.Posicion;
import com.tmoreno.androrun.beans.Sesion;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AndroRunDataBaseHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;
	private static final String DATABASE_NAME = "androRunDB.sqlite";

	public AndroRunDataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE sesiones (" +
                   " id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                   " fecha REAL, " +
                   " duracion TEXT, " +
                   " distancia REAL);");
		
		db.execSQL("CREATE TABLE posiciones (" +
                   " id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                   " idsesion INTEGER, " +
                   " latitud REAL, " +
                   " longitud REAL, " +
                   " FOREIGN KEY(idsesion) REFERENCES Sesiones(id));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS sesiones");
		db.execSQL("DROP TABLE IF EXISTS posiciones");
		
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
	    super.onOpen(db);
	    
	    if (!db.isReadOnly()) {
	        db.execSQL("PRAGMA foreign_keys=ON;");
	    }
	}
	
	public int insertarSesion(long duracion){
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		
		ContentValues nuevaSesion = new ContentValues();
		nuevaSesion.put("fecha", System.currentTimeMillis());
		nuevaSesion.put("duracion", timeFormat.format(duracion));
		nuevaSesion.put("distancia", 0);
		
		SQLiteDatabase db = getWritableDatabase();
		int idSesion = (int) db.insert("sesiones", null, nuevaSesion);
		db.close();
		
		return idSesion;
	}
	
	public void borrarSesion(int idSesion) {
		String [] args = new String[] {String.valueOf(idSesion)};
		
		SQLiteDatabase db = getWritableDatabase();
		
		// El ON DELETE CASCADE no lo soporta la SQLite de Android 2.1
		db.delete("posiciones", "idsesion = ?", args);
		db.delete("sesiones", "id = ?", args);
		db.close();
	}
	
	public void insertarPosicion(int idSesion, double latitud, double longitud){
		ContentValues nuevaPosicion = new ContentValues();
		nuevaPosicion.put("idsesion", idSesion);
		nuevaPosicion.put("latitud", latitud);
		nuevaPosicion.put("longitud", longitud);
		
		SQLiteDatabase db = getWritableDatabase();
		db.insert("posiciones", null, nuevaPosicion);
		db.close();
	}

	public void actualizaDistancia(int idSesion, float distanceTo) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("UPDATE sesiones SET distancia = distancia + " + distanceTo + " WHERE id = " + idSesion);
		db.close();
	}

	public Sesion [] getSesiones() {
		Sesion sesion = null;
		List<Sesion> sesiones = new ArrayList<Sesion>();
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("SELECT id, fecha, duracion, distancia FROM sesiones ORDER BY fecha DESC", null);
		
		while(c.moveToNext()){
			sesion = new Sesion();
			sesion.setId(c.getInt(0));
			sesion.setFecha(c.getLong(1));
			sesion.setDuracion(c.getString(2));
			sesion.setDistancia(c.getFloat(3));
			
			sesiones.add(sesion);
	    }
		
		c.close();
		db.close();
		
		Sesion [] sesionesArray = new Sesion [sesiones.size()];
		return sesiones.toArray(sesionesArray);
	}

	public List<Posicion> getPosiciones(int idSesion) {
		Posicion posicion = null;
		List<Posicion> posiciones = new ArrayList<Posicion>();
		
		String [] args = new String [] {String.valueOf(idSesion)};
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("SELECT latitud, longitud FROM posiciones WHERE idSesion = ?", args);
		
		while(c.moveToNext()){
			posicion = new Posicion();
			posicion.setLatitud(c.getDouble(0));
			posicion.setLongitud(c.getDouble(1));
			
			posiciones.add(posicion);
	    }
		
		c.close();
		db.close();
		
		return posiciones;
	}
}
