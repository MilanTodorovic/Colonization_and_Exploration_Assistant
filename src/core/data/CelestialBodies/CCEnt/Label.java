package core.data.CelestialBodies.CCEnt;

public class Label {

    protected float[] location;
    protected String name;

    public Label(String name, float[] location){
        this.name = name;
        this.location = location;
    }

    public float[] getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

}
