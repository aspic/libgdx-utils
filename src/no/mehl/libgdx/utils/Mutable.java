package no.mehl.libgdx.utils;

/** A simple class for some mutable primitivies **/
public class Mutable {
	
	public static class Float {
		private float value;
		
		public float get() {
			return this.value;
		}
		
		public float set(float value) {
			return this.value = value;
		}
	}
	
	public static class Integer {
		private int value;
		
		public int get() {
			return this.value;
		}
		
		public int set(int value) {
			return this.value = value;
		}
	}
}
