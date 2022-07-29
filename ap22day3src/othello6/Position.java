package othello6;

import java.util.*;

public class Position {
	public static List<Position> all;
	public static Position[][] offsets;

	int col;
	int row;

	static {
		Position.all = new ArrayList<>();
		for (int row = 0; row < Board.SIZE; row++)
			for (int col = 0; col < Board.SIZE; col++)
				Position.all.add(new Position(col, row));

		Position[][] offsets = new Position[8][Board.SIZE - 1];
		for (int i = 1; i < Board.SIZE; i++) {
			if (i == 0) continue;
			int[][] deltas = { { -i, 0 }, { -i, i }, { 0, i }, { i, i }, { i, 0 }, { i, -i }, { 0, -i }, { -i, -i } };
			for (int k = 0; k < deltas.length; k++)
				offsets[k][i - 1] = new Position(deltas[k][0], deltas[k][1]);
		}
		Position.offsets = offsets;
	}
	
	public Position(int col, int row) {
		this.col = col;
		this.row = row;
	}

	public Position(int index) {
	  this(index % Board.SIZE, index / Board.SIZE);
	}
	
	public Position(String position) {
		this.col = position.charAt(0) - 'a';
		this.row = position.charAt(1) - '1';
	}

	public static int toIndex(String position) {
	  int col = (position.charAt(0) - 'a');
	  int row = (position.charAt(1) - '1');
	  return Board.SIZE * row + col;
	}
	
	public int getRow() { return this.row; }
	public int getCol() { return this.col; }
	public int getIndex() { return Board.SIZE * this.row + this.col; }

	public int hashCode() {
		return Objects.hash(this.col, this.row);
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Position other = (Position)obj;
		return this.row == other.row && this.col == other.col;
	}

	public String toString() {
		return Position.colToString(this.col) + (this.row + 1);
	}

	public static String colToString(int col) {
		return Character.toString('a' + col);
	}

	public boolean isValid(int size) {
		return 0 <= this.row && this.row < size && 0 <= this.col && this.col < size;
	}

	public Position shift(int col, int row) {
	  return new Position(this.col + col, this.row + row);
	}
	
	public Position shift(Position offset) {
		return new Position(this.col + offset.col, this.row + offset.row);
	}

	List<Position> adjacencies() {
		ArrayList<Position> ps = new ArrayList<>();
		for (int i = 0; i < Position.offsets.length; i++) {
			Position p = this.shift(Position.offsets[i][0]);
			if (p.isValid(Board.SIZE)) ps.add(p);
		}
		return ps;
	}
}