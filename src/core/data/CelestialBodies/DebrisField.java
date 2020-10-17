package core.data.CelestialBodies;

public class DebrisField {
    protected int parent;
    protected int zValue;

    public DebrisField(int zValue, int parent){
        this.zValue = zValue;
        this.parent = parent;
    }

    @Override
    public String toString(){
        return "Jumppoint at: " + zValue + " " + parent;
    }

}
