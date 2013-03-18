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
	private Vector3 toPos = new Vector3();

	float posZ;
	
	private ShaderManager() {
		shaders = new ObjectMap<String, ShaderProgram>();
	}
	
	/**
	 * Loads this {@link ShaderManager}
	 * @param width Viewport width
	 * @param height Viewport height
	 */
	public void load(float aspect, float width, float height, float depth) {
		load(aspect, width, height, depth, 200f);
	}
	
	public void load(float aspect, float width, float height, float depth, float far) {
		camera = new PerspectiveCamera(45, width * aspect, height);
		camera.far = far;
		camera.position.z = depth;
		posZ = depth;
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
	
	public void translate(float x, float y, float z) {
		toPos.x = x;
		toPos.y = y;
	}
	
	public void updateCamera(float delta) {
		float diffX = (toPos.x - this.camera.position.x)*2*delta;
		float diffY = (toPos.y - this.camera.position.y)*2*delta;
		float diffZ = (toPos.z - this.camera.position.z)*2*delta;

		this.camera.position.add(diffX, diffY, diffZ);
		
		this.camera.update();
	}
	
	public static ShaderManager getInstance() {
		return INSTANCE;
	}

	public void keepZDistance(float value) {
		
		float diff = camera.position.z - value;
		if(diff < 15) {
			toPos.z += .5f;
		} else if(diff > 20) {
			toPos.z -= .5f;
		}
	}
}
