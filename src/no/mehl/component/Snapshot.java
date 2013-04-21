package no.mehl.component;

import no.mehl.libgdx.utils.Dimension;
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
	
	public boolean b_0;
	
	public UserData data;
	
	public Snapshot() {}
	
	public void set(Snapshot snapshot) {
		this.id = snapshot.id;
		// Floats
		this.f_0 = snapshot.f_0;
		this.f_1 = snapshot.f_1;
		this.f_2 = snapshot.f_2;
		// Strings
		this.s_0 = snapshot.s_0;
		this.s_1 = snapshot.s_1;
		this.s_2 = snapshot.s_2;
		// Vectors
		this.v2_0 = snapshot.v2_0;
		this.v2_1 = snapshot.v2_1;
		this.v2_2 = snapshot.v2_2;
		
		this.v3_0 = snapshot.v3_0;
		this.v3_1 = snapshot.v3_1;
		this.v3_2 = snapshot.v3_2;
		
		this.c_0 = snapshot.c_0;
		this.c_1 = snapshot.c_1;
		this.c_2 = snapshot.c_2;
		
		this.d_0 = snapshot.d_0;
		this.d_1 = snapshot.d_1;
		this.d_2 = snapshot.d_2;
		
		this.b_0 = snapshot.b_0;
		
		this.data = snapshot.data;
	}
	
	public Snapshot validate() {
		if(f_0 != null || s_0 != null || v2_0 != null || (v3_0 != null && v3_0.len() > 0) || c_0 != null || d_0 != null) return this;
		return null;
	}

	

}
