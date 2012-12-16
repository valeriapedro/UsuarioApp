package br.ufpr.usuarioapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;


public class MyHomeLocation extends MapActivity implements LocationListener {
	private static final String CATEGORIA = "livro";
	private MapController controlador;
	private MapView mapa;
	private MyLocationOverlay ondeEstou;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.mapview);
		mapa = (MapView) findViewById(R.id.mapa);
		controlador = mapa.getController();
		controlador.setZoom(16);
		
		//Adiciona os controladores de zoom no mapa automaticamente
		mapa.setBuiltInZoomControls(true);
		
		// Centraliza o mapa na �ltima localiza��o conhecida
		Location loc = getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);

		// Se existe �ltima localiza��o converte para GeoPoint
		if (loc != null) {
			GeoPoint ponto = new Coordenada(loc);
			Log.i(CATEGORIA, "�ltima localizacao: " + ponto);

			controlador.setCenter(ponto);
		}

		ondeEstou = new MyLocationOverlay(this, mapa);
		ondeEstou.runOnFirstFix(new Runnable() {
			public void run() {
				Log.i(CATEGORIA, "MyOverlay runOnFirstFix: " + ondeEstou.getMyLocation());
			}
		});
		mapa.getOverlays().add(ondeEstou);

		getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

	private LocationManager getLocationManager() {
		return (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Registra o listener
		ondeEstou.enableMyLocation();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Remove o listener
		ondeEstou.disableMyLocation();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Remove o listener para n�o ficar atualizando mesmo depois de sair
		getLocationManager().removeUpdates(this);
	}

	public void onLocationChanged(Location location) {
		Log.i(CATEGORIA, "onLocationChanged: latitude: " + location.getLatitude() + " - longitude: " + location.getLongitude());

		GeoPoint point = new Coordenada(location);

		controlador.animateTo(point);

		mapa.invalidate();
	}
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	public void onProviderDisabled(String provider) {
	}
	public void onProviderEnabled(String provider) {
	}
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}