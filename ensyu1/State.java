class State {

    //インスタンス
    State parent;
    Action action;
    World world;
    
    //コンストラクタ（引数:2）
    State(World world) {
        this.parent = null;
        this.world = world;
    }

    //コンストラクタ（引数:3）
    State(State parent, Action action, World nextWorld) {
        this.parent = parent;
        this.action = action;
        this.world = nextWorld;
    }

    public String toString() {
        return this.world.toString();
    }

    boolean isGoal() {
        return this.world.isGoal();
    }

}
