package core.data.CelestialBodies.CCEnt;

public class Sensor_array extends CCEntApparatus {

    public Sensor_array(){
        super();
    }

    @Override
    public Object setMakeshift(boolean isMakeshift) {
        this.isMakeshift = isMakeshift;
        return this;
    }

    @Override
    public void setParents(int parentSystem, int parentStar) {
        this.parentStar = parentStar;
        this.parentSystem = parentSystem;
    }

    @Override
    public void setName(String name) {

    }
}
