package no.mehl.libgdx.net.android;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Logger;

/** A handler for network events */
public class NetworkHandler implements ConnectionListener {
	
	protected ConnectionController controller;
	private ServerListener server;
	private ClientListener client;
	private MenuListener menu;
	
	private Logger log = new Logger(this.getClass().getSimpleName());

	public void signedIn(String id) {
		if(menu != null) menu.signedIn();
	}

	public void signInFailed() {
		if(menu != null) menu.signedInFailed();
	}
	
	public void onSignOutComplete() {
		if(menu != null) menu.signedOutComplete();
	}
	
	public void joinGame() {
		// TODO Auto-generated method stub
		
	}

	public void roomError(int error_code) {
		// TODO Auto-generated method stub
		
	}

	public void realtimeMessageReceived(byte[] bytes, String participant) {
		System.out.println("is server:" + server + " is client " + client + "Received message from " + participant + " size: " + bytes.length);
		if(client != null) client.realtimeMessageReceived(bytes, participant);
		else if(server != null) server.realtimeMessageReceived(bytes, participant);
	}

	public void startGame(ArrayList<String> participantIds, String roomId) {
	}

	public void startedGame() {
		log.info("All players are ready, start to play!");
		if(server != null) server.gameReady();
	}

	public void peerJoined(String roomId, ArrayList<String> participants) {
		log.info("Peer joind: roomId " + roomId + " participants: " + participants.size());
//		if(server != null) server.peerJoined(roomId, participants);
	}

	public void registerController(ConnectionController controller) {
		this.controller = controller;
	}
	
	public ConnectionController getController() {
		return this.controller;
	}
	
	/** Registers a {@link ServerListener} */
	public void registerServer(ServerListener listener) {
		this.server = listener;
	}
	
	/** Registers a {@link ClientListener} for client specific methods */
	public void registerClient(ClientListener listener) {
		this.client = listener;
	}
	
	public void registerAccountListener(MenuListener listener) {
		this.menu = listener;
	}

	public void startedGame(boolean creator) {
		log.info("Created room, game should be started in background.");
		if(menu != null) menu.loadGame(creator);
	}
}
