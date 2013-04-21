package no.mehl.component.contact;

import com.badlogic.gdx.graphics.Color;

import no.mehl.component.Contact;
import no.mehl.component.GameEntity;
import no.mehl.component.renderer.ModelRenderer;

/**
 * The {@link DestroyContact} will destroy the ball if it is hit.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class DestroyContact extends Contact {
	
	@Override
	public void collidesWith(GameEntity with) {
		with.getExtends(ModelRenderer.class).setColor(Color.RED);
	}
}
