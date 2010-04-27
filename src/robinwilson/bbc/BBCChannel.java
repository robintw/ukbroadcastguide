package robinwilson.bbc;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BBCChannel {
	
	public ArrayList<ScheduleItem> mProgrammes;

	public BBCChannel() {
		// Initialise the ArrayList
		mProgrammes = new ArrayList<ScheduleItem>();
		
	}

	private String getTextNodeContent(Node node) {
		StringBuffer buffer = new StringBuffer();
		
		
		NodeList childList = node.getChildNodes();
		
		for (int i = 0; i < childList.getLength(); i++) {
		    Node child = childList.item(i);
		    if (child.getNodeType() != Node.TEXT_NODE)
		        continue; // skip non-text nodes
		    buffer.append(child.getNodeValue());
		}

		return buffer.toString(); 
	}
	
	public void LoadData()
	{
		//String baseURL = "http://www0.rdthdo.bbc.co.uk/cgi-perl/api/query.pl?method=bbc.schedule.getProgrammes&";
		//String long_url = "http://www0.rdthdo.bbc.co.uk/cgi-perl/api/query.pl?method=bbc.schedule.getProgrammes&channel_id=,BBCRFour&start=2010-04-22T19:08:00Z&end=2010-04-22T23:59:59Z&limit=100&detail=schedule";
		String url = "http://www0.rdthdo.bbc.co.uk/cgi-perl/api/query.pl?method=bbc.schedule.getProgrammes&channel_id=,BBCRFour&limit=100&detail=schedule";
		
		// Parse the XML and catch any errors that may occur
		try {
			parseXML(url);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			System.out.println("ParserConfigException");
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			System.out.println("SAXException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		for(ScheduleItem item : mProgrammes) {
//			System.out.println(item.mTitle);
//		}
		
		
		// Print out some data for testing
		//System.out.println(mProgrammes.get(1).mTitle);
		//System.out.println(mProgrammes.get(1).mSynopsis);
		//System.out.println(mProgrammes.get(1).mStart);
		//System.out.println(mProgrammes.get(1).mDuration);
	}
	
	private void parseXML(String str_url) throws ParserConfigurationException, SAXException, IOException, ParseException
	{
		// Create the document object and get the data via HTTP
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		URL url = new URL(str_url);
		
		InputStream is = url.openStream();
		
		Document doc = db.parse(is);
		
		// Check to see that the server said that data is ok
		if (doc.getDocumentElement().getAttribute("stat").equals("ok") == false)
		{
			System.out.println("ARRRGH!");
			// Response isn't ok - do something here
		}
		
		NodeList programmes = doc.getElementsByTagName("programme");
		
		for (int i = 0; i < programmes.getLength(); i++) {
			Node nProgramme = programmes.item(i);
			
			// Create a new ScheduleItem object to store the data in
			ScheduleItem currentProgramme = new ScheduleItem();
			
			// Get the list of attributes for the programme node
			NamedNodeMap attribs = nProgramme.getAttributes();
			
			// Get the title string
			Node nTitle = attribs.getNamedItem("title");
			currentProgramme.mTitle = nTitle.getNodeValue();
			
			// Get the programme ID string
			Node nID = attribs.getNamedItem("programme_id");
			currentProgramme.mId = nID.getNodeValue();
			
			// Get the children of this programme (other data about it) ready to iterate
			// through it
			NodeList nlChildren = nProgramme.getChildNodes();
			
			for (int j = 0; j < nlChildren.getLength(); j++) {
				Node CurrentNode = nlChildren.item(j);
				
				// If the current node isn't an element node (ie. one with text data)
				// then ignore it
				if (CurrentNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				
				// Assign the data in the node to variables depending on the name of the node
				
				if (CurrentNode.getNodeName().equals("synopsis")) {
					currentProgramme.mSynopsis = getTextNodeContent(CurrentNode);
				} else if (CurrentNode.getNodeName().equals("channel_id")) {
					currentProgramme.mChannelID = getTextNodeContent(CurrentNode);
				} else if (CurrentNode.getNodeName().equals("start")) {
					String time = getTextNodeContent(CurrentNode);
					
					DateFormat in_sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
					
					Date date = in_sdf.parse(time);
					
					currentProgramme.setStart(date);
					
				} else if (CurrentNode.getNodeName().equals("duration")) {
					currentProgramme.mDuration = getTextNodeContent(CurrentNode);
				}

			}
			
			mProgrammes.add(currentProgramme);
		}
	
		
		
	}
	
	
}
