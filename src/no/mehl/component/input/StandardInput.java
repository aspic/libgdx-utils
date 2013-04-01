package no.mehl.component.input;

import no.mehl.component.GameEntity;
import no.mehl.component.Input;
import no.mehl.component.Physics;

public class StandardInput extends Input {
	
	@Override
	public void addForce(GameEntity entity, float acclX, float acclY) {
		Physics p = (Physics)entity.getExtends(Physics.class);
		if(p != null) {
			p.applyForce(acclX * 5f, acclY * 5f);
		}
	}

	@Override
	public void doJump(GameEntity entity) {
		Physics p = (Physics)entity.getExtends(Physics.class);
		if(p != null) p.doJump();
	}

	@Override
	public void load(GameEntity entity) {
		
	}
}
