package othello6;

import java.util.*;
import java.util.stream.Collectors;

public class Board implements Cloneable {
  public final static int SIZE = 6;
  public final static int BLACK = 1;
  public final static int WHITE = -1;
  public final static int NONE = 0;

  int board[];
  Map<Integer, Integer> counts = new HashMap<>();
  Set<Position> adjacencies = new HashSet<>();
  Map<Integer, Set<Position>> legals = new HashMap<>();
  Move lastMove = null;
  int currentPlayer = BLACK;

  public Board() {
    this.board = new int[SIZE * SIZE];
    Arrays.fill(this.board, NONE);
  }

  private Board(int board[]) {
    this.board = Arrays.copyOf(board, board.length);
  }

  void init() {
    this.set(new Position("c3"), BLACK);
    this.set(new Position("d4"), BLACK);
    this.set(new Position("d3"), WHITE);
    this.set(new Position("c4"), WHITE);
  }

  public boolean equals(Object otherObj) {
    if (otherObj instanceof Board) {
      Board other = (Board) otherObj;
      return Arrays.equals(this.board, other.board);
    }
    return false;
  }

  public Board clone() {
    Board other = new Board(this.board);
    other.counts = this.counts; // don't reuse
    other.adjacencies = new HashSet<Position>(this.adjacencies);
    other.legals = this.legals; // don't reuse
    other.lastMove = this.lastMove; // don't reuse
    other.currentPlayer = this.currentPlayer;
    return other;
  }

  public String toString() {
    return BoardFormatter.format(this);
  }

  public int getCount(int color) {
    return this.counts.getOrDefault(color, 0);
  }

  public List<Move> getLegalMoves(int color) {
    return this.legals.get(color).stream()
      .map(p -> new Move(p, color)).collect(Collectors.toList());
  }

  public void place(Move move) {
    List<Line> lines = this.lines(move.square);
    for (Line line : lines)
      for (Move move1 : line.outflankingDiscs(move.color))
        this.board[move1.square.getIndex()] = move.color;
    this.set(move.square, move.color);
    this.lastMove = move;
    this.currentPlayer *= -1;
  }

  public void pass() {
    this.lastMove = new Move(Move.PASS.square, this.currentPlayer);
    this.currentPlayer *= -1;
  }

  public int get(Position p) {
    return this.board[p.getIndex()];
  }

  public void set(Position p, int color) {
    this.board[p.getIndex()] = color;
    this.update(p);
  }

  void update(Position p) {
    this.adjacencies.remove(p);
    for (Position q: p.adjacencies())
      if (this.get(q) == NONE)
        this.adjacencies.add(q);

    this.legals = new HashMap<>();
    this.legals.put(BLACK, this.findLegals(BLACK));
    this.legals.put(WHITE, this.findLegals(WHITE));

    this.counts = new HashMap<>();
    this.counts.put(BLACK, 0);
    this.counts.put(WHITE, 0);
    this.counts.put(NONE, 0);
    Arrays.stream(this.board).forEach(color -> {
      this.counts.put(color, counts.get(color) + 1);
    });
  }

  Line line(Position p, int direction) {
    Line line = new Line();
    Position[] os = Position.offsets[direction];
    for (Position o: os) {
      Position q = p.shift(o);
      if (q.isValid(SIZE)) {
        Move move = new Move(q, this.get(q));
        line.add(move);
      }
    }
    return line;
  }

  List<Line> lines(Position p) {
    ArrayList<Line> lines = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      Line line = this.line(p, i);
      lines.add(line);
    }
    return lines;
  }

  Set<Position> findLegals(int color) {
    var ps = new HashSet<Position>();
    for (Position p: this.adjacencies) {
      var lines = this.lines(p);
      for (Line line: lines)
        if (line.outflankingDiscs(color).size() > 0)
          ps.add(p);
    }
    return ps;
  }

  public void flip() {
    for (int i = 0; i < this.board.length; i++)
      this.board[i] *= -1;
    swapLegals();
    swapCounts();
  }

  void swapLegals() {
    this.legals = new HashMap<>(this.legals);
    var tmp = this.legals.get(BLACK);
    this.legals.put(BLACK, this.legals.get(WHITE));
    this.legals.put(WHITE, tmp);
  }

  void swapCounts() {
    this.counts = new HashMap<>(this.counts);
    var tmp = this.counts.get(BLACK);
    this.counts.put(BLACK, this.counts.get(WHITE));
    this.counts.put(WHITE, tmp);
  }
}
