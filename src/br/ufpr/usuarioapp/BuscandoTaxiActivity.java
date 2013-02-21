package br.ufpr.usuarioapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;



import android.R.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
//import br.livro.android.cap7.R;
import android.widget.Toast;

/**
 * Exemplo de como fazer o download de uma imagem da Web através de uma URL
 * 

 * 
 * @author valeria
 * 
 */
public class BuscandoTaxiActivity extends Activity {
	//private static final String URL = "http://chart.apis.google.com/chart?chs=150x150&cht=qr&chl=www.livroandroid.com.br&choe=UTF-8";
	private ProgressDialog dialog;
	private Handler handler = new Handler();
	private boolean controle ;
	private int indice ;
	
	private static final String CATEGORIA = "TESTE TELA 3: PROGRESSDIALOG";
	//parametros da tela anterior: DadosClienteActivity
	private String lat, lng, end, ref;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.progress_dialog);
		
		getDados();	//Obtém os dados do Pedido a partir da Intent anterior
	   
		
		//Abre a janela com a barra de progresso 
		dialog = ProgressDialog.show(this,"Exemplo", "Buscando taxi, por favor aguarde...", false,true);

		//buscaTaxi();
		enviarPedido() ; //Envia o pedido à WS
	}
	
	//Recupera parametros da classe Bundle da tela anterior: Latitude, Longitude e Endereco
	//Método que pega os dados da Intent anterior e aramazena na classe Bundle
	public void getDados(){
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
	
	
	//Faz o download da imagem em uma nova Thread
	/*private void downloadImagem(final String urlImg) {
		new Thread() {
			@Override
			public void run() {
				try {
					//Faz o download da imagem
					URL url = new URL(urlImg);
					InputStream is = url.openStream();
					final Bitmap imagem = BitmapFactory.decodeStream(is);
					is.close();

					//Atualiza a tela
					atualizaTela(imagem);

				} catch (MalformedURLException e) {
					return;
				} catch (IOException e) {
					//Uma aplicação real deveria tratar este erro
					Log.e("Erro ao fazer o download", e.getMessage(),e);
				}
			}
		}.start();
	}*/
	//Método que envia os dados à WS
    public void enviarPedido() {
    	HashMap params = new HashMap() ;
    	params.put("Endereco", end) ;
    	params.put("Referencia", ref) ;
    	params.put("Latitude", lat) ;
    	params.put("Longitude", lng) ;
    	
    	JSONObject jsonParams = new JSONObject(params) ; //Cria o objeto JSON com as informações do HashMap
    	JSONObject resp = HttpClient.SendHttpPost(this.getString(R.string.urlWSenviarPedido), jsonParams) ; 
    	boolean enviado = false ;
    	indice = 0 ;
    	//Recebe os dados da resposta
    	try {
    		enviado = resp.getBoolean("Enviado") ; //Boolean para confirmar se dados foram enviados com sucesso
			indice = resp.getInt("Indice") ; //Pega índice do Pedido
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	if(enviado){
    		handler.post(run) ; //Se foram enviados os dados, inicia Runnable 
    	}
    }//Fecha enviarPedido
	
    /*******************************************************/
	//Aqui começa a execução da tarefa desta Activity com ProgressDialog
    //Objeto Runnable que é executado continuamente verificando se o pedido teve confirmação de algum taxista
	//private void buscaTaxi() {
	Runnable run = new Runnable() {
		//new Thread() {
		@Override
		public void run() {
			try {
				if(controle){
					checarConfirmacao() ;
					handler.postDelayed(this, 3000) ;
					
					//Atualiza a tela
					//atualizaTela();
					//Passar parametros vindos da Ws para atualizar a tela
					//Nome taxista e placa do taxi que vieram pela web service
					//atualizaTela(imagem);
				}
			} catch (Exception e){
				//Tratar esse erro com um Dialog para o usuario
				Log.e("Erro ao buscar o taxi", e.getMessage(),e);
			}
		}//Fecha run
	};//.start();//Fecha Thread
	//}
	/*******************************************************/
	
	//Atualizar a tela com uma mensagem de confirmaçao para o usuario informando nome e placa
	//Utiliza um Handler para atualizar a tela
	private void atualizaTela(final String nomeTaxista, final String placaTaxista) {
		handler.post(new Runnable(){
			public void run() {
				//Fecha a janela de progresso
				dialog.dismiss();
				
				
				EditText cpNomeTaxista = (EditText) findViewById(R.id.cpNomeTaxista);
				EditText cpPlacaTaxista = (EditText) findViewById(R.id.cpPlacaTaxista);
				
				cpNomeTaxista.setText(nomeTaxista);
				cpPlacaTaxista.setText(placaTaxista);
				//ImageView imgView = (ImageView) findViewById(R.id.img);
				//imgView.setImageBitmap(imagem);
				
			}});
	}
	/**************************************************************************/

    
    //Método da Runnable que acessa a WS para verificar se há Confirmação do Pedido enviado
    public void checarConfirmacao(){
    	HashMap params = new HashMap() ;
    	params.put("Indice", indice) ; //Usa o índice para pegar o mesmo Pedido do Array
    	JSONObject jsonParams = new JSONObject(params) ;
    	JSONObject resp = HttpClient.SendHttpPost(this.getString(R.string.urlWSchecarConfirmacao), jsonParams) ;
    	int achou = 0;
    	String nomeTaxista = "", placaTaxi = "" ;
    	try{
    		//Obtém-se todos os dados da resposta
    		achou = resp.getInt("Achou") ;
    		nomeTaxista = resp.getString("NomeTaxista") ;
    		placaTaxi = resp.getString("PlacaTaxi") ;
    	}catch(JSONException e){
    		e.printStackTrace() ;
    	}
    	//Verifica valor da variavel "confirm"
    	if(achou == 1){ // 1)O pedido foi aceito
    		Toast.makeText(this, nomeTaxista+" | "+placaTaxi, Toast.LENGTH_SHORT).show() ;
    		controle = false ; //Cancela o loop da Runnable
    		atualizaTela(nomeTaxista, placaTaxi);
    	}
	}//Fecha checarConfirmacao
    
   
    
    @Override
    protected void onPause() {
    	super.onPause() ;    	
    	controle = false ;
    	dialog.dismiss();
    	finish() ;
    }//Fecha onPause
	
    @Override
    protected void onResume(){
    	super.onResume() ;
    	controle = true ;
    }
    
    @Override
    protected void onRestart(){
    	super.onRestart() ;
    	controle = true ;
    }
}





