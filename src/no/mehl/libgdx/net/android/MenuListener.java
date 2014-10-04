package no.mehl.libgdx.net.android;

public interface MenuListener {
	public void signedIn();
	
	public void signedInFailed();
	
	/** Load the actual game */
	public void loadGame(boolean creator);
	public void signedOutComplete();
}
