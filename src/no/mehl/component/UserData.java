package no.mehl.component;

import no.mehl.component.GameEntity;
import no.mehl.component.Physics;

import com.badlogic.gdx.utils.ObjectMap;

public class UserData {
	
	public static transient final String D_BODY = "BODY";
	public static transient final String D_OWNER = "owner";
	public static transient final String D_FORCE = "force";
	public static transient final String D_SENSOR = "sensor";
	
	// Transients
	private transient GameEntity entity;
	
	private ObjectMap<String, Object> store = new ObjectMap<String, Object>();
	
	public UserData() {}
	
	public UserData load(GameEntity entity) {
		return load(entity, null);
	}
	
	public UserData load(GameEntity entity, Physics physics) {
		this.entity = entity;
		return this;
	}
	
	public void put(String key, Object item) {
		store.put(key, item);
	}
	
	public <T> T get(String key, Class<T> type) {
		Object item = store.get(key);
		return item != null ? (T)item : null;
	}
	
	public Object get(String key) {
		return store.get(key);
	}
	
	public boolean contains(String key) {
		return store.containsKey(key);
	}
	
	public GameEntity getEntity() {
		return this.entity;
	}
	
}
