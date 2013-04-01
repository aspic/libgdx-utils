package no.mehl.component.physics;

import no.mehl.component.BodyData;
import no.mehl.component.Contact;
import no.mehl.component.Dimension;
import no.mehl.component.GameEntity;
import no.mehl.component.Physics;
import no.mehl.component.contact.DestroyContact;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class MoveableRectangle extends Physics {
	
	public MoveableRectangle() {}
	
	public MoveableRectangle(Vector2 position, Dimension dimension) {
		this(new Vector3(position.x, position.y, 0), dimension, null);
	}
	
	public MoveableRectangle(Vector3 position, Dimension dimension, Vector3 toPos) {
		this.position = position;
//		this.startPos = 
		this.dim = dimension;
		this.velocity = new Vector3(3f, 3f, 3f);
		this.toPos = toPos;
	}

	@Override
	public void load(GameEntity entity) {
		this.body = entity.getWorld().createBody(createBodyDef());
		this.body.setUserData(entity.getId());
		
		// Update fixture
		PolygonShape s = new PolygonShape();
		s.setAsBox(dim.getWidth()*0.5f, dim.getHeight()*0.5f);
		
		Fixture fix = body.createFixture(s, 1f);
		
		Contact contact = entity.getExtends(Contact.class);
		
		if(contact != null && contact instanceof DestroyContact) {
			fix.setSensor(true);
		}

		fix.setUserData(((BodyData)entity.getUserdata()).setPhysics(this));
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

}