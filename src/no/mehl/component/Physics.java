package no.mehl.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import no.mehl.libgdx.utils.Compare;
import no.mehl.libgdx.utils.Dimension;
import no.mehl.libgdx.utils.Mutable;

/**
 * This component definition is tightly coupled to the {@link Body} class in Box2D.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public abstract class Physics extends Component {

	// Fields
	protected Body body;
	protected Vector3 position;
	protected Vector3 velocity = new Vector3();
	protected Vector3 lastVel = new Vector3();
	protected Vector3 lastPos = new Vector3();
	protected Vector3 toPos;
	protected float angle;
	protected Userdata data;
	
	protected Vector2 force = new Vector2();
	
	protected Dimension dim;
	protected Snapshot snapshot = new Snapshot();
	protected Snapshot dS = new Snapshot();
	
	protected float gravityZ = 0f;
	protected static final float GRAV_Z = -25;
	
	@Override
	protected void loadServer(GameEntity entity)  {
		
	}
	
	@Override
	public void runServer(GameEntity entity, float delta) {
		
		setChanged();
	}

	@Override
	public void runClient(GameEntity entity, float delta) {
		
	}
	
	/** Returns the angle of the body in radians */
	public float getAngle() {
		if(body != null) {
			angle = body.getAngle();
		}
		return angle;
	}
	
	public String toString() {
		return getClass().getSimpleName();
	}
	
	public void applyForce(float forceX, float forceY) {
		if(force == null) force = new Vector2();
		body.applyForceToCenter(force.set(forceX, forceY), true);
	}
	
	/** Sets the direction for this shape **/
	public abstract void updateFixture();
	protected abstract BodyDef createBodyDef();
	
	/** Applies angular impulse to body */
	public void rotateBy(float rot) {
		if(this.body != null) this.body.applyAngularImpulse(rot, true);
	}
	
	/** Returns the current velocity for this component */
	public Vector3 getVelocity() {
		if(this.velocity == null) this.velocity = new Vector3();
		return body != null ? this.velocity.set(body.getLinearVelocity().x, body.getLinearVelocity().y, this.velocity.z) : this.velocity;
	}
	
	public Vector2 getForce() {
		Vector2 tempForce = new Vector2(force);
		force.set(0, 0);
		return tempForce;
	}
	
	/** Returns the current position for this component, or the initial position. **/
	public Vector3 getPosition() {
		if(this.position == null) this.position = new Vector3();
		return body != null ? this.position.set(body.getPosition().x, body.getPosition().y, this.position.z) : this.position;
	}
	
	public void setDimension(Dimension dim) {
		this.dim = new Dimension(dim);
			
		if(this.body != null) {
			for (int i = 0; i < body.getFixtureList().size(); i++) {
				body.destroyFixture(body.getFixtureList().get(i));
			}
			updateFixture();
		}
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
	
	/** Update the position and angle for this body */
	public void updateTransform(Vector3 pos, float angle) {
		this.position = pos != null ?  new Vector3(pos) : new Vector3();
		
		if(this.body != null) {
			this.body.setTransform(position.x, position.y, angle);
		}
		this.angle = angle;
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
		angle = snapshot.f_0 != null ? snapshot.f_0.get() : angle;
		if(snapshot.v3_0 != null) updateTransform(snapshot.v3_0, angle);
		if(snapshot.v3_1 != null) updateVelocity(snapshot.v3_1.x, snapshot.v3_1.y, snapshot.v3_1.z);
		if(snapshot.v2_0 != null) updateForce(snapshot.v2_0.x, snapshot.v2_0.x);
		if(snapshot.d_0 != null) setDimension(snapshot.d_0);
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

	public void setGravityZ(float grav) {
		this.gravityZ = grav;
		if(grav == 0) this.velocity.z = 0;
	}
	
	public void setPosZ(float z) {
		this.position.z = z;
	}
	
	public void setUserdata(Object object) {
		if(this.body != null) this.body.setUserData(object);
	}
	
	public interface Userdata {
		public Userdata load(GameEntity entity);
		public Userdata load(GameEntity entity, Physics physics);
		
		public <T> T get(String key, Class<T> clazz);
		public void put(String key, Object object);
	}
	
	/** Load common data for bodies */
	protected void loadBody(GameEntity entity) {
		if(data != null) body.setUserData(data.load(entity, this));
		updateTransform(position, angle);
		setDimension(dim);
	}
}
