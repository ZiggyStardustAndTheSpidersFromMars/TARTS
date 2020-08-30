package translating;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import automata.Automaton;
import automata.Location;
import automata.Template;
import main.StringTemplateHandler;
/**
 * Translator for timed automaton models to C Code.
 * Using a CodeBuilder to generate Code for a specific Board.
 * @author Torben Friedrich Goerner
 */
public class Translator {
	
	/**
	 * CodeBuilder to generate Code for a specific Board
	 */
	private static CodeBuilder codeBuilder;
	/**
	 * target platform
	 */
	private static String targetBoard;
	
	/**
	 * automaton template for translation 
	 */
	private static Template automatonTemplate;
	/**
	 * pins used by automaton
	 */
	private static List<String> pins;
	/**
	 * locations in automaton
	 */
	private static List<String> locationNames;
	/**
	 * ids of automaton locations
	 */
	private static List<String> locationIds;
	/**
	 * init Values of Pins
	 */
	private static List<Integer> pinValues;
	
	
	/**
	 * Setup function for the Translator.
	 * Sets a CodeBuilder by a board
	 * @param board target board
	 */
	public static void setup(String board) {
		targetBoard = board;
		if(board.equals("NUCLEO-F030R8")) {
			codeBuilder = new NucleoF030R8_Builder();
		}else if(board.equals("ArduinoUnoR3")) {
			codeBuilder = new ArduinoUnoR3_Builder();
		}else {
			System.out.println("ERROR : UNKNOWN TARGET PLATFORM\r\n\tCHECK THE CONFIG FILE");
			System.exit(-1);
		}
	} 
	
	
	/**
	 * Translates a automaton model into program code. 
	 * @param automaton to translate in code
	 * @param path path to existing project to integrate the code
	 */
	public static void translate(Automaton automaton, String path) {

		// analyzing the automaton for translation 
		setPins(automaton);
		setautomatonTemplate(automaton);
		setLocationNamesAndIds();
		
		// generating code for files
		String ta_types_h = codeBuilder.generateCodeFor_ta_types_h(automaton, pins);	
		String ta_functions_h = codeBuilder.generateCodeFor_ta_functions_h();
		String ta_functions_c = codeBuilder.generateCodeFor_ta_functions_c();
		String ta_model_h = codeBuilder.generateCodeFor_ta_model_h(automaton, locationNames, pins);
		String ta_model_c = codeBuilder.generateCodeFor_ta_model_c(automaton, automatonTemplate, pins, pinValues, locationNames, locationIds);
		String ta_sensoresActors_h = codeBuilder.generateCodeFor_ta_sensoresActors_h();
		String ta_sensoresActors_c = codeBuilder.generateCodeFor_ta_sensoresActors_c(automaton);
		String ta_userCode_h = codeBuilder.generateCodeFor_ta_userCode_h(automatonTemplate);
		String ta_userCode_c = codeBuilder.generateCodeFor_ta_userCode_c(automatonTemplate);
		String mainCode[] = codeBuilder.createMainCode(automaton, path);
		
		// generating files
		generateFile(ta_types_h, "ta_types.h", path);
		generateFile(ta_functions_h, "ta_functions.h", path);
		generateFile(ta_functions_c, "ta_functions.c", path);
		generateFile(ta_model_h, "ta_model.h", path);
		generateFile(ta_model_c, "ta_model.c", path);
		generateFile(ta_sensoresActors_h, "ta_sensoresActors.h", path);
		generateFile(ta_sensoresActors_c, "ta_sensoresActors.c", path);
		generateFile(ta_userCode_h, "ta_userCode.h", path);
		generateFile(ta_userCode_c, "ta_userCode.c", path);
		
		if(targetBoard.equals("NUCLEO-F030R8")) {
			generateFile(mainCode[0], "main.h", path);
			generateFile(mainCode[1], "main.c", path);
		}else if(targetBoard.equals("ArduinoUnoR3")) {
			generateFile(mainCode[1], "main.ino", path);
		}
		
		System.out.println("LOG : GENERATED AUTOMATON INTO C CODE");
		
	}
	
	/**
	 * generates .c and .h and other files
	 * @param data data for the file
	 * @param name file name 
	 * @param path path to existing project to generate the code into it
	 */
	private static void generateFile(String data, String name, String path) {
		try {
			if(targetBoard.equals("NUCLEO-F030R8")) {
				if(name.endsWith(".c")) {
				Files.write(Paths.get(path + "/Src/" + name), data.getBytes());
				}else if(name.endsWith(".h")){
				Files.write(Paths.get(path +"/Inc/" + name), data.getBytes());
				}
			}else if(targetBoard.equals("ArduinoUnoR3")) {
				if(name.endsWith(".ino")) {
					Files.write(Paths.get(path + "/" + path + ".ino"), data.getBytes());
				}else {
					Files.write(Paths.get(path + "/" + name), data.getBytes());
				}
			}
		} catch (IOException e) {
			System.err.println("ERROR : " + e.getStackTrace().toString());
        	System.exit(-1);
        }
		
		System.out.println("LOG : GENERATED " + name);
	}
	
	
	//-----------automaton analysis functions begin------------
	
	/**
	 * gets the used/needed pins from automaton and writes them into pins 
	 * and adds the init value for input pins (always 0) and output pins
	 * @param automaton for translation
	 */
	private static void setPins(Automaton automaton) {
		List<String> neededPins = new LinkedList<>();
		List<Integer> values = new LinkedList<>();
		
		for(Template template : automaton.getTemplates()) {
			if(Arrays.asList(StringTemplateHandler.getPins()).contains(template.getName())) {
				neededPins.add(template.getName());
				values.add(0);
			}
		}
		
		String lines[] = automaton.getDeclaration().split("\n");
		boolean inOutputArea = false;
		for(String line : lines) {
			if(line.toLowerCase().startsWith("//output end") || line.toLowerCase().startsWith("// output end")) {
				inOutputArea = false;
				break;
			}
			if(inOutputArea) {
				String ops[] = line.split(";");
				for(String op : ops) {
					String tokens[] = op.replaceAll("\n", " ").split(" ");
					if(tokens.length >= 4 && Arrays.asList(StringTemplateHandler.getPins()).contains(tokens[1].toUpperCase())) {
						boolean isValue = false;
						boolean pinNameDec = false; // boolean pin name is declared 
						for(String token : tokens) {
							if(!token.equals("bool") && !token.equals("int") && !token.equals("") && !pinNameDec) {
								neededPins.add(token.toUpperCase());
								pinNameDec = true;
							}
							if(!token.equals("") && isValue) {
								token = token.replaceAll(";", "").replaceAll(" ", "");
								if(token.equals("false") || token.equals("0")) {
									values.add(0);
								}else {
									values.add(1);
								}
								break;
							}
							if(token.equals("=")) isValue = true;
						}
					}
				}
			}
			
			if(line.toLowerCase().startsWith("//output begin") || line.toLowerCase().startsWith("// output begin")) {
				inOutputArea = true;
			}
		}
		
		// add LD2 for NUCLEO-F030R8
		if(targetBoard.equals("NUCLEO-F030R8")) {
			neededPins.add("LD2");
			values.add(0);
		}
		
		pins = neededPins;
		pinValues = values;
	}
	
	/**
	 * gets all location-names and ids in automaton and write them to locationNames and locationIds
	 */
	private static void setLocationNamesAndIds() {
		List<String> usedLocations = new LinkedList<>();
		List<String> ids = new LinkedList<>();

		if (!Arrays.asList(StringTemplateHandler.getPins()).contains(automatonTemplate.getName())
				&& !automatonTemplate.getName().equals("Cycle")) {
			for (Location location : automatonTemplate.getLocations()) {
				if (location.getName().equals("")) {
					usedLocations.add(location.getId());
				} else {
					usedLocations.add(location.getName());
				}
				ids.add(location.getId());
			}
		}

		locationNames = usedLocations;
		locationIds = ids;
	}
	
	/**
	 * sets the automaton template for translation
	 * @param automaton to check
	 */
	private static void setautomatonTemplate(Automaton automaton) {
		String templateName = "";
		
		String automatonTemplates[] = automaton.getSystemDeclaration().replaceAll(" ", "").replaceAll("system", "")
				.replaceAll("\n", "").replaceAll(";", "").split(",");
		for(String template :automatonTemplates) {
			if(!template.equals("Cycle") && !Arrays.asList(StringTemplateHandler.getPins()).contains(template)) {
				templateName = template;
				break;
			}
		}
		
		for(Template template : automaton.getTemplates()) {
			if(template.getName().equals(templateName)) {
				automatonTemplate = template;
				break;
			}
		}
		
		if(templateName.equals("")) { // ERROR HANDLING
			System.out.println("ERROR : NO USER TEMPLATE FOUND");
			System.exit(-1);
		}
	}
	
	//------------automaton analysis functions end-------------

}
