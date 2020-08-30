package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
/**
 * Builder for new UPPAAL projects. Initializes a timed automaton by In and Output Pins.  
 * @author Torben Friedrich Goerner
 */
public class NewProjectBuilder {
	
	/**
	 * creates a new automaton based on in and output pins
	 * @param inputDigital digital input pins
	 * @param inputAnalogue analogue input pins
	 * @param output output pins
	 */
	public static void createNewautomaton(String inputDigital, String inputAnalogue, String output) {
		
		String args[] = new String[4];
		
		String temps = StringTemplateHandler.getStringByTemplate("Cycle", null);
		String[] dig = inputDigital.split(",");
		for(String pin : dig) {
			if(Arrays.asList(StringTemplateHandler.getPins()).contains(pin)) {
				String arg[] = new String[2]; 
				arg[0] = pin.toUpperCase();
				arg[1] = pin.toLowerCase();
				temps += StringTemplateHandler.getStringByTemplate("inputDigital", arg);
			}
		}
		String[] an = inputAnalogue.split(",");
		for(String pin : an) {
			if(Arrays.asList(StringTemplateHandler.getPins()).contains(pin)) {
				String arg[] = new String[2]; 
				arg[0] = pin.toUpperCase();
				arg[1] = pin.toLowerCase();
				temps += StringTemplateHandler.getStringByTemplate("inputAnalogue", arg);
			}
		}
		args[0] = temps;
		
		String names = "";
		String AllPins = inputDigital + "," + inputAnalogue;
		String[] pins = AllPins.split(",");
		for(String pin : pins) {
			if(Arrays.asList(StringTemplateHandler.getPins()).contains(pin)) {
				names += ", " + pin;
			}
		}
		args[1] = names;
		
		String in = "";
		for(String pin : dig) { // digital pins
			if(Arrays.asList(StringTemplateHandler.getPins()).contains(pin)) {
				in += "\nbool " + pin.toLowerCase() + " = false;";
			}
		}
		for(String pin : an) { // analogue pins
			if(Arrays.asList(StringTemplateHandler.getAnalogueInPins()).contains(pin)) {
				in += "\nint " + pin.toLowerCase() + " = 0;";
			}
		}
		args[2] = in;
		
		String out = "";
		String ou[] = output.split(",");
		for(String pin : ou) {
			if(Arrays.asList(StringTemplateHandler.getPins()).contains(pin)) {
				out += "\nbool " + pin.toLowerCase() + " = false;";
			}
		}
		args[3] = out;
		
		writeFile(StringTemplateHandler.getStringByTemplate("newautomaton", args));
	}
	
	/**
	 * writes a xml
	 * @param xml data for xml
	 */
	private static void writeFile(String xml) {
		try {
            Files.write(Paths.get("automaton.xml"), xml.getBytes());
        } catch (IOException e) {
        	System.err.println("ERROR : WRITE XML FAILED");
        	System.exit(-1);
        }
        System.out.println("LOG : CREATED automaton AS 'automaton.xml'");
	}

}
