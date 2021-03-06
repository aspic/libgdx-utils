package no.mehl.libgdx.utils;

import java.awt.LinearGradientPaint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * A simple class to manage several instances of {@link ShaderProgram}.
 * @author Kjetil Mehl <kjetil@no.logic.no.mehl.jd.logic.entity.logic.no>
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
	
	public Camera load(float aspect, float width, float height, float depth, float far) {
		camera = new PerspectiveCamera(45, width * aspect, height);
		camera.far = far;
		camera.position.z = depth;
		posZ = depth;
		origin = new Vector3(camera.position);
		
		decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
		
		return camera;
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
		toPos.set(x, y, z);
	}
	
	Vector3 diff = new Vector3();
	
	public void updateCamera(float delta) {
		
		float dX = (toPos.x - camera.position.x)*0.5f;
		float dY = (toPos.y - camera.position.y)*0.5f;
//		float dZ = (toPos.z - camera.position.z)*delta;
		
//		System.out.println(diffX);
//		camera.position.add(dX, dY, 0);
//		this.camera.position.set(toPos);
//		
//		diffX = (toLookAt.x - this.camera.direction.x)*10*delta;
//		diffY = (toLookAt.y - this.camera.direction.y)*10*delta;
//		diffZ = (toLookAt.z - this.camera.direction.z)*10*delta;
//		this.camera.direction.add(diffX, diffY, diffZ);
		
//		this.camera.position.z = 5f;
		
//		this.camera.update();
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
		return decalBatch;
	}
	private Environment lights;
	
	public Environment getLights() {
		if(lights == null) {
			lights = new Environment();
			lights.add(new DirectionalLight().set(new Color(0.9f, 0.8f, 0.8f, 1f), 1, 1, -1));
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
