package no.mehl.component;

import no.mehl.libgdx.utils.Mutable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Simple data holder class for a snapshot. This class must keep track of appropriate data types.
 * 
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class Snapshot {

	public int id;
	
	public Mutable.Float f_0;
	public Mutable.Float f_1;
	public Mutable.Float f_2;
	
	public String s_0;
	public String s_1;
	public String s_2;
	
	public Vector2 v2_0;
	public Vector2 v2_1;
	public Vector2 v2_2;
	
	public Vector3 v3_0;
	public Vector3 v3_1;
	public Vector3 v3_2;
	
	public Color c_0;
	public Color c_1;
	public Color c_2;
	
	public Dimension d_0;
	public Dimension d_1;
	public Dimension d_2;
	
	public Snapshot() {}
	
	public Snapshot validate() {
		if(f_0 != null || s_0 != null || v2_0 != null || v3_0 != null || c_0 != null || d_0 != null) return this;
		return null;
	}

}
