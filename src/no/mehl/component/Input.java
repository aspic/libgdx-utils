package no.mehl.component;

public abstract class Input extends Component {
	
	private Snapshot snapshot = new Snapshot();
	
	/** Accelerates a player */
	public abstract void addForce(GameEntity entity, float acclX, float acclY);
	/** Triggers jump for a player */
	public abstract void doJump(GameEntity entity);
	
	@Override
	public void runServer(GameEntity entity, float delta) {
		
	}

	@Override
	public void runClient(GameEntity entity, float delta) {
		
	}
	
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
}
