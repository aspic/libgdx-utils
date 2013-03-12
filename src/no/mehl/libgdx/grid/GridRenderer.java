package no.mehl.libgdx.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GridRenderer {
    ShapeRenderer renderer;
    Camera camera;
   
    public GridRenderer(Camera camera){
            renderer = new ShapeRenderer();
            this.camera = camera;
    }
   
	public void renderGrid(Grid grid, Color color, boolean visible) {
		if (visible) {
			renderer.setProjectionMatrix(camera.combined);
			renderer.begin(ShapeType.Line);
			renderer.setColor(color);

			for (int i = 0; i < grid.lines.size; i++) {
				Grid.GridLine line = grid.lines.get(i);

				if (line.x == line.x2) { // check if the lines are vertical
					if ((line.x > camera.position.x
							+ (camera.viewportWidth / 2))) {
						line.x -= (grid.cellSize * grid.horizontal);
						line.x2 = line.x;
					}
					if ((line.x < camera.position.x
							- (camera.viewportWidth / 2))) {
						line.x += (grid.cellSize * grid.horizontal);
						line.x2 = line.x;
					}
					renderer.line(line.x, camera.position.y
							- (camera.viewportHeight / 2), line.x2,
							camera.position.y + (camera.viewportHeight / 2));
				} else { // if they are horizontal
					if ((line.y > camera.position.y
							+ (camera.viewportHeight / 2))) {
						line.y -= (grid.cellSize * grid.vertical);
						line.y2 = line.y;
					}
					if ((line.y < camera.position.y
							- (camera.viewportHeight / 2))) {
						line.y += (grid.cellSize * grid.vertical);
						line.y2 = line.y;
					}
					renderer.line(camera.position.x
							- (camera.viewportWidth / 2), line.y, 0.1f,
							camera.position.x + (camera.viewportWidth / 2),
							line.y2, 0.1f);
				}
			}

			renderer.end();

		}
	}
}