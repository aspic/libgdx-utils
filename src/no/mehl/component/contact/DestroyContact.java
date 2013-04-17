package no.mehl.component.contact;

import no.mehl.component.Contact;
import no.mehl.component.GameEntity;

/**
 * The {@link DestroyContact} will destroy the ball if it is hit.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class DestroyContact extends Contact {
	
	@Override
	public void collidesWith(GameEntity with) {
//		if(contact instanceof StandardContact) {
//			ModelRenderer r = (ModelRenderer)entity.getComponent(Component.getComponentId(ModelRenderer.class));
//			if(r != null) {
//				r.setColor(Color.RED);
//			}
//		}
	}
}
