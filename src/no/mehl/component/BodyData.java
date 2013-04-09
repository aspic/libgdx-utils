package no.mehl.component;

import no.mehl.libgdx.utils.Dimension;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/**
 * A pure data class referenced by all fixtures. This data will be used to handle collisions.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class BodyData {
	private int id;
	private Physics physics;
	
	public BodyData(int id) {
		this.id = id;
	}
	
	public BodyData setPhysics(Physics physics) {
		this.physics = physics;
		return this;
	}
	
	public int getId() {
		return this.id;
	}
	
	public Vector3 getPosition() {
		return this.physics.getPosition();
	}
	
	public Dimension getDimension() {
		return this.physics.getDimension();
	}
	
	public static boolean collides(BodyData one, BodyData two) {
		float z1 = one.getPosition().z;
		float z2 = two.getPosition().z;
		
		float depth1 = z1 + one.getDimension().getDepth();
		float depth2 = z2 + two.getDimension().getDepth();
		
		if((z1 < z2 && depth1 <= z2) || (z2 < z1 && depth2 <= z1)) return false;
		return true;
	}
	
	public static boolean landedOn(BodyData one, BodyData two) {
		float z1 = one.getPosition().z;
		float z2 = two.getPosition().z;
		
		float height1 = z1 + one.getDimension().getDepth();
		float height2 = z2 + two.getDimension().getDepth();
		
		if(z1 > z2 && (z1 < height2 && (height1 - height2 >= -one.getDimension().getDepth()))) {
			one.landed(height2);
			return true;
		} else if(z2 > z1 && (z2 < height1 && (height2 - height1 >= -two.getDimension().getDepth()))) {
			two.landed(height1);
			return true;
		}
		return false;
	}
	
	private void landed(float posZ) {
		if(this.physics.getBody().getType() == BodyType.StaticBody) return;
		
		this.physics.setPosZ(posZ);
		this.physics.setGravityZ(0);
	}

//	public void resetGravity() {
//		this.physics.setGravityZ(Configuration.GRAVITY_Z);
//	}
}
