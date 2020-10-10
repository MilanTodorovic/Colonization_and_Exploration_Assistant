package core.data.CelestialBodies;

public class StarSystem {
    private int[] location; // <LocationToken><loc>: -9500.0|-14500.0

    // <LocationToken><orbit><cl>
    private long zValue; // z=
    private String name; // dN=

    Star star;
    Planet[] planets;
    JumpPoint[] jumppoints;
}
