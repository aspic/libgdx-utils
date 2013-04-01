package no.mehl.component;

import no.mehl.libgdx.utils.Compare;

import com.badlogic.gdx.graphics.Color;

public abstract class Renderer extends Component<Renderer> {

	// Fields
	protected Snapshot snapshot = new Snapshot();
	protected Snapshot dS = new Snapshot();
	
	protected Color color;
	
	@Override
	public Renderer fill(Snapshot snapshot) {
		this.color = snapshot.c_0;
		
		System.out.println("Fills: " + snapshot.c_0);
		return this;
	}
	
	/** Return the color for this {@link Renderer}. */
	protected Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		setChanged();
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
		return "	Renderer: " + getClass().getSimpleName() + " color: " + color;
	}
	
	@Override
	public void destroy(GameEntity entity) {
	}
}
