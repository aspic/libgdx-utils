package no.mehl.libgdx.core;

import no.mehl.libgdx.screen.Screen;
import no.mehl.libgdx.ui.UIManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
 
/**
 * The wrapper class for this application coupled with the Libgdx {@link ApplicationListener}
 * @author Kjetil Mehl <kjetil@no.logic.no.mehl.jd.logic.entity.logic.no>
 */
public abstract class Application implements ApplicationListener {
	
	protected Screen currentScreen;
	protected UIManager uiManager;
	private float[] color = {1f, 1f, 1f, 1.0f};
	
	public Application() {
		uiManager = UIManager.getInstance();
	}
	
	public void create() {
		initializeUIManager();
		this.currentScreen = getStartScreen();
	}

	public void resize(int width, int height) {
	}

	public void render() {
		float delta = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		if(uiManager.initializeAssets()) {
			currentScreen.setLoaded();
		}

		currentScreen.render(delta);
		currentScreen.update(delta);
	}

	public void pause() {
		currentScreen.pause();
	}

	public void resume() {
		currentScreen.resume();
	}

	public void dispose() {
	}
	
	public void setScreen(Screen screen) {
		// Do asset loading
		this.currentScreen = screen;
		this.currentScreen.setLoaded();
	}
	
	public void setStartScreen(Screen screen) {
		this.currentScreen = screen;
	}
	
	public abstract Screen getStartScreen();
	
	/** Setup the {@link UIManager} with correct paths */
	public abstract void initializeUIManager();
}
