package no.mehl.component.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import no.mehl.component.EntityManager;
import no.mehl.component.GameEntity;
import no.mehl.component.Physics;
import no.mehl.component.Renderer;
import no.mehl.component.Snapshot;
import no.mehl.libgdx.utils.ShaderManager;

public class DecalRenderer extends Renderer {
	
	private Decal decal;
	private Physics physics;
	private float rotationY;
	
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
			decal = Decal.newDecal(physics.getDimension().width, physics.getDimension().height, EntityManager.assets.get(key));
			if(color != null) decal.setColor(color.r, color.g, color.b, color.a);
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
				ShaderManager.getInstance().translate(position.x, position.y, position.z + physics.getDimension().depth);
				ShaderManager.getInstance().keepZDistance(position.z);
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
