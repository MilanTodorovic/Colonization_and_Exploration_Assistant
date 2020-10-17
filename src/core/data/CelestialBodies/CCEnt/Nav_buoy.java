package core.data.CelestialBodies.CCEnt;

public class Nav_buoy extends CCEntApparatus{

    public Nav_buoy(){
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
