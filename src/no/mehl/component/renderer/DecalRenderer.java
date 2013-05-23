package no.mehl.component.renderer;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.lights.PointLight;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import no.mehl.component.EntityManager;
import no.mehl.component.GameEntity;
import no.mehl.component.Physics;
import no.mehl.component.Renderer;
import no.mehl.component.Snapshot;
import no.mehl.libgdx.ui.UIManager;
import no.mehl.libgdx.utils.ShaderManager;

public class DecalRenderer extends Renderer {
	
	protected Decal decal;
	protected Physics physics;
	private float rotationY;
	private PointLight light = new PointLight();
	
	public DecalRenderer() {
		this("staticSpaceObject");
	}
	
	public DecalRenderer(String key) {
		this.key = key;
	}
	
	@Override
	public void loadClient(GameEntity entity) {
		physics = entity.getExtends(Physics.class);
		if(physics != null) {
			System.out.println(physics.getDimension());
			decal = Decal.newDecal(physics.getDimension().width, physics.getDimension().height, EntityManager.assets.get(key), true);
			if(color != null) decal.setColor(color.r, color.g, color.b, color.a);
			light.color.set(Color.WHITE);
			light.intensity = 20f;
			ShaderManager.getInstance().getLights().add(light);
		}
	}
	
	@Override
	public void setColor(Color color) {
		if(decal != null) decal.setColor(color.r, color.g, color.b, color.a);
		super.setColor(color);
	}

	@Override
	public void runServer(GameEntity entity, float delta) {
		
	}
	
	float dist = 7f;
	
	@Override
	public void runClient(GameEntity entity, float delta) {
		if(physics != null) {
			Vector3 position = physics.getPosition();
			decal.setPosition(position.x + offset.x, position.y + offset.y, position.z + offset.z);
			
			if(up != null && rot != null) {
				decal.setRotation(rot, up);
			} else {
				decal.setRotationZ(physics.getAngle()*MathUtils.radDeg);
			}
			
			ShaderManager.getInstance().getDecalBatch().add(decal);
			if(follow) {
				ShaderManager.getInstance().getCamera().position.set(position.x, position.y - dist, 5f);
				ShaderManager.getInstance().getCamera().lookAt(position.x, position.y + dist, 0);
//				ShaderManager.rotatePoint(ShaderManager.getInstance().getCamera().position, position, physics.getAngle()*MathUtils.radDeg);
//				ShaderManager.getInstance().keepZDistance(position.z);
				float x = MathUtils.sin(physics.getAngle());
				float y = MathUtils.cos(physics.getAngle());
				
				light.position.set(position.x + 2*x, position.y + 2*y, 0);
			}
		}
	}
	
	@Override
	public Renderer fill(Snapshot snapshot) {
		if((key != null && snapshot.s_0 != null) && !key.equals(snapshot.s_0)) {
			if(decal != null) {
				decal.setTextureRegion(EntityManager.assets.get(snapshot.s_0));
			}
		}
		return super.fill(snapshot);
	}
	
	@Override
	public Snapshot getSnapshot(boolean delta) {
		if(!delta) snapshot.s_0 = key;
		
		return super.getSnapshot(delta);
	}

	@Override
	public String[] listTextures() {
		return EntityManager.assets.listTextures();
	}

	public void setRotationY(float rot) {
		this.rotationY = rot;
	}
	public void setRotationX(int rot) {
		decal.setRotationX(rot);
	}
	
	private Vector3 rot, up;
	
	public void setRotation(Vector3 rot, Vector3 up) {
		this.rot = rot;
		this.up = up;
	}
	
	public Vector3 getRot() {
		return this.rot;
	}
	
	public Vector3 getUp() {
		return this.up;
	}
}
