package br.ufpr.usuarioapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
//import br.livro.android.cap4.activity.Tela2;
//import br.ufpr.threadcontrol.R;
//import br.ufpr.threadcontrol.HttpClient;
//import br.ufpr.threadcontrol.R;
//import br.ufpr.threadcontrol.ThreadActivity.TaxiRun;
//import br.ufpr.threadcontrol.ThreadActivity.TaxiRun;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;


public class MyHomeLocationActivity extends MapActivity implements LocationListener {
	
	//thread do pedido de taxi
	private Handler handlerPedido ;	
	//teste para instanciar mais de uma thread
	private ArrayList<ClienteRun> listaCliente ;
	
	//botões usados para testar as threads 
	//private Button btnLigar, btnDesligar ;
	
	//Location do cliente
	private Location local ;
	private static final String CATEGORIA = "livro";
	
	//atributos para implementar o mapa e o overlay no mapa
	private MapController controlador;
	private MapView mapa;
	private MyLocationOverlay ondeEstou;
	
	
	/*
	 * 
	 * Ver se dá para separar esse código da Activity Home
	 * 
	 * */
	//----Classe Runnable que será instanciada 10 vezes----\\
	public class ClienteRun extends Thread {
			//placa do taxi
			String endereco ;
			//controle = flag de pedido ??
			boolean controle, pedido ;
			// 
			int numero ;
			
			//construtor da classe Taxi Run
			public ClienteRun(boolean controle, String endereco, int numero){
				this.controle = controle ;
				this.endereco = endereco ;
				this.numero = numero ; //numero do cliente, depois implementar o nome do cliente
	
			}
			
			//Implementa Run
			@Override
			public void run() {
				if(controle){
					//Pegar coordenadas
					//Envia para REST_JSON
					EnviarCoord(endereco, numero) ;
					if(pedido){
						this.turnOff() ;
					}
					handlerPedido.postDelayed(this, 3000) ;
	    		}
			}
			
			//desliga o controle 
			public void turnOff(){
				this.controle = false ;
			}
			
			//liga o controle
			public void turnOn(){
				this.controle = true ;
			}
			
		
	}
	/***********************************************************************/
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.mapview);
		mapa = (MapView) findViewById(R.id.mapa);
		controlador = mapa.getController();
		controlador.setZoom(16);
		
		//instancia thread do pedido e a lista de ClienteRun
		handlerPedido = new Handler() ;
		listaCliente = new ArrayList<ClienteRun>() ;
    	
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.thread_main);
        
        //Botões para ligar e desligar threads vindos do layout xml thread_main como exemplo
        //btnLigar = (Button) findViewById(R.id.btnLigar) ;
        //btnDesligar= (Button) findViewById(R.id.btnDesligar) ;
        //btnLigar.setEnabled(true) ;
        //btnDesligar.setEnabled(false) ;
		
		// Adiciona os controladores de zoom no mapa automaticamente
		mapa.setBuiltInZoomControls(true);
		
		// Centraliza o mapa na última localização conhecida
		Location loc = getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);

		// Se existe última localização converte para GeoPoint
		if (loc != null) {
			GeoPoint ponto = new Coordenada(loc);
			Log.i(CATEGORIA, "Última localizacao: " + ponto);

			controlador.setCenter(ponto);
		}
		

		
		
		//Implementa overlay no mapa usando MyLocationOverlay
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

	
	//Implementa o evento OnClick do botão 'Continuar >>'
	//
	public void onClick(View v){
		    	switch(v.getId()){
		    	case R.id.btnInicio:
		    		
		    		//placa que retorna o taxi mais proximo
		    		//String placa = null ;
		    		//É criado um HashMap que possui os dados de coordenadas GPS do usuário
		    		HashMap<String, String> params = getCoordenadas() ; 
		    		String lat = params.get("lat");
		    		String lng = params.get("lng");
		    		String end = params.get("end");
			    	
		    		//Log.d("TESTE BOTAO", "Lat: "+lat+ " Lng: "+lng+ "Endereco: " +end) ;
		    		
		        	//Chamada pra tela de confirmação com os dados do cliente.. 
		    		//Lat, long, Endereço encontrado pelo Geocoder e Referencia
		    		//Esses dados irão compor a classe Pedido na WebService
			    	
		    				    		
			    	//Cria o bundle que contem os parametros para a proxima tela
		    		Intent it = new Intent(this,DadosClienteActivity.class);
					Bundle params1 = new Bundle();
					params1.putString("lat", lat);
					params1.putString("lng", lng);
					params1.putString("end", end);
					
					it.putExtras(params1);
					startActivity(it);
					
	        	
		        	
		    		break ;
		    	}//Fecha switch

		}//Fecha onClick
	
	//Implementa o evento OnClick do botão 'Chamar Taxi'
	//Aqui a lógica de criação do objeto Json é tomada e em seguida a conexão via http é efetuada para o servidor
	/*public void onClick(View v){
	    	switch(v.getId()){
	    	case R.id.btnInicio:
	    		//placa que retorna o taxi mais proximo
	    		String placa = null ;
	    		//É criado um HashMap que possui os dados de coordenadas GPS do usuário
	    		HashMap<String, String> params = getCoordenadas() ; 

	    		//Cria-se o objeto JSON a partir do HashMap
	            JSONObject jsonParams = new JSONObject(params);
	            
	            //Obtém-se a 'reposta' da WebService, definido o método a ser acessado e os parâmetros
	            //url_ws1 = url da ws a apartir do emulador
	        	JSONObject resp = HttpClient.SendHttpPost(this.getString(R.string.url_ws1_chamartaxi), jsonParams);
	        	
	        	
	        	//Conecta no webservice e mostra na tela do emulador a placa mais próxima 
	        	try {
					placa = resp.getString("Placa") ;
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        	//mostra a placa encontrada
	        	Toast.makeText(this, "Placa: "+placa, Toast.LENGTH_LONG).show() ;
	        	
	    		break ;
	    	}//Fecha switch
	}//Fecha onClick*/
	
	/*
	 *   
	 *  public void onClick(View view) {
    	switch(view.getId()){
    	case R.id.btnLigar:
    		btnLigar.setEnabled(false) ;
    		btnDesligar.setEnabled(true) ;
    		IniciarTeste() ;
    		break ;
    	
    	case R.id.btnDesligar:
    		btnLigar.setEnabled(true) ;
            btnDesligar.setEnabled(false) ;
    		PararTeste() ;
    		break ;
		}//Fecha switch
	}//Fecha onClick
 
	 * 
	 */
	
	//Pega as coordenadas lat e long pelo GPS e registra no HashMap
	//@SuppressLint("NewApi")
	public HashMap<String, String> getCoordenadas(){
		    	
		    	String lat1 = "", lng1 = "" ;
		    	Double lat = 0.0, lng = 0.0 ;
		    	
		    	HashMap<String, String> params = new HashMap<String, String>();
		    	LocationManager LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		    	LM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		        String bestProvider = LM.getBestProvider(new Criteria(), true) ;
		        
		        //Se obtém o local mais recente marcado pelo GPS
		        local = LM.getLastKnownLocation(bestProvider) ;
		        
		        //Obtém-se os valores de latitude e longitude
		        //tipo Double
		        lat = local.getLatitude() ;
		        lng = local.getLongitude() ;
		        //tipo String
		        lat1 = String.valueOf(local.getLatitude()) ;
		        lng1 = String.valueOf(local.getLongitude()) ;
		        
		        /*
		         * Pegar endereco atraves do reverse geocoder
		         * 
		         * */
		        
		        Geocoder gc = new Geocoder(this);
		        List<Address> addresses = null;
		        Endereco end = new Endereco(null);
		        String addressText = "Nao foi possível obter o  endereço" ;
		        //Retorna uma list<address> atraves do metodo getEndereco da classe Endereco
		        addresses = end.getEndereco(lat, lng, gc);
		        
		        //Formata o list em uma String de endereço
		        if (addresses != null && addresses.size() > 0) {
	                Address address = addresses.get(0);
	                // Formata a primeira linha do endereço (se disponivel), cidade e pais
	                addressText = String.format("%s, %s, %s",
	                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
	                        address.getLocality(),
	                        address.getCountryName());
	                // atualiza o endereço
	               
	            }
		        //
		        //
		        //Mostra um resultado na tela do android com o endereço capturado das coordenadas.
		        //Context context = getApplicationContext();
		        //CharSequence text = addressText;
		        //int duration = Toast.LENGTH_SHORT;

		        //Toast toast = Toast.makeText(context, addressText, duration);
		        //toast.show();
		        
		        /*
		        try {
		            addresses = gc.getFromLocation(lat, lng, 1); // Reverse Geocoding
		            for (Address address : addresses) {
		                Log.i("GeocoderLog", "Endereço: " + address.getFeatureName()); 
		            }
		            
		            /*addresses = gc.getFromLocationName("Zachary's Chicago Pizza", 10); // Geocoding
		            for (Address address : addresses) {
		                Log.i("GeocodeLog", "Endereço: " + address.getFeatureName()); 
		            }
		        } catch (IOException e) {
		            Log.e("GeocoderLog", "Deu erro o geo coder - " + e.getMessage());
		        }
		        */
		       
				
		        //Log.d("TESTE Tela Home", "Lat: "+lat+ " Lng: "+lng+ "Endereco: " +addressText) ;
		        
		        //Valores são inseridos no HashMap
		        params.put("lat", lat1);
		    	params.put("lng", lng1);
		    	//agora adiciona para o HashMap a string endereço, utilizada na proxima tela
		    	params.put("end", addressText);
		    	return params ;
		    	
	}//Fecha getCoordenadas
	
    //----MÉTODO QUE ACESSA A WEB SERVICE----\\
    public void EnviarCoord(String endereco, int num){
    	
    	//Cria-se um objeto HashMap que receberá atributos
    	HashMap params = new HashMap() ;
    	//Cliente, invés da string placa, deve enviar a string endereço
    	
    	params.put("Endereco", endereco) ;
    	params.put("Numero", num) ;
    	
    	//Cria-se o objeto JSON a partir do HashMap
        JSONObject jsonParams = new JSONObject(params);
        
        //Obtém-se a 'reposta' da WebService, definido o método a ser acessado e os parâmetros
    	JSONObject resp = HttpClient.SendHttpPost(this.getString(R.string.url_ws1_chamartaxi), jsonParams);
    	
    	try{
    		//Respostas da WebService - Placa e pedido encontrados e atendidos
    		String placaResp = resp.getString("Placa") ;
    		boolean pedido = resp.getBoolean("Pedido") ;
    		int numero = resp.getInt("Numero") ;
    		if(pedido){
    			listaCliente.get(numero-1).turnOff() ;
    			//Imprime no LogCat que o Táxi aceitou um pedido
    			Log.d("Taxi", placaResp+": Pedido enviado") ;
    		}else{
    			//Imprime no LogCat a placa do Taxi que envia
            	//Log.d("Taxi", "Enviando:"+placaResp+" | Posição LAT:"+latResp+" LNG:"+lngResp) ;
    			Log.d("Cliente", "Enviando:"+placaResp) ;
    		}
    	}catch(JSONException e){
    		e.printStackTrace() ;
    	}
    	
    }//Fecha EnviarCoord

	//Metodos do ciclo de vida da Activity
		 
	@Override
	protected void onDestroy() {
		   super.onDestroy();
		    //btnDesligar.setEnabled(false) ;
			//btnLigar.setEnabled(true) ;
		   
			// Remove o listener para não ficar atualizando mesmo depois de sair
			getLocationManager().removeUpdates(this);
			PararTeste() ;
	}//Fecha onDestroy
		    
	@Override
	protected void onPause() {
			super.onPause();
			// Remove o listener
			ondeEstou.disableMyLocation();
		    //btnDesligar.setEnabled(false) ;
			//btnLigar.setEnabled(true) ;
			PararTeste() ;
		    	
	}//Fecha onPause
	
	@Override
	protected void onResume() {
		super.onResume();
		// Registra o listener
		ondeEstou.enableMyLocation();
	}
	
	//Implementa evento OnClick dos botões ligar e desligar threds - Utiliza metodos IniciarTeste e PararTeste
	/*public void onClick(View view) {
		    switch(view.getId()){
		    	case R.id.btnLigar:
		    		btnLigar.setEnabled(false) ;
		    		btnDesligar.setEnabled(true) ;
		    		IniciarTeste() ;
		    		break ;
		    	
		    	case R.id.btnDesligar:
		    		btnLigar.setEnabled(true) ;
		            btnDesligar.setEnabled(false) ;
		    		PararTeste() ;
		    		break ;
				}//Fecha switch
	}//Fecha onClick*/
	
	
	/***VER PARA SEPARAR ESSES METODOS DA ACTIVITY - MVC***/
	//----MÉTODOS DE LIGAR E DESLIGAR THREADS----\\
    public void IniciarTeste(){
    	//instancia as 10 threads 
    	for(int i = 0; i<10; i++){
    		//                TaxiRun(boolean controle, String endereco, int numero) 
    		ClienteRun cli = new ClienteRun(true, "Cliente "+(i+1), i+1) ;
    		listaCliente.add(cli) ;
    	}
    	
    	for(int i = 0; i<listaCliente.size(); i++){
    		ClienteRun tax = listaCliente.get(i) ;
    		if(!tax.controle){
    			tax.turnOn() ;
    		}else{
    			tax.start() ;
    		}
    			
    	}
    	
    }//Fecha Testar
    
    public void PararTeste(){
    	for(int i=0; i<listaCliente.size(); i++){
    		ClienteRun tax = listaCliente.get(i) ;
    		tax.turnOff() ;
    	}//Fecha for
    	
    }//Fecha PararTeste
    


	//Métodos obrigatórios da interface LocationListener
	public void onLocationChanged(Location location) {
		Log.i(CATEGORIA, "onLocationChanged: latitude: " + location.getLatitude() + " - longitude: " + location.getLongitude());
		
		//forçar o getcoodenadas
		//HashMap params1 = new HashMap() ;
		//params1 = getCoordenadas();
		
		GeoPoint point = new Coordenada(location);
		//centraliza o ponto no mapa
		mapa.getController().setCenter(point); 
		
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
	
	