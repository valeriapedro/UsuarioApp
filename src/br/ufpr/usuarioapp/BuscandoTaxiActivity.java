package br.ufpr.usuarioapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class BuscandoTaxiActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buscando_taxi);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_buscando_taxi, menu);
		return true;
	}

}
