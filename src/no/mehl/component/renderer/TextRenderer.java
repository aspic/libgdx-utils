package no.mehl.component.renderer;

import com.badlogic.gdx.graphics.Camera;
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
	
	public TextRenderer() {}
	
	public TextRenderer(String text) {
		this.text = text;
		this.offset = new Vector2(1.5f, 1.5f);
	}

	@Override
	public void runServer(GameEntity entity, float delta) {
		runClient(entity, delta);
	}

	@Override
	public void runClient(GameEntity entity, float delta) {
		if(physics != null && text != null) {
			Matrix4 projection = camera.combined.cpy();
			
			projection.translate(physics.getPosition().x - camera.viewportWidth*0.5f + offset.x, physics.getPosition().y - camera.viewportHeight*0.5f + offset.y, physics.getPosition().z);
			projection.scale(0.1f, 0.1f, 0.1f);
			
			batch.setProjectionMatrix(projection);
			batch.begin();
			font.draw(batch, text, 0, 0);
			batch.end();
		}
	}
	
	Camera camera;
	
	@Override
	public void load(GameEntity entity) {
		physics = entity.getExtends(Physics.class);
		font = new BitmapFont();
		batch = new SpriteBatch();
		camera = ShaderManager.getInstance().getCamera();
	}
	
	@Override
	public Renderer fill(Snapshot snapshot) {
		if(snapshot.s_0 != null) text = snapshot.s_0;
		if(snapshot.v2_0 != null) this.offset = snapshot.v2_0;
		
		return super.fill(snapshot);
	}
	
	public Snapshot getSnapshot(boolean delta) {
		this.snapshot.s_0 = text;
		this.snapshot.v2_0 = this.offset;
		
		return super.getSnapshot(delta);
	}

}
