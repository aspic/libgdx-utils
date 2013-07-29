package no.mehl.libgdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.materials.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class Chunk {

	private float x, y, z;
	private Model model;
	private float width, height, depth;
	private Color[][] pixels;
	
	public Chunk(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Model rebuild() {
		
		Pixmap map = new Pixmap(Gdx.files.external("dev/git/gc/assets/3Doverlay/smile.png"));
		
		width = 2f/map.getWidth();
		height = 2f/map.getHeight();
		depth = 2f/map.getWidth();

		pixels = new Color[map.getWidth()][map.getHeight()];
		
		// Fill
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				
				// Retrieve colour
				Color c = new Color();
				Color.rgba8888ToColor(c, map.getPixel(i, j));

				// Update arrays
				if(c.a == 0) c = null;
				pixels[i][j] = c;
			}
		}

		// Create model
		ModelBuilder builder = new ModelBuilder();
		builder.begin();
		
		TextureAttribute pixelTexture = TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("overlay/pixel-test.png")));
		TextureAttribute glassTexture = TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("overlay/pixel-glass.png")));
		
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				
				if(pixels[i][j] == null) continue;
				
				// Do check
				
				Color c = new Color();
				Color.rgba8888ToColor(c, map.getPixel(i, j));
				
				// Skip translucent pixels
				if(c.a == 0) continue;
				
				Material material;
				if(c.a < 1f) {
					material = new Material(ColorAttribute.createDiffuse(c), new BlendingAttribute(GL20.GL_ONE, GL20.GL_ONE));
				} else {
					material = new Material(ColorAttribute.createDiffuse(c), pixelTexture);
				}
				MeshPartBuilder part = builder.part(i+"#"+j, GL10.GL_TRIANGLES, Usage.Position | Usage.TextureCoordinates | Usage.Normal, material);
				
				// Add faces manually
				if(((j > 0) && pixels[i][j-1] == null) || (j == 0 && pixels[i][j] != null)) {
					addTopFace(part, i*width, (j-1)*height, 0, width, height, depth);
				}
				
				if((i > 0 && pixels[i-1][j] == null) || (i == 0 && pixels[i][j] != null)) {
					addLeftFace(part, i*width, j*height, 0, width, height, depth);
				}
				
				if((j < pixels[i].length-1 && pixels[i][j+1] == null) || (j == pixels[i].length-1 && pixels[i][j-1] != null)) {
					addBotFace(part, i*width, (j+1)*height, 0, width, height, depth);
				}
				
				if((i < pixels.length-1 && pixels[i+1][j] == null) || (i == pixels.length && pixels[i][j] != null)) {
					addRightFace(part, i*width, j*height, 0, width, height, depth);
				}
				
				// Add nevertheless
				addFrontFace(part, i*width, j*height, 0, width, height, depth);
				addBackFace(part, i*width, j*height, 0, width, height, depth);
			}
		}
		
		return builder.end();
	}
	
	private void addTopFace(MeshPartBuilder part, float x, float y, float z, float w, float h, float d) {
		part.rect(x, y+h, z, x+w, y+h, z, x+w, y+h, z+d, x, y+h, z+d, 0, -1, 0);
	}
	
	private void addBotFace(MeshPartBuilder part, float x, float y, float z, float w, float h, float d) {
		part.rect(x, y, z, x, y, z+d, x+w, y, z+d, x+w, y, z, 0, 1, 0);		
	}
	
	private void addFrontFace(MeshPartBuilder part, float x, float y, float z, float w, float h, float d) {
		part.rect(x, y, z, x, y+h, z, x+w, y+h, z, x+w, y, z, 0, 0, -1);
	}
	
	private void addBackFace(MeshPartBuilder part, float x, float y, float z, float w, float h, float d) {
		part.rect(x+w, y, z+d, x+w, y+h, z+d, x, y+h, z+d, x, y, z+d, 0, 0, 1);
	}
	
	private void addRightFace(MeshPartBuilder part, float x, float y, float z, float w, float h, float d) {
		part.rect(x+w, y, z, x+w, y+h, z, x+w, y+h, z+d, x+w, y, z+d, -12, 0, 0);
	}
	
	private void addLeftFace(MeshPartBuilder part, float x, float y, float z, float w, float h, float d) {
		part.rect(x, y, z+d, x, y+h, z+d, x, y+h, z, x, y, z, 1f, 0, 0);
	}
}
