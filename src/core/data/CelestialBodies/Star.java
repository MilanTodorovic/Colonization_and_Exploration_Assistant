package core.data.CelestialBodies;

import java.util.Hashtable;

public class Star extends Plnt {

    protected Hashtable<Integer,Object> satelites; // stores objects that orbit this object (planets, entities etc.)

    public Star() {

    }
}
