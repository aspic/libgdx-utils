package no.mehl.libgdx.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;

public interface AssetsGetter {
	public TextureRegion get(String key);
	public String[] listTextures();
	public Model getModel(String path);
}
