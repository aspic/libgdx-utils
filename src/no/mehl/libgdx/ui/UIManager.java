package no.mehl.libgdx.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
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
	
	private UIManager() {
		super();
	}
	
	public void loadUI() {
		this.load(atlasPath, TextureAtlas.class);
	}
	
	private void initializeUI() {
		uiSkin = new Skin(Gdx.files.internal(skinPath), this.get(atlasPath, TextureAtlas.class));
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
