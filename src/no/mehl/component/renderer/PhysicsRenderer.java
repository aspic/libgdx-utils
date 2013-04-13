package no.mehl.component.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
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
import no.mehl.component.physics.MarblePhysics;
import no.mehl.component.physics.MoveableQuad;
import no.mehl.component.physics.StaticQuad;
import no.mehl.component.physics.StaticRectangle;
import no.mehl.libgdx.utils.ShaderManager;

public class PhysicsRenderer extends Renderer {
	
	private Mesh mesh;
	
	private Texture texture;
	private Physics physics;
	private boolean follow;
	private ShaderProgram shader;
	
	public PhysicsRenderer() {}
	
	public PhysicsRenderer(Color color) {
		setColor(color);
	}
	
	@Override
	public void loadClient(GameEntity entity) {
		physics = entity.getExtends(Physics.class);
		
		if(physics instanceof MarblePhysics) {
			StillModel model = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/jatteplanet.obj"));
			mesh = model.getSubMeshes()[0].getMesh();
			texture = new Texture(Gdx.files.internal("overlay/marble-normal.jpg"));
			rotate = true;
		} 
		else {
			StillModel model = ModelLoaderRegistry.loadStillModel(Gdx.files.internal("models/beveled_box.obj"));
			mesh = model.getSubMeshes()[0].getMesh();
			int rnd = MathUtils.random(1);
			texture = new Texture(Gdx.files.internal("overlay/metal_normal.png"));
		}
		if(color == null) setColor(Color.WHITE);
		
		shader = ShaderManager.getInstance().compileShader("normal", "shaders/normal2.vert", "shaders/normal2.frag");
		shader.begin();
		shader.setUniformi("u_normal", 0);
		shader.end();
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
	private float angle = 0;
	private Matrix4 combined;
	private boolean rotate;
	
	public void setCameraFollow(boolean follow)	 {
		this.follow = follow;
	}
	
	@Override
	public void runServer(GameEntity entity, float delta) {
//		runClient(entity, delta);
	}
	
	@Override
	public void runClient(GameEntity entity, float delta) {
		if(physics == null) return;
		
		Vector3 position = physics.getPosition();
		
		PerspectiveCamera camera = ShaderManager.getInstance().getCamera();
		Vector3 rotationAxis = new Vector3();
		
		if(rotate) {
			Vector3 velocity = new Vector3(physics.getVelocity().x, physics.getVelocity().y, 0);
			float length = velocity.len();
			
			rotationAxis = surfaceNormal.cpy().crs(velocity.mul(1/length));
			angle += length;
			
			Matrix4 temp = matrix.idt();
			temp.rotate(rotationAxis, angle);
			matrix.mul(temp);
		}
		
		if(follow) {
			ShaderManager.getInstance().translate(position.x, position.y, position.z + physics.getDimension().depth);
			ShaderManager.getInstance().keepZDistance(position.z);
		}
		
		combined = camera.combined.cpy();
		combined.translate(position.x, position.y, physics.getDimension().getDepth() * 0.5f + position.z);
		combined.rotate(surfaceNormal, physics.getAngle()*MathUtils.radDeg);
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
    	if(color != null) {
    		shader.setUniformf("u_color", color);
    	}
		mesh.render(shader, GL20.GL_TRIANGLES);
		shader.end();
		Gdx.gl20.glDisable(GL20.GL_DEPTH_TEST);
		Gdx.gl20.glDisable(GL20.GL_CULL_FACE);
	}
}
