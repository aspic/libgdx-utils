package no.mehl.component.physics;

import no.mehl.component.GameEntity;
import no.mehl.component.Physics;
import no.mehl.libgdx.utils.Dimension;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class EdgePhysics extends Physics {
	
	public EdgePhysics() {}
	
	public EdgePhysics(Vector2 position, Dimension dimension) {
		this.position = new Vector3(position.x, position.y, 0);
		this.dim = dimension;
	}

	@Override
	public void applyForce(float forceX, float forceY) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void load(GameEntity entity) {
		this.body = entity.getWorld().createBody(createBodyDef());
		this.body.setUserData(entity.getId());
		EdgeShape shape = new EdgeShape();
		shape.set(new Vector2(position.x, position.y), new Vector2(position.x + dim.getWidth(), position.y + dim.getHeight()));
		
		body.createFixture(shape, 1f);
	}

	@Override
	protected BodyDef createBodyDef() {
		BodyDef def = new BodyDef();
		def.type = BodyType.StaticBody;
		return def;
	}
}
