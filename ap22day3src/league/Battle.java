package league;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import othello6.*;

import static league.Game.*;
import static othello6.World.*;

public class Battle {
  List<Class<? extends Player>> classes = List.of();
  List<Record> records = List.of();

  long timeLimit = -1;
  TimeUnit timeUnit;

  String[] names = {"", ""};
  int[] wins = {0, 0};
  int[] loses = {0, 0};
  int draws = 0;
  int[] scores = {0, 0};
  long[] times = {0, 0};

  public Battle(
      List<Class<? extends Player>> classes,
      long timeLimit,
      TimeUnit timeUnit)
  {
    this.classes = classes;
    this.records = List.of(new Record(BLACK), new Record(WHITE));
    this.timeLimit = timeLimit;
    this.timeUnit = timeUnit;
    var black = newPlayer(this.classes.get(BLACK_ID), BLACK);
    var white = newPlayer(this.classes.get(WHITE_ID), WHITE);
    this.names[BLACK_ID] = black.toString();
    this.names[WHITE_ID] = white.toString();
  }

  public Battle(List<Class<? extends Player>> classes) {
    this(classes, -1, TimeUnit.SECONDS);
  }

  Player newPlayer(Class<? extends Player> playerClass, int color) {
    try {
      Constructor<? extends Player> constructor = playerClass.getConstructor(int.class);
      return constructor.newInstance(color);
    } catch (Exception e) {
      System.err.println(e);
      return null;
    }
  }

  public void play(int n) {
    for (int i = 0; i < n; i++) {
      var black = newPlayer(this.classes.get(BLACK_ID), BLACK);
      var white = newPlayer(this.classes.get(WHITE_ID), WHITE);

      var players = List.of(black, white);
      var game = new Game(players, this.timeLimit, this.timeUnit);
      game.play();
      record(game);
      System.out.println(game);
    }
  }

  void record(Game game) {
    if (game.winner == BLACK) {
      this.wins[0] += 1;
      this.loses[1] += 1;
    } else if (game.winner == WHITE) {
      this.wins[1] += 1;
      this.loses[0] += 1;
    } else {
      this.draws += 1;
    }

    for (int j = 0; j < 2; j++) {
      this.scores[j] += game.scores[j];
      this.times[j] += game.times[j];
    }
  }

  public String toString() {
    return String.format("%4s %2d  %2d  %2d %-4s [%2d, %2d]",
        this.names[BLACK_ID], this.wins[BLACK_ID],
        this.draws,
        this.wins[WHITE_ID], this.names[WHITE_ID],
        this.scores[BLACK_ID], this.scores[WHITE_ID]);
  }
}
