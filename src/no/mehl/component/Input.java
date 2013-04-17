package no.mehl.component;

public abstract class Input extends Component {
	
	private Snapshot snapshot = new Snapshot();
	
	/** Accelerates an {@link GameEntity} */
	public abstract void addForce(GameEntity entity, float acclX, float acclY);
	/** Triggers jump for an {@link GameEntity} */
	public abstract void doJump(GameEntity entity);
	
	public abstract void control(IType direction);
	
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

		setSynced();
		return snapshot;
	}

	@Override
	public Input fill(Snapshot snapshot) {
		return this;
	}
	
	@Override
	public void destroy(GameEntity entity) {
		
	}
	
	public static enum IType {
		LEFT, RIGHT, UP, DOWN, JUMP, ACTION;
	}
	
	@Override
	protected void loadServer(GameEntity entity) {
		
	}
}
