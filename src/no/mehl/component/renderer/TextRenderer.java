package no.mehl.component.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import no.mehl.component.GameEntity;
import no.mehl.component.Physics;
import no.mehl.component.Renderer;
import no.mehl.component.Snapshot;
import no.mehl.libgdx.utils.ShaderManager;

public class TextRenderer extends Renderer {
	
	private String text;
	private Vector2 offset;
	private BitmapFont font;
	private Physics physics;
	private SpriteBatch batch;
	private Camera camera;
	
	public TextRenderer() {
		this("Test text!");
	}
	
	public TextRenderer(String text) {
		this(text, new Vector2());
	}
	
	public TextRenderer(String text, Vector2 offset) {
		this(null, text, offset);
	}
	
	public TextRenderer(String key, String text, Vector2 offset) {
		this.key = listTextures()[0];
		this.text = text;
		this.offset = offset;
	}
	
	@Override
	public void loadClient(GameEntity entity) {
		physics = entity.getExtends(Physics.class);
		font = null;
		if(key != null) {
			font = new BitmapFont(Gdx.files.internal(key), false);
		} else {
			font = new BitmapFont(); // Standard font
		}
		batch = ShaderManager.getInstance().getSpriteBatch();
		camera = ShaderManager.getInstance().getCamera();
		
		if(color != null) font.setColor(color);
	}

	@Override
	public void runServer(GameEntity entity, float delta) {
		runClient(entity, delta);
	}

	@Override
	public void runClient(GameEntity entity, float delta) {
		if(physics != null && text != null) {
			Matrix4 projection = camera.combined.cpy();
			
			projection.translate(physics.getPosition().x, physics.getPosition().y , physics.getPosition().z + 1f);
			projection.scale(0.1f, 0.1f, 0.1f);
			
			batch.setProjectionMatrix(projection);
			batch.begin();
			font.draw(batch, text, 0, 0);
			batch.end();
		}
	}
	
	@Override
	public void setColor(Color color) {
		if(font != null) font.setColor(color);
		super.setColor(color);
	}
	
	
	@Override
	public Renderer fill(Snapshot snapshot) {
		if(snapshot.s_1 != null) text = snapshot.s_1;
		if(snapshot.v2_0 != null) this.offset = snapshot.v2_0;
		
		return super.fill(snapshot);
	}
	
	public Snapshot getSnapshot(boolean delta) {
		this.snapshot.s_1 = text;
		this.snapshot.v2_0 = this.offset;
		
		return super.getSnapshot(delta);
	}

	@Override
	public String[] listTextures() {
		return new String[]{
				"skin/minecraftia.fnt"
		};
	}

}
