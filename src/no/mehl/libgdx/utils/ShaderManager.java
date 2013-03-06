package no.mehl.libgdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * A simple class to manage several instances of {@link ShaderProgram}.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class ShaderManager {

	private static final ShaderManager INSTANCE = new ShaderManager();
	private PerspectiveCamera camera;
	
	private ObjectMap<String, ShaderProgram> shaders;
	
	// Camera controls
	private Vector3 origin;
	private Vector3 atPos = new Vector3();
	private Vector3 toPos;
	private static final float VIEWPORT_DEPTH = 30f;

	float posZ;
	
	private ShaderManager() {
		shaders = new ObjectMap<String, ShaderProgram>();
	}
	
	/**
	 * Loads this {@link ShaderManager}
	 * @param width Viewport width
	 * @param height Viewport height
	 */
	public void load(float aspect, float width, float height) {
		
		camera = new PerspectiveCamera(45, width * aspect, height);
		camera.position.z = VIEWPORT_DEPTH;
		origin = new Vector3(camera.position);
	}
	
	public ShaderProgram getShader(String key) {
		return shaders.get(key);
	}
	
	/**
	 * Compiles and loads a new shader.
	 * @param key
	 * @param pathVert
	 * @param pathFrag
	 * @return
	 */
	public ShaderProgram compileShader(String key, String pathVert, String pathFrag) {
		ShaderProgram shader = new ShaderProgram(Gdx.files.internal(pathVert), Gdx.files.internal(pathFrag));
		this.shaders.put(key, shader);
		return shader;
	}
	
	public PerspectiveCamera getCamera() {
		return this.camera;
	}
	
	public void translateXY(float x, float y) {
		if(x == 0 && y == 0) toPos = new Vector3(origin);
		else toPos = new Vector3(x, y, 0);
	}
	
	public void updateCamera(float delta) {
		float diffX = (toPos.x - atPos.x)*delta;
		float diffY = (toPos.y - atPos.y)*delta;
		
		float z = (posZ - origin.z)*delta;
		origin.z += z;
		atPos.add(diffX, diffY, 0);
		
		this.camera.position.set(atPos.x, atPos.y, origin.z);
	}
	
	public void translateZ(float z) {
		float diffZ = z - origin.z;
		
		if(Math.abs(diffZ) > 10) {
			posZ += diffZ;
		}
	}
	
	public static ShaderManager getInstance() {
		return INSTANCE;
	}

}
