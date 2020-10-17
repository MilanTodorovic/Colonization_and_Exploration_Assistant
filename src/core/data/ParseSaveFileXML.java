package core.data;

import core.data.CelestialBodies.*;
import core.data.CelestialBodies.CCEnt.CCEntApparatus;
import core.data.CelestialBodies.CCEnt.CCEntFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.stream.Collectors;


// https://docs.oracle.com/cd/B28359_01/appdev.111/b28394/adx_j_parser.htm#ADXDK19137
// http://saxon.sourceforge.net/#F10HE
// https://stackoverflow.com/questions/32280824/fastest-way-to-read-a-large-xml-file-in-java
// https://www.tutorialspoint.com/java_xml/java_sax_query_document.htm

// https://howtodoinjava.com/java/xml/read-xml-dom-parser-example/  OVO JE KORISNO

// TODO make it a separate thread with a loading bar
// TODO parse also the description xml file before this?
// TODO go through every planets.json file and extract all the types and jpegs

// TODO currently adapted only for normal saves, check nexerline and varya's

// TODO future plans:
//  - parse available officers data from save file
//  - parse available admins from save file
//  - parse market information
//  - parse storage information
//  - parse Abandoned stations data
//  - parse faction data (relations etc.)


public final class ParseSaveFileXML {

	// TODO what to do with this?
	private final String starSystems = "<CampaingEngine><starSystems><Sstm ref='XXXXX'>";

    //"<CampaingEngine><uiData><hyperMapCoordinates>72.89575|252.19226</hyperMapCoordinates>";
	private float[] playerLocation = new float[2];
	private final Hashtable<Integer, LocationToken> LocationTokens = new Hashtable<Integer, LocationToken>();
    private final Hashtable<Integer, Plnt> Plnts = new Hashtable<Integer, Plnt>();
    private final Hashtable<Integer, Object> CCEnts = new Hashtable<Integer, Object>();
    private final Hashtable<Integer, DebrisField> DebrisFields = new Hashtable<Integer, DebrisField>();
    private final Hashtable<Integer, JumpPoint> JumpPoints = new Hashtable<Integer, JumpPoint>();
    private final Hashtable<Integer, StarSystem> StarSystems = new Hashtable<Integer, StarSystem>();

	
	private final String playerFleet = "<CampaingEngine><playerFleet ref='XXXXX'>"; //unused for now

	private final Document doc;

	public ParseSaveFileXML(String path) throws ParserConfigurationException, IOException, SAXException {
		super();

		// TODO get also the description file from the same folder
		// 	which factions hate you on the minimap or system data

		// TODO set thread to daemon?
		System.out.println("File path to parse " + path);
        final File fXmlFile = new File(path);
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(fXmlFile);
        //doc.getDocumentElement().normalize(); // TODO check if it is performance intensive for larger files
	}

//	@Override
//	public void start(){
//		// check this in IDE
//
//		this.mergeData();
//	}

    public void parse() {
		System.out.println("Starting parsing....");
		this.handleTheMess();
        this.mergeData();
    }

	private void handleTheMess() {
		System.out.println("Entering handlethemess");
		// In Nexerlin LocationTokens can refer to jumppoints within other tags, check vanilla

		// everything is a lie. we have to search for the tag that contains all the data. doesnt have to be the first tag.
		// search for <economy><stepper><econ><markets><Market>

		Element rootNode = doc.getDocumentElement();
		NodeList childNodes = rootNode.getChildNodes();
		for (int i=0; i < childNodes.getLength(); i++){
			Node node = childNodes.item(i);
			System.out.println("Node " + node);
			if (node.getNodeType() == Node.ELEMENT_NODE){
				System.out.println("Found one " + node);
				Element eNode = (Element) node;
				Element o = (Element) eNode.getElementsByTagName("o").item(0); // <hyperspace><o>
				System.out.println("o tag " + o);
				Element saved = (Element) o.getElementsByTagName("saved").item(0);

				NodeList allChildrenOfSaved = saved.getChildNodes();
				System.out.println(allChildrenOfSaved.toString());

				// parses all child tags: LocationToken, Plnt and so on
				parseEachChildNode(allChildrenOfSaved);

				// Player Location on hyperspace map
				// TODO
//				NodeList uiData = (Element) node.getElementsByTagName("uiData");
//				this.playerLocation = this.parseLocTag(uiData);
				System.out.println("Leaving handlethemess");
				break;
			}
		}


	}
	
	private void parseLocationToken(Element node) {
		// TODO how to leave empty classes for nodes that refer to other nodes?

		LocationToken lk = new LocationToken();

		// TODO we only need one parent variable, ref and z point tot the same location
		//	take care of the case when we have a ref in LocationToken
		//	it points to a subtag <sP> which contains additional info
		if (node.hasAttribute("ref")){
			int ref = Integer.parseInt(node.getAttribute("ref"));
			lk.setzValue(ref);
			System.out.println("Parsing LocationToken ref: " + ref);
			LocationTokens.put(ref, lk);
		} else {
			int zValue = Integer.parseInt(node.getAttribute("z"));
			lk.setzValue(zValue);
			System.out.println("Parsing LocationToken ref: " + zValue);
            LocationTokens.put(zValue, lk);

			// get <loc> tag
			NodeList nList = node.getElementsByTagName("loc");
			float[] location = parseLocTag(nList);
			// walk the tree and find specific children
			// TODO since some LocationTokens can be empty, we should extract <orbit><e> and <f>
			try {
				Element orbit = (Element) node.getElementsByTagName("orbit").item(0);
				Element s = (Element) orbit.getElementsByTagName("s").item(0);
				Element o = (Element) s.getElementsByTagName("o").item(0);
				Element saved = (Element) o.getElementsByTagName("saved").item(0);

				NodeList allChildrenOfSaved = saved.getChildNodes();

				// contains all the elements of a star system: planets, debris fields, entities etc.
				parseEachChildNode(allChildrenOfSaved);
			} catch (Exception e) {
				// no nested information
				System.out.println("parseLocationToken " + e.toString());
			}

		}
	}

	private String parsej0Tag(String content, String pattern) {
	    String result = "";
	    // removes curley braces {}
	    String str = content.substring(1, content.length());
	    String[] str1 = str.split(",");
	    for (String s: str1) {
	        if (s.startsWith(pattern)){
                String[] tmp = s.split(":");
                result = tmp[1].substring(1,tmp[1].length()-1);
                break;
            }
        }
        return result;
    }

	// TODO don't return, but append to a List<Object> to the parent tag
	// 	check if it is doable even with the major nested tags
	private void parsePlnt(Element node) {
		// TODO Multi-star systems?

		// <j0>{"f6":0,"f0":"Pontus","f2":[255,220,190,255],"f4":"pontus"}</j0>
		// <orbit><f> refers to the body it orbits (star or other planet)
		// if it doesnt contain <orbit> then it is a star
		// don't get <loc>, loc refers to the location within the star system
		// take care of <market>: a normal Planet never has <economy><stepper>... under market

		Plnt planet = new Plnt();

        if (node.hasAttribute("ref")){
            int ref = Integer.parseInt(node.getAttribute("ref"));
            planet.setzValue(ref);
            Plnts.put(ref, planet);
			System.out.println("Parsing Planet ref: "+ ref);
        } else {
            int zValue = Integer.parseInt(node.getAttribute("z"));
            planet.setzValue(zValue);
			System.out.println("Parsing Planet z: "+ zValue);

            Element j0 = (Element) node.getElementsByTagName("j0").item(0);
            String json_ = j0.getTextContent();
            String nameOfPlnt = this.parsej0Tag(json_, "\"f0\"");
            // TODO parent id
            planet.setName(nameOfPlnt);

            NodeList orbit = node.getElementsByTagName("obit");
            if (orbit.getLength() > 0) {
                Element orb = (Element) orbit.item(0);
                Element f = (Element) orb.getElementsByTagName("f").item(0);
                // TODO store parent id
                int parent = Integer.parseInt(f.getAttribute("ref"));
            }

            Element tags = (Element) node.getElementsByTagName("tags").item(0); // check if <st>star<st>
            boolean isStar = tags.getFirstChild().getTextContent().equalsIgnoreCase("star");
            planet.setStar(isStar);

			Plnts.put(zValue, planet);

            try {
                Element market = (Element) node.getElementsByTagName("market").item(0);

                Element economy = (Element) market.getElementsByTagName("economy").item(0);

                // stepper > econ > markets can occur multiple times, but they stay empty
                NodeList stepper = economy.getElementsByTagName("stepper");
                this.checkForStepperTag(stepper);
            } catch (Exception e) {
                // doesn't have any nested information
				System.out.println("parsePlnt no nested nodes: " + e.toString());
            }

			System.out.println(planet);
        }
	}
	
	private void parseCCEnt(Element node) {
		System.out.println("Parsing CCEnt");
		// TODO where do I see which items where discovered?
        int zValue = 0;
        int ref = 0;
		int parentBody = 0;
		String nameOfCCEnt = "";
		String typeOfCCEnt = "";
		int parentSystem = 0;
		int parentStar = 0;
		float[] location = new float[2];

		// many more objects are under this tag. scrape anyway and just dont show?
		// scrape nexerlin and other folders for CCEnt data?
        if (node.hasAttribute("ref")){
            zValue = Integer.parseInt(node.getAttribute("ref"));
        } else {
            ref = Integer.parseInt(node.getAttribute("z"));
        }
		System.out.println("CCEnt z:" + zValue + ", ref:" + ref);


		try {
			// <j0>{"f6":211.14685,"f0":"Ancyra Relay","f3":"comm_relay","f2":[255,220,190,255],"f4":"ancyra_relay"}</j0>
			Element j0 = (Element) node.getElementsByTagName("j0").item(0);
			String json_ = j0.getTextContent();
			nameOfCCEnt = this.parsej0Tag(json_, "\"f0\"");
			typeOfCCEnt = this.parsej0Tag(json_, "\"f3\"");

			// if there is no f3 key, then it means that the CCEnt is a Label for a constelation
			if (typeOfCCEnt.isEmpty()) {
				typeOfCCEnt = "label";
				location = parseLocTag(node.getElementsByTagName("loc"));
			} else {
				if (nameOfCCEnt.startsWith("Abandoned")) {
					// TODO covers all types of abandoned stations in vanilla
					typeOfCCEnt = "abandoned_station";
				}

				try {
					Element orbit = (Element) node.getElementsByTagName("orbit").item(0);
					Element fTag = (Element) orbit.getElementsByTagName("").item(0);
					// this ID can refer to either a Plnt tag or CampaignTerrain
					parentBody = Integer.parseInt(fTag.getAttribute("ref"));
				} catch (Exception e) {
					// CCEnt that doesn't have an orbit tag
					System.out.println("parseCCEnt " + e.toString());
				}

				Element cL = (Element) node.getElementsByTagName("cL").item(0);
				parentSystem = Integer.parseInt(cL.getAttribute("ref"));
				Element ls = (Element) node.getElementsByTagName("ls").item(0);
				parentStar = Integer.parseInt(ls.getAttribute("ref"));

				// some makeshifts have a stable location attached under <me><d> as <e>
				// <e> contains <st> and <CCEnt>, parse the CCEnt
				// TODO check it again, might not need it
				//		doesn't work with stations!!!
				try {
					Element meTag = (Element) node.getElementsByTagName("me").item(0);
					Element dTag = (Element) meTag.getElementsByTagName("d").item(0);
					if (dTag.hasChildNodes()) {
						// first child is <e>, last child is <CCEnt>
						Element eTag = (Element) dTag.getElementsByTagName("e").item(0);
						Element CCEntTag = (Element) dTag.getElementsByTagName("CCEnt").item(0);
					}
				} catch (Exception e){
					// isn't a makeshift object
					System.out.println("parseCCEnt - second catch: " + e.toString());
				}
			}
		} catch (Exception e) {
			System.out.println("parseCCEnt - third catch: (empty tag) " + e.toString());
		}

		CCEntFactory factory = new CCEntFactory();
        Object entity = factory.getCCEnt(zValue, parentBody, parentStar, parentSystem, typeOfCCEnt, nameOfCCEnt, location);
        if (entity != null){
			CCEnts.put(zValue, entity);
			System.out.println(entity);
		} else {
			System.out.println("Empty CCEnt object due to empty CCEnt tag");
		}
	}

	private void parseJumpPoint(Element node) {
		System.out.println("Parsing JumpPoint");
        int zValue = 0;
        int parentStar = 0;
		if (node.hasAttribute("ref")){
            zValue = Integer.parseInt(node.getAttribute("ref"));
			System.out.println("Jumppoint " + zValue);
		} else {
			try {
				zValue = Integer.parseInt(node.getAttribute("z"));
				System.out.println("Jumppoint " + zValue);
				// get <loc> tag
				NodeList nList = node.getElementsByTagName("loc");
				float[] location = parseLocTag(nList);
				// maybe also cL tag? might not be needed
				Element lsTag = (Element) node.getElementsByTagName("ls").item(0);
				parentStar = Integer.parseInt(lsTag.getAttribute("ref"));
			} catch (Exception e) {
				System.out.println("Jumppoint exception: " + e);
			}
		}
        JumpPoint jmp = new JumpPoint(zValue, parentStar);
        JumpPoints.put(zValue, jmp);

		System.out.println(jmp.toString());
	}
	
	private void parseCampaignTerrain(Element node) {
		int zValue = 0;
		int ref = 0;
		if (node.hasAttribute("z")){
			zValue = Integer.parseInt(node.getAttribute("z"));
		} else {
			ref = Integer.parseInt(node.getAttribute("ref"));
		}
		System.out.println("Parsing CampaignTerrain z:" + zValue + ", ref: " + ref);
		// we are only interested in the debris_field type
		// other types are nebula and corona
		boolean isDebrisField = node.getAttribute("type").equalsIgnoreCase("debris_field");

		try {
			if (isDebrisField) {
				Element cL = (Element) node.getElementsByTagName("cL").item(0);
				int parentSystem = Integer.parseInt(cL.getAttribute("ref"));
				Element ls = (Element) node.getElementsByTagName("ls").item(0);
				int parentStar = Integer.parseInt(ls.getAttribute("ref"));
				Element orbit = (Element) node.getElementsByTagName("orbit").item(0);
				Element fTag = (Element) orbit.getElementsByTagName("f").item(0);
				int parentBody = Integer.parseInt(fTag.getAttribute("ref"));

				DebrisField df = new DebrisField(zValue, parentStar);
				DebrisFields.put(zValue, df);
			}
		} catch (Exception e) {
			System.out.println("CampaignTerrain - Debris Field - error with parsing:" + e.toString());
		}
	}

	private float[] parseLocTag(NodeList nList){
		float[] location = new float[2];

		if (nList.getLength() > 0){ // 123.4|123.4
			String[] _loc = nList.item(0).getTextContent().split("\\|");
//			System.out.println("Parsed location " + Arrays.toString(_loc));
			for (int i=0; i < _loc.length; i++) {
				location[i] = Float.parseFloat(_loc[i]);
			}
		}
		return location;
	}

	private void parseEachChildNode(NodeList allChildrenOfSaved){
		System.out.println("Parsing eachChildNode");
		System.out.println("Children of saved " + allChildrenOfSaved);
		for (int i=0; i < allChildrenOfSaved.getLength(); i++) {
			Node node = allChildrenOfSaved.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element n = (Element) node;
				String name = n.getNodeName();
				switch (name) {
					case "LocationToken": {
						this.parseLocationToken(n);
						break;
					}
					case "CampaignTerrain": {
						this.parseCampaignTerrain(n);
						break;
					}
					case "Plnt": {
						this.parsePlnt(n);
						break;
					}
					case "JumpPoint": {
						this.parseJumpPoint(n);
						break;
					}
					case "CCEnt": {
						this.parseCCEnt(n);
						break;
					}
					default: {
						// Flt, NGW, RingBand
						System.out.println("parseEachChildNode - Default switch triggered on: " + name);
						break;
					}
				}
			}
		}
	}

	private void checkForStepperTag(NodeList stepper) {
		System.out.println("Checking stepper");
		if (stepper.getLength() > 0) {
			Element step = (Element) stepper.item(0);
			Element econ = (Element) step.getElementsByTagName("econ").item(0);
			Element markets = (Element) econ.getElementsByTagName("markets").item(0);
			NodeList uppercaseMarket = markets.getElementsByTagName("Market");
			System.out.println("Total <Market> found: "+uppercaseMarket.getLength());
			if (uppercaseMarket.getLength() > 0) {
				// parse every <Market>
				for (int i=0; i < uppercaseMarket.getLength(); i++) {
					Element uMarket = (Element) uppercaseMarket.item(i);
					this.parseUppercaseMarket(uMarket);
					System.out.println("Done with parsing <Market> at: " + i);
				}
			}
		}
	}

	private void parseUppercaseMarket(Element uMarket) {
		System.out.println("------------Parsing uppercaseMarket-----------");
		int zValue = 0;
		int ref = 0;
		// only occurs under lowercase <markets>

		// get id, name, factionId
		// primaryEntry can contain other Plnt data
		// primaryEntry > orbit > f.hasChildren()
		// f > cL > o > saved

		if (uMarket.hasChildNodes()) {
			zValue = Integer.parseInt(uMarket.getAttribute("z"));
			System.out.println("Uppercase Market, z:" + zValue + " ref:" + ref);
			Element primaryEntity = (Element) uMarket.getElementsByTagName("primaryEntity").item(0);
			System.out.println("Uppercase Market, primaryEntity:" + primaryEntity);

			// primaryEntity under <Market> is equivalent to Plnt, CCEnt and maybe something else so we need to parse it
			// other primaryEntity tags under parent <Plnt> etc. aren't important for now
			String cl = primaryEntity.getAttribute("cl");
			System.out.println("Uppercase Market, cl tag:" + cl);
			switch (cl) {
				case "CCEnt": {
					System.out.println("Uppercase Market, primaryEntity:" + cl);
					this.parseCCEnt(primaryEntity);
					break;
				}
				case "Plnt": {
					System.out.println("Uppercase Market, primaryEntity:" + cl);
					this.parsePlnt(primaryEntity);
					break;
				}
                case "CampaignTerrain":{
                    // maybe this can't happen
					System.out.println("Uppercase Market, primaryEntity:" + cl);
                    this.parseCampaignTerrain(primaryEntity);
                    break;
                }
				default: {
					System.out.println("Uppercase Market, primaryEntity default:" + cl);
					break;
				}
			}

			Element orbit = (Element) primaryEntity.getElementsByTagName("orbit").item(0);
			Element f = (Element) orbit.getElementsByTagName("f").item(0);
			// <f> contains more nested Plnt and other stuff
			if (f.hasChildNodes()) {
				// Since there are other tags under <f>, we need to parse
				Element cL = (Element) f.getElementsByTagName("cL").item(0);
				Element o = (Element) cL.getElementsByTagName("o").item(0);
				Element saved = (Element) o.getElementsByTagName("saved").item(0);
				NodeList allChildrenOfSaved = saved.getChildNodes();
				parseEachChildNode(allChildrenOfSaved);
			}
		} else {
		    // TODO Currently not used
			ref = Integer.parseInt(uMarket.getAttribute("ref"));
			System.out.println("Uppercase Market, 'No children' z:" + zValue + " ref:" + ref);
		}
	}

	private void parseLowercaseMarket(Element lMarket) {
        // TODO finish parseLowercaseMarket method later on
	}

	private void mergeData(){
		// TODO fill all Hashsets and then merge all missing values
        System.out.println(this.LocationTokens);
        System.out.println(this.Plnts);
        System.out.println(this.CCEnts);
        System.out.println(this.DebrisFields);
        System.out.println(this.JumpPoints);
		System.out.println(Arrays.toString(this.playerLocation));
	}

}
