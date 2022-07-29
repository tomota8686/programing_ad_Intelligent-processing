package othello6;

import java.util.*;

public class Line extends ArrayList<Move> {
  private static final long serialVersionUID = 1L;

  public ArrayList<Move> outflankingDiscs(int color) {
    ArrayList<Move> flippables = new ArrayList<>();
    if (this.size() <= 1) return flippables;

    for (int i = 0; i < this.size(); i++) {
      Move move = this.get(i);
      if (move.color == -color)
        flippables.add(move);
      else if (move.color == color)
        return flippables;
      else
        break;
    }

    flippables.clear();

    return flippables;
  }
}