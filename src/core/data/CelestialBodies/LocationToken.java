package core.data.CelestialBodies;

import java.util.List;

public class LocationToken {
    protected int zValue;
    protected int ref;
    protected float[] coordinates;

    protected List<StarSystem> systems; // TODO multiple star systems at one location?
    // each starsystem has a Star class with List<Plnt>

}
