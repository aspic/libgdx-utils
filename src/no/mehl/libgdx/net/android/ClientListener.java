package no.mehl.libgdx.net.android;

public interface ClientListener {
	// Client receives some type of message
	public void realtimeMessageReceived(byte[] bytes, String participant);
	// Client passes input to back end
	public void passInput(Object command);
}
