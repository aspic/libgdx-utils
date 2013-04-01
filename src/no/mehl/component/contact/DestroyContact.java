package no.mehl.component.contact;

import no.mehl.component.Component;
import no.mehl.component.Contact;
import no.mehl.component.GameEntity;
import no.mehl.component.renderer.PhysicsRenderer;

import com.badlogic.gdx.graphics.Color;

/**
 * The {@link DestroyContact} will destroy the ball if it is hit.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class DestroyContact extends Contact {
	
	@Override
	public void handle(Contact contact) {
		if(contact instanceof StandardContact) {
			PhysicsRenderer r = (PhysicsRenderer)entity.getComponent(Component.getComponentId(PhysicsRenderer.class));
			if(r != null) {
				r.setColor(Color.RED);
			}
		}
	}
}
