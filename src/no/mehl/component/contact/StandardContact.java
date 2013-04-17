package no.mehl.component.contact;

import no.mehl.component.Contact;
import no.mehl.component.GameEntity;

/**
 * A response describing standard behaviour.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class StandardContact extends Contact {

	@Override
	public void handle(GameEntity contact) {
//		if(contact instanceof DestroyContact) {
//			this.entity.setAlive(false);
//		} else {
//			System.out.println("No case for this collision.");
//		}
	}
}
