package no.mehl.libgdx.net.android;

/** These methods are triggered by the {@link NetworkHandler} */
public interface ServerListener {
	/** Received a message from a client */
	public void realtimeMessageReceived(byte[] bytes, String participant);
	
	/** A peer joined this game */
//	public void peerJoined(String roomId, ArrayList<String> participants);

	/** Called when room has been created, and enough players are present */
	public void gameReady();
	
}
