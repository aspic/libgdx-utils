package no.mehl.component.physics;

import no.mehl.component.GameEntity;
import no.mehl.component.Physics;
import no.mehl.component.UserData;
import no.mehl.libgdx.utils.Dimension;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class MarblePhysics extends Physics {

	public MarblePhysics() {
		this(new UserData(), new Vector2(), new Dimension(1));
	}
	
	public MarblePhysics(UserData data, Vector2 position, Dimension dimension) {
		this(data, new Vector3(position.x, position.y, 0), dimension);
	}
	
	public MarblePhysics(UserData data, Vector3 position, Dimension dim) {
		this.position = position;
		this.dim = dim;
		this.data = data;
	}
	
	@Override
	public void loadClient(GameEntity entity) {
		this.body = entity.getWorld().createBody(createBodyDef());
		super.loadBody(entity);
		// Set shape and fixture
		getPosition();
	}
	
	@Override
	public void runServer(GameEntity entity, float step) {
		// Do server side updating
		if(this.position.z < -10f) {
			entity.setAlive(false);
		}
		
		this.position.z += this.velocity.z * step;
		this.velocity.z += gravityZ * step;
		
		// Always reset gravity
		this.setGravityZ(GRAV_Z);
		setChanged();
	}
	
	@Override
	protected BodyDef createBodyDef() {
		BodyDef def = new BodyDef();
		
		if(data.contains(UserData.D_BODY)) {
			def.type = BodyType.valueOf((String)data.get(UserData.D_BODY));
		} else {
			def.type = BodyType.DynamicBody;
		}
		def.linearDamping = 0.5f;
		
		
		return def;
	}
	
	public void doJump() {
		if(Math.abs(this.velocity.z) < 0.5f) {
			this.velocity.z = 30f;
			this.gravityZ = GRAV_Z;
		}
	}

	@Override
	public void updateFixture() {
		CircleShape s = new CircleShape();
		s.setRadius(dim.getRadius());
		
		Fixture fix = body.createFixture(s, 1f);
		fix.setUserData(data);
		if(data.contains(UserData.D_SENSOR)) {
			fix.setSensor((Boolean)data.get(UserData.D_SENSOR));
		}
		
		s.dispose();
	}
}
