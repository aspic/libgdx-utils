package no.mehl.libgdx.utils;

import no.mehl.component.Dimension;
import no.mehl.libgdx.utils.Mutable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/** 
 * All snapshot shares an identificator. Id defines what type of component this snapshot is.
 * A snapshot will not get serialized and passed on the network unless it has been changed.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class Compare {
	
	private static final float DELTA_ERROR = 0.01f;
	
	/** Method for checking if dimension has changed. */
	public static Dimension dimension(Dimension prevDim, Dimension currentDim) {
		if(prevDim != null && prevDim.compare(currentDim) < 0) {
			return prevDim.set(currentDim);
		}
		return null;
	}
	
	/** Helper method for only transmitting velocity if it has been changed (too some degree). */
	public static Vector2 vector(Vector2 prevVector, Vector2 currentVector) {
		float len = Math.abs(prevVector.len() - currentVector.len());
		if(len > DELTA_ERROR) {
			return prevVector.set(currentVector);
		}
		return null;
	}
	
	public static Vector3 vector(Vector3 prevVector, Vector3 currentVector) {
		float len = Math.abs(prevVector.len() - currentVector.len());
		if(len > DELTA_ERROR) {
			return prevVector.set(currentVector);
		}
		return null;
	}
	
	/** Compare values. Return 0 if no difference. */
	public static Mutable.Float mutableFloat(Mutable.Float prevValue, float currentValue) {
		float diff = Math.abs(prevValue.get() - currentValue);
		if(diff > DELTA_ERROR) {
			return prevValue.set(currentValue);
		}
		return null;
	}
	
	/** Compare integers **/
	public static int compareInteger(Mutable.Integer prevValue, int currentValue) {
		int prev = prevValue.get();
		if(prev != currentValue) {
			return prevValue.set(currentValue);
		}
		return prev;
	}
	
	/** Compares two {@link Color} */
	public static Color compareColor(Color prevColor, Color currentColor) {
		if(!prevColor.equals(currentColor)) {
			return prevColor.set(currentColor);
		}
		return null;
	}
	
}
