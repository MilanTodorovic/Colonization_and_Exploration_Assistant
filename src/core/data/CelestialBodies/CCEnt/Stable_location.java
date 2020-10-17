package core.data.CelestialBodies.CCEnt;

public class Stable_location extends CCEntApparatus {

    public Stable_location(){
        super();
    }


    @Override
    public Object setMakeshift(boolean isMakeshift) {
        return this;
    }

    @Override
    public void setParents(int parentSystem, int parentStar) {
        this.parentStar = parentStar;
        this.parentSystem = parentSystem;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
