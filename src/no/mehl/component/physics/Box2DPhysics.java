package no.mehl.component.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Shape;

import no.mehl.component.Component;
import no.mehl.component.GameEntity;
import no.mehl.component.Physics;
import no.mehl.component.Snapshot;
import no.mehl.component.UserData;
import no.mehl.libgdx.utils.Dimension;

/**
 * This component definition is tightly coupled to the {@link Body} class in Box2D.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class Box2DPhysics extends Physics {
	
	public static final String TYPE_CIRCLE = "circle";
	public static final String TYPE_RECT = "rect";
	public static final String TYPE_EDGE = "edge";

	// Fields
	protected Body body;
	protected UserData data;
	protected Filter filter;
	
	protected float gravityZ = 0f;
	public static final float GRAV_Z = -25;
	
	public Box2DPhysics() {
		this(new UserData());
	}
	
	public Box2DPhysics(UserData data) {
		this(data, new Vector2(), new Dimension());
	}
	
	public Box2DPhysics(UserData data, Vector2 position, Dimension dimension) {
		this(data, new Vector3(position.x, position.y, 0), dimension);
	}
	
	public Box2DPhysics(UserData data, Vector3 position, Dimension dimension) {
		this.position = position;
		this.dim = dimension;
		this.data = data;
	}

	
	/** Loads the client specification, unless overridden */
	@Override
	protected void loadServer(GameEntity entity)  {
		if(!initialized) loadClient(entity);
	}
	
	@Override
	public void runServer(GameEntity entity, float delta) {
		
		setChanged();
	}

	@Override
	public void runClient(GameEntity entity, float delta) {
	}
	
	@Override
	public void loadClient(GameEntity entity) {
		this.body = entity.getWorld().createBody(createBodyDef());
		
		setUserdata(data.load(entity, this));
		updateTransform(position.x, position.y, position.z, angle);
		setDimension(dim);
	}
	
	private BodyDef createBodyDef() {
		BodyDef def = new BodyDef();
		if(data.contains(UserData.D_BODY)) {
			def.type = BodyType.valueOf((String)data.get(UserData.D_BODY));
		} else {
			def.type = BodyType.StaticBody;
		}
		if(data.contains(UserData.DEF_FIXED_ROT)) {
			def.fixedRotation = (Boolean) data.get(UserData.DEF_FIXED_ROT);
		}
		
		return def;
	}
	
	/** Will update the fixture for this {@link Box2DPhysics}, based on the {@link UserData} */
	protected void updateFixture() {
		// Create shape based on reflection
		String type = (String) data.get(UserData.D_TYPE);
		Shape shape = null;
		if(type != null) {
			if(type.equals(TYPE_CIRCLE)) {
				shape = new CircleShape();
				shape.setRadius(dim.getRadius());
			} else if(type.equals(TYPE_RECT)) {
				shape = new PolygonShape();
				((PolygonShape) shape).setAsBox(dim.getWidth()*0.5f, dim.getHeight()*0.5f);
			} else if(type.equals(TYPE_EDGE)) {
				shape = new EdgeShape();
				((EdgeShape) shape).set(new Vector2(position.x, position.y), new Vector2(position.x + dim.getWidth(), position.y + dim.getHeight()));
			}
			Fixture fix = body.createFixture(shape, 0.5f);
			fix.setUserData(data);
			fix.setFriction(0.3f);
			
			if(data.contains(UserData.D_SENSOR)) {
				fix.setSensor((Boolean) data.get(UserData.D_SENSOR));
			}
			shape.dispose();
			
		} else {
			System.err.println("NO SHAPE FOR " + type);
		}
	}
	
	/** Returns the angle of the body in radians */
	public float getAngle() {
		if(body != null) {
			angle = body.getAngle();
		}
		return angle;
	}
	
	/** Applies angular impulse to body */
	public void rotateBy(float rot) {
		if(this.body != null) {
			this.body.applyAngularImpulse(rot, true);
		}
	}
	
	public void setDimension(Dimension dim) {
		super.setDimension(dim);
			
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
	public void updateTransform(float x, float y, float z, float angle) {
		// Could be null
		if(this.body != null) {
			this.body.setTransform(x, y, angle);
		}
		super.updateTransform(x, y, z, angle);
	}
	
	/** Update the velocity for this body */
	protected void updateVelocity(float x, float y, float z) {
		if(this.body != null) {
			this.body.setLinearVelocity(x, y);
		}
		super.updateVelocity(x, y, z);
	}
	
	/** Update the velocity for this body */
	public void updateImpulse(float x, float y, float z) {
		if(this.body != null) {
			this.body.applyLinearImpulse(x, y, body.getPosition().x, body.getPosition().y, true);
		}
		super.updateImpulse(x, y, z);
	}
	
	/** Returns the current velocity for this component */
	public Vector3 getVelocity() {
		return body != null ? this.velocity.set(body.getLinearVelocity().x, body.getLinearVelocity().y, this.velocity.z) : super.getVelocity();
	}
	
	/** Returns the current position for this component, or the initial position. **/
	public Vector3 getPosition() {
		return body != null ? this.position.set(body.getPosition().x, body.getPosition().y, this.position.z) : super.getPosition();
	}
	
	public Snapshot get() {
		snapshot.data = data.retrieve();
		
		return super.get();
	}
	
	public Snapshot getFull() {
		snapshot.data = data;
		
		return super.getFull();
	}
	
	/**
	 * Fills this Component with snapshot data from the server.
	 * @param snapshot The data driven snapshot.
	 * @return The updated {@link Component}.
	 */
	public Physics fill(Snapshot snapshot) {
		angle = snapshot.f_0 != null ? snapshot.f_0.get() : angle;
		if(snapshot.data != null) setUserdata(snapshot.data);
		if(snapshot.v3_0 != null) this.updateTransform(snapshot.v3_0.x, snapshot.v3_0.y, snapshot.v3_0.z, angle);
		if(snapshot.v3_1 != null) this.updateVelocity(snapshot.v3_1.x, snapshot.v3_1.y, snapshot.v3_1.z);
		if(snapshot.v3_2 != null) this.updateImpulse(snapshot.v3_2.x, snapshot.v3_2.y, snapshot.v3_2.z);
		if(snapshot.d_0 != null) setDimension(snapshot.d_0);
		return this;
	}
	
	@Override
	public void destroy(GameEntity entity) {
		entity.getWorld().destroyBody(body);
	}
	
	public void setUserdata(UserData object) {
		// Set
		this.data = object;
		this.snapshot.data = this.data;
		
		// Update
		if(this.body != null) {
			Filter filter = null;
			if(data.contains(UserData.D_FILTER)) {
				filter = (Filter) data.get(UserData.D_FILTER);
			}
			
			for (int i = 0; i < body.getFixtureList().size(); i++) {
				body.getFixtureList().get(i).setUserData(object);
				if(filter != null) body.getFixtureList().get(i).setFilterData(filter);
			}
			this.body.setUserData(object);
		}
	}
	
	public UserData getUserdata() {
		return this.data;
	}
	
	/** Set a new filter for the fixtures. Will transmit changes */
	public void setFilterData(Filter filter) {
		this.data.put(UserData.D_FILTER, filter);
		
		if(this.body != null) {
			for (int i = 0; i < body.getFixtureList().size(); i++) {
				body.getFixtureList().get(i).setFilterData(filter);
			}
		}
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

	public void setSensor(boolean b) {
		if(this.body != null) {
			for (int i = 0; i < body.getFixtureList().size(); i++) {
				body.getFixtureList().get(i).setSensor(b);
			}
		}
	}
	
	public void updateForceZ(float value) {
		if(Math.abs(this.velocity.z) < 0.5f) {
			this.velocity.z = value;
			this.gravityZ = GRAV_Z;
		}
	}
	
	public Filter getFilter() {
		return this.filter;
	}
}
