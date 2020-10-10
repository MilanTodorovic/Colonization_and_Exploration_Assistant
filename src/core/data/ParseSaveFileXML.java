package core.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

// https://www.tutorialspoint.com/java_xml/java_jdom_parse_document.htm
//import org.jdom2.Attribute;
//import org.jdom2.Document;
//import org.jdom2.Element;
//import org.jdom2.JDOMException;
//import org.jdom2.input.SAXBuilder;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import core.data.GlobalParsedData;
import core.data.CelestialBodies.*;


// https://docs.oracle.com/cd/B28359_01/appdev.111/b28394/adx_j_parser.htm#ADXDK19137
// http://saxon.sourceforge.net/#F10HE
// https://stackoverflow.com/questions/32280824/fastest-way-to-read-a-large-xml-file-in-java
// https://www.tutorialspoint.com/java_xml/java_sax_query_document.htm

// https://howtodoinjava.com/java/xml/read-xml-dom-parser-example/  OVO JE KORISNO

// TODO make it a separate thread with a loading bar
// TODO parse also the description xml file before this?
// TODO go through every planets.json file and extract all the types and jpegs

// TODO currently adapted only for normal saves, check nexerlin and varayas

public final class ParseSaveFileXML extends Thread {

	private final String startingPoint = "<CampaingEngine><hyperspace><o><saved>";
	
	private final String starSystems = "<CampaingEngine><starSystems><Sstm ref='XXXXX'>";
	
	private final String playerLocation = "<CampaingEngine><uiData><hyperMapCoordinates>72.89575|252.19226</hyperMapCoordinates>";
	
	// TODO since this is a thread, pass a reference to the Hashtables and lists from the main thread
	// make a Hashtable<zValue,LocationToken>
	// make a Hashtable<zValue,JumpPoint>
	// make a Hashtable for CCEnts and so on
	
	private final String playerFleet = "<CampaingEngine><playerFleet ref='XXXXX'>"; //unused for now

	private final Document doc;

	public ParseSaveFileXML(String path) throws ParserConfigurationException, IOException, SAXException {
		super();

		// TODO get also the description file from the same folder
		// 	which factions hate you on the minimap or system data

		// TODO set thread to daemon?

		File fXmlFile = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize(); // TODO check if it is performance intensive for larger files
		
	}


	@Override
	public void start(){
		// check this in IDE
		this.parse();
	}


    private void parse() {

		this.handleTheMess();
        this.storeParsedData();
    }


	private void handleTheMess() {
		// In Nexerlin LocationTokens can refer to jumppoints within other tags, check vanilla

		// everything is a lie. we have to search for the tag that contains all the data. doesnt have to be the first tag.
		// search for <economy><stepper><econ><markets><Market>

		Element rootNode = doc.getDocumentElement();
		Element o = (Element) rootNode.getFirstChild().getFirstChild(); // <hyperspace><o>
		Element saved = (Element) o.getElementsByTagName("saved").item(0);

		NodeList allChildrenOfSaved = saved.getChildNodes();

		// parses all child tags: LocationToken, Plnt and so on
		parseEachChildNode(allChildrenOfSaved);


		// after that search for the first tage to contain <primaryEntry><orbit><f> with children
		// step through every relevant tag in <f><cL><o><saved>
		
		// take the FIRST <LocationToken> and extract all the important information that shouldn't be stored like this
		// get _z attribute_ from LocationToken and <loc> value 123.45|-123.45
		// get <s cl="Sstm" z="13" dN="Galatia Star System" bN="Galatia">
		// GOTO <orbit><s><o><saved>
		
		// first <Plnt> is the star/centeral body like in the regular <LocationToken> //
		// get _z attribute_
		// examine <j0>{"f6":0,"f0":"Galatia","f2":[255,220,190,255],"f4":"galatia"}</j0>
		// get <Plnt><radius>600.0</radius> and <Plnt><type>star_yellow</type>
		
		// second <Plnt> //
		// get _z attribute_
		// <j0>{"f6":0,"f0":"Ancyra","f3":"planet_ancyra","f2":[255,220,190,255],"f4":"ancyra"}</j0>
		// if <Plnt><ow> exists, get _z attribute_ and <ow><id>hegemony</id>
		// get <Plnt><orbit> <e> is self, <f> is the body it orbits
		// <cL cl="Sstm" ref="13"></cL> the starsystem it belongs to
		// <ls cl="Plnt" ref="21"></ls> central body of that system
		// if <market> exists, either a colony or fully surveyed
		// GOTO <market><economy><stepper><econ><markets> and scrape every <Market>
		
		// individual <Market> tags //
		// try to get _z attribute_, if it fails, get ref="12345"
		// think about scraping market data in a later release
		// GOTO <primaryEntry>
		// get <j0>{"f6":286.33234,"f0":"Derinkuyu Mining Station","f3":"orbital_station_default","f2":[255,220,190,255],"f4":"derinkuyu_station"}</j0>
		// GOTO <orbot> and check if <f> has CHILDREN
		
		
		list = getChildren("f");
		if (list.size() > 0){
			// if <f> HAS children:
			// <f><cL><o><saved>
			// it's exactly the same as <LocationTokne> from now on
			// get every <Plnt>, <CCEnt> and <JumpPoint>
			// consider saving <Flt> for another release
			// here are also many many <LocationTokens>
			this.parseLocationToken("full path to the <f><cL><o><saved>")
		} else {
			// if <f> has NO children: get all <orbit> tags: <e cl="CCEnt" ref="472"></e> <f cl="Plnt" ref="21"></f>
			// get <cL> and <ls>, try z - catch ref
		}
		
	}
	
	
	private void parseLocationToken(Element node) {
		// TODO how to leave empty classes for nodes that refer to other nodes?
		LocationToken lk = new LocationToken();

		if (node.hasAttribute("ref")){
			long a = Long.parseLong(node.getAttribute("ref"));
		} else {
			long a = Long.parseLong(node.getAttribute("z"));
			// get <loc> tag
			NodeList nList = node.getElementsByTagName("loc");
			float[] location = parseLocTag(nList);
			// walk the tree and find specific children
			Element orbit = (Element) node.getElementsByTagName("orbit").item(0);
			Element s = (Element) orbit.getElementsByTagName("s").item(0);
			Element o = (Element) s.getElementsByTagName("o").item(0);
			Element saved = (Element) o.getElementsByTagName("saved").item(0);

			NodeList allChildrenOfSaved = saved.getChildNodes();

			// contains all the elements of a star system: planets, debris fields, entities etc.
			parseEachChildNode(allChildrenOfSaved);

		}
	}

	// TODO don't return, but append to a List<Object> to the parent tag
	// 	check if it is doable even with the major nested tags
	private Plnt parsePlnt(Element node) {
		// <j0>{"f6":0,"f0":"Pontus","f2":[255,220,190,255],"f4":"pontus"}</j0>
		// <orbit><f> refers to the body it orbits (star or other planet)
		// if it doesnt contain <orbit> then it is a star
		// don't get <loc>, loc refers to the location within the star system
		// take care of <market>: a normal Planet never has <economy><stepper>... under market

		Plnt planet = new Plnt();

		Element j0 = (Element) node.getElementsByTagName("j0").item(0);

		NodeList orbit = node.getElementsByTagName("obit");
		if (orbit.getLength() > 0) {
			Element orb = (Element) orbit.item(0);
			Element f = (Element) orb.getElementsByTagName("f").item(0);
			// TODO store parent id
			int parent = Integer.parseInt(f.getAttribute("ref"));
		}

		Element tags = (Element) node.getElementsByTagName("tags").item(0); // check if <st>star<st>

		Element market = (Element) node.getElementsByTagName("market").item(0);

		Element economy = (Element) market.getElementsByTagName("economy").item(0);

		// stepper econ markets can occur multiple times, but they stay empty
		NodeList stepper = market.getElementsByTagName("stepper");
		if (stepper.getLength() > 0) {
			Element step = (Element) stepper.item(0);
			Element econ = (Element) step.getElementsByTagName("econ").item(0);
			Element markets = (Element) econ.getElementsByTagName("markets").item(0);
			NodeList uppercaseMarket = markets.getElementsByTagName("Market");
			if (uppercaseMarket.getLength() > 0) {
				// TODO parse every <Market>
				for (int i=0; i<uppercaseMarket.getLength(); i++) {
					Element uMarket = (Element) uppercaseMarket.item(i);
					this.parseUppercaseMarket(uMarket);
				}
			}
		}


		return planet;
	}
	
	private CCEnt parseCCEnt(Element node) {
		// <j0> f0 is the name, getText and then extract f0 as json; f3: type
		// <orbit><f> what planet/star it orbits, get ref
		// <cL> starsystem, <ls> star, get ref
		// <tags> first <st> is the type of CCEnt
		
		// many more objects are under this tag. scrape anyway and just dont show?
		// scrape nexerlin and other folders for CCEnt data?
		CCEnt entity = new CCEnt();

		return entity;
	}

	private JumpPoint parseJumpPoint(Element node) {

		JumpPoint jmp = new JumpPoint();

		if (node.hasAttribute("ref")){
			long a = Long.parseLong(node.getAttribute("ref"));
		} else {
			long a = Long.parseLong(node.getAttribute("z"));
			// get <loc> tag
			NodeList nList = node.getElementsByTagName("loc");
			float[] location = parseLocTag(nList);
		}

		return jmp;
	}
	
	private CampaignTerrain parseCampaignTerrain(Element node) {
		// can contain debris field
		// get attribute type from tag "debris_field"
		// has <orbit>, <cL>, <ls> tag, take a look at it

		CampaignTerrain cmp = new CampaignTerrain();

		return cmp;
	}

	private float[] parseLocTag(NodeList nList){
		float[] location = new float[2];

		if (nList.getLength() > 0){ // 123.4|123.4
			String[] _loc = nList.item(0).getTextContent().split("|",1);
			for (int i=0; i < _loc.length; i++) {
				location[i] = Float.parseFloat(_loc[i]);
			}
		}

		return location;
	}

	private void parseEachChildNode(NodeList allChildrenOfSaved){

		for (int i=0; i < allChildrenOfSaved.getLength(); i++){
			Element n = (Element) allChildrenOfSaved.item(i);
			String name = n.getNodeName();
			switch(name){
				case "LocationToken": {
					this.parseLocationToken(n);
					break;
				}
				case "CampaignTerrain": {
					this.parseCampaignTerrain(n);
					break;
				}
				case "Plnt":{
					this.parsePlnt(n);
					break;
				}
				case "JumpPoint":{
					this.parseJumpPoint(n);
					break;
				}
				case "CCEnt":{
					this.parseCCEnt(n);
					break;
				}
				default: {
					// Flt, NGW, RingBand
					break;
				}
			}
		}
	}

	private void parseUppercaseMarket(Element uMarket) {
		// only occurs under lowercase <markets>

		// get id, name, factionId
		// primaryEntry can contain other Plnt data
		// primaryEntry > orbit > f.hasChildren()
		// f > cL > o > saved

		if (uMarket.hasChildNodes()){
			Element primaryEntry = (Element) uMarket.getElementsByTagName("primaryEntry").item(0);
			Element orbit = (Element) primaryEntry.getElementsByTagName("orbit").item(0);
			Element f = (Element) orbit.getElementsByTagName("f").item(0);
			// <f> contains more nested Plnt and other stuff
			if (f.hasChildNodes()) {
				Element cL = (Element) f.getElementsByTagName("cL").item(0);
				Element o = (Element) cL.getElementsByTagName("o").item(0);
				Element saved = (Element) o.getElementsByTagName("saved").item(0);
				NodeList allChildrenOfSaved = saved.getChildNodes();

				parseEachChildNode(allChildrenOfSaved);
			}

		} else {
			int ref = Integer.parseInt(uMarket.getAttribute("ref"));
		}
	}

	private void parseLowercaseMarket(Element lMarket) {

	}

    private void storeParsedData () {
        for(;;){
            // TODO store data in GlobalParsedData
        }
    }
}
