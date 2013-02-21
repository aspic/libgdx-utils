package no.mehl.screen;

import no.mehl.core.GameListener;

/** Class structure for controlling how a {@link Screen} is used **/
public abstract class Screen {
	
	protected GameListener listener;
//	protected Assets assets;
	
	public Screen(GameListener listener) {
		this.listener = listener;
//		this.assets = game.getAssets();
	}
	
	public void setScreen(Screen screen) {
		listener.setScreen(screen);
	}
	
	public abstract void update(float deltaTime);

	public abstract void render(float deltaTime);

	public abstract void pause();

	public abstract void resume();
	
	public abstract void loaded();
	
	/**
	 * A method for setting {@link Theme} in between loading screens.
	 * @return The {@link Theme} to set to this screen.
	 */
//	public abstract Theme getTheme();
}
