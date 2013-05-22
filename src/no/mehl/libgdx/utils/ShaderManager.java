package no.mehl.libgdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.lights.PointLight;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * A simple class to manage several instances of {@link ShaderProgram}.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class ShaderManager {

	private static final ShaderManager INSTANCE = new ShaderManager();
	
	private PerspectiveCamera camera;
	private SpriteBatch spriteBatch;
	private DecalBatch decalBatch;
	
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
		shader.pedantic = false;
		this.shaders.put(key, shader);
		return shader;
	}
	
	public PerspectiveCamera getCamera() {
		return this.camera;
	}
	
	public void shift(float dX, float dY, float dZ) {
		toPos.x += dX;
		toPos.y += dY;
		toPos.z += dZ;
		
		this.camera.position.add(0, 0, dZ);
	}
	
	public void translate(float x, float y, float z) {
		toPos.x = x;
		toPos.y = y;
//		toPos.z = z;
	}
	
	public void updateCamera(float delta) {
		float diffX = (toPos.x - this.camera.position.x)*10*delta;
		float diffY = (toPos.y - this.camera.position.y)*10*delta;
		float diffZ = (toPos.z - this.camera.position.z)*2*delta;

		this.camera.position.add(diffX, diffY, 0);
//		this.camera.position.z = 5f;
//		this.camera.lookAt(this.camera.position.x, this.camera.position.y+10, 0);
		
		this.camera.update();
	}
	
	public static ShaderManager getInstance() {
		return INSTANCE;
	}

	public void keepZDistance(float value) {
		float diff = camera.position.z - value;
		if(diff < 20) {
			toPos.z += .5f;
		} else if(diff > 40) {
			toPos.z -= .5f;
		}
	}
	
	public void updateBatch(Matrix4 projection) {
		if(this.spriteBatch != null) this.spriteBatch.setProjectionMatrix(projection);
	}
	
	/** Returns a new {@link SpriteBatch **/
	public SpriteBatch getSpriteBatch() {
		if(this.spriteBatch == null) {
			spriteBatch = new SpriteBatch();
			spriteBatch.setProjectionMatrix(camera.combined);
		}
		return spriteBatch;
	}
	/** 
	 * Creates and returns a new {@link DecalBatch}.
	 * If {@Decal} is added to this batch, call flush at the end of the manager run method. 
	 */
	public DecalBatch getDecalBatch() {
		if(decalBatch == null) {
			decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
		}
		return decalBatch;
	}
	private Lights lights;
	
	public Lights getLights() {
		if(lights == null) {
			lights = new Lights();
			lights.ambientLight.set(0.3f, 0.3f, 0.3f, 1f);
			lights.add(new DirectionalLight().set(Color.GRAY, 5, 5f, -5));
			lights.add(new PointLight().set(new Color(1f, 1f, 1f, 1f), new Vector3(1f, 1f, 1f), 100f));
		}
		return lights;
	}
	
	public static Vector3 rotatePoint(Vector3 point, Vector3 aroundPoint, float degrees) {
		float theta = (degrees - 180f) * MathUtils.degreesToRadians;
		float x = MathUtils.cos(theta) * (point.x - aroundPoint.x)
				- MathUtils.sin(theta) * (point.y - aroundPoint.y)
				+ aroundPoint.x;
		float y = MathUtils.sin(theta) * (point.x - aroundPoint.x)
				+ MathUtils.cos(theta) * (point.y - aroundPoint.y)
				+ aroundPoint.y;
		return point.set(x, y, 5f);
	}
}
