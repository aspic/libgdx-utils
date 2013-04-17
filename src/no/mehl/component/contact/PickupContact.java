package no.mehl.component.contact;

import com.badlogic.gdx.graphics.Color;

import no.mehl.component.Contact;
import no.mehl.component.GameEntity;
import no.mehl.component.renderer.ModelRenderer;

/**
 * The {@link PickupContact} will give the touched entity some property.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class PickupContact extends Contact {
	
	@Override
	public void handle(GameEntity with) {
		ModelRenderer r = new ModelRenderer(Color.GREEN);
		with.attachComponent(r);
		
		// Destroy this item
		entity.setAlive(false);
	}
}
