package league;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import othello6.*;

import static league.Game.*;

public class League {
  List<Class<? extends Player>> playerClasses;
  List<Player> players = new ArrayList<Player>();
  List<Battle> games = new ArrayList<Battle>();

  public League(List<Class<? extends Player>> playerClasses) throws Exception {
    this.playerClasses = playerClasses;
    for (var playerClass: this.playerClasses) {
      Constructor<? extends Player> constructor = playerClass.getConstructor(int.class);
      var player = constructor.newInstance(BLACK_ID);
      this.players.add(player);
    }
  }

  public void perform(int count, long timeLimit, TimeUnit timeUnit) {
    // setup
    Standing standing = new Standing(this.playerClasses.size());
    for (int i = 0; i < this.playerClasses.size(); i++) {
      for (int j = 0; j < this.playerClasses.size(); j++) {
        if (i == j) continue;
        var classes = List.of(this.playerClasses.get(i), this.playerClasses.get(j));
        var repetition = new Battle(classes);
        standing.table[i][j] = repetition;
        this.games.add(repetition);
      }
    }

    // round robin
    System.out.printf("-- start --\n");
    System.out.println(this.players);
    System.out.println();

    var shuffled = new ArrayList<Battle>(this.games);
    Collections.shuffle(shuffled);
    executeAsync(shuffled, count);

    System.out.printf("-- done --\n");

    standing.rank();
    System.out.println(standing);
  }

  void execute(ArrayList<Battle> battles, int count) {
    int i = 1;
    for (var battle: battles) {
      System.out.printf("-- %d / %d -- %s vs %s\n", i++, battles.size(),
          battle.names[0], battle.names[1]);
      battle.play(count);
      System.out.println("--------------------------------------");
      System.out.println(battle);
      System.out.println("======================================");
      System.out.println();
    }
  }

  void executeAsync(ArrayList<Battle> battles, int count) {
    var futures = new ArrayList<CompletableFuture<Battle>>();
    battles.forEach(battle -> {
      CompletableFuture<Battle> future = CompletableFuture.supplyAsync(() -> {
        battle.play(count);
        return battle;
      });
      futures.add(future);
    });
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

    futures.forEach(future -> {
      try {
        // nothing to do
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
