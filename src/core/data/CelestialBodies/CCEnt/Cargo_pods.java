package core.data.CelestialBodies.CCEnt;

public class Cargo_pods extends CCEntApparatus{

    // TODO maybe scrape the contents of the pods as well?
    public Cargo_pods(String name, int parentSystem, int parentStar){
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
