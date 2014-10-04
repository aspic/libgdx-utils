package no.mehl.libgdx.net.android;

import java.util.ArrayList;

/** A connection listener for various call backs from the back end */
public interface ConnectionListener {
	/** Sucessfully signed in */
	public void signedIn(String id);
	/** Signed in failed */
	public void signInFailed();
	
	/** Signed in failed */
	public void onSignOutComplete();
	
	/** Managed to join a game */
	public void joinGame();
	public void roomError(int error_code);
	
	/** Received a real time message from the given participant */
	public void realtimeMessageReceived(byte[] bytes, String participant);
	/** The game starts */
	public void startGame(ArrayList<String> participantIds, String roomId);
	public void startedGame();
	
	/** A peer joined this game */
	public void peerJoined(String roomId, ArrayList<String> participants);
	public void startedGame(boolean creator);
}
