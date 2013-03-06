package no.mehl.libgdx.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * A singleton class for handling all loading of assets, such as fonts and textures.
 * 
 * @author Kjetil Mehl <kjetil@mehl.no>
 */
public class UIManager extends AssetManager {
	
	private static UIManager INSTANCE = new UIManager();
	
	private boolean loaded;
	private final String DEFAULT = "DEFAULT";
	
	private Skin uiSkin;
	
	private String atlasPath;
	private String skinPath;
	private TextureParameter params;
	
	private float scale;
	
	private UIManager() {
		super();
	}
	
	public void loadUI(float screenHeight) {
		this.load(atlasPath, TextureAtlas.class);
		
		this.scale = screenHeight/768; // Reference height
	}
	
	private void initializeUI() {
		uiSkin = new Skin(Gdx.files.internal(skinPath), this.get(atlasPath, TextureAtlas.class));
		
		BitmapFont font = uiSkin.getFont("DEFAULT");
		font.setScale((15 * scale)/font.getCapHeight());
		uiSkin.add("DEFAULT", font);
	}
	
	public Button getButton() {
		return getButton(DEFAULT);
	}
	
	public Button getButton(String key) {
		return new Button(uiSkin.get(key, ButtonStyle.class));
	}
	
	public TextButton getTextButton(String text) {
		return getTextButton(DEFAULT, text);
	}
	
	public TextButton getTextButton(String key, String text) {
		return new TextButton(text, uiSkin.get(DEFAULT, TextButtonStyle.class));
	}
	
	public Label getLabel() {
		return getLabel("");
	}
	
	public Label getLabel(String text) {
		return new Label(text, uiSkin.get(DEFAULT, LabelStyle.class));
	}
	
	public Slider getSlider(float min, float max, float step) {
		return getSlider(DEFAULT, min, max, step);
	}
	
	public Slider getSlider(String key, float min, float max, float step) {
		return new Slider(min, max, step, false, uiSkin.get(key, SliderStyle.class));
	}
	
	public TextField getTextField(String value) {
		return getTextField(value, "DEFAULT");
	}
	
	public TextField getTextField(String value, String key) {
		return new TextField(value, uiSkin.get(key, TextFieldStyle.class));
	}
	
	public TextureRegion getRegion(String key) {
		return uiSkin.getRegion(key);
	}
	
	public Drawable getDrawable(String key) {
		return this.uiSkin.get(key, Drawable.class);
	}
	
	public boolean initializeAssets() {
		if(!loaded && update()) {
			initializeUI();
			loaded = true;
			return true;
		}
		return false;
	}
	
	public static UIManager getInstance() {
		return UIManager.INSTANCE;
	}
	
	public void build(String atlasPath, String skinPath, TextureParameter params) {
		this.atlasPath = atlasPath;
		this.skinPath = skinPath;
		this.params = params;
	}
}
