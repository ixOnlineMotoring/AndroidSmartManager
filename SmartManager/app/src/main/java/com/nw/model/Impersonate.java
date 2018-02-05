package com.nw.model;

import java.util.ArrayList;

public class Impersonate {
	ArrayList<Client> clients;

	public ArrayList<Client> getClients() {
		if (clients == null)
			clients = new ArrayList<Client>();
		return clients;
	}

	public void setClients(ArrayList<Client> clients) {
		this.clients = clients;
	}

}
