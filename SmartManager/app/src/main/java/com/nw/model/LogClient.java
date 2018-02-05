package com.nw.model;

import java.util.ArrayList;

public class LogClient {
	String latetude;
	String longitude;
	String Address;
	ArrayList<Client> clients;
	
	boolean error = false;

	public String getLatetude() {
		return latetude;
	}

	public void setLatetude(String latetude) {
		this.latetude = latetude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public ArrayList<Client> getClients() {
		if (clients == null)
			clients = new ArrayList<Client>();
		return clients;
	}

	public void setClients(ArrayList<Client> clients) {
		this.clients = clients;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}
}
