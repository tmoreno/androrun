package com.tmoreno.androrun.mapoverlays;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import com.tmoreno.androrun.R;
import com.tmoreno.androrun.beans.Posicion;

public class OverlayMapa extends Overlay {

	private List<Posicion> posiciones;
 
    public OverlayMapa(List<Posicion> posiciones) {
		this.posiciones = posiciones;
	}

	@Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Point p1 = new Point();
        Point p2 = new Point();
        Path path = new Path();
        
        GeoPoint gP1 = null;
		GeoPoint gP2 = null;
		
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(8);
        
        Projection projection = mapView.getProjection();
        
		if(posiciones.size() >= 2){
			
			gP1 = new GeoPoint(posiciones.get(0).getLatitud1E6(), 
					           posiciones.get(0).getLongitud1E6());
			
			projection.toPixels(gP1, p1);
			Bitmap marcadorInicio = BitmapFactory.decodeResource(mapView.getResources(), R.drawable.marcador_inicio);
			canvas.drawBitmap(marcadorInicio, p1.x - (marcadorInicio.getWidth() / 2), 
					          p1.y - marcadorInicio.getHeight(), paint);
			
			for(int i = 1; i < posiciones.size(); i++){
				gP2 = new GeoPoint(posiciones.get(i).getLatitud1E6(), 
				                   posiciones.get(i).getLongitud1E6());
				
				projection.toPixels(gP1, p1);
	            projection.toPixels(gP2, p2);

	            path.moveTo(p2.x, p2.y);
	            path.lineTo(p1.x, p1.y);

	            canvas.drawPath(path, paint);
	            
	            gP1 = gP2;
			}
			
			Bitmap marcadorFin = BitmapFactory.decodeResource(mapView.getResources(), R.drawable.marcador_fin);
			canvas.drawBitmap(marcadorFin, p2.x - (marcadorFin.getWidth() / 2), 
					          p2.y - marcadorFin.getHeight(), paint);
		}
    }
}
