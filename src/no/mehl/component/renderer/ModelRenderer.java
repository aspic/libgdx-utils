package no.mehl.component.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
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
	
	private com.badlogic.gdx.graphics.g3d.Model mesh;
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
		this.objectKey = objKey != null ? objKey : Model.BOX.file;
		setColor(color);
	}
	
	@Override
	public void loadClient(GameEntity entity) {
		physics = entity.getExtends(Physics.class);
		
		mesh = new ObjLoader().loadObj(Gdx.files.internal(ModelRenderer.path + objectKey));
		texture = new Texture(Gdx.files.internal(key));
		
		camera = ShaderManager.getInstance().getCamera();
		
		if(objectKey.equals(Model.SPHERE.file)) {
			rotate = true;
		}
		
		material = new Material(ColorAttribute.createDiffuse(color), new TextureAttribute(TextureAttribute.Normal, texture));
		modelBatch = new ModelBatch();
		mesh = ModelBuilder.createFromMesh(mesh.meshes.get(0), GL20.GL_TRIANGLES, material);
		instance1 = new ModelInstance(mesh);
		
//		com.badlogic.gdx.graphics.g3d.Model test = modelBuilder.createBox(1, 1, 1, new Material(new TextureAttribute(TextureAttribute.Normal, texture), ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
//		instance2 = new ModelInstance(test);
		
		System.out.println("HERE?");
	}
	
	private ModelBatch modelBatch;
	private ModelInstance instance1;
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

		Matrix4 modelMat = instance1.transform.cpy();
		instance1.transform.translate(position.x, position.y, physics.getDimension().getDepth() * 0.5f + position.z);
		instance1.transform.rotate(surfaceNormal, physics.getAngle()*MathUtils.radDeg);
		instance1.transform.scale(physics.getDimension().width, physics.getDimension().height, physics.getDimension().depth);
		
		modelBatch.begin(camera);
		modelBatch.render(instance1, ShaderManager.getInstance().getLights());
		modelBatch.end();
		
		instance1.transform.set(modelMat);
		
		if(follow) {
			ShaderManager.getInstance().translate(position.x, position.y, position.z + physics.getDimension().depth + 15f);
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
