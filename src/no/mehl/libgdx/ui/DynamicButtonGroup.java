package no.mehl.libgdx.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class DynamicButtonGroup {
	
	private Array<Button> buttons;
	private float startX, startY;
	private Interpolation ipol;
	private float padding = 5f;
	private float duration = 0.5f;
	private boolean visible;
	private Type type;
	
	public DynamicButtonGroup(Type type, Interpolation ipol, Button... buttons) {
		this(type, 0, 0, ipol, buttons);
	}
	
	public DynamicButtonGroup(Type type, float x, float y, Interpolation ipol, Button... buttons) {
		this.buttons = new Array<Button>(buttons);
		this.startX = x;
		this.startY = y;
		this.ipol = ipol;
		this.type = type;
		
		setup();
		run(-1);
	}
	
	private void setup() {
		float width = 0;
		for (int i = 0; i < buttons.size; i++) {
			if(buttons.items[i].getWidth() > width) width = buttons.items[i].getWidth();
			buttons.items[i].setPosition(startX, startY);
		}
		for (int i = 0; i < buttons.size; i++) {
			buttons.items[i].setWidth(width);
		}
		
	}
	
	public void toggle() {
		visible = !visible;
		if(visible) run(1);
		else run(-1);
		setTouchable();
	}
	
	public void setTouchable() {
		Touchable t = visible ? Touchable.enabled : Touchable.disabled;
		for (int i = 0; i < buttons.size; i++) {
			buttons.items[i].setTouchable(t);
		}
	}
	
	public void toggle(boolean hide) {
		if(visible && hide) toggle();
	}
	
	public void toggle(float x, float y) {
		// Only reposition when hidden
		if(!visible) {
			startX = x;
			startY = y;
			setup();
		}
		toggle();
	}
	
	public void reset() {
		for (int i = 0; i < buttons.size; i++) {
			buttons.items[i].setPosition(startX, startY);
			buttons.items[i].setVisible(true);
		}
		
	}
	
	private void topDown(int dir) {
		int height = 0;
		for (int i = 0; i < buttons.size; i++) {
			buttons.items[i].addAction(moveTo(startX, startY + height, duration, ipol));
			if(dir > 0) height += buttons.items[i].getHeight() + padding;
			else height = 0;
		}
	}
	
	private void circle(int dir) {
		
		Circle circle = new Circle(buttons.size);
		
		float angle = MathUtils.PI2/circle.points;
		
		Action action = null;
		
		for (int i = 0; i < circle.points; i++) {
			float pX = 0;
			float pY = 0;
			
			// Distribute around circle
			if(dir > 0) {
				pX = circle.radius * MathUtils.sin(angle*i) - circle.radius * 0.5f;
				pY = circle.radius * MathUtils.cos(angle*i) - circle.radius * 0.5f;
				action = sequence(fadeIn(0), moveTo(startX + pX, startY + pY, duration, ipol));
			}  
			// Go back to origin
			else {
				action = sequence(moveTo(startX + pX, startY + pY, duration, ipol), fadeOut(0.1f));
			}
			buttons.items[i].addAction(action);
		}
		
		
	}
	
	private void run(int dir) {
		switch (type) {
		case TOPDOWN:
			topDown(dir);
			break;
		case CIRCLE:
			circle(dir);
		default:
			break;
		}
	}

	public void addToStage(Stage stage) {
		for (int i = 0; i < buttons.size; i++) {
			stage.addActor(buttons.get(i));
		}
	}
	
	public static class Circle {
		public int points;
		public int angle = 360;
		public float radius = 35f;
		
		public Circle(int points) {
			this.points = points;
		}
	}
	
	public enum Type {
		TOPDOWN, CIRCLE;
	}
	
	public float getX() {
		return this.startX;
	}
	
	public float getY() {
		return this.startY;
	}

}
