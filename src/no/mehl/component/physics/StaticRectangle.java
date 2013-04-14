package no.mehl.component.physics;

import no.mehl.component.GameEntity;
import no.mehl.component.Physics;
import no.mehl.component.UserData;
import no.mehl.libgdx.utils.Dimension;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class StaticRectangle extends Physics {
	
	public StaticRectangle() {
		this(new UserData());
	}
	
	public StaticRectangle(Userdata data, Vector2 position, Dimension dimension) {
		this(data, new Vector3(position.x, position.y, 0), dimension);
	}
	
	public StaticRectangle(Userdata data, Vector3 position, Dimension dimension) {
		this.position = position;
		this.dim = dimension;
		this.data = data;
	}

	public StaticRectangle(Userdata data) {
		this.data = data;
	}

	@Override
	public void loadClient(GameEntity entity) {
		this.body = entity.getWorld().createBody(createBodyDef());
		super.loadBody(entity);
		
		Vector2 force = null;
		if((force = data.get(UserData.D_FORCE, Vector2.class)) != null) {
			applyForce(force.x, force.y);
		}
		
		getPosition();
	}

	@Override
	protected BodyDef createBodyDef() {
		BodyDef def = new BodyDef();
		
		BodyType type = data.get(UserData.D_BODY, BodyType.class);
		def.type = type != null ? type : BodyType.StaticBody;
		
		return def;
	}

	@Override
	public void updateFixture() {
		PolygonShape s = new PolygonShape();
		s.setAsBox(dim.getWidth()*0.5f, dim.getHeight()*0.5f);
		
		body.createFixture(s, 1f).setUserData(data);
		s.dispose();
	}
}
