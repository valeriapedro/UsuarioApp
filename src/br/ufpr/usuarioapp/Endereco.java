package br.ufpr.usuarioapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

/**
 * Ajuda a construir um GeoPoint
 * 
 * @author ricardo
 * 
 */
public class Endereco {
	private final Address address;

	
	




	public Endereco(Address address) {
		this.address = address;
	}
	


	//
	public static List<Endereco> buscar(Context context, String rua) {
		Geocoder gc = new Geocoder(context, new Locale("pt","BR"));
		List<Address> addresses = null;
		try {
			addresses = gc.getFromLocationName(rua, 10);
			if(addresses != null){
				List<Endereco> enderecos = new ArrayList<Endereco>();
				for (Address a : addresses) {
					enderecos.add(new Endereco(a));
				}
				return enderecos;
			}
		} catch (IOException e) {
			System.out.println("Deu erro o geo coder - " + e.getMessage());
		}
		return null;
	}
	
	//buscar endereco a partir das coordenadas lat e long e geocoder
	//
	public List<Address> getEndereco(Double lat, Double lng, Geocoder gc) {
	
	 /*
     * 
     * */
		//Geocoder gc = new Geocoder(this);
		List<Address> addresses = null;
		try {
			addresses = gc.getFromLocation(lat, lng, 5); // Reverse Geocoding: Retorna nome da rua a partir das coordenadas
			for (Address address : addresses) {
				Log.i("GeocoderLog", "Endereço: " + address.getFeatureName()); 
        }
        
        /*addresses = gc.getFromLocationName("Zachary's Chicago Pizza", 10); // Geocoding: Retorna coordenadas a partir do nome da rua
        	for (Address address : addresses) {
            	Log.i("GeocodeLog", "Endereço: " + address.getFeatureName()); 
        }*/
		} catch (IOException e) {
			Log.e("GeocoderLog", "Deu erro o geo coder - " + e.getMessage());
    }
    /*
     * 
     * */
		return addresses;
	}

	public String getEstado() {
		return address.getAdminArea();
	}

	public String getCidade() {
		return address.getLocality();
	}

	public String getRua() {
		return address.getFeatureName();
	}

	public String getDesc() {
		return getRua() + ", " + getCidade() + " - " + getEstado();
	}

	public double getLatitude() {
		return address.getLatitude();
	}

	public double getLongitude() {
		return address.getLongitude();
	}
}