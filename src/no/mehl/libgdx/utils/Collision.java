package no.mehl.libgdx.utils;


import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import no.mehl.component.Contact;
import no.mehl.component.EntityManager;
import no.mehl.component.GameEntity;
import no.mehl.component.Physics;
import no.mehl.component.UserData;
import no.mehl.component.physics.Box2DPhysics;

/** Simple class for collision in a 3D space */
public class Collision {
	
	public static boolean collides(UserData one, UserData two) {
		Physics p1 = one.getEntity().getExtends(Box2DPhysics.class);
		Physics p2 = two.getEntity().getExtends(Box2DPhysics.class);
		
		if(p1 == null || p2 == null) return false;
		
		float z1 = p1.getPosition().z;
		float z2 = p2.getPosition().z;
		
		float depth1 = p1.getDimension().getDepth();
		float depth2 = p2.getDimension().getDepth();
		
		boolean collides = true;
		
		if(z1 > z2 && z1 - depth1 > z2) {
			collides = false;
		} else if(z2 > z1 && z2 - depth2 > z1) {
			collides = false;
		}
		
		
		return collides;
		
	}
	
	public static boolean landedOn(UserData one, UserData two) {
		Box2DPhysics p1 = one.getEntity().getExtends(Box2DPhysics.class);
		Box2DPhysics p2 = two.getEntity().getExtends(Box2DPhysics.class);
		
		if(p1 == null || p2 == null) return false;
		
		float z1 = p1.getPosition().z;
		float z2 = p2.getPosition().z;
		
		float height1 = z1 + p1.getDimension().getDepth();
		float height2 = z2 + p2.getDimension().getDepth();
		
		if(z1 > z2 && (z1 < height2 && (height1 - height2 > -p1.getDimension().getDepth()))) {
			p1.landed(height2);
			return true;
		} else if(z2 > z1 && (z2 < height1 && (height2 - height1 > -p2.getDimension().getDepth()))) {
			p2.landed(height1);
			return true;
		}
		return false;
	}
	
	/** A listener for enabling 2D and 3D contacts */
	public static ContactListener get3DListener(final EntityManager manager) {
		return new ContactListener() {
			public void preSolve(com.badlogic.gdx.physics.box2d.Contact contact, Manifold oldManifold) {
				UserData body1 = (UserData)contact.getFixtureA().getUserData();
				UserData body2 = (UserData)contact.getFixtureB().getUserData();
				
				if(body1 == null || body2 == null) return;
				
				if(!Collision.collides(body1, body2)) {
					contact.setEnabled(false);
				} else if(Collision.landedOn(body1, body2)) {
					contact.setEnabled(false);
				}
			}
			
			public void postSolve(com.badlogic.gdx.physics.box2d.Contact contact, ContactImpulse impulse) {}
			
			public void endContact(com.badlogic.gdx.physics.box2d.Contact contact) {}
			
			public void beginContact(com.badlogic.gdx.physics.box2d.Contact contact) {
				UserData body1 = (UserData)contact.getFixtureA().getUserData();
				UserData body2 = (UserData)contact.getFixtureB().getUserData();
				
				if(body1 == null || body2 == null) return;
				
				GameEntity e1 = body1.getEntity();
				GameEntity e2 = body2.getEntity();
				
				
				if(e1 == null || e2 == null) return;
				
				Contact cr1 = e1.getExtends(Contact.class);
				Contact cr2 = e2.getExtends(Contact.class);
				
				// Only do contact responses if both no.no.logic.no.mehl.jd.logic.entity.logic.no.mehl.jd.logic have such component defined
				if(cr1 != null && !cr1.isHandled() && cr2 != null && !cr2.isHandled()) {
					cr1.collidesWith(e2);
					cr2.collidesWith(e1);
				}
			}
		};
	}
}
