package br.ufpr.usuarioapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class BKP_BuscandoTaxiActivity extends Activity {
	
	private static final String CATEGORIA = "TESTE TELA 3: PROGRESSDIALOG";
	//parametros da tela anterior: DadosClienteActivity
	private String lat, lng, end, ref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buscando_taxi);
		//Recupera parametros da classe Bundle da tela anterior: Latitude, Longitude e Endereco
		Intent it = getIntent();
		if(it != null){
			Bundle paramsBundle = it.getExtras();
			if (paramsBundle != null) {
				lat = paramsBundle.getString("lat");
				lng = paramsBundle.getString("lng");
				end = paramsBundle.getString("end");
				ref = paramsBundle.getString("ref");
				Log.i(CATEGORIA, "Latitude: " + lat+ "Longitude: " +lng+ "Endereco: " +end+ "Referencia: "+ref);
			}
		}		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_buscando_taxi, menu);
		return true;
	}

}
