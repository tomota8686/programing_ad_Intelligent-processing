package league;

import othello6.*;

class Play {
  int i;
  Player player;
  long time;
  long t;
  Move move;

  Play(int i, Player player) {
    this.i = i;
    this.player = player;
  }

  Move think(World world) {
    this.t = System.currentTimeMillis();
    this.move = player.think(world);
    return move;
  }

  void stopTimer() {
    this.time += Long.max(1, System.currentTimeMillis() - this.t);
  }
}

class Record {
  int color;
  int n;
  int win;
  int lose;
  long time;

  Record(int color) {
    this.color = color;
  }
}

class LeagueRecord {
  Record[][] records;
}