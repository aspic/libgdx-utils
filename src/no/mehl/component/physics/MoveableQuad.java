package no.mehl.component.physics;

import no.mehl.component.Contact;
import no.mehl.component.GameEntity;
import no.mehl.component.Physics;
import no.mehl.component.Snapshot;
import no.mehl.component.contact.DestroyContact;
import no.mehl.libgdx.utils.Dimension;
import no.mehl.libgdx.utils.Mutable;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class MoveableQuad extends Physics {
	
	public MoveableQuad() {}
	
	public MoveableQuad(Userdata data, Vector2 position, Dimension dimension) {
		this(data, new Vector3(position.x, position.y, 0), dimension, null);
	}
	
	public MoveableQuad(Userdata data, Vector3 position, Dimension dimension, Vector3 toPos) {
		this.position = position;
//		this.startPos = 
		this.dim = dimension;
		this.velocity = new Vector3(3f, 3f, 3f);
		this.toPos = toPos;
		this.data = data;
	}

	@Override
	public void load(GameEntity entity) {
		this.body = entity.getWorld().createBody(createBodyDef());
		super.loadBody();
		
		
		// Update fixture
		PolygonShape s = new PolygonShape();
		s.setAsBox(dim.getWidth()*0.5f, dim.getHeight()*0.5f);
		
		Fixture fix = body.createFixture(s, 1f);
		if(data != null) {
			fix.setUserData(data.load(entity, this));
		}
		s.dispose();
		
		getPosition();
	}

	@Override
	public void runServer(GameEntity entity, float step) {
		turn();
		
		float dX = velocity.x * step;
		float dY = velocity.y * step;
		float dZ = velocity.z * step;
		
		this.position.add(dX, dY, dZ);
		this.body.setTransform(position.x, position.y, 0);
		
		setChanged();
	}
	
	private void turn() {
		this.velocity.x *= turn(position.x - toPos.x);
		this.velocity.y *= turn(position.y - toPos.y);
		this.velocity.z *= turn(position.z - toPos.z);
	}
	
	private int turn(float diff) {
		if(diff >= 10f || diff < 0) return -1;
		return 1;
	}
	
	@Override
	public Snapshot get() {
		snapshot.v3_2 = null;
		return  super.get();
	}
	
	@Override
	public Snapshot getFull() {
		snapshot.v3_2 = toPos;
		return super.getFull();
	}
	
	@Override
	public Physics fill(Snapshot snapshot) {
		if(snapshot.v3_2 != null) this.toPos = new Vector3(snapshot.v3_2);
		return super.fill(snapshot);
	}
	
	
	@Override
	public void applyForce(float forceX, float forceY) {
		
	}

	@Override
	protected BodyDef createBodyDef() {
		BodyDef def = new BodyDef();
		def.type = BodyType.StaticBody;
		if(position != null) {
			def.position.set(position.x, position.y);
		}
		return def;
	}

	@Override
	public void accelerate(float force) {
		
	}

}
