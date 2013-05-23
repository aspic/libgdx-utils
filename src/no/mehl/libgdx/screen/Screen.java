package no.mehl.libgdx.screen;

import no.mehl.libgdx.core.Application;

/** Class structure for controlling how a {@link Screen} is used **/
public abstract class Screen {
	
	private boolean loaded;
	
	protected Application application;
//	protected Assets assets;
	
	public Screen(Application listener) {
		this.application = listener;
//		this.assets = game.getAssets();
	}
	
	public void setScreen(Screen screen) {
		application.setScreen(screen);
	}
	
	public void setLoaded() {
		if(!loaded) {
			loaded = true;
			loaded();
		}
	}
	
	public boolean isLoaded() {
		return this.loaded;
	}
	
	/** Gets triggered when assets have been loaded */
	protected abstract void loaded();
	
	/** Update loop from the {@link Application} */
	public abstract void update(float delta);

	/** Render loop from the {@link Application} */
	public abstract void render(float delta);

	public abstract void pause();

	public abstract void resume();
	
	/**
	 * A method for setting {@link Theme} in between loading screens.
	 * @return The {@link Theme} to set to this screen.
	 */
//	public abstract Theme getTheme();
}
