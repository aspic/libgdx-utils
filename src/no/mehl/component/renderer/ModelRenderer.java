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
import no.mehl.component.physics.MarblePhysics;
import no.mehl.libgdx.utils.ShaderManager;

public class ModelRenderer extends Renderer {
	
	private StillModel mesh;
	private Texture texture;
	private Physics physics;
	private ShaderProgram shader;
	private Camera camera;
	
	/** Renderer based on first available texture */
	public ModelRenderer() {
		this.key = listTextures()[0];
	}
	
	public ModelRenderer(Color color) {
		setColor(color);
		this.key = listTextures()[0];
	}
	
	@Override
	public void loadClient(GameEntity entity) {
		physics = entity.getExtends(Physics.class);
		
		if(physics instanceof MarblePhysics) {
			mesh = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/jatteplanet.obj"));
			rotate = true;
		} 
		else {
			mesh = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/beveled_box.obj"));
		}
		
		texture = new Texture(Gdx.files.internal(key));
		if(color == null) setColor(Color.WHITE);
		
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
			
			Vector3 rotationAxis = surfaceNormal.cpy().crs(rotVector.scl(1f/length));
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
		if((snapshot.s_0 != null && key != null) && !key.equals(snapshot.s_0)) {
			reload = true;
		}
		
		return super.fill(snapshot);
	}

	@Override
	public String[] listTextures() {
		return new String[]{
				"overlay/marble-normal.jpg",
				"overlay/metal_normal.png",
				"overlay/ground_normal.png",
		};
	}
}
