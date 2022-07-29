package league;

import static league.Game.*;

import java.util.*;

class Standing {
  int n;
  public Battle[][] table;
  ArrayList<Rank> ranks;

  Standing(int n) {
    this.n = n;
    this.table = new Battle[this.n][this.n];
  }

  public String toString() {
    var nl = System.lineSeparator();
    var buf = new StringBuilder();
    var lines = winLoss(this.ranks);
    buf.append("***** RANKING *****" + nl);
    buf.append("   ||name| win|loss|draw|| ratio| diff |  time ||");
    buf.append(lines[0] + nl);
    buf.append("---++----+----+----+----++------+------+-------++");
    buf.append(lines[1] + nl);

    for (int i = 0; i < this.n; i++) {
      var rank = this.ranks.get(i);
      var m = rank.win + rank.lose + rank.draw;
      float rate = 100.0f * rank.win / m;
      var t = rank.time / m / 1000.0f;
      var s = String.format("%3d||%4s|%4d|%4d|%4d||%5.1f%%|%6d|%6.3fs||",
          i + 1, rank.name, rank.win, rank.lose, rank.draw,
          rate, rank.gain - rank.loss, t);
      buf.append(s);
      buf.append(lines[i+2]);
      buf.append(nl);
    }

    return buf.toString();
  }

  String[] winLoss(List<Rank> ranks) {
    var n = ranks.size();
    var lines = new String[n + 2];
    lines[0] = "";
    lines[1] = "";
    var standings = new String[n][n];

    // make a ranked table
    var rankedTable = new Battle[this.n][this.n];;
    for (int k = 0; k < n; k++) {
      var i = this.ranks.get(k).i;
      for (int l = 0; l < n; l++) {
        var j = this.ranks.get(l).i;
        rankedTable[k][l] = this.table[i][j];
      }
    }

    // determine the column width and fill a standings
    for (int j = 0; j < n; j++) {
      int[] ws = new int[4];

      // make a column format
      for (var i = 0; i < n; i++) {
        var d = rankedTable[i][j];
        if (d == null) continue;
        int[] vs = {d.wins[0], d.wins[1], d.draws, d.scores[0] - d.scores[1]};
        for (int k = 0; k < 4; k++)
          ws[k] = Math.max(ws[k], String.format("%d", vs[k]).length());
      }

      var format = String.format("%%%dd-%%%dd %%%dd |%%%dd", ws[0], ws[1], ws[2], ws[3]);
      var len = String.format(format, 0, 0, 0, 0).length();

      // make cell strings
      for (var i = 0; i < n; i++) {
        var buf = new StringBuilder();
        var d = rankedTable[i][j];
        if (d != null) {
          int v1 = d.wins[0], v2 = d.wins[1], v3 = d.draws, v4 = d.scores[0] - d.scores[1];
          buf.append(String.format(format, v1, v2, v3, v4));
        } else {
          for (int a = 0; a < len; a++) buf.append(" ");
        }
        buf.append("|");
        standings[i][j] = buf.toString();
      }

      // name
      var rank = ranks.get(j);
      var f1 = String.format("%%%ds|", len);
      var f2 = String.format("%%%ds%%4s%%%ds", (len - 4) / 2, (len - 4) /2);
      lines[0] += String.format(f1, String.format(f2, "", rank.name, ""));

      // line
      for (int a = 0; a < len; a++) lines[1] += "-";
      lines[1] += "+";
    }

    // make lines fron standings
    for (int i = 0; i < n; i++) {
      lines[i+2] = "";
      for (int j = 0; j < n; j++) {
        lines[i+2] += standings[i][j];
      }
    }

    return lines;
  }

  public ArrayList<Rank> rank() {
    var ranks = new ArrayList<Rank>(this.n);
    for (int i = 0; i < this.n; i++) {
      var rank = new Rank();
      rank.i = i;
      ranks.add(rank);
    }

    for (int i = 0; i < this.n; i++) {
      var rank = ranks.get(i);
      for (int j = 0; j < this.n; j++) {
        if (i == j) continue;
        rank.add(this.table[i][j], BLACK_ID);
      }
      for (int j = 0; j < this.n; j++) {
        if (i == j) continue;
        rank.add(this.table[j][i], WHITE_ID);
      }
    }

    Collections.sort(ranks, (a, b) -> b.score() - a.score());
    this.ranks = ranks;

    return ranks;
  }
}


class Rank {
  int i;
  String name;
  int win;
  int lose;
  int draw;
  int gain;
  int loss;
  long time;

  void add(Battle result, int index) {
    if (this.name == null) this.name = result.names[index];
    var opponent = (index + 1) % 2;
    this.win += result.wins[index];
    this.lose += result.loses[index];
    this.draw += result.draws;
    this.gain += result.scores[index];
    this.loss += result.scores[opponent];
    this.time += result.times[index];
  }

  int score() {
    var k = 100 * (this.win + this.lose + this.draw);
    return k * (this.win - this.lose) + (this.gain - this.loss);
  }
}
