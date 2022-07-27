import java.util.*;

class World implements Cloneable {
    int missionary = 3;
    int cannibal = 3;
    int boat = 1;

    public String toString() {
        return String.format("(%d,%d,%d)", this.missionary, this.cannibal, this.boat);
    }

    public boolean equals(Object otherObj) {
        if (otherObj instanceof World) {
            World other = (World)otherObj;
            return this.missionary == other.missionary &&
                this.cannibal == other.cannibal &&
                this.boat == other.boat;
        }
        return false;
    }

    public World clone() {
        World another = new World();
        another.missionary = this.missionary;
        another.cannibal = this.cannibal;
        another.boat = this.boat;
        return another;
    }

    boolean isGoal() {
        return this.missionary == 0 && this.cannibal == 0;
    }

    List<Action> getAllActions() {
        return new ArrayList<>(Action.allActions);
    }

    World perform(Action action) {
        World newWorld = this.clone();
        newWorld.missionary += action.missionary;
        newWorld.cannibal += action.cannibal;
        newWorld.boat += action.boat;
        return newWorld;
    }

    boolean isValid() {
        if (this.missionary < 0 || this.missionary > 3) {
            return false;
        }
        if (this.cannibal < 0 || this.cannibal > 3) {
            return false;
        }
        if (this.boat < 0 || this.boat > 1) {
            return false;
        }
        if (this.missionary > 0 && this.missionary < this.cannibal) {
            return false;
        }
        if ((3 - this.missionary) > 0 && (3 - this.missionary) < (3 - this.cannibal)) {
            return false;
        }
        return true;
    }
}

class Action {
    int missionary;
    int cannibal;
    int boat;

    Action(int missionary, int cannibal, int boat) {
        this.missionary = missionary;
        this.cannibal = cannibal;
        this.boat = boat;
    }

    static List<Action> allActions = Arrays.asList(
            new Action(-2, 0, -1), new Action(-1, -1, -1), new Action(0, -2, -1),
            new Action(-1, 0, -1), new Action(0, -1, -1), new Action(+2, 0, +1),
            new Action(+1, +1, +1), new Action(0, +2, +1), new Action(+1, 0, +1),
            new Action(0, +1, +1));
}
