package core.data.CelestialBodies;

import java.util.Hashtable;


public class Plnt { // <LocationToken>

    protected int zValue; // ID of the planet/star <Plnt z="99999">
    protected int parentZValue;
    protected Hashtable<Integer,Object> satelites; // stores objects that orbit this object (planets, entities etc.)

    // <Plnt><market>
    protected String name; // <name>
    protected short surveyed; // <surveyLevel> doesn't exist unless player has visited the planet, clicking on it counts as preliminary. Levels: FULL, PRELIMINARY. Make is 0 none, 1 preliminary, 2 full
    protected String[] conditions; // <cond> foreach <st>
    protected String[] surveyedConds; // <surveyed> foreach <st>

    protected boolean isStar; // <Plnt><tags><st> 'star' or 'planet'

    protected float planetRadius; // <Plnt><radius>

    protected String planetType; // <Plnt><type> !!! 'barren-bombarded', 'lava_minor' !!!

    // <Plnt><diff><j> {"texture":"graphics/planets/barren02.jpg","planetColor":[220,230,255,255]}
    protected String texture; // empty string if not exists
    protected short[] planetColorRGBA;

    protected Hashtable<String,Object> market; // TODO later release


    public Plnt() {

    }

    public int getzValue() {
        return zValue;
    }

    public void setzValue(int zValue) {
        this.zValue = zValue;
    }

    public int getParentZValue() {
        return parentZValue;
    }

    public void setParentZValue(int parentZValue) {
        this.parentZValue = parentZValue;
    }

    public Hashtable<Integer, Object> getSatelites() {
        return satelites;
    }

    public void setSatelites(Hashtable<Integer, Object> satelites) {
        this.satelites = satelites;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public boolean isStar() {
        return isStar;
    }

    public void setStar(boolean star) {
        isStar = star;
    }

    @Override
    public String toString(){
        // TODO make a toString
        return "Plnt " + zValue+ " " + name + " "+ isStar;
    }
}
