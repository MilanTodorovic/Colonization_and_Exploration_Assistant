package core.data.CelestialBodies;

import java.util.Hashtable;

public class StarSystem {
    protected float[] location; // <LocationToken><loc>: -9500.0|-14500.0
    protected Hashtable<Integer,Object> satelites; // stores objects that orbit this object (planets, entities etc.)

    // <LocationToken><orbit><cl>
    protected int zValue; // z=
    protected String name; // dN=

    protected Star star;
    protected Plnt[] plnts;
    protected JumpPoint[] jumppoints;
    protected Object[] ccent;
}
