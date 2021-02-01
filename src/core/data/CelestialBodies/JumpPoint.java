package core.data.CelestialBodies;

import java.util.Hashtable;

public class JumpPoint {

    protected int zValue; // ID of the planet/star <Plnt z="99999">
    protected int parentZValue;
    protected float[] location;
    protected Hashtable<Integer,Object> satelites; // stores objects that orbit this object (planets, entities etc.)

    protected String name; // <name>

    public JumpPoint(int zValue, int parentZValue, float[] location){
        this.zValue = zValue;
        this.parentZValue = parentZValue;
        this.location = location;
    }

    @Override
    public String toString(){
        return "JumpPoint " + zValue + " " + name;
    }

}
