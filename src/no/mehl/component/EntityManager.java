package no.mehl.component;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import no.mehl.component.GameEntity.EntitySnapshot;
import no.mehl.libgdx.utils.AssetsGetter;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

/**
 * A clean interface for adding and removing {@link GameEntities}.
 * This class should always know the consistent state of entities in play.
 * 
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class EntityManager {
	
	public static AssetsGetter assets;
	public static boolean interpolate;
	public static float step;
	
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
	public void run(float delta) {
		Iterator<Entry<Integer, GameEntity>> it = entities.entries().iterator();
		
		if(context == Context.SERVER) {
			while(it.hasNext()) {
				entities.get(it.next().key).runServer(delta);
			}
		} 
		else if(context == Context.CLIENT) {
			while(it.hasNext()) {
				entities.get(it.next().key).runClient(delta);
			}
		} 
		else if(context == Context.BOTH) {
			while(it.hasNext()) {
				entities.get(it.next().key).runBoth(delta);
			}
		}
	}
	
	/** Loops through removed entities, and properly destroys all attached {@link Component}s. */
	public void checkRemoval(World world) {
		for (GameEntity entity : entities.values()) {
			if(entity.isRemoved()) {
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
	public void queueForRemoval(GameEntity entity) {
		entitiesToRemove.add(entity);
	}
	
	/** Loop through "removed entities" and clear them from the engine */
	private void checkForRemoval(World world) {
		while(entitiesToRemove.size() > 0) {
			GameEntity removed = entitiesToRemove.poll();
			if(listener != null) listener.removesEntity(removed);
			
			removed.destroy();
			entities.remove(removed.getId());
		}
	}

	/** Retrieves full/delta snapshots, ready to be serialised */
	public Array<EntitySnapshot> getSnapshots(boolean delta) {
		
		if(entities == null || entities.size == 0) return null;
		
		Array<EntitySnapshot> snapshots = new Array<EntitySnapshot>();
		Iterator<Entry<Integer, GameEntity>> it = entities.entries().iterator();
		
		while (it.hasNext()) {
			GameEntity e = entities.get(it.next().key);
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
		
		if(listener != null) listener.loadedEntity(entity, entitiesToAdd.size());
		
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
	
	/** Interface for broadcasting changes in this {@link Entity} list */
	public interface ManagerListener {
		/** Triggers after a new entity has been successfully inserted into the manager */
		public void loadedEntity(GameEntity entity, int left);
		/** Triggers before this entity gets removed from the manager */
		public void removesEntity(GameEntity entity);
	}
	
	public Context getContext() {
		return this.context;
	}
	
	public void setContext(Context context) {
		System.out.println("Set context: " +context);
		this.context = context;
	}
	
	/** Should be located in this class */
	public static void registerAssetLoader(AssetsGetter getter) {
		EntityManager.assets = getter;
	}
	
	public enum Context {
		SERVER, CLIENT, BOTH;
	}
}
