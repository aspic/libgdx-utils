package no.mehl.libgdx.net.android;

/** A controller for interacting with the network back end */
public interface ConnectionController {
	/** Starts sign in process for user account */
	public void beginSignIn();
	/** Explicitly logs of user */
	public void signOut();
	/** Start a new game */
	public void startQuickGame(int minPlayers);
	/** Will open the invite view */
	public void invite();
	/** Pass real time message to the specified participant */
	public void passRealtimeMessage(byte[] message, String participant);
	public void passRealtimeMessageToServer(byte[] message);
	public void passUnrealtimeMessageToServer(byte[] message);
	
	/** Broadcast message */
	public void broadcastMessage(byte[] message);
	public void connect();
	
	/** Returns this participants id */
	public String getMyId();
	/** Broadcasts message to clients */
	public void broadcastUnreliableMessage(byte[] data);
}
