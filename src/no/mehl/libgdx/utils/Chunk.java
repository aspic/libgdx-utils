package no.mehl.libgdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;

public class Chunk {

	private Model model;
	private float width, height, depth;
	private Color[][] pixels;
    private Array<String> parts = new Array<String>();

    private MeshPart[][] grid;


	public Chunk(int sizeX, int sizeY) {
        grid = new MeshPart[sizeX][sizeY];
	}

    public Model rebuild() {
        return rebuild("dev/git/gc/assets/3Doverlay/smile.png");
    }
	
	public Model rebuild(String imagePath) {

        parts.clear();
		
		Pixmap map = new Pixmap(Gdx.files.internal(imagePath));
		
		width = 1f;
		height = 1f;
		depth = 1f;

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

        grid = new MeshPart[pixels.length][pixels[0].length];

		// Create model
		ModelBuilder builder = new ModelBuilder();
		builder.begin();

		TextureAttribute pixelTexture = TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("assets/textures_sprites/tile_tex1.png")));
		// TextureAttribute glassTexture = TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("overlay/pixel-glass.png")));
		
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
                String id = i + "#" + j;
				MeshPartBuilder part = builder.part(id, GL20.GL_TRIANGLES, Usage.Position | Usage.TextureCoordinates | Usage.Normal, material);
                parts.add(id);
				
				// Add faces manually
                grid[i][j] = createPillar(part, i, j, pixels);
			}
		}
        model = builder.end();
        return model;
	}

    public MeshPart createVoxel(MeshPartBuilder part, int i, int j, Object[][] grid) {
        if(((j > 0) && grid[i][j-1] == null) || (j == 0 && grid[i][j] != null)) {
            addTopFace(part, i*width, j*height, 0, width, height, depth);
        }

        if((i > 0 && grid[i-1][j] == null) || (i == 0 && grid[i][j] != null)) {
            addLeftFace(part, i*width, j*height, 0, width, height, depth);
        }

        if((j < grid[i].length-1 && grid[i][j+1] == null) || (j == grid[i].length-1)) {
            addBotFace(part, i*width, j*height, 0, width, height, depth);
        }

        if((i < grid.length-1 && grid[i+1][j] == null) || (i == grid.length - 1)) {
            addRightFace(part, i*width, j*height, 0, width, height, depth);
        }

        // Add nevertheless
        addFrontFace(part, i*width, j*height, 0, width, height, depth);
        addBackFace(part, i*width, j*height, 0, width, height, depth);

        return part.getMeshPart();
    }

    public MeshPart createPillar(MeshPartBuilder part, int i, int j, Object[][] grid) {
        addTopFace(part, i*width, j*height, 0, width, height, depth);
        addLeftFace(part, i*width, j*height, 0, width, height, depth);
        addRightFace(part, i*width, j*height, 0, width, height, depth);
        addBackFace(part, i*width, j*height, 0, width, height, depth);
        return part.getMeshPart();
    }

    public void remove(int x, int y) {
        if(grid[x][y] != null) {
            grid[x][y].numVertices = 0;
            model.meshParts.removeValue(grid[x][y],false);
            grid[x][y] = null;
        }
    }

	private void addTopFace(MeshPartBuilder part, float x, float y, float z, float w, float h, float d) {
		part.rect(x, y+h, z, x, y+h, z+d, x+w, y+h, z+d, x+w, y+h, z, 0, 1, 0);
	}
	
	private void addBotFace(MeshPartBuilder part, float x, float y, float z, float w, float h, float d) {
		part.rect(x, y, z, x+w, y, z, x+w, y, z+d, x, y, z+d, 0, 1, 0);
	}
	
	private void addFrontFace(MeshPartBuilder part, float x, float y, float z, float w, float h, float d) {
		part.rect(x, y, z, x, y+h, z, x+w, y+h, z, x+w, y, z, 0, 0, -1);
	}
	
	private void addBackFace(MeshPartBuilder part, float x, float y, float z, float w, float h, float d) {
		part.rect(x+w, y, z+d, x+w, y+h, z+d, x, y+h, z+d, x, y, z+d, 0, 0, 1);
	}
	
	private void addRightFace(MeshPartBuilder part, float x, float y, float z, float w, float h, float d) {
		part.rect(x+w, y, z, x+w, y+h, z, x+w, y+h, z+d, x+w, y, z+d, -1, 0, 0);
	}
	
	private void addLeftFace(MeshPartBuilder part, float x, float y, float z, float w, float h, float d) {
		part.rect(x, y, z+d, x, y+h, z+d, x, y+h, z, x, y, z, 1f, 0, 0);
	}

    public Array<String> getParts() {
        return this.parts;
    }

    public Model getModel() {
        return model;
    }

    public MeshPart[][] getGrid() {
        return this.grid;
    }
}
