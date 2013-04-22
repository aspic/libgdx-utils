package no.mehl.component;

import no.mehl.component.EntityManager.Context;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

/**
 * Main component class. Every component is described by an id and each {@link Component} needs to "implement" specific methods.
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public abstract class Component {

	private static Array<Class<? extends Component>> classes = new Array<Class<? extends Component>>();
	private boolean changed = true;
	protected boolean initialized;
	private int id = -1;
	
	/** This method registers all serializeable components. Must be loaded in each end point. */
	public static void registerComponents(Class... comp) {
		for (int i = 0; i < comp.length; i++) {
			classes.add(comp[i]);
		}
	}
	
	/** Marks this component as changed. Will get sent upon next network update */
	public void setChanged() {
		this.changed = true;
	}
	
	/** Marks this component as synchronised, meaning that it has been transmitted.
	 * This method should only be triggered by the class doing the transmittal. */
	public void setSynced() {
		this.changed = false;
	}
	/** Indicates whether this component has been changed during last transmit */
	public boolean isChanged() {
		return this.changed;
	}
	
	/** 
	 * Returns the internal mapping for this component. Is used to reflect correct class on client side
	 * TODO: Cache this id?
	 */
	public int getId() {
		return getComponentId(this.getClass());
	}
	/** Gets the identificator for this {@link Component} */
	public int getComponentId(Class<? extends Component> class1) {
		if(this.id >= 0) return this.id;
		int id = 0;
		for (Class<? extends Component> class2 : classes) {
			if(class1.equals(class2)) {
				this.id = id;
				return this.id;
			}
			id++;
		}
		return -1;
	}
	/** Returns the {@link Component} given the identificator */
	public static Component getComponent(int id) {
		if(id < 0) {
			System.err.println("Component not found");
		}
		
		try {
			return classes.get(id).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** Indicates whether this {@link Component} extends given @param Clazz */
	public boolean componentExtends(Class clazz) {
		return clazz.isAssignableFrom(getClass());
	}
	
	public void getGraphicalRepresentation(Table table, Object object) {
		System.out.println("NO GRAPHICAL REP FOR " + getClass());
	}
	
	public void initialize(GameEntity entity, EntityManager.Context context) {
		if(initialized) return;
		
		if(context == Context.CLIENT) {
			loadClient(entity);
		} else if(context == Context.SERVER) {
			loadServer(entity);
		} else if(context == Context.BOTH) {
			loadServer(entity);
			loadClient(entity);
		}
		
		initialized = true;
	}
	
	/** This method gets run on the game loop, to initialize a {@link Component} */
	protected abstract void loadClient(GameEntity entity);
	
	protected abstract void loadServer(GameEntity entity);
	
	/** {@link Component} gets run in a server context */
	public abstract void runServer(GameEntity entity, float delta);
	/** {@link Component} gets run in a client context */
	public abstract void runClient(GameEntity entity, float delta);
	
	/** Returns the updated version for this component snapshot. */
	public abstract Snapshot getSnapshot(boolean delta);
	/** Returns the component updated with the provided snapshot. */
	public abstract Component fill(Snapshot snapshot);
	/** Method triggered in {@link Component} to tear it down */
	public abstract void destroy(GameEntity entity);
	
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
