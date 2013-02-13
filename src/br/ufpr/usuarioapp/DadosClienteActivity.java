package br.ufpr.usuarioapp;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DadosClienteActivity extends Activity {


	private static final String CATEGORIA = "TESTE TELA 2";
	private EditText inpEndereco;
	private EditText inpReferencia;
	
	//parametros da tela anterior
	private Bundle paramsBundle ;
	private String lat, lng, end;


	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		setContentView(R.layout.dados_cliente);
		
		inpEndereco = (EditText) findViewById(R.id.inpEndereco);
		inpReferencia = (EditText) findViewById(R.id.inpReferencia);
		
		//Recupera parametros da classe Bundle da tela anterior: Latitude, Longitude e Endereco
		Intent it = getIntent();
		if(it != null){
			Bundle paramsBundle = it.getExtras();
			if (paramsBundle != null) {
				lat = paramsBundle.getString("lat");
				lng = paramsBundle.getString("lng");
				end = paramsBundle.getString("end");
				inpEndereco.setText(paramsBundle.getString("end"));               

				Log.i(CATEGORIA, "Latitude: " + lat+ "Longitude: " +lng+ "Endereco: " +paramsBundle.getString("end"));
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
	//Essa tela tem a finalidade em confirmar os dados pelo usuario .Ao clicar em Chamar taxi, outra tela é chamada para processamento do pedido
	public void onClick(View v){
		    	switch(v.getId()){
		    	case R.id.BtnChamarTaxi:
		    		Log.i(CATEGORIA, "Latitude: " +lat+ "Longitude: " +lng+ "Endereco: " +end);
		    		Toast.makeText(DadosClienteActivity.this, "Botão Funcionando Ok", Toast.LENGTH_SHORT).show();
		    		
		    		//Pega referencia digitada pelo usuario - Só passa para a proxima tela quando a referencia é informada
		    		String referencia = inpReferencia.getText().toString();
		    		
		    		//Testa se a referencia esta preenchida
		    		if (referencia.equals(""))  { 		    				
		    			//Implementa diálogo para o usuario
		    			AlertDialog.Builder mensagem = new
			                       AlertDialog.Builder(DadosClienteActivity.this);
			    				mensagem.setTitle("Aviso");
			    				mensagem.setMessage("Por favor, entre com uma referência para ajudar nosso taxista a encontrá-lo!");
			    				mensagem.setNeutralButton("OK", null);
			    				mensagem.show();
		    			
		    			Log.i(CATEGORIA, "ALERT: Referencia = "+referencia);
		    				
		    				
		    		} else {
						
				    	//Cria um novo Bundle para a proxima tela.. Todos os parametros passaods + a referencia
			    		Intent it = new Intent(this,BuscandoTaxiActivity.class);
			    		Bundle params1 = new Bundle();
						params1.putString("lat", lat);
						params1.putString("lng", lng);
						params1.putString("end", end);
						params1.putString("ref", referencia);	
						it.putExtras(params1);
						startActivity(it);
		    		
		    		}
		    		//}

		    			
		    		/*	
		    		//É criado um HashMap que possui os parametros da classe Bundle enviados pela tela anterior
		    		//
		    		
		    		
		    		
		    		Log.i("Teste Botao Tela 2", "Latitude: " + lat+ "Longitude: " +lng+ "Endereco: " +paramsBundle.getString("end"));
		    		//Insere parametros no HashMap
		    		HashMap<String, String> paramsHm = new HashMap<String, String>() ; 

		    		paramsHm.put("lat", lat);
		    		paramsHm.put("lng", lng);
		    		paramsHm.put("end", end);
		    		paramsHm.put("ref", referencia);
		    		
		    		//Cria-se o objeto JSON a partir do HashMap
		            JSONObject jsonParams = new JSONObject(paramsHm);
		            
		            //Obtém-se a 'reposta' da WebService, definido o método a ser acessado e os parâmetros
		            //url_ws1 = url da ws a apartir do emulador
		        	JSONObject resp = HttpClient.SendHttpPost(this.getString(R.string.url_ws1_chamartaxi), jsonParams);
		        	
		    		//placa que retorna o taxi mais proximo do usuario
		    		String placa = null ;
		        	//Conecta no webservice e mostra na tela do emulador a placa mais próxima 
		        	try {
						placa = resp.getString("Placa") ;
					} catch (JSONException e) {
						e.printStackTrace();
					}
		        	//mostra a placa encontrada
		        	Toast.makeText(this, "Placa: "+placa, Toast.LENGTH_LONG).show() ;
		        	*/
		    		break ;
		    	//Fecha switch
	}//Fecha onClick
	}
}
	

