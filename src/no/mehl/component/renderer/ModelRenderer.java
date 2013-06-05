package no.mehl.component.renderer;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import no.mehl.component.EntityManager;
import no.mehl.component.GameEntity;
import no.mehl.component.Physics;
import no.mehl.component.Renderer;
import no.mehl.component.Snapshot;
import no.mehl.libgdx.utils.ShaderManager;

public class ModelRenderer extends Renderer {
	
	public final static String path = "models/";
	
	private Model model;
	private Texture texture;
	private Physics physics;
	private Camera camera;
	private Material material;
	
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
		this.objectKey = objKey != null ? objKey : listTextures()[0];
		setColor(color);
	}
	
	@Override
	public void loadClient(GameEntity entity) {
		physics = entity.getExtends(Physics.class);
		model = EntityManager.assets.getModel(ModelRenderer.path + objectKey);
//		if(models == null) {
//			models = new ObjLoader().loadObj(Gdx.files.internal(ModelRenderer.path + objectKey));
//			meshes.put(objectKey, models);
//		}
		
		camera = ShaderManager.getInstance().getCamera();
		
//		if(objectKey.equals(Model.SPHERE.file)) {
//			rotate = true;
//		}
		
		material = new Material(ColorAttribute.createDiffuse(color));
		modelBatch = new ModelBatch();
		model = ModelBuilder.createFromMesh(model.meshes.get(0), GL20.GL_TRIANGLES, material);
		instance = new ModelInstance(model);
	}
	
	private ModelBatch modelBatch;
	private ModelInstance instance;
	private Matrix4 matrix = new Matrix4();
	private Vector3 surfaceNormal = new Vector3(0, 0, 1f);

	/**
	 * n = the normal
	 * d = the vector pushing the ball (vel)
	 * 
	 * axis = n x d / length(d)
	 */
	private float rotateAngle = 0;
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
		
//		combined.translate(position.x, position.y, physics.getDimension().getDepth() * 0.5f + position.z);
		
//		combined.scale(physics.getDimension().width, physics.getDimension().height, physics.getDimension().depth);
//		combined.mul(matrix);

		Matrix4 modelMat = instance.transform.cpy();
		instance.transform.translate(position.x, position.y, physics.getDimension().getDepth() * 0.5f + position.z);
		instance.transform.rotate(surfaceNormal, physics.getAngle()*MathUtils.radDeg);
		instance.transform.scale(physics.getDimension().width, physics.getDimension().height, physics.getDimension().depth);
		
		modelBatch.begin(camera);
		modelBatch.render(instance, ShaderManager.getInstance().getLights());
		modelBatch.end();
		
		instance.transform.set(modelMat);
		
		if(follow) {
			ShaderManager.getInstance().translate(position.x, position.y, position.z + physics.getDimension().depth + 30f);
//			ShaderManager.getInstance().keepZDistance(position.z);
		}
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
	
	public String[] listModels() {
		return new String[] {
			"jatteplanet.obj",
			"disc.obj",
			"beveled_box.obj"
		};
	}
}
