import java.util.*;

class Solver {
    void solve(State root) {
        State goal = this.search(root);
        if (goal != null) {
            this.printSolution(goal);
        }
    }

    State search(State root) {
        ArrayList<State> openList = new ArrayList<State>();
        openList.add(root);

        while (openList.size() > 0) {
            State state = this.get(openList);
            if (state.isGoal()) {
                return state;
            }
            ArrayList<State> children = this.expand(state);
            openList = this.concat(openList, children);
        }

        return null;
    }

    State get(ArrayList<State> list) {
        return list.remove(0);
    }

    ArrayList<State> expand(State state) {
        ArrayList<State> children = new ArrayList<State>();
        for (Action action: state.world.getAllActions()) {
            World newWorld = state.world.perform(action);
            if (newWorld.isValid()) {
                State newState = new State(state, action, newWorld);
                children.add(newState);
            }
        }
        return children;
    }

    ArrayList<State> concat(ArrayList<State> xs, ArrayList<State> ys) {
        ArrayList<State> list = new ArrayList<State>(xs);
        list.addAll(ys);
        return list;
    }
    
    void printSolution(State goal) {
        while (goal != null) {
            System.out.print(goal);
            System.out.print(" <- ");
            goal = goal.parent;
        }
        System.out.println();
    }
}
