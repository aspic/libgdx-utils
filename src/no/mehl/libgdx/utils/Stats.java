package no.mehl.libgdx.utils;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * A simple class for generating some statistics.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class Stats {

	public static final String FPS = "fps";
	public static final String TICKS = "ticks";
	public static final String INPUT = "input";
	public static final String UPDATES = "updates";
	public static final String SERVER_UPDATES = "server_updates";
	public static final String RECEIVED_TICKS = "received_ticks";
	public static final String CMD = "commands";
	
	public static final Stats INSTANCE = new Stats();
	
	private static ObjectMap<String, Metric> values;
	private static long start;
	
	private Stats() {
		values = new ObjectMap<String, Metric>();
		start = System.currentTimeMillis();
	};
	
	public static Metric get(String key) {
		Metric m = values.get(key);
		if(m != null) return m;
		
		m = new Metric(key);
		values.put(key, new Metric(key));
		return m;
	}
	
	public static long runtime() {
		return System.currentTimeMillis() - start;
	}
	
	public static void stop() {
		for (Metric m : values.values()) {
			m.end();
			System.out.println(m);
		}
	}
	
	public static void printTemp(float delta) {
		for (Metric m : values.values()) {
			m.printTemp(delta);
		}
	}
	
	public static class Metric {
		private long start;
		private long end;
		private String key;
		
		private float value;
		private float tempValue;
		
		public Metric(String key) {
			this.key = key;
			start = System.currentTimeMillis();
		}
		
		public void inc() {
			this.value++;
			tempValue++;
		}
		
		public float get() {
			return this.value;
		}
		
		public void add(float value) {
			this.value += value;
			tempValue += value;
		}
		
		public void end() {
			end = System.currentTimeMillis();
		}
		
		public String toString() {
			float duration = (end - start) * 0.001f;
			return "	avg. " + key + " per second: " + value/duration;
		}
		
		public void printTemp(float seconds) {
			System.out.println(key + " in " + getTemp(seconds) + " p/s");
		}
		
		public float getTemp(float seconds) {
			float old = tempValue;
			tempValue = 0;
			return old/seconds;
		}
	}
	
	public static Stats getInstance() {
		return INSTANCE;
	}

}
