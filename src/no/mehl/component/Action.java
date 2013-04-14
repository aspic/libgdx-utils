package no.mehl.component;

public abstract class Action extends Component {
	
	private Snapshot snapshot = new Snapshot();
	/**
	 * This method will execute the given {@link Action} (whatever that means).
	 * Some actions do return a created game object, which then need to be inserted
	 * into manager.
	 * @param go The invoking {@link GameObject}.
	 * @return A created {@link GameObject}, null otherwise.
	 */
	public abstract GameEntity activate(GameEntity entity);
	/**
	 * A method for checking if this {@link Action} has cool down period.
	 * @return True if {@link Action} is cooling down, false otherwise.
	 */
	public abstract boolean isCoolingDown();
	
	@Override
	public Snapshot getSnapshot(boolean delta) {
		if(!delta) snapshot.id = getId();
		return snapshot;
	}

	@Override
	public Component fill(Snapshot snapshot) {
		return this;
	}
	
	@Override
	public void loadClient(GameEntity entity) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void destroy(GameEntity entity) {
		// TODO:
	}
	
	@Override
	public void runServer(GameEntity entity, float delta) {
		
	}
	
	@Override
	protected void loadServer(GameEntity entity) {
		loadClient(entity);
	}
}
