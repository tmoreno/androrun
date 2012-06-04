package com.tmoreno.androrun.actividades;

import com.tmoreno.androrun.R;
import com.tmoreno.androrun.adaptadores.SesionesAdaptador;
import com.tmoreno.androrun.basedatos.AndroRunDataBaseHelper;
import com.tmoreno.androrun.beans.Sesion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class HistorialActivity extends Activity {

	private AndroRunDataBaseHelper ardbh;
	private SesionesAdaptador adaptador;
	private ListView historialListView;
	
	private Sesion sesionBorrar;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial);
        
        ardbh = new AndroRunDataBaseHelper(this);
    	adaptador = new SesionesAdaptador(this, ardbh.getSesiones());
        
        historialListView = (ListView)findViewById(R.id.historialListView);
        historialListView.setAdapter(adaptador);
        
        historialListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
            	Sesion sesion = (Sesion) a.getItemAtPosition(position);
            	
            	Intent intent = new Intent(getApplicationContext(), MapaActivity.class);
            	intent.putExtra("ID_SESION", sesion.getId());
		        startActivity(intent);
            }
        });
        
        historialListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {
				sesionBorrar = (Sesion) a.getItemAtPosition(position);
				showDialog(0);
				
				return true;
			}
		});
	}
	
	@Override
	public Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(getString(R.string.borrarSesion));
	    
	    final Activity activity = this;
	    
	    builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ardbh.borrarSesion(sesionBorrar.getId());
				
				adaptador = new SesionesAdaptador(activity, ardbh.getSesiones());
				historialListView.setAdapter(adaptador);
				
				dialog.cancel();
			}
	    });
	    
	    builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
	        @Override
	    	public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	        }
	    });
	 
	    return builder.create();
	}
}
