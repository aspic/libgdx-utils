package no.mehl.ui;

/**
 * Simple interface for generalising UI transitions.
 * @author aspic
 */
public interface Transition {
	/**
	 * Triggers a forward {@link Transition}.
	 */
	public void forward();
	/**
	 * Triggers a backward {@link Transition}.
	 */
	public void backward();
}