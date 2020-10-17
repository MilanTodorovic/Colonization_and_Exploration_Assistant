package core.data.CelestialBodies.CCEnt;

public class Comm_relay extends CCEntApparatus{

    public Comm_relay(){
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
        this.name = name;
    }
}
