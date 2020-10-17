package core.data.CelestialBodies;

import java.util.List;

public class LocationToken {

    protected int zValue;
    protected float[] coordinates;

    protected List<StarSystem> systems; // TODO multiple star systems at one location?
    // each starsystem has a Star class with List<Plnt>

    public int getzValue() {
        return zValue;
    }

    public void setzValue(int zValue) {
        this.zValue = zValue;
    }

    public float[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(float[] coordinates) {
        this.coordinates = coordinates;
    }

    public List<StarSystem> getSystems() {
        return systems;
    }

    public void setSystems(List<StarSystem> systems) {
        this.systems = systems;
    }

    @Override
    public String toString(){
        return "LocationToken " + zValue;
    }

}
