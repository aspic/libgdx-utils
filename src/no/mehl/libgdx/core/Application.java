package no.mehl.libgdx.core;

import javax.swing.UIManager;

import no.mehl.libgdx.screen.Screen;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
 
/**
 * The wrapper class for this application coupled with the Libgdx {@link ApplicationListener}
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public abstract class Application implements ApplicationListener {
	
	protected Screen currentScreen;
	private float[] color = {0.1f, 0.1f, 0.1f, 1.0f};
	
	public Application() {
		
	}
	
	public void create() {
		initializeUIManager();
		this.currentScreen = getStartScreen();
	}

	public void resize(int width, int height) {
	}

	public void render() {
		float delta = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(color[0], color[1], color[2], color[3]);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
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
