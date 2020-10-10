package core.data.CelestialBodies;

import java.util.HashMap;

public class Planet { // <LocationToken>

    private long zValue; // ID of the planet/star <Plnt z="99999">

    private String orbits; // TODO how to store data about what the planet orbits?

    // <Plnt><market>
    private String name; // <name>
    private int surveyed; // <surveyLevel> dosnt exist unless player has visited the planet, clicking on it counts as preliminary. Levels: FULL, PRELIMINARY. Make is 0 none, 1 preliminary, 2 full
    private String[] conditions; // <cond> foreach <st>
    private String[] surveyedConds; // <surveyed> foreach <st>

    private boolean isStar; // <Plnt><tags><st> 'star' or 'planet'

    private float planetRadius; // <Plnt><radius>

    private String planetType; // <Plnt><type> !!! 'barren-bombarded', 'lava_minor' !!!

    // <Plnt><diff><j> {"texture":"graphics/planets/barren02.jpg","planetColor":[220,230,255,255]}
    private String texture; // empty string if not exists
    private int[] planetColorRGBA;


    public Planet() {

    }

    // TODO toString()
}
