package no.mehl.component;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

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
	
	// Fields
	private boolean alive = true;
	private boolean removed;
	private int id;
	private EntitySnapshot snapshot;
	
	private String owner;

	/** Creates an empty {@link GameObject}. */
	public GameEntity(Component...comps) {
		components.addAll(comps);
		snapshot = new EntitySnapshot();
	}
	
	/** Creates a {@link GameEntity} based on a full {@link Snapshot} */
	public GameEntity(EntitySnapshot snapshot) {
		create(snapshot);
	}
	

	/** Run all attached components */
	public void run(float delta, boolean isServer) {
		for (int i = 0; i < components.size; i++) {
			if(!isServer) components.get(i).runClient(this, delta);
			else components.get(i).runServer(this, delta);
		}
	}
	
	/** Initialises all components. Some components need to load textures, others need to create bodies.
	 * This happens on the game loop
	 * @param world
	 */
	public void load(World world) {
		this.world = world;
		for (int i = 0; i < components.size; i++) {
			components.get(i).load(this);
		}
	}
	
	/** Attach some userdata to this {@link GameEntity} */
	public void attachUserdata(Object userdata) {
		this.userdata = userdata;
	}
	
	public void attachComponents(Component... comps) {
		components.addAll(comps);
	}
	
	public void attachComponent(Component comp) {
		this.components.add(comp);
	}
	
	public void removeComponent(Component comp) {
		this.components.removeValue(comp, true);
	}
	
	public String toString() {
		return "\nGameEntity #" + id + ", owner: " + this.owner + "\n" + components.size + "\n";
	}
	
	/** Whether or not this {@link GameEntity} is alive */
	public boolean isAlive() {
		return this.alive;
	}
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public void setRemoved(boolean b) {
		this.removed = b;
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
	
	/** Gets called after body has been removed */
	public void destroy() {
		for (Component component : components) {
			component.destroy(this);
		}
		this.snapshot = null;
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
		
		public Array<Snapshot> cps;
		
		public EntitySnapshot() {
			cps = new Array<Snapshot>();
		}
		
		/** Forces a delta update of this {@link EntitySnapshot} */
		public EntitySnapshot getDelta(GameEntity entity) {
			cps.clear();
			
			// Loop through components, append delta
			for (Component component : entity.getComponents()) {
				int id = component.getId();
				if(component.isChanged()) cps.add(component.getSnapshot(true));
			}
			
			destroyed = entity.isRemoved();
			full = false;
			owner = null;
			
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
		
		/** Indicates whether it's necessary to transmit update */
		public boolean shouldTransmit() {
			if(full || cps.size > 0 || destroyed) return true;
			return false;
		}
		
		public String toString() {
			return "EntitySnapshot (#" + id + ")";
		}
	}
	
	public World getWorld() {
		return this.world;
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
	public <T> T getExtends(Class<T> clazz) {
		for (Component c : components) {
			if(c.componentExtends(clazz))  {
				return (T) c;
			}
		}
		return null;
	}
	
	public Component getComponent(int componentId) {
		for (int i = 0; i < components.size; i++) {
			if(components.get(i).getId() == componentId) return components.get(i);
		}
		return null;
	}


	public void update(EntitySnapshot snapshot) {
		if(snapshot.destroyed) setRemoved(true);
		else if(snapshot.cps != null && snapshot.cps.size > 0 && snapshot.cps.get(0) != null) {
			for (int i = 0; i < snapshot.cps.size; i++) {
				for (int j = 0; j < components.size; j++) {
					if(components.get(j).getId() == snapshot.cps.get(i).id) {
						components.get(j).fill(snapshot.cps.get(i));
						break;
					}
				}
			}
		}
	}
	
	public void create(EntitySnapshot snapshot) {
		components.clear();
		for(Snapshot s : snapshot.cps) {
			this.components.add((Component)Component.getComponent(s.id).fill(s));
		}
	}
}
