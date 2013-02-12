package br.ufpr.usuarioapp;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DadosClienteActivity extends Activity {


	private static final String CATEGORIA = "TESTE TELA 2";
	private EditText inpEndereco;
	private EditText inpReferencia;
	

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		setContentView(R.layout.dados_cliente);
		
		inpEndereco = (EditText) findViewById(R.id.inpEndereco);
		inpReferencia = (EditText) findViewById(R.id.inpReferencia);
		
		//Recupera parametros da classe Bundle da tela anterior: Latitude, Longitude e Endereco
		Intent it = getIntent();
		if(it != null){
			Bundle params = it.getExtras();
			if (params != null) {
				String lat = params.getString("lat");
				String lng = params.getString("lng");
				inpEndereco.setText(params.getString("end"));               

				Log.i(CATEGORIA, "Latitude: " + lat+ "Longitude: " +lng+ "Endereco: " +params.getString("end"));
			}
		}
		
		

	}// fecha OnCreate

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_dados_cliente, menu);
		return true;
	}
	
	//Implementa o evento OnClick do botão 'Chamar Taxi'
	//Aqui a lógica de criação do objeto Json é tomada e em seguida a conexão via http é efetuada para o servidor
	public void onClick(View v){
		    	switch(v.getId()){
		    	case R.id.BtnChamarTaxi:
		    		
		    		//Pega referencia digitada pelo usuario
		    		String referencia = null;
		    		
		    		if (inpReferencia.getText() != null) {
		    			referencia = inpReferencia.getText().toString();
		    		} else {
		    			Log.i(CATEGORIA, "ALERT: Usuário não preencheu a referência.");
		    		}
		    			
		    		//placa que retorna o taxi mais proximo
		    		String placa = null ;
		    		//É criado um HashMap que possui os dados de coordenadas GPS do usuário
		    		//HashMap<String, String> params = getCoordenadas() ; 

		    		//Cria-se o objeto JSON a partir do HashMap
		            //JSONObject jsonParams = new JSONObject(params);
		            
		            //Obtém-se a 'reposta' da WebService, definido o método a ser acessado e os parâmetros
		            //url_ws1 = url da ws a apartir do emulador
		        	//JSONObject resp = HttpClient.SendHttpPost(this.getString(R.string.url_ws1_chamartaxi), jsonParams);
		        	
		        	
		        	//Conecta no webservice e mostra na tela do emulador a placa mais próxima 
		        	//try {
					//	placa = resp.getString("Placa") ;
					//} catch (JSONException e) {
					//	e.printStackTrace();
					//}
		        	//mostra a placa encontrada
		        	//Toast.makeText(this, "Placa: "+placa, Toast.LENGTH_LONG).show() ;
		        	
		    		break ;
		    	}//Fecha switch
	}//Fecha onClick

}
