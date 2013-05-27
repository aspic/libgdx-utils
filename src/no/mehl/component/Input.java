package no.mehl.component;

public abstract class Input extends Component {
	
	private Snapshot snapshot = new Snapshot();
	
	/** Accelerates an {@link GameEntity} */
	public abstract void addForce(GameEntity entity, float acclX, float acclY);
	/** Triggers jump for an {@link GameEntity} */
	public abstract void doJump(GameEntity entity);
	/** Set some kind of input */
	public abstract void setInput(Object object);
	
	@Override
	public void runServer(GameEntity entity, float delta) {
		
	}

	@Override
	public void runClient(GameEntity entity, float delta) {
		
	}
	
	public abstract void processInput();
		
	@Override
	public Snapshot getSnapshot(boolean delta) {
		if(delta) return null;
		
		snapshot.id = getId();

		return snapshot;
	}

	@Override
	public Input fill(Snapshot snapshot) {
		return this;
	}
	
	@Override
	public void destroy(GameEntity entity) {
		
	}
	
	@Override
	protected void loadServer(GameEntity entity) {
		
	}
}
