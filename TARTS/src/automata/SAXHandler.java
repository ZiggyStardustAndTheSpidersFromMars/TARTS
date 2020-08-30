package automata;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
 * SAXHandler for parsing a timed automaton by an XML File. 
 * @author Torben Friedrich Goerner
 */
public class SAXHandler extends DefaultHandler {
	
	/**
	 * automaton object to build by xml
	 */
	private Automaton automaton;
	
	/**
	 * temporary template to add in automaton
	 */
	private Template tempTemplate;
	/**
	 * temporary location to add in automaton
	 */
	private Location tempLocation;
	/**
	 * temporary transition to add in automaton
	 */
	private Transition tempTransition;
	/**
	 * temporary label to add in automaton
	 */
	private Label tempLabel;
	/**
	 * temporary nail to add to transition 
	 */
	private Nail tempNail;
	/**
	 * temporary query for verification 
	 */
	private Query tempQuery;
	
	/**
	 * name/type of the actual temporary object
	 */
	private String tempObjectName;
	/**
	 * temporary context String
	 */
	private String tempContentText;
	
	
	public SAXHandler() {
		this.automaton = new Automaton();
		this.tempTemplate = null;
		this.tempLocation = null;
		this.tempTransition = null;
		this.tempLabel = null;
		this.tempNail = null;
		this.tempQuery = null;
		this.tempObjectName = "";
		this.tempContentText = "";
	}
	
	@Override
    public void startDocument() throws SAXException {
        automaton = new Automaton();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    	
		switch (qName) {
		
		case "template":
			this.tempTemplate = new Template();
			break;

		case "location":
			this.tempLocation = new Location();
			this.tempLocation.setId(attributes.getValue(0));
			this.tempLocation.setPosX(Integer.parseInt(attributes.getValue(1)));
			this.tempLocation.setPosY(Integer.parseInt(attributes.getValue(2)));
			this.tempLocation.setColor(attributes.getValue(3));
			break;

		case "transition":
			this.tempTransition = new Transition();
			break;

		case "label":
			this.tempLabel = new Label();
			this.tempLabel.setKind(attributes.getValue(0));
			this.tempLabel.setPosX(Integer.parseInt(attributes.getValue(1)));
			this.tempLabel.setPosY(Integer.parseInt(attributes.getValue(2)));
			break;
			
		case "source" :
			this.tempTransition.setSource(attributes.getValue(0));
			break;
			
		case "target" :
			this.tempTransition.setTarget(attributes.getValue(0));
			break;
			
		case "init":
			tempTemplate.getLocationById(attributes.getValue(0)).setInitial(true);
			break;
			
		case "urgent" : 
			this.tempLocation.setUrgent(true);
			break;
			
		case "committed" :
			this.tempLocation.setCommitted(true);
			break;
			
		case "nail" :
			this.tempNail = new Nail();
			this.tempNail.setPosX(Integer.parseInt(attributes.getValue(0)));
			this.tempNail.setPosY(Integer.parseInt(attributes.getValue(1)));
			break;
			
		case "name" :
			if(this.tempLocation != null) {
				this.tempLocation.setPosXName(Integer.parseInt(attributes.getValue(0)));
				this.tempLocation.setPosYName(Integer.parseInt(attributes.getValue(1)));
			}
			break;
		
		case "query" :
			this.tempQuery = new Query();
			break;

		default:
			break;
		}
    	
    	tempObjectName = qName;
    	tempContentText = "";
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

		switch (qName) {
		
		case "template":
			this.automaton.addTemplate(tempTemplate);
			this.tempTemplate = null;
			break;

		case "location":
			this.tempTemplate.addLocation(tempLocation);
			this.tempLocation = null;
			break;

		case "transition":
			this.tempTemplate.addTransition(tempTransition);
			this.tempTransition = null;
			break;

		case "label":
			if(this.tempLocation == null) {
				this.tempTransition.addLabel(tempLabel);
			}else {
				this.tempLocation.setLabel(tempLabel);
			}
			
			this.tempLabel = null;
			break;
			
		case "nail" :
			if(this.tempTransition != null) this.tempTransition.setNail(tempNail);
			this.tempNail = null;
			break;
			
		case "query" :
			this.automaton.addQuery(tempQuery);
			this.tempQuery = null;
			break;

		default:
			break;
		}
		
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

    	tempContentText += new String(ch, start, length).replaceAll("\t", "");
    	
    	switch(tempObjectName) {
    	
    	case "declaration" :
    		if(this.tempTemplate == null) {
    			this.automaton.setDeclaration(tempContentText);
    		}else {
    			this.tempTemplate.setDeclaration(tempContentText);
    		}
    		break;
    		
		case "name":
			if(this.tempLocation == null) {
				if(this.tempTemplate.getName().equals("")) {
					this.tempTemplate.setName(tempContentText.replaceAll("\n", ""));
				}
			}else {
				this.tempLocation.setName(tempContentText.replaceAll("\n", ""));
			}
			break;
		
		case "label" :
			if(this.tempLabel != null) {
				this.tempLabel.setContent(tempContentText);
			}
			break;
			
		case "system" :
			this.automaton.setSystemDeclaration(tempContentText);
			break;
			
		case "formula" :
			if(this.tempQuery != null) {
				this.tempQuery.setFormula(tempContentText);
			}
			break;
			
		case "comment" :
			if(this.tempQuery != null) {
				this.tempQuery.setComment(tempContentText);
			}
			break;
    		
    	default : 
    		break;
    	}
    	
    }

	public Automaton getAutomaton() {return this.automaton;}

}
