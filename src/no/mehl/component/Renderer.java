package no.mehl.component;

import no.mehl.libgdx.utils.Compare;

import com.badlogic.gdx.graphics.Color;

public abstract class Renderer extends Component {

	// Fields
	protected Snapshot snapshot = new Snapshot();
	protected Snapshot dS = new Snapshot();
	
	protected Color color;
	
	/** Return the color for this {@link Renderer}. */
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		setChanged();
	}
	
	@Override
	public Renderer fill(Snapshot snapshot) {
		this.color = snapshot.c_0;
		
		return this;
	}
	
	public Snapshot getSnapshot(boolean delta) {
		setSynced();

		snapshot.id = getId();
		
		if(delta) {
			if(dS.c_0 == null) dS.c_0 = new Color();
			
			snapshot.c_0 = Compare.compareColor(dS.c_0, getColor());
		} else {
			snapshot.c_0 = this.color;
		}
		
		return snapshot;
	}
	
	public String toString() {
		return getClass().getSimpleName();
	}
	
	@Override
	public void destroy(GameEntity entity) {
		
	}
	
	@Override
	protected void loadServer(GameEntity entity) {
		System.out.println("Renderer loaded in server context");
	}
}
