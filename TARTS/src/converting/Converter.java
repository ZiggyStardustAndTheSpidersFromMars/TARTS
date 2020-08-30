package converting;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import automata.Automaton;
import automata.Label;
import automata.Location;
import automata.Template;
import automata.Transition;
import main.StringTemplateHandler;
/**
 * Converter to transform a timed automaton to a specified scheme. 
 * @author Torben Friedrich Goerner
 */
public class Converter {
	
	/**
	 * automaton for converting
	 */
	private static Automaton automatonToConvert;
	/**
	 * flag for xml has cycle template already
	 */
	private static boolean hasCycle;
	/**
	 * flag for xml has cycleTime already
	 */
	private static boolean hasCycleTime;
	/**
	 * flag for xml has cycle synchronization already
	 */
	private static boolean hasCycleSync;
	/**
	 * flag for xml has clock x already
	 */
	private static boolean hasClockX;
	/**
	 * list of all digital inputs used in automaton
	 */
	private static List<String> inputsDigital;
	/**
	 * list of all analogue inputs used in automaton
	 */
	private static List<String> inputsAnalogue;
	/**
	 * list of all existing input templates
	 */
	private static List<String> existingInputTemplates;
	
	
	/**
	 * converts a automaton to the needed special scheme for translation
	 * @param path to automaton file
	 */
	public static void convert(String path) {
		
		//-----------set initial values------------
		hasCycle = false;
		hasCycleTime = false;
		hasCycleSync = false;
		hasClockX = false;
		inputsDigital = new LinkedList<>();
		inputsAnalogue = new LinkedList<>();
		existingInputTemplates = new LinkedList<>();
		//------------------------------------------
		
		automatonToConvert = Automaton.fromXML(path); // generate automaton as java object
		checkAutomaton(automatonToConvert); // check the automaton
		
		String newautomaton = transformAutomaton(automatonToConvert); // transform / convert the automaton to the specified scheme
		
		createXML(newautomaton, path); // generate the new automaton as new XML file
	}
	
	/**
	 * checks a automaton for existing and missing parts
	 * @param automaton to check
	 */
	private static void checkAutomaton(Automaton automaton) {
		boolean inInputArea = false;
		String lines[] = automaton.getDeclaration().split("\n"); //global declarations
		for(String line : lines) {
			
			if(line.contains("const int cycleTime")) hasCycleTime = true;
			if(line.contains("broadcast chan cycle;")) hasCycleSync = true;
			if(line.contains("clock x")) hasClockX = true;
			
			if(inInputArea) {
				String tokens[] = line.split(" |=");
				for(String token : tokens) {
					if(Arrays.asList(StringTemplateHandler.getPins()).contains(token.toUpperCase())) {
						if(tokens[0].equals("int")) {
							inputsAnalogue.add(token);
						}else if(tokens[0].equals("bool")){
							inputsDigital.add(token);
						}
					}
				}
			}
			
			if(line.toLowerCase().contains("//input end") || line.toLowerCase().contains("// input end")) {
				inInputArea = false;
			}else if(line.toLowerCase().contains("//input begin") || line.toLowerCase().contains("// input begin")) {
				inInputArea = true;
			}
		}
		
		for(Template template : automaton.getTemplates()) {
			if(template.getName().equals("Cycle")) hasCycle = true;
			if(Arrays.asList(StringTemplateHandler.getPins()).contains(template.getName())) 
				existingInputTemplates.add(template.getName());
		}
		
	}
	
	/**
	 * transforming and annotating a automaton XML to a special scheme
	 * @param automaton to transform
	 * @return new automaton in special scheme for translation
	 */
	private static String transformAutomaton(Automaton automaton) {
		String annotateautomaton = annotateAutomaton(automaton); // annotate automaton and transform it as String
		
		String newautomaton = ""; // automaton with missing Templates
		//------------Add missing Templates to automaton------------
		String lines[] = annotateautomaton.split("\r\n");
		lines = lines.length <= 1 ? annotateautomaton.split("\n") : lines;
		for(String line : lines) {
			if(line.contains("<system>")){ // add Templates
				if(!hasCycle) { // add Cycle
					newautomaton += StringTemplateHandler.getStringByTemplate("Cycle", null) + "\r\n";
				}
				
				//add digital input automaton
				for(String in : inputsDigital) {
					if(!existingInputTemplates.contains(in.toUpperCase())) {
						String args[] = {in.toUpperCase(), in};
						newautomaton += StringTemplateHandler.getStringByTemplate("inputDigital", args) + "\r\n";
					}
				}
				
				//add analogue input automaton
				for(String in : inputsAnalogue) {
					if(!existingInputTemplates.contains(in.toUpperCase())) {
						String args[] = {in.toUpperCase(), in};
						newautomaton += StringTemplateHandler.getStringByTemplate("inputAnalogue", args) + "\r\n";
					}
				}
				newautomaton += line + "\r\n";
			}else {
				newautomaton += line + "\r\n";
			}
		}
		//----------------------------------------------------------------
		
		return newautomaton;
	}
	
	/**
	 * annotates an automaton by labels and declarations 
	 * @param automaton to annotate
	 * @param templatesToAdd missing Templates to add in system declaration
	 * @return annotated automaton
	 */
	private static String annotateAutomaton(Automaton automaton) {
		Automaton newautomaton = automaton;
		
		//---------------annotate global declaration----------------
		String newDeclaration = "";
		String lines[] = newautomaton.getDeclaration().split("\n");
		for(String line : lines) {
			if(line.contains("// global declarations")){ // add global declaration missing parts
				newDeclaration += line + "\n";
				if(!hasCycleTime) // add cycleTime
					newDeclaration += "//CYCLETIME\nconst int cycleTime = 100;\r\n";
				if(!hasCycleSync) // add cycle channel
					newDeclaration += "//CHANS BEGIN\r\n" + "broadcast chan cycle;\r\n" + "//CHANS END\r\n";
				if(!hasClockX) // add clock x
					newDeclaration += "//CLOCKS BEGIN\r\nclock x; // This is the main clock. It will be translated.\r\n//CLOCKS END";
			}else { // skip line
				newDeclaration += line + "\r\n";
			}
		}
		newautomaton.setDeclaration(newDeclaration);
		//---------------------------------------------------------
		
		//--------------annotate main Template---------------------
		for(Template template : automaton.getTemplates()) {
			if(!Arrays.asList(StringTemplateHandler.getPins()).contains(template.getName()) && !template.getName().equals("Cycle")) {
				
				// annotate Transitions by syncs, guards, updates and add missing labels
				for(Transition transition : template.getTransitions()) {
					boolean hadSync = false;
					boolean hadUpdate = false;
					for(Label label : transition.getlabels()) {
						if(label.getKind().equals("synchronisation")) {
							label.setContent(annotateLabel(label.getContent(), label.getKind()));
							hadSync = true;
						}else if(label.getKind().equals("assignment")) {
							label.setContent(annotateLabel(label.getContent(), label.getKind()));
							hadUpdate = true;
						}else if(label.getKind().equals("guard")) {
							label.setContent(annotateLabel(label.getContent(), label.getKind()));
						}
					}
					if(!hadSync) {
						Label newLabel = new Label();
						newLabel.setKind("synchronisation");
						newLabel.setContent("cycle ?");
						newLabel.setPosX(template.getLocationById(transition.getSource()).getPosX() + 5);
						newLabel.setPosY(template.getLocationById(transition.getSource()).getPosY() + 2);
						transition.addLabel(newLabel);
					}
					if(!hadUpdate) {
						Label newLabel = new Label();
						newLabel.setKind("assignment");
						newLabel.setContent("x := 0");
						newLabel.setPosX(template.getLocationById(transition.getSource()).getPosX() + 5);
						if(!hadSync) newLabel.setPosY(template.getLocationById(transition.getSource()).getPosY() + 15);
						else newLabel.setPosY(template.getLocationById(transition.getSource()).getPosY() + 2);
						transition.addLabel(newLabel);
					}

					hadSync = false;
					hadUpdate = false;
				}
				
				// annotate location invariants 
				for(Location location : template.getLocations()) {
					if(location.getLabel() != null) {
						location.getLabel().setContent(annotateLabel(location.getLabel().getContent(), "invariant"));
					}
				}
				
				// annotate system declaration
				String systemDec = "system " + template.getName() + ", Cycle";
				for(String in : inputsDigital) {
					systemDec += ", " + in.toUpperCase();	
				}
				for(String in : inputsAnalogue) {
					systemDec += ", " + in.toUpperCase();	
				}
				systemDec += ";";
				newautomaton.setSystemDeclaration(systemDec);
				
				break;
			}
		}
		//---------------------------------------------------------

		return newautomaton.toString();
	}
	
	/**
	 * annotates a label content
	 * @param content of the label
	 * @param kind of the label
	 * @return new label content
	 */
	private static String annotateLabel(String content, String kind) {
		if(kind.equals("synchronisation")) { // sync annotation
			return content.contains("cycle") ? content : content + ", cycle ?";
		}else if(kind.equals("assignment")) {
			if(!content.contains("x := 0") && !content.contains("x:=0") && !content.contains("x :=0") && !content.contains("x:= 0"))
				return content += ", x := 0";
			else return content;
		}else if(kind.equals("guard")) { // guard annotation
			String newContent = "";
			String tokens[] = content.replaceAll(" ", "").split("");
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i].equals("=") && tokens[i + 1].equals("=") && tokens[i - 1].equals("x") 
						&& (i - 2 < 0 || i + 1 > tokens.length || !tokens[i - 2].matches("[a-zA-z0-9_]") 
								|| !tokens[i + 1].matches("[a-zA-z0-9_]"))) {
					newContent += ">";
				} else {
					newContent += tokens[i];
				}
			}
			return newContent;
		}else if(kind.equals("invariant")) { // invariant annotation
			String newContent = "";
			boolean usedX = false;
			String tokens[] = content.replaceAll(" ", "").split("");
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i].equals("x") 
						&& (i - 2 < 0 || i + 1 >= tokens.length || !tokens[i - 2].matches("[a-zA-z0-9_]") 
								|| !tokens[i + 1].matches("[a-zA-z0-9_]"))) {
					newContent += tokens[i];
					usedX = true;
				} else if(usedX && tokens[i].matches("[a-zA-z0-9_]") && 
						(i + 1 >= tokens.length || !tokens[i + 1].matches("[a-zA-z0-9_]"))){
					newContent += tokens[i] + " + cycleTime";
					usedX = false;
				}else {
					newContent += tokens[i];
				}
			}
			return newContent;
		}
		return content;
	}
	
	/**
	 * writes an XML from a String
	 * @param content XML as String
	 * @param path to existing automaton file
	 */
	private static void createXML(String content, String path) {
		try {
			String newFile = path.substring(0, path.length() - 4);
            Files.write(Paths.get(newFile + "_converted.xml"), content.getBytes());
            System.out.println("LOG : GENERATED automaton AS '" + newFile + "_converted.xml'");
        } catch (Exception e) {
        	System.err.println("ERROR : WRITING XML FAILED");
        	System.exit(-1);
        }
	}

}
