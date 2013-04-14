package no.mehl.component;

import java.util.LinkedList;
import java.util.Queue;

import no.mehl.component.EntityManager.Context;
import no.mehl.component.GameEntity.EntitySnapshot;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * A clean interface for adding and removing {@link GameEntities}.
 * This class should always know the consistent state of entities in play.
 * 
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class EntityManager {
	
	private ObjectMap<Integer, GameEntity> entities = new ObjectMap<Integer, GameEntity>();
	private Queue<GameEntity> entitiesToAdd = new LinkedList<GameEntity>();
	private Queue<GameEntity> entitiesToRemove = new LinkedList<GameEntity>();
	
	private int entityId = START_ID;
	public static final int START_ID = 0;
	
	private ManagerListener listener;
	private Context context;
	
	public EntityManager(Context context) {
		this(null, context);
	}
	
	public EntityManager(ManagerListener listener, Context context) {
		this.listener = listener;
		this.context = context;
	}
	
	/** Will run all components attached to a {@link GameEntity}. The meaning of «run» is decided by the components themselves. */
	public void run(float step) {
		if(context == Context.SERVER) {
			for(Integer id : entities.keys()) {
				entities.get(id).runServer(step);
			}
		} 
		else if(context == Context.CLIENT) {
			for(Integer id : entities.keys()) {
				entities.get(id).runClient(step);
			}
		} 
		else if(context == Context.BOTH) {
			for(Integer id : entities.keys()) {
				GameEntity entity = entities.get(id);
				entity.runServer(step);
				entity.runClient(step);
			}
		}
	}
	
	/** Loops through removed entities, and properly destroys all attached {@link Component}s. */
	public void checkRemoval(World world) {
		for (GameEntity entity : entities.values()) {
			if(!entity.isAlive() && !entity.isRemoved()) {
				// Will mark the entity as removed, transmitting on the wire and removing on next pass.
				entity.setRemoved(true);
			} else if(entity.isRemoved()) {
				// Will remove entirely from engine
				queueForRemoval(entity);
			}
		}
		checkForRemoval(world);
	}
	
	/** Queues a new {@link GameEntity}, which later on  will be appended into the engine. */
	public void addEntity(GameEntity entity) {
		entitiesToAdd.add(entity);
	}
	
	/** Convenient method for adding several of entities */
	public void addEntities(GameEntity... entities) {
		for (int i = 0; i < entities.length; i++) {
			entitiesToAdd.add(entities[i]);
		}
	}
	
	/** Appends the {@link GameEntity} to the removal list */
	private void queueForRemoval(GameEntity entity) {
		entitiesToRemove.add(entity);
	}
	
	/** Loop through "removed entities" and clear them from the engine */
	private void checkForRemoval(World world) {
		while(entitiesToRemove.size() > 0) {
			GameEntity removed = entitiesToRemove.poll();
			removed.destroy();
			entities.remove(removed.getId());
		}
	}

	/** Retrieves full/delta snapshots, ready for serialization */
	public Array<EntitySnapshot> getSnapshots(boolean delta) {
		
		if(entities == null || entities.size == 0) return null;
		
		Array<EntitySnapshot> snapshots = new Array<EntitySnapshot>();
		
		for (int i = 0; i < entities.size; i++) {
			GameEntity e = entities.get(i);
			if(e == null) continue;
			
			EntitySnapshot snapshot = e.getSnapshot(delta);
			if(snapshot != null) snapshots.add(snapshot);
		}
		return snapshots;
	}
	
	/** 
	 * Polls the top element from entitiesToAdd, inserts
	 * into the engine, and loads the entity.
	 * @return The loaded {@link GameEntity}
	 */
	public GameEntity loadEntity(World world) {
		if(entitiesToAdd.size() == 0) return null;
		
		GameEntity entity = entitiesToAdd.poll();
		if(entity == null) return null;
		
		if(entity.getId() > entityId) {
			entityId = entity.getId();
		} else {
			entity.setId(++entityId);
		}
		entity.load(world, this);
		entities.put(entityId, entity);
		
		if(listener != null) listener.loadedEntity(entity);
		
		return entity;
	}
	
	/** Returns the {@link GameEntity} based on the id */
	public GameEntity get(int id) {
		return this.entities.get(id);
	}
	
	/** Returns all normal entities (not recently added, or removed) */
	public ObjectMap<Integer, GameEntity> getEntities() {
		return this.entities;
	}

	public int getWaiting() {
		return entitiesToAdd.size();
	}

	public void clear() {
		
	}
	
	public void reload(GameEntity entity) {
		GameEntity removed = entities.remove(entity.getId());
		if(removed != null) {
			addEntities(removed);
		}
	}
	
	public void setListener(ManagerListener listener) {
		this.listener = listener;
	}
	
	public interface ManagerListener {
		public void loadedEntity(GameEntity entity);
	}
	
	public Context getContext() {
		return this.context;
	}
	
	public void setContext(Context context) {
		System.out.println("Set context: " +context);
		this.context = context;
	}
	
	public enum Context {
		SERVER, CLIENT, BOTH;
	}
}
