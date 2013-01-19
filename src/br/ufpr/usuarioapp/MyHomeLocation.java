package br.ufpr.usuarioapp;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;


public class MyHomeLocation extends MapActivity implements LocationListener {
	
	private Location local ;
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
		
		// Centraliza o mapa na última localização conhecida
		Location loc = getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);

		// Se existe última localização converte para GeoPoint
		if (loc != null) {
			GeoPoint ponto = new Coordenada(loc);
			Log.i(CATEGORIA, "Última localizacao: " + ponto);

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
	
	//Metodos sobrescritos para implementar o ciclo de vida do Overlay corretamente
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
		// Remove o listener para não ficar atualizando mesmo depois de sair
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
	
	//Implementa ação do botão 'Chamar Taxi'
	public void onClick(View v){
    	switch(v.getId()){
    	case R.id.btnInicio:
    		String placa = null ;
    		//É criado um HashMap que possui os dados de coordenadas GPS do usuário
    		HashMap<String, Double> params = getCoordenadas() ; 

    		//Cria-se o objeto JSON a partir do HashMap
            JSONObject jsonParams = new JSONObject(params);
            
            //Obtém-se a 'reposta' da WebService, defindo o método a ser acessado e os parâmetros
        	JSONObject resp = HttpClient.SendHttpPost(this.getString(R.string.url_ws), jsonParams);
        	
        	try {
				placa = resp.getString("Placa") ;
			} catch (JSONException e) {
				e.printStackTrace();
			}
        	
        	Toast.makeText(this, "Placa: "+placa, Toast.LENGTH_LONG).show() ;
        	
    		break ;
    	}//Fecha switch
    }//Fecha onClick
	
	 public HashMap<String, Double> getCoordenadas(){
	    	
	    	double lat = 0, lng = 0 ;
	    	
	    	HashMap<String, Double> params = new HashMap<String, Double>();
	    	LocationManager LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    	LM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	        String bestProvider = LM.getBestProvider(new Criteria(), true) ;
	        
	        //Se obtém o local mais recente marcado pelo GPS
	        local = LM.getLastKnownLocation(bestProvider) ;
	        
	        //Obtém-se os valores de latitude e longitude
	        lat = local.getLatitude() ;
	        lng = local.getLongitude() ;
	        
	        //Log.d("TESTE", "Lat: "+lat+ " Lng: "+log) ;
	        
	        //Valores são inseridos no HashMap
	        params.put("latitude", lat);
	    	params.put("longitude", lng);
	    	
	    	return params ;
	    	
	    }//Fecha getCoordenadas
}