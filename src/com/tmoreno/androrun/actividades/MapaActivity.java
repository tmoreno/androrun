package com.tmoreno.androrun.actividades;

import java.util.List;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import com.tmoreno.androrun.R;
import com.tmoreno.androrun.basedatos.AndroRunDataBaseHelper;
import com.tmoreno.androrun.beans.Posicion;
import com.tmoreno.androrun.mapoverlays.OverlayMapa;


public class MapaActivity extends MapActivity {
	
	private static final int VER_CAPAS_MENU_OP = 1;
	private static final int MAPA_CAPA_OP = 2;
	private static final int SATELITE_CAPA_OP = 3;
	
	private MapView mapa;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);
        
        Bundle extras = getIntent().getExtras();
        int idSesion = extras.getInt("ID_SESION");
        
        AndroRunDataBaseHelper ardbh = new AndroRunDataBaseHelper(this);
        List<Posicion> posiciones = ardbh.getPosiciones(idSesion);
        
        mapa = (MapView)findViewById(R.id.mapa);
        mapa.setBuiltInZoomControls(true);
        
        List<Overlay> mapOverlays = mapa.getOverlays();
        mapOverlays.add(new OverlayMapa(posiciones));
        
        if(!posiciones.isEmpty()){
        	GeoPoint gp = new GeoPoint(posiciones.get(0).getLatitud1E6(), 
                                       posiciones.get(0).getLongitud1E6());
        	
        	MapController controlMapa = mapa.getController();
        	controlMapa.setCenter(gp);
        	controlMapa.setZoom(17);
        }
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu smnu = menu.addSubMenu(Menu.NONE, VER_CAPAS_MENU_OP, Menu.NONE, getString(R.string.capas)).setIcon(android.R.drawable.ic_menu_mapmode);
        smnu.add(Menu.NONE, MAPA_CAPA_OP, Menu.NONE, getString(R.string.mapa));
        smnu.add(Menu.NONE, SATELITE_CAPA_OP, Menu.NONE, getString(R.string.satelite));
        
        smnu.setGroupCheckable(Menu.NONE, true, true);
    
        if(mapa.isSatellite()){
             smnu.getItem(1).setChecked(true);
        }
        else {
             smnu.getItem(0).setChecked(true);
        }
        
        return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case MAPA_CAPA_OP :
	            item.setChecked(true);
	            mapa.setSatellite(false);
	            break;
	            
	        case SATELITE_CAPA_OP :
	            item.setChecked(true);
	            mapa.setSatellite(true);
	            break;
	    }
	    
	    return true;
	}
}
