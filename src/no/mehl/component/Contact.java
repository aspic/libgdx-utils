package no.mehl.component;

/**
 * An abstract class defining contact responses for collisions.
 * @author Kjetil Mehl <kjetil@no.logic.no.mehl.jd.logic.entity.logic.no>
 */
public abstract class Contact extends Component {
	
	private Snapshot snapshot = new Snapshot();
	private boolean destroyed;
	private boolean handled;
	protected GameEntity entity;
	
	/** Load this component with reference to its entity. */
	public void loadClient(GameEntity entity) {
		this.entity = entity;
	}
	
	@Override
	protected void loadServer(GameEntity entity) {
		this.entity = entity;
	}
	
	@Override
	public void runServer(GameEntity entity, float delta) {
		
	}

	@Override
	public void runClient(GameEntity entity, float delta) {
		
	}
	
	/** Boolean indicating whether the {@link GameEntity} is destroyed. */
	public boolean isDestroyed() {
		return destroyed;
	}
	
	/** If true, this response will not be used more. Typically triggered by no.no.logic.no.mehl.jd.logic.entity.logic.no.mehl.jd.logic which are destroyed. */
	protected void setHandled() {
		this.handled = true;
	}
	
	/** Boolean indicating whether this component is handled, and should not be triggered again. */
	public boolean isHandled() {
		return this.handled;
	}
	
	/** Setting this contact as destroyed will mark its linked component as destroyed on the next update-pass. */
	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}
	
	@Override
	public void destroy(GameEntity entity) {
		
	}
	
	@Override
	public Snapshot getSnapshot(boolean delta) {
		snapshot.id = getId();
		
		return snapshot;
	}
	
	@Override
	public Contact fill(Snapshot snapshot) {
		return this;
	}
	
	/** Both responses of this collision impact. Will only be triggered if this contact has not been handled. */
	public abstract void collidesWith(GameEntity entity);
	
	public GameEntity getEntity() {
		return this.entity;
	}
}
