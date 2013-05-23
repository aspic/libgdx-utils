package no.mehl.component.input;

import com.badlogic.gdx.math.Vector2;

import no.mehl.component.GameEntity;
import no.mehl.component.Input;
import no.mehl.component.physics.Box2DPhysics;

public class StandardInput extends Input {
	
	Vector2 input = new Vector2();
	
	@Override
	public void addForce(GameEntity entity, float acclX, float acclY) {
		Box2DPhysics p = entity.getExtends(Box2DPhysics.class);
//		System.out.println(p);
		if(p != null) {
			input.set(acclX, acclY);
			input.nor();
			p.updateImpulse(input.x*5f, input.y*5f, 0);
		}
	}

	@Override
	public void doJump(GameEntity entity) {
		Box2DPhysics p = entity.getExtends(Box2DPhysics.class);
//		if(p != null) p.a
		if(p != null) p.updateForceZ(30f);
		
	}

	@Override
	public void loadClient(GameEntity entity) {
		
	}

	@Override
	public void setInput(Object object) {
		// TODO Auto-generated method stub
		
	}
}
