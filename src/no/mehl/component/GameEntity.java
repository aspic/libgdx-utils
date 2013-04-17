package no.mehl.component;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * A generic class defined by its components.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class GameEntity {
	
	private Array<Component> components = new Array<Component>();
	
	// Components
	private Object userdata;
	
	// Reference for Box2D world.
	private World world;
	private EntityManager manager;
	
	// Fields
	private boolean alive = true;
	
	private boolean removed;
	private int id = EntityManager.START_ID;
	private EntitySnapshot snapshot = new EntitySnapshot();
	
	private String owner;
	

	/** Creates an empty {@link GameObject}. */
	public GameEntity() {
	}
	
	
	public GameEntity(Component... comps) {
		components.addAll(comps);
	}
	
	/** Creates a {@link GameEntity} based on a full {@link Snapshot} */
	public GameEntity(EntitySnapshot snapshot) {
		create(snapshot);
	}
	

	/** Run all attached components in a server context */
	public void runServer(float delta) {
		for (int i = 0; i < components.size; i++) {
			Component component = components.get(i);
			component.initialize(this, true);
			component.runServer(this, delta);
		}
	}
	
	/** Run all attached components in a client context */
	public void runClient(float delta) {
		for (int i = 0; i < components.size; i++) {
			Component component = components.get(i);
			component.initialize(this, false);
			component.runClient(this, delta);
		}
	}
	
	/** Initialises all components. Some components need to load textures, others need to create bodies.
	 * This happens on the game loop
	 * @param world
	 */
	public void load(World world, EntityManager manager) {
		this.world = world;
		this.manager = manager;
	}
	
	/** Add a single {@link Component} */
	public void attachComponent(Component comp) {
		attachComponents(comp);
	}
	
	/** Add a range of components */
	public void attachComponents(Component... comps) {
		components.addAll(comps);
		snapshot.updateCapacity(comps.length);
	}
	
	public void removeComponent(Component comp) {
		comp.destroy(this);
		this.components.removeValue(comp, true);
	}
	
	public String toString() {
		return " GameEntity #" + id + ", owner: " + this.owner + "\n";
	}
	
	/** Whether or not this {@link GameEntity} is alive */
	public boolean isAlive() {
		return this.alive;
	}
	
	/** Will properly remove this {@link GameEntity}, after transmitting the change. */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	
	/** Will remove this {@link GameEntity} upon next {@link EntityManager} iteration */
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
	
	public boolean isRemoved() {
		return this.removed;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	/** Gets called after body has been removed, always returns null */
	public GameEntity destroy() {
		for (Component component : components) {
			component.destroy(this);
		}
		this.snapshot = null;
		return null;
	}
	
	/** 
	 * Updates and returns the {@link EntitySnapshot} for this {@link GameEntity} 
	 * @param delta False if this should return the whole entity snapshot.
	 */
	public EntitySnapshot getSnapshot(boolean delta) {
		if(delta && snapshot.fullTransmitted) return snapshot.getDelta(this);
		return snapshot.getFull(this);
	}
	
	/** 
	 * A condensed version of a {@link GameEntity}. Will be serialised and transmitted
	 * over the network.
	 * @author Kjetil Mehl <kjetil@mehl.no>
	 */
	public static class EntitySnapshot {
		public int id;
		public String owner;
		
		public boolean full;
		public boolean destroyed;
		public transient boolean fullTransmitted = false; // We need to transmit full entity upon creation
		private transient boolean changed;
		
		public Array<Snapshot> cps;
		
		public EntitySnapshot() {
			cps = new Array<Snapshot>();
		}
		
		// Allocate snapshot capacity
		public void updateCapacity(int size) {
			for (int i = 0; i < size; i++) {
				cps.add(null);
			}
		}

		/** Forces a delta update of this {@link EntitySnapshot} */
		public EntitySnapshot getDelta(GameEntity entity) {
			id = entity.getId();
			// Loop through components, append delta
			for (int i = 0; i < entity.getComponents().size; i++) {
				
				// Let component decide
				if(!entity.getComponents().get(i).isChanged()) {
					cps.set(i, null);
					continue;
				}
				
				// Changed component, extract delta values
				Snapshot delta = entity.getComponents().get(i).getSnapshot(true);
				if(delta != null) {
					cps.set(i, delta);
					changed = true;
				}
				
			}
			
			destroyed = entity.isRemoved();
			full = false;
			owner = null;
			
			if(destroyed || full) return this;
			else if(!changed) return null;
			changed = false;
			return this;
		}
		
		/** Forces a full update of this {@link EntitySnapshot} */
		public EntitySnapshot getFull(GameEntity entity) {
			id = entity.getId();
			owner = entity.getOwner();
			cps.clear();
			
			// Loop through components, append full
			for (Component component : entity.getComponents()) {
				cps.add(component.getSnapshot(false));
			}
			
			destroyed = entity.isRemoved();
			full = true;
			fullTransmitted = true;

			return this;
		}
		
		public String toString() {
			return "EntitySnapshot (#" + id + ")";
		}
		
		public void setSize(int size) {
			if(cps.size != size) {
				cps.ensureCapacity(size);
			}
		}
	}
	
	public World getWorld() {
		return this.world;
	}
	
	public EntityManager getManager() {
		return this.manager;
	}
	
	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String loc) {
		this.owner = loc;
	}

	public Object getUserdata() {
		return this.userdata;
	}
	
	
	public Array<Component> getComponents() {
		return this.components;
	}
	
	/** Returns the first {@link Component} extending some class 
	 * @return */
	/** TODO: Used to access some "existing" components, should be used otherwise */
	public <T> T getExtends(Class<T> clazz) {
		for (Component c : components) {
			if(c.componentExtends(clazz))  {
				return (T) c;
			}
		}
		return null;
	}
	
	/** Method for swapping two components */
	public void swapComponent(Component replacement, Class class1) {
		Component predecessor = getExtends(class1);
		if(predecessor != null) {
			predecessor.destroy(this);
			components.removeValue(predecessor, true);
		}
		attachComponent(replacement);
	}
	
	public Component getComponent(int componentId) {
		for (int i = 0; i < components.size; i++) {
			if(components.get(i).getId() == componentId) return components.get(i);
		}
		return null;
	}

	/** Updates all components in this {@link GameEntity}, according to {@link EntitySnapshot} */
	public void update(EntitySnapshot snapshot) {
		if(snapshot.destroyed) setRemoved(true);
		/**
		 * TODO: Could/should be optimized
		 */
		else if(snapshot.cps != null && snapshot.cps.size > 0) {
			for (int i = 0; i < snapshot.cps.size; i++) {
				Snapshot cp = snapshot.cps.get(i);
				if(cp == null) continue;
				
				if(i >= components.size) {
					components.add(Component.getComponent(cp.id).fill(cp));
				} else components.get(i).fill(cp);
			}
		}
	}
	
	/** Clears existing components, and fills with new instances based on the {@link EntitySnapshot}. */
	/** TODO: Call destroy and wait for next iteration? */
	public void create(EntitySnapshot snapshot) {
		components.clear();
		for(Snapshot s : snapshot.cps) {
			this.components.add((Component)Component.getComponent(s.id).fill(s));
		}
	}
	public boolean owns(Class class1) {
		for (Component c : components) {
			if(c.componentExtends(class1)) {
				return true;
			}
		}
		return false;
	}
}
