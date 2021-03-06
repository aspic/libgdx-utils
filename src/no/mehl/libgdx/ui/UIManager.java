package no.mehl.libgdx.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import no.mehl.libgdx.utils.AssetsGetter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * A singleton class for handling all loading of assets, such as fonts and textures.
 * 
 * @author Kjetil Mehl <kjetil@no.logic.no.mehl.jd.logic.entity.logic.no>
 */
public class UIManager extends AssetManager implements AssetsGetter {
	
	private static UIManager INSTANCE = new UIManager();
	
	private boolean loaded;
	private final String DEFAULT = "DEFAULT";
	
	private Skin uiSkin;
	
	private String atlasPath;
	private String skinPath;

	private float scale;
	
	private UIManager() {
		super();
	}
	
	public void loadUI(float screenHeight) {
		this.load(atlasPath, TextureAtlas.class);
		
//		load("models/beveled_box.obj", Model.class);
//		load("models/jatteplanet.obj", Model.class);
		
		this.scale = screenHeight/768; // Reference height
	}

    public UIManager appendAtlas(String path) {
        load(path, TextureAtlas.class);
        return this;
    }
	
	private void initializeUI() {

        uiSkin = new Skin(Gdx.files.internal(skinPath), this.get(atlasPath, TextureAtlas.class));
		
		BitmapFont font = uiSkin.getFont(DEFAULT);
		font.setScale((15 * scale)/font.getCapHeight());
		uiSkin.add(DEFAULT, font);
	}
	
//	/** Loads all .obj models from the specified path */
//	public void loadModels(String modelPath) {
//		FileHandle handle = Gdx.files.internal(modelPath);
//		
//		System.out.println(handle.exists() + " " + handle.list().length);
//		
//		for (FileHandle child : handle.list()) {
//			System.out.println("Loads: " + child.path());
//			if(child.extension().equals("obj")) load(child.path(), Model.class);
//		}
//	}
	
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
		return new TextButton(text, uiSkin.get(key, TextButtonStyle.class));
	}
	
	public Label getLabel() {
		return getLabel("");
	}
	
	public Label getLabel(String text) {
		return getLabel(DEFAULT, text);
	}
	
	public Label getLabel(String key, String text) {
		return new Label(text, uiSkin.get(key, LabelStyle.class));
	}
	
	public com.badlogic.gdx.scenes.scene2d.ui.Slider getSlider(float min, float max, float step) {
		return getSlider(DEFAULT, min, max, step);
	}
	
	public com.badlogic.gdx.scenes.scene2d.ui.Slider getSlider(String key, float min, float max, float step) {
		return new com.badlogic.gdx.scenes.scene2d.ui.Slider(min, max, step, false, uiSkin.get(key, SliderStyle.class));
	}

    public TextField getTextField() {
        return getTextField("");
    }
	
	public TextField getTextField(String value) {
		return getTextField(value, DEFAULT);
	}
	
	public TextField getTextField(String value, String key) {
		return new TextField(value, uiSkin.get(key, TextFieldStyle.class));
	}
	
	public com.badlogic.gdx.scenes.scene2d.ui.List getList(String[] items) {
		return getList(items, DEFAULT);
	}
	
	public com.badlogic.gdx.scenes.scene2d.ui.List getList(String[] items, String key) {
		com.badlogic.gdx.scenes.scene2d.ui.List<String> list = new com.badlogic.gdx.scenes.scene2d.ui.List<String>(uiSkin.get(key, ListStyle.class));
		list.setItems(items);
		return list;
	}
	
	public TextureRegion getRegion(String key) {
		return uiSkin.getRegion(key);
	}
	
	public Drawable getDrawable(String key) {
		return this.uiSkin.get(key, Drawable.class);
	}
	
	public com.badlogic.gdx.scenes.scene2d.ui.SelectBox getSelectBox(Object[] items) {
		return getSelectBox(DEFAULT, items);
	}

	public SelectBox getSelectBox(String key, Object[] items) {
		SelectBox<Object> selectBox = new SelectBox<Object>(uiSkin.get(key, SelectBoxStyle.class));
		selectBox.setItems(items);
		return selectBox;
	}

    public ScrollPane getScrollPane(Actor widget) {
        return new ScrollPane(widget, uiSkin.get(DEFAULT, ScrollPane.ScrollPaneStyle.class));
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
	
	public void build(String atlasPath, String skinPath) {
		this.atlasPath = atlasPath;
		this.skinPath = skinPath;
	}

    @Override
    public TextureRegion get(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String[] listTextures() {
		// TODO Auto-generated method stub
		return null;
	}

	public Model getModel(String path) {
		return this.get(path, Model.class);
	}

	public TextureRegion get(String path, String key) {
        return get(path, TextureAtlas.class).findRegion(key);
	}
}
