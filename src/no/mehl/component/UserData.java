package no.mehl.component;

import no.mehl.component.GameEntity;
import no.mehl.component.Physics;

import com.badlogic.gdx.utils.ObjectMap;

public class UserData {
	
	public static transient final String D_BODY = "BODY";
	public static transient final String D_OWNER = "owner";
	public static transient final String D_FORCE = "force";
	public static transient final String D_SENSOR = "sensor";
	public static transient final String D_FILTER = "filter";
	public static transient final String DEF_FIXED_ROT = "fixed";
	public static transient final String D_TYPE = "type";
	
	// Transients
	private transient GameEntity entity;
	private transient boolean changed;
	
	private ObjectMap<String, Object> store = new ObjectMap<String, Object>();
	
	public UserData() {}
	
	public UserData(DataPair... pairs) {
		for (int i = 0; i < pairs.length; i++) {
			store.put(pairs[i].key, pairs[i].value);
		}
	}
	
	public UserData load(GameEntity entity) {
		return load(entity, null);
	}
	
	public UserData load(GameEntity entity, Physics physics) {
		this.entity = entity;
		return this;
	}
	
	/** Update a value in this key store */
	public void put(String key, Object item) {
		changed = true;
		store.put(key, item);
	}
	
	/** Get a value from this key store */
	public <T> T get(String key, Class<T> type) {
		Object item = store.get(key);
		return item != null ? (T)item : null;
	}
	
	/** Get a value from this key store */
	public Object get(String key) {
		return store.get(key);
	}
	
	public boolean contains(String key) {
		return store.containsKey(key);
	}
	
	/** Retrieve this {@link UserData} given that is has been changed */
	public UserData retrieve() {
		if(changed) {
			changed = false;
			return this;
		}
		return null;
	}
	
	public GameEntity getEntity() {
		return this.entity;
	}
	
	/**
	 * A simple class describing a {@link String} {@link Object} pair
	 * @author Kjetil Mehl <kjetil@mehl.no>
	 */
	public static class DataPair {
		Object value;
		String key;
		
		public DataPair(String key, Object value) {
			this.key = key;
			this.value = value;
		}
		
		public void setValue(Object object) {
			this.value = object;
		}
	}
	
}
