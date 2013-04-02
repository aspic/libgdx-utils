package no.mehl.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import no.mehl.libgdx.utils.Compare;
import no.mehl.libgdx.utils.Mutable;

/**
 * This component definition is tightly coupled to the {@link Body} class in Box2D.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public abstract class Physics extends Component {

	// Fields
	protected Body body;
	protected Vector3 position = new Vector3();
	protected Vector3 velocity = new Vector3();
	protected Vector3 lastVel = new Vector3();
	protected Vector3 lastPos = new Vector3();
	protected Vector3 toPos;
	
	protected Vector2 force = new Vector2();
	
	protected Dimension dim;
	private Snapshot snapshot = new Snapshot();
	private Snapshot dS = new Snapshot();
	
	protected float gravityZ = 0f;
	protected static final float GRAV_Z = -40;
	
	@Override
	public void runServer(GameEntity entity, float delta) {
		
	}

	@Override
	public void runClient(GameEntity entity, float delta) {
		
	}
	
	public float getAngle() {
		return -1;
	}
	
	public String toString() {
		return "	" + getClass().getSimpleName();
	}
	
	/** Sets the direction for this shape **/
	public abstract void applyForce(float forceX, float forceY);
	
	/** Returns the current velocity for this component */
	public Vector3 getVelocity() {
		return this.velocity.set(body.getLinearVelocity().x, body.getLinearVelocity().y, this.velocity.z);
	}
	
	public Vector2 getForce() {
		Vector2 tempForce = new Vector2(force);
		force.set(0, 0);
		return tempForce;
	}
	
	/** Returns the current position for this component, or the initial position. **/
	public Vector3 getPosition() {
		return this.position.set(body.getPosition().x, body.getPosition().y, this.position.z);
	}
	
	public void setDimension(Dimension dim) {
		this.dim = dim;
	}
	
	public Dimension getDimension() {
		return this.dim;
	}
	
	
	/** Updates and returns the {@link Snapshot} for this component. */
	@Override
	public Snapshot getSnapshot(boolean delta) {
		setSynced();
		return delta ? get() : getFull();
	}
	
	protected abstract BodyDef createBodyDef();
	
	/** Update the position and angle for this body */
	private void updateTransform(Vector3 pos, float angle) {
		if(this.body != null && updateTransform(this.body.getPosition().x, this.body.getPosition().y, position.z, pos.x, pos.y, pos.z)) {
			this.body.setTransform(pos.x, pos.y, angle);
		}
		this.position.set(pos);
	}
	
	private boolean updateTransform(float x, float y, float z, float toX, float toY, float toZ) {
		if(Math.abs(x - toX) > 1f) return true;
		if(Math.abs(y - toY) > 1f) return true;
		if(Math.abs(z - toZ) > 1f) return true;
		return false;
	}
	
	/** Update the velocity for this body */
	private void updateVelocity(float x, float y, float z) {
		if(this.body != null && x != 0 && y != 0) {
			this.body.setLinearVelocity(x, y);
		}
	}
	
	private void updateForce(float x, float y) {
		if(this.body != null && (x != 0 || y != 0)) {
			this.body.applyForceToCenter(x, y, true);
		}
	}
	
	public Snapshot get() {
		
		if(dS.d_0 == null) dS.d_0 = new Dimension();
		if(dS.v3_0 == null) dS.v3_0 = new Vector3();
		if(dS.v3_1 == null) dS.v3_1 = new Vector3();
		if(dS.v2_0 == null) dS.v2_0 = new Vector2();
		if(dS.f_0 == null) dS.f_0 = new Mutable.Float(0);
		
		snapshot.id = getId();
		
		snapshot.d_0 = Compare.dimension(dS.d_0, getDimension());
		snapshot.v3_0 = Compare.vector(dS.v3_0, getPosition());
		snapshot.v3_1 = Compare.vector(dS.v3_1, getVelocity());
		
		snapshot.v2_0 = Compare.vector(dS.v2_0, getForce());
		snapshot.f_0 = Compare.mutableFloat(dS.f_0, getAngle());
		
		return snapshot.validate();
	}
	
	public Snapshot getFull() {
		snapshot.id = getId();
		
		snapshot.d_0 = getDimension();
		snapshot.v3_0 = getPosition();
		snapshot.v3_1  = getVelocity();
		snapshot.v2_0 = getForce();
		
		snapshot.f_0 = new Mutable.Float(getAngle());
		
		return snapshot;
	}
	
	/**
	 * Fills this Component with snapshot data from the master.
	 * @param snapshot The data driven snapshot.
	 * @return The updated {@link Component}.
	 */
	public Physics fill(Snapshot snapshot) {
		float angle = snapshot.f_0 != null ? snapshot.f_0.get() : 0;
		if(snapshot.v3_0 != null) updateTransform(snapshot.v3_0, angle);
		if(snapshot.v3_1 != null) updateVelocity(snapshot.v3_1.x, snapshot.v3_1.y, snapshot.v3_1.z);
		if(snapshot.v2_0 != null) updateForce(snapshot.v2_0.x, snapshot.v2_0.x);
		if(snapshot.d_0 != null) setDimension(snapshot.d_0);
//		if(snapshot.toX != 0 || snapshot.toY != 0 || snapshot.toZ != 0) toPos = new Vector3(snapshot.toX, snapshot.toY, snapshot.toZ);
		
		return this;
	}
	
	@Override
	public void destroy(GameEntity entity) {
		entity.getWorld().destroyBody(body);
	}
	
	public void setUserdata(int id) {
		this.body.setUserData(id);
	}

	public Vector3 getToPos() {
		return this.toPos;
	}

	public Body getBody() {
		return this.body;
	}

	public void doJump() {
		this.velocity.z = 30f;
		this.gravityZ = GRAV_Z;
	}
	
	public void setGravityZ(float grav) {
		this.gravityZ = grav;
		if(grav == 0) this.velocity.z = 0;
	}
	
	public void setPosZ(float z) {
		this.position.z = z;
	}
}
