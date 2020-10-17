package core.data.CelestialBodies.CCEnt;

// TODO scrape with Python all possible CCEnt tags
public class CCEntFactory {

    public Object getCCEnt(int zValue, int parentBody, int parentStar, int parentSystem,
                           String typeOfCCEnt, String nameOfCCEnt, float[] location){
        // TODO complete all the constructors
        System.out.println("Generating CCEnt object - CCEntFactory");
        Object obj;
        switch(typeOfCCEnt) {
            case "comm_relay":{
                obj = new Comm_relay();
                break;
            }
            case "comm_relay_makeshift":{
                obj = new Comm_relay().setMakeshift(true);
                break;
            }
            case "sensor_array":{
                obj = new Sensor_array();
                break;
            }
            case "sensor_array_makeshift":{
                obj = new Sensor_array().setMakeshift(true);
                break;
            }
            case "nav_buoy":{
                obj = new Nav_buoy();
                break;
            }
            case "nav_buoy_makeshift":{
                obj = new Nav_buoy().setMakeshift(true);
                break;
            }
            case "stable_location":{
                obj = new Stable_location();
                break;
            }
            case "cargo_pods":{
                obj = new Cargo_pods(nameOfCCEnt, parentSystem, parentStar);
                break;
            }
            case "supply_cache":{
                obj = new Supply_cache(nameOfCCEnt, parentSystem, parentStar);
                break;
            }
            case "label":{
                // TODO
                obj = new Label(nameOfCCEnt, location);
                break;
            }
            case "weapon_cache":{
                obj = new Weapon_cache(nameOfCCEnt, parentSystem, parentStar);
                break;
            }
            case "derelict_ship":{
                obj = new Derelict_ship(nameOfCCEnt, parentSystem, parentStar);
                break;
            }
            case "inactive_gate":{
                // TODO Terraforming mod allows constructing Astral gates
                //  either add the support now or later on
                obj = new Inactive_gate(nameOfCCEnt, parentSystem, parentStar);
                break;
            }
            // TODO check if there are any other types of stations
            case "orbital_station_default":{
                obj = new Orbital_station(nameOfCCEnt, parentSystem, parentStar);
                break;
            }
            case "abandoned_station":{
                obj = new Abandoned_station(nameOfCCEnt, parentSystem, parentStar);
                break;
            }
            default:{
                // TODO do we need this?
                obj = null;
                break;
            }
        }
        if (obj != null) {
            System.out.println("Returning object" + obj.toString());
        } else {
            System.out.println("Returning NULL object");
        }
        return obj;
    }
}
