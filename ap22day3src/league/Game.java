package league;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import othello6.*;
import static othello6.World.*;

public class Game {
  public final static int BLACK_ID = 0;
  public final static int WHITE_ID = 1;
  public final static List<Integer> colors = List.of(BLACK, WHITE);
  public final static Map<Integer, Integer> idColors = Map.of(BLACK_ID, BLACK, WHITE_ID, WHITE);
  public final static Map<Integer, Integer> colorIds = Map.of(BLACK, BLACK_ID, WHITE, WHITE_ID);

  final static long NO_TIMELIMIT = -1;

  public boolean verbose = false;

  List<Player> players = List.of();
  long timeLimit = -1;
  TimeUnit timeUnit;
  int turn;
  
  // record
  Integer scores[] = {0, 0};
  long times[] = {0, 0};
  int winner;
  World world;
  long time;

  public Game(List<Player> players, long timeLimit, TimeUnit timeUnit) {
    this.players = players;
    this.timeLimit = timeUnit != null ? TimeUnit.MILLISECONDS.convert(timeLimit, timeUnit) : NO_TIMELIMIT;
    this.timeUnit = timeUnit;
  }

  Player player(int color) { return this.players.get(colorIds.get(color)); }
  Player black() { return this.players.get(BLACK_ID); }
  Player white() { return this.players.get(WHITE_ID); }

  boolean hasTimeLimit() {
    return this.timeLimit > 0;
  }

  public void play() {
    this.world = new World();

    var plays = List.of(new Play(BLACK_ID, black()), new Play(WHITE_ID, white()));

    Play lastPlay = null;
    boolean pass = false;
    boolean violated = false;

    this.winner = NONE;
    this.time = System.currentTimeMillis();
    String message = "";

    // play this game
    while (world.isGoal() == false) {
      var id = colorIds.get(world.getNextTurn());
      Play play = plays.get(id);
      Player player = play.player;
      World copy = new World(world);
      Move move = Move.PASS;

      lastPlay = play;
      CompletableFuture<Move> future = null;

      if (hasTimeLimit() == false) {
        move = play.think(copy);
        play.stopTimer();
      } else {
        try {
          future = CompletableFuture.supplyAsync(() -> {
            return play.think(copy);
          });
          long timeLeft = this.timeLimit - play.time;
          future.get(timeLeft, TimeUnit.MILLISECONDS);
          move = future.get();
        } catch (TimeoutException e) {
          future.cancel(true);
          violated = true;
          message = String.format("timeout: %s\n", play.player);
          System.err.println(message);
          break;
        } catch (Exception e) {
          future.cancel(true);
          violated = true;
          message = String.format("exception: %s\n", play.player);
          System.err.println(message);
          break;
        } finally {
          play.stopTimer();
        }
      }

      violated = violated | isViolated(world, player, move);
      if (violated) break;

      world = world.perform(move);
      if (this.verbose) printProgress(world, play);

      if (pass && move.isPass()) break;
      pass = move.isPass();

      this.turn += 1;
    }

    // record
    this.time = System.currentTimeMillis() - this.time;
    this.times[BLACK_ID] = plays.get(BLACK_ID).time;
    this.times[WHITE_ID] = plays.get(WHITE_ID).time;

    this.scores = Game.colors.stream()
        .map(color -> this.world.getBoard().getCount(color))
        .collect(Collectors.toList())
        .toArray(new Integer[2]);
    
//    result.set(world, black(), white(), t);

    if (violated)
      this.winner = -lastPlay.player.getColor();
    else
      this.winner = world.getWinner();

    if (violated)
      System.out.println("violated: " + lastPlay.player + " ");

//    return result;
  }

  boolean isViolated(World world, Player player, Move move) {
    var legals = world.getLegalMoves();

    if (legals.isEmpty()) {
      if (move.isPass() == false) {
        System.err.println("illegal move (pass required): " + move);
        return true;
      }
    } else {
      if (legals.indexOf(move) == -1) {
        System.err.println("illegal move: " + move);
        System.err.println(world);
        return true;
      }
    }
    return false;
  }

  public String toString() {
    var timeStr = String.format("(%.3fs, %.3fs, %.3fs)",
        (double) this.times[0] / 1000.0,
        (double) this.times[1] / 1000.0,
        (double) this.time / 1000.0);
    var black = this.players.get(0);
    var white = this.players.get(1);
    var blackScore = this.scores[0];
    var whiteScore = this.scores[1];
    var blackWin = this.winner == BLACK ? "*" : " ";
    var whiteWin = this.winner == WHITE ? "*" : " ";
    var middleStrs = Map.of(NONE, " - ", BLACK, " - ", WHITE, " - ");
    var sign = middleStrs.get(this.winner);
    var middleStr = String.format("%s%2d%s%2d%s", blackWin, blackScore, sign, whiteScore, whiteWin);
    return String.format("%4s %s %-4s %s", black, middleStr, white, timeStr);
  }

  void printProgress(World world, Play play) {
    System.out.printf("%d: %s %s %.3fs\n",
        this.turn, play.player, play.move, (double) play.time / 1000.0);
    System.out.println(world);
    System.out.println();
  }
}

