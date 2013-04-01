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
import com.badlogic.gdx.physics.box2d.World;

public class StaticRectangle extends Physics {
	
	public StaticRectangle() {}
	
	public StaticRectangle(Vector2 position, Dimension dimension) {
		this(new Vector3(position.x, position.y, 0), dimension);
	}
	
	public StaticRectangle(Vector3 position, Dimension dimension) {
		this.position = position;
		this.dim = dimension;
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
