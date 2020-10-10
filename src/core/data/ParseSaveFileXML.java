package core.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
// TODO go through every planets.json file and extract all the types and jpegs

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
		doc.getDocumentElement().normalize();

		//returns specific attribute
		getAttribute("attributeName").getValue();

		//returns a list of subelements of specified name
		getChildren("subelementName").getText(); 

		//returns a list of all child nodes
		getChildren(); 

		//returns first child node
		getChild("subelementName"); 
		
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
		// In Nexerlin LocationTokens can reffer to jumppoints within other tags, check vanilla

		// everything is a lie. we have to search for the tag that contains all the data. doesnt have to be the first tag.
		// search for <economy><stepper><econ><markets><Market>

		Element rootNode = doc.getDocumentElement();
		Element tagO = (Element) rootNode.getFirstChild().getFirstChild();
		Element saved = (Element) tagO.getElementsByTagName("saved").item(0);

		NodeList allChildrenOfSaved = saved.getChildNodes();

		for (int i=0; i < allChildrenOfSaved.getLength(); i++){
			Element n = (Element) allChildrenOfSaved.item(i);
			String name = n.getNodeName();
			switch(name){
				case "NGW": {
					continue;
				}
				case "LokationToken":{
					this.parseLocationToken(n);
				}
				case "JumpPoint":{
					this.parseJumpPoint(n);
				}
			}
		}


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
		}

		// TODO if the locationtoken carries all the info, performa check

		this.parsePlnt(node);
		this.parseJumpPoint(node);
		this.parseCCEnt(node);
	}
	
	private void parsePlnt(Element node) {
		// if Plnt tag says planet, don't get <loc>
		// take care of <market>
	}
	
	private void parseCCEnt(Element node) {
		// <j0> f0 is the name, getText and then extract f0 as json; f3: type
		// <orbit><f> what planet/star it orbits, get ref
		// <cL> starsystem, <ls> star, get ref
		// <tags> first <st> is the type of CCEnt
		
		// many more objects are under this tag. scrape anyway and just dont show?
		// scrape nexerlin and other folders for CCEnt data?
		
	}

	private void parseJumpPoint(Element node) {
		
	}
	
	private void parseCampaignTerrain(Element node) {
		// can contain debris field
		// get attribute type from tag "debris_field"
		// has <orbit>, <cL>, <ls> tag, take a look at it
	}

    private void storeParsedData () {
        for(;;){
            // TODO store data in GlobalParsedData
        }
    }
}
