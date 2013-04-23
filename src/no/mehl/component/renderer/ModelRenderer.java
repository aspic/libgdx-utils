package no.mehl.component.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.loaders.ModelLoaderRegistry;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import no.mehl.component.GameEntity;
import no.mehl.component.Physics;
import no.mehl.component.Renderer;
import no.mehl.component.Snapshot;
import no.mehl.libgdx.utils.ShaderManager;

public class ModelRenderer extends Renderer {
	
	private final static String path = "models/";
	
	private StillModel mesh;
	private Texture texture;
	private Physics physics;
	private ShaderProgram shader;
	private Camera camera;
	
	// Field filled
	private String objectKey;
	
	public ModelRenderer() {
		this(null);
	}
	
	/** Renderer based on first available texture */
	public ModelRenderer(String objKey) {
		this(null, objKey, Color.WHITE);
	}
	
	public ModelRenderer(String texKey, String objKey, Color color) {
		this.key = texKey != null ? texKey : listTextures()[0];
		this.objectKey = objKey != null ? objKey : Model.BOX.file;
		setColor(color);
	}
	
	@Override
	public void loadClient(GameEntity entity) {
		physics = entity.getExtends(Physics.class);
		
		mesh = ModelLoaderRegistry.loadStillModel(Gdx.files.internal(ModelRenderer.path + objectKey));
		texture = new Texture(Gdx.files.internal(key));
		
		shader = ShaderManager.getInstance().compileShader("normal", "shaders/normal2.vert", "shaders/normal2.frag");
		shader.begin();
		shader.setUniformi("u_normal", 0);
		shader.end();
		
		camera = ShaderManager.getInstance().getCamera();
	}
	
	private Vector3 initialLight = new Vector3(1f, 1f, 5f);
	private Vector3 lightDir = new Vector3();
	private Matrix4 matrix = new Matrix4();
	private Vector3 surfaceNormal = new Vector3(0, 0, 1f);

	/**
	 * n = the normal
	 * d = the vector pushing the ball (vel)
	 * 
	 * axis = n x d / length(d)
	 */
	private float rotateAngle = 0;
	private Matrix4 combined;
	private boolean rotate;
	private Vector3 rotVector = new Vector3();
	private Vector3 rotationAxis = new Vector3();
	
	@Override
	public void runClient(GameEntity entity, float delta) {
		
		if(physics == null) return;
		
		
		if(reload) {
			loadClient(entity);
			reload = false;
		}
		
		Vector3 position = physics.getPosition();
		
		if(rotate) {
			rotVector.set(physics.getVelocity().x, physics.getVelocity().y, 0);
			float length = rotVector.len();
			
			if(length != 0) {
				rotationAxis = surfaceNormal.cpy().crs(rotVector.scl(1f/length));
			}
			
			
			rotateAngle += length;
			
			Matrix4 temp = matrix.idt();
			temp.rotate(rotationAxis, rotateAngle);
			matrix.mul(temp);
		}
		
		if(follow) {
			ShaderManager.getInstance().translate(position.x, position.y, position.z + physics.getDimension().depth);
			ShaderManager.getInstance().keepZDistance(position.z);
		}
		
		combined = camera.combined.cpy();
		combined.translate(position.x, position.y, physics.getDimension().getDepth() * 0.5f + position.z);
		if(!rotate) combined.rotate(surfaceNormal, physics.getAngle()*MathUtils.radDeg);
		combined.scale(physics.getDimension().width, physics.getDimension().height, physics.getDimension().depth);
		
		combined.mul(matrix);
		lightDir.set(initialLight);

		// Enable
		Gdx.gl20.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
		
		texture.bind(0);
		shader.begin();
		shader.setUniformMatrix("u_MVMatrix", combined);
    	shader.setUniformf("u_lightDir", lightDir);
		if(color != null) shader.setUniformf("u_color", color);
		mesh.render(shader);
		shader.end();
		Gdx.gl20.glDisable(GL20.GL_DEPTH_TEST);
		Gdx.gl20.glDisable(GL20.GL_CULL_FACE);
	}
	
	boolean reload;
	
	@Override
	public Renderer fill(Snapshot snapshot) {
		// Reload texture
		if(((snapshot.s_0 != null && key != null) && !key.equals(snapshot.s_0)) 
				|| (snapshot.s_1 != null && objectKey != null) && !objectKey.equals(snapshot.s_1)) {
			reload = true;
			
			this.objectKey = snapshot.s_1;
		}
		
		return super.fill(snapshot);
	}
	
	@Override
	public Snapshot getSnapshot(boolean delta) {
		if(!delta) snapshot.s_1 = objectKey;
		
		return super.getSnapshot(delta);
	}

	@Override
	public String[] listTextures() {
		return new String[]{
			"overlay/marble-normal.jpg",
			"overlay/metal_normal.png",
			"overlay/ground_normal.png",
		};
	}
	
	public void setRotate(boolean b) {
		this.rotate = b;
	}
	
	public enum Model {
		SPHERE("jatteplanet.obj"), DISC("disc.obj"), BOX("beveled_box.obj");
		public String file;
		Model(String file) {
			this.file = file;
		}
		
		public String toString() {
			return this.file;
		}
		
	}
}
