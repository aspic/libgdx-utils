package no.mehl.libgdx.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class Grid {
	
	public Array<GridLine> lines;
	public Color color;

	int horizontal, vertical;
	float cellSize;

	public Grid(int horizontal, int vertical, float cellSize) {
		this.horizontal = horizontal;
		this.vertical = vertical;

		this.cellSize = cellSize;

		lines = new Array<GridLine>();
		if ((this.horizontal == 1) || (this.vertical == 1)) {
			setupFloor();
		} else {
			setupCells();
		}
	}

	private void setupCells() {
		for (int x = 0; x < horizontal; x++) { // vertical lines
			GridLine line = new GridLine(x * cellSize, 0, x * cellSize,
					cellSize * vertical);
			lines.add(line);
		}

		for (int y = 0; y < vertical; y++) { // horizontal lines
			GridLine line = new GridLine(0, y * cellSize,
					horizontal * cellSize, y * cellSize);
			lines.add(line);
		}
	}

	private void setupFloor() {
		GridLine line = new GridLine(0, 0, horizontal * cellSize, 0);
		lines.add(line);
	}

	public class GridLine {
		public float x, y, x2, y2;
		public Color color;

		public GridLine(float x, float y, float x2, float y2) {
			this.x = x;
			this.y = y;
			this.x2 = x2;
			this.y2 = y2;

			color = new Color();
		}
	};
}
