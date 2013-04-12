package no.mehl.component.physics;

import no.mehl.component.GameEntity;
import no.mehl.component.Physics;
import no.mehl.libgdx.utils.Dimension;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class StaticQuad extends Physics {
	
	public StaticQuad() {}
	
	public StaticQuad(Userdata data, Vector2 position, Dimension dimension) {
		this(data, new Vector3(position.x, position.y, 0), dimension);
	}
	
	public StaticQuad(Userdata data, Vector3 position, Dimension dimension) {
		this.position = position;
		this.dim = dimension;
		this.data = data;
	}

	@Override
	public void load(GameEntity entity) {
		this.body = entity.getWorld().createBody(createBodyDef());
		super.loadBody(entity);
		if(data != null) body.setUserData(data.load(entity, this));
		
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

	@Override
	public void accelerate(float force) {
		
	}

	@Override
	public void updateFixture() {
		// Update fixture
		PolygonShape s = new PolygonShape();
		s.setAsBox(dim.getWidth() * 0.5f, dim.getHeight() * 0.5f);

		body.createFixture(s, 1f);
		s.dispose();
	}
}
