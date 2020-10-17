package core.data.CelestialBodies.CCEnt;

public class Supply_cache extends CCEntApparatus{


    public Supply_cache(String name, int parentSystem, int parentStar){
        this.name = name;
        this.parentSystem = parentSystem;
        this.parentStar = parentStar;
    }

    @Override
    public Object setMakeshift(boolean isMakeshift) {
        return this;
    }

    @Override
    public void setParents(int parentSystem, int parentStar) {
        this.parentSystem = parentSystem;
        this.parentStar = parentStar;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
