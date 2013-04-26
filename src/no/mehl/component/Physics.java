package no.mehl.component;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import no.mehl.libgdx.utils.Compare;
import no.mehl.libgdx.utils.Dimension;
import no.mehl.libgdx.utils.Mutable;

/**
 * A generic physics component. It provides position, velocity, impulse,
 * angle and dimensions for some entity.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public abstract class Physics extends Component {

	// Fields
	protected Vector3 position = new Vector3();
	protected Vector3 velocity = new Vector3();
	protected Vector3 lastVel = new Vector3();
	protected Vector3 lastPos = new Vector3();
	protected Vector3 impulse = new Vector3();
	protected Vector3 toPos;
	protected float angle;
	
	protected Dimension dim;
	protected Snapshot snapshot = new Snapshot();
	protected Snapshot dS = new Snapshot();
	
	protected Snapshot prevSnapshot;
	protected Snapshot nextSnapshot;
	
	// Interpolation
	private float diff = 0;
	
	/** Loads the client specification, unless overridden */
	@Override
	protected void loadServer(GameEntity entity)  {
		loadClient(entity);
	}
	
	/** Will mark this component as changed */
	@Override
	public void runServer(GameEntity entity, float delta) {
		setChanged();
	}
	
	float acc = 0;
	
	@Override
	public void runClient(GameEntity entity, float delta) {
		if(EntityManager.interpolate && prevSnapshot != null && nextSnapshot != null) {
			
			float progress = acc/0.03f;
			
			Vector3 pos = nextSnapshot.v3_0;
			Vector3 vel = nextSnapshot.v3_1;
			Vector3 imp = nextSnapshot.v3_2;
			
			if(progress < 1f) {
				pos = Compare.interpolate(prevSnapshot.v3_0, pos, progress, Interpolation.linear);
				vel = Compare.interpolate(prevSnapshot.v3_1, vel, progress, Interpolation.linear);
				imp = Compare.interpolate(prevSnapshot.v3_2, imp, progress, Interpolation.linear);
			}
			
			if(pos != null) updateTransform(pos.x, pos.y, pos.z, angle);
			if(vel != null) updateVelocity(vel.x, vel.y, vel.z);
			if(imp != null) updateImpulse(imp.x, imp.y, imp.z);
			
			acc+=delta;
		}
	}
	
	public void setAngle(float angle) {
		this.angle = angle;
	}

	/** Returns the angle of the body in radians */
	public float getAngle() {
		return angle;
	}
	
	/** Applies angular impulse to body */
	public void rotateBy(float rot) {
	}
	
	/** Returns the current velocity for this component */
	public Vector3 getVelocity() {
		return this.velocity;
	}
	
	public Vector3 getImpulse() {
		return this.impulse;
	}
	
	/** Returns the current position for this component, or the initial position. **/
	public Vector3 getPosition() {
		return this.position;
	}
	
	public void setDimension(Dimension dim) {
		this.dim = new Dimension(dim);
	}
	
	public Dimension getDimension() {
		return this.dim;
	}
	
	
	/** Updates and returns the {@link Snapshot} for this component. */
	@Override
	public Snapshot getSnapshot(boolean delta) {
		return delta ? get() : getFull();
	}
	
	/** Update the position and angle for this body */
	public void updateTransform(float x, float y, float z, float angle) {
		this.position.set(x, y, z);
		this.angle = angle;
	}
	
	/** Update the velocity for this body */
	public void updateVelocity(float x, float y, float z) {
		this.velocity.set(x, y, z);
	}
	
	/** Sets the given impulse to this body */
	public void updateImpulse(float x, float y, float z) {
		this.impulse.set(x, y, z);
	}
	
	public Snapshot get() {
		if(dS.d_0 == null) dS.d_0 = new Dimension();
		if(dS.v3_0 == null) dS.v3_0 = new Vector3();
		if(dS.v3_1 == null) dS.v3_1 = new Vector3();
		if(dS.v3_2 == null) dS.v3_2 = new Vector3();
		if(dS.f_0 == null) dS.f_0 = new Mutable.Float(0);
		
		snapshot.id = getId();
		
		snapshot.d_0 = Compare.dimension(dS.d_0, getDimension());
		snapshot.v3_0 = Compare.vector(dS.v3_0, getPosition());
		snapshot.v3_1 = Compare.vector(dS.v3_1, getVelocity());
		snapshot.v3_2 = Compare.vector(dS.v3_2, getImpulse());

		snapshot.f_0 = Compare.mutableFloat(dS.f_0, getAngle());
		
		return snapshot.validate();
	}
	
	public Snapshot getFull() {
		snapshot.id = getId();
		
		snapshot.d_0 = getDimension();
		
		snapshot.v3_0 = getPosition();
		snapshot.v3_1  = getVelocity();
		snapshot.v3_2 = getImpulse();
		
		snapshot.f_0 = new Mutable.Float(getAngle());
		
		return snapshot;
	}
	
	/**
	 * Fills this Component with snapshot data from the master.
	 * @param snapshot The data driven snapshot.
	 * @return The updated {@link Component}.
	 */
	public Physics fill(Snapshot snapshot) {
		// We want to interpolate
		if(EntityManager.interpolate) {
			if(nextSnapshot == null) {
				setValues(snapshot);
				nextSnapshot = snapshot;
				return this;
			}
			snapshot.at = System.currentTimeMillis();
			prevSnapshot = nextSnapshot;
			nextSnapshot = snapshot;
			acc = 0;
		} 
		// Immediately update
		else {
			setValues(snapshot);
		}
		return this;
	}
	
	private void setValues(Snapshot snapshot) {
		angle = snapshot.f_0 != null ? snapshot.f_0.get() : angle;
		if(snapshot.v3_0 != null) updateTransform(snapshot.v3_0.x, snapshot.v3_0.y, snapshot.v3_0.z, angle);
		if(snapshot.v3_1 != null) updateVelocity(snapshot.v3_1.x, snapshot.v3_1.y, snapshot.v3_1.z);
		if(snapshot.v3_2 != null) updateImpulse(snapshot.v3_2.x, snapshot.v3_2.y, snapshot.v3_2.z);
		if(snapshot.d_0 != null) setDimension(snapshot.d_0);
	}
	
	@Override
	public void destroy(GameEntity entity) {
		
	}
	
	public Vector3 getToPos() {
		return this.toPos;
	}
}
