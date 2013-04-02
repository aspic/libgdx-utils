package no.mehl.component;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.physics.box2d.World;
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
	private int entityId = 0;
	
	/** Will run all components attached to a {@link GameEntity}. The meaning of «run» is decided by the components themselves. */
	public void run(float step, boolean isServer) {
		for (GameEntity entity : entities.values()) {
			entity.run(step, isServer);
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
	
	/** 
	 * Polls the top element from entitiesToAdd, inserts
	 * into the engine, and loads the entity.
	 * @return The loaded {@link GameEntity}
	 */
	public GameEntity loadEntity(World world) {
		if(entitiesToAdd.size() == 0) return null;
		
		GameEntity entity = entitiesToAdd.poll();
		
		if(entity.getId() > entityId) {
			entityId = entity.getId();
		} else {
			entity.setId(entityId);
		}
		entity.attachUserdata(new BodyData(entityId));
		entity.load(world);
		entities.put(entityId, entity);
		
		entityId++;
		
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
}
