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
	private Button BtnChamarTaxi;
	//parametros da tela anterior
	private Bundle paramsBundle ;

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
				String lat = paramsBundle.getString("lat");
				String lng = paramsBundle.getString("lng");
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
	//Aqui a lógica de criação do objeto Json é tomada e em seguida a conexão via http é efetuada para o servidor
	public void onClick(View v){
		    	switch(v.getId()){
		    	case R.id.BtnChamarTaxi:
		    		Toast.makeText(DadosClienteActivity.this, "Botão Funcionando Ok", Toast.LENGTH_SHORT).show();
		    		
		    		//Pega referencia digitada pelo usuario - Só passa para a proxima tela quando a referencia é informada
		    		String referencia = inpReferencia.getText().toString();
		    		
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
		    			Intent it = new Intent(this,BuscandoTaxiActivity.class);
						it.putExtra("msg", "Olá");
						startActivity(it);
		    		}
		    		//}

		    			
		    		/*	
		    		//É criado um HashMap que possui os parametros da classe Bundle enviados pela tela anterior
		    		//
		    		String lat = paramsBundle.getString("lat");
		    		String lng = paramsBundle.getString("lng");
		    		String end = paramsBundle.getString("end");
		    		
		    		
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
	

