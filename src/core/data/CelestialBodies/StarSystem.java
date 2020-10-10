package core.data.CelestialBodies;

public class StarSystem {
    protected float[] location; // <LocationToken><loc>: -9500.0|-14500.0

    // <LocationToken><orbit><cl>
    protected int zValue; // z=
    protected String name; // dN=

    protected Star star;
    protected Plnt[] plnts;
    JumpPoint[] jumppoints;
}
