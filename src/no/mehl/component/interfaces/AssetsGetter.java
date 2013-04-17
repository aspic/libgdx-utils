package no.mehl.component.interfaces;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface AssetsGetter {
	public TextureRegion get(String key);
	public String[] listTextures();
}
