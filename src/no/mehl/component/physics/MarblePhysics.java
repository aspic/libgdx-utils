package no.mehl.component.physics;

import no.mehl.component.GameEntity;
import no.mehl.component.Physics;
import no.mehl.libgdx.utils.Dimension;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Shape;

public class MarblePhysics extends Physics {

	public MarblePhysics() {}
	
	public MarblePhysics(Userdata data, Vector2 position, Dimension dimension) {
		this(data, new Vector3(position.x, position.y, 0), dimension);
	}
	
	public MarblePhysics(Userdata data, Vector3 position, Dimension dim) {
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
		def.type = BodyType.DynamicBody;
		if(position != null) {
			def.position.set(position.x, position.y);
			def.linearDamping = 1f;
		}
		return def;
	}
	
	public void doJump() {
		this.velocity.z = 30f;
		this.gravityZ = GRAV_Z;
	}

	@Override
	public void updateFixture() {
		CircleShape s = new CircleShape();
		s.setRadius(dim.getRadius());
		
		body.createFixture(s, 1f);
		s.dispose();
	}
}
