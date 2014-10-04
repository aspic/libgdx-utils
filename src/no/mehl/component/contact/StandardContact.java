package no.mehl.component.contact;

import no.mehl.component.Contact;
import no.mehl.component.GameEntity;

/**
 * A response describing standard behaviour.
 * @author Kjetil Mehl <kjetil@no.logic.no.mehl.jd.logic.entity.logic.no>
 */
public class StandardContact extends Contact {

	@Override
	public void collidesWith(GameEntity contact) {
//		if(contact instanceof DestroyContact) {
//			this.entity.setAlive(false);
//		} else {
//			System.out.println("No case for this collision.");
//		}
	}
}
