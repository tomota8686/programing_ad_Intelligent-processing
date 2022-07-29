package othello6;

import static othello6.Board.*;

public class BoardFormatter {

  public static String format(Board board) {
    StringBuilder buf = new StringBuilder();

    buf.append("  ");
    for (int i = 0; i < SIZE; i++) buf.append(Position.colToString(i));
    buf.append(String.format("  %s %s\n",
        lastMoveToString(board),
        countsToString(board)));

    for (Position p : Position.all) {
      int color = board.get(p);
      if (p.getCol() == 0) buf.append((p.getRow() + 1) + "|");

      if (color == NONE) {
        boolean b = board.legals.get(BLACK).contains(p);
        boolean w = board.legals.get(WHITE).contains(p);
        boolean legal = false;
        if (board.currentPlayer == BLACK && b) legal = true;
        if (board.currentPlayer == WHITE && w) legal = true;
        buf.append(legal ? '.' : ' ');
      } else {
        char c = diskToChar(color);
        if (board.lastMove != null &&
            board.lastMove.isPass() == false &&
            board.lastMove.square.equals(p)) {
          c += 'A' - 'a';
        }
        buf.append(c);
      }

      if (p.getCol() == SIZE - 1) buf.append("|\n");
    }

    buf.append(legalsToString(board));

    return buf.toString();
  }

  public static char diskToChar(int color) {
    return (color == 1 ? 'o' : 'x');
  }

  public static String lastMoveToString(Board board) {
    if (board.lastMove == null) {
      return "";
    } else if (board.lastMove.isPass()) {
      return board.lastMove.toString();
    } else {
      return board.lastMove.square.toString() + diskToChar(board.lastMove.color);
    }
  }

  public static String legalsToString(Board board) {
    var buf = new StringBuilder();
    buf.append(diskToChar(BLACK));
    buf.append(":");
    buf.append(board.legals.get(BLACK));
    buf.append(" ");
    buf.append(diskToChar(WHITE));
    buf.append(":");
    buf.append(board.legals.get(WHITE));
    return buf.toString();
  }

  public static String countsToString(Board board) {
    var buf = new StringBuilder();
    buf.append(diskToChar(BLACK));
    buf.append(":");
    buf.append(board.counts.get(BLACK));
    buf.append(" ");
    buf.append(diskToChar(WHITE));
    buf.append(":");
    buf.append(board.counts.get(WHITE));
    return buf.toString();
  }
}
