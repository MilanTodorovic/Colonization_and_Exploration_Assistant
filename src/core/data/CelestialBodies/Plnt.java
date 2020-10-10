package core.data.CelestialBodies;

import java.util.Hashtable;


public class Plnt { // <LocationToken>

    protected int zValue; // ID of the planet/star <Plnt z="99999">

    protected int orbits; // TODO remove this and just append the Plnt class to it's parent LocationToken

    // <Plnt><market>
    protected String name; // <name>
    protected int surveyed; // <surveyLevel> dosnt exist unless player has visited the planet, clicking on it counts as preliminary. Levels: FULL, PRELIMINARY. Make is 0 none, 1 preliminary, 2 full
    protected String[] conditions; // <cond> foreach <st>
    protected String[] surveyedConds; // <surveyed> foreach <st>

    protected boolean isStar; // <Plnt><tags><st> 'star' or 'planet'

    protected float planetRadius; // <Plnt><radius>

    protected String planetType; // <Plnt><type> !!! 'barren-bombarded', 'lava_minor' !!!

    // <Plnt><diff><j> {"texture":"graphics/planets/barren02.jpg","planetColor":[220,230,255,255]}
    protected String texture; // empty string if not exists
    protected int[] planetColorRGBA;

    protected Hashtable<String,Object> market; // TODO later release


    public Plnt() {

    }

    // TODO toString()
}
