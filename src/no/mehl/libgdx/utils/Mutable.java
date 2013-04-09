package no.mehl.libgdx.utils;

/** A simple class for some mutable primitivies **/
public class Mutable {
	
	public static class Float {
		public float value;
		
		public Float() {
			this.value = 0;
		}
		
		public Float(float value) {
			this.value = value;
		}
		
		public float get() {
			return this.value;
		}
		
		public Mutable.Float set(float value) {
			this.value = value;
			return this;
		}
	}
	
	public static class Integer {
		public int value;
		
		public Integer() {
			this.value = 0;
		}
		
		public int get() {
			return this.value;
		}
		
		public int set(int value) {
			return this.value = value;
		}
	}
}
