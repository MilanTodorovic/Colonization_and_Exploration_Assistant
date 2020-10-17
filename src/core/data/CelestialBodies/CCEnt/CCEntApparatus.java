package core.data.CelestialBodies.CCEnt;

// Coom relay, Nav buoy and Sensory array
public abstract class CCEntApparatus {
    protected boolean isMakeshift;
    protected int parentStar; // <ls>
    protected int parentSystem; // <cL>

    protected String name; // <j0>

    /**
     * When used on non-makeshift type objects, it returns without any modifications
     * @param isMakeshift
     * @return instance
     */
    public abstract Object setMakeshift(boolean isMakeshift);
    public abstract void setParents(int parentSystem,int parentStar);
    public abstract void setName(String name);

}
