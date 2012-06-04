package com.tmoreno.androrun.adaptadores;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.tmoreno.androrun.R;
import com.tmoreno.androrun.beans.Sesion;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SesionesAdaptador extends ArrayAdapter<Sesion> {

	private Activity context;
	private Sesion [] sesiones;
	private DecimalFormat df = new DecimalFormat("0.00");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	static class ViewHolder {
	    TextView fecha;
	    TextView duracion;
	    TextView distancia;
	    TextView velocidad;
	}
	
	public SesionesAdaptador(Activity context, Sesion [] sesiones) {
		super(context, R.layout.historial_item, sesiones);
		
		this.context = context;
		this.sesiones = sesiones;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		View item = convertView;
		
		if(item == null){
	        LayoutInflater inflater = context.getLayoutInflater();
	        item = inflater.inflate(R.layout.historial_item, null);
	        
	        holder = new ViewHolder();
	        holder.fecha = (TextView) item.findViewById(R.id.fechaTextView);
	        holder.duracion = (TextView) item.findViewById(R.id.duracionTextView);
	        holder.distancia = (TextView) item.findViewById(R.id.distanciaTextView);
	        holder.velocidad = (TextView) item.findViewById(R.id.velocidadTextView);
	        
	        item.setTag(holder);
	    }
		else {
			holder = (ViewHolder) item.getTag();
		}
		
		holder.fecha.setText(dateFormat.format(sesiones[position].getFecha()));
		holder.duracion.setText(sesiones[position].getDuracion());
		holder.distancia.setText(df.format(sesiones[position].getDistancia() / 1000) + " Km");
		holder.velocidad.setText(df.format(sesiones[position].getDistancia() / toHoras(sesiones[position].getDuracion()) / 1000) + " Km/h");
		
		return(item);
	}
	
	private float toHoras(String duracion){
		float horas = Float.parseFloat(duracion.substring(0, 2));
		float minutos = Float.parseFloat(duracion.substring(3));
		
		return horas + (minutos / 60);
	}
}
