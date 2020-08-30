package translating;

import java.util.Arrays;
import java.util.List;

import automata.Automaton;
import automata.Label;
import automata.Location;
import automata.Template;
import automata.Transition;
import main.StringTemplateHandler;

/**
 * The ArduinoUnoR3_Builder is a CodeBuilder, which extends the DefaultCodeBuilder.
 * It overwrites the specific code parts for the ArduinoUnoR3 
 * @author Torben Friedrich Goerner
 */
public class ArduinoUnoR3_Builder extends DefaultCodeBuilder{
	
	
	public ArduinoUnoR3_Builder() {
		super();
	}
	
	@Override
	public String generateCodeFor_ta_model_h(Automaton automaton, List<String> locationNames, List<String> pins) {
		String args[] = new String[2];
		
		String locationValues = "ERROR_CASE = -1, ";
		for(int i = 0; i < locationNames.size(); i++) {
			locationValues += locationNames.get(i) + " = " + i + ", ";
		}
		String locationEnumArgs[] = new String[2];
		locationEnumArgs[0] = locationValues.substring(0, locationValues.length() - 2);
		locationEnumArgs[1] = "location";
		args[0] = StringTemplateHandler.getStringByTemplate("enumST", locationEnumArgs);
		
		String pinValues = "";
		for(int i = 0; i < pins.size(); i++) {
			pinValues += pins.get(i) + " = " + i + ", ";
		}
		String pinEnumArgs[] = new String[2];
		pinEnumArgs[0] = pinValues.substring(0, pinValues.length() - 2);
		pinEnumArgs[1] = "pins";
		args[1] = StringTemplateHandler.getStringByTemplate("enumST", pinEnumArgs);
		
		return "#include \"Arduino.h\"\n" + StringTemplateHandler.getStringByTemplate("ta_model_h", args);
	}
	
	@Override
	public String generateCodeFor_ta_model_c(Automaton automaton, Template automatonTemplate, List<String> pins, 
			List<Integer> pinValues, List<String> locationNames, List<String> locationIds) {
		
		String args[] = new String[4];
		
		String vars = "";
		String lines[] = automaton.getDeclaration().split("\n");
		boolean inArea = false;
		for(String line : lines) {
			if(line.toLowerCase().startsWith("//user variables end")) {
				inArea = false;
			}
			if(inArea) {
				vars += line;
			}
			if(line.toLowerCase().startsWith("//user variables begin")) {
				inArea = true;
			}
		}
		args[0] = vars;
		
		String initPins = "";
		for(int i = 0; i < pins.size(); i++) {
			String[] argsInitPin = new String[2];
			argsInitPin[0] = pins.get(i);
			argsInitPin[1] = pinValues.get(i).toString();
			initPins += StringTemplateHandler.getStringByTemplate("initPin", argsInitPin);
		}
		args[1] = initPins;
		
		String doTransition = "";
		for(int i = 0; i < locationNames.size(); i++) {
			String arg[] = new String[4];
			arg[0] = locationNames.get(i);
			arg[1] = "";
			arg[2] = "";
			for(Transition transition : automatonTemplate.getTransitions()) {
				if(transition.getSource().equals(locationIds.get(i))) {
					boolean noGuard = true;
					String argTrans[] = new String[3];
					argTrans[2] = "";
					for(Label label : transition.getlabels()) {
						if(label.getKind().equals("guard")) {
							noGuard = false;
							argTrans[0] = getLabelCode(label);
							for(int j = 0; j < locationIds.size(); j++) {
								if(transition.getTarget().equals(locationIds.get(j))) {
									argTrans[1] = locationNames.get(j);
									break;
								}
							}
						}else if(label.getKind().equals("assignment")) {
							argTrans[2] = getLabelCode(label);
						}
					}
					if(noGuard) {
						for(int j = 0; j < locationIds.size(); j++) {
							if(transition.getTarget().equals(locationIds.get(j))) {
								arg[1] += "automaton.location = " + locationNames.get(j) + ";\n" +
											argTrans[2] + "return true;";
								break;
							}
						}
					}else {
						arg[1] += StringTemplateHandler.getStringByTemplate("transitionTree", argTrans);
					}
				}
			}
			doTransition += StringTemplateHandler.getStringByTemplate("doTransition", arg);
		}
		args[2] = doTransition;
		
		Location init = automatonTemplate.getInit();
		if (init.getName().equals("")) {
			args[3] = init.getId();
		} else {
			args[3] = init.getName();
		}
		
		return StringTemplateHandler.getStringByTemplate("ta_model_c_Arduino", args);
	}
	
	@Override
	public String generateCodeFor_ta_sensoresActors_h() {
		return StringTemplateHandler.getStringByTemplate("ta_sensoresActors_h_Arduino", null);
	}

	@Override
	public String generateCodeFor_ta_sensoresActors_c(Automaton automaton) {
		String args[] = new String[3];
		
		String out = "";
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
						out += "digitalWrite(" + tokens[1].replaceAll("p", "") + ", automaton->pins[" + tokens[1].toUpperCase() + "]);\n";
					}
				}
			}
			if(line.toLowerCase().startsWith("//output begin") || line.toLowerCase().startsWith("// output begin")) {
				inOutputArea = true;
			}
		}

		args[0] = out;
		
		String inDig = "";
		String inAn = "";
		for(Template template : automaton.getTemplates()) {
			if(Arrays.asList(StringTemplateHandler.getPins()).contains(template.getName())) {
				if(template.getTransitions().size() == 1) { // analogue pin handling
					inAn += "newInput(" + template.getName() + ", analogRead(" + template.getName().replaceAll("P", "") + "));\n";
				}else { // digital pin handling
					inDig += "newInput(" + template.getName() + ", digitalRead(" + template.getName().replaceAll("P", "") + "));\n";
				}
			}
		}
		args[1] = inDig;
		args[2] = inAn;
		
		return StringTemplateHandler.getStringByTemplate("ta_sensoresActors_c_Arduino", args);
	}
	
	@Override
	public String[] createMainCode(Automaton automaton, String path) {
		String mainCode[] = {null, ""}; 
		
		String args[] = new String[2];
		args[0] = "";
		String lines[] = automaton.getDeclaration().split("\n");
		boolean inOutputArea = false;
		for(String line : lines) { // setup Output pins
			if(line.toLowerCase().startsWith("//output end") || line.toLowerCase().startsWith("// output end")) {
				inOutputArea = false;
				break;
			}
			if(inOutputArea) {
				String ops[] = line.split(";");
				for(String op : ops) {
					String tokens[] = op.replaceAll("\n", " ").split(" ");
					if(tokens.length >= 4 && Arrays.asList(StringTemplateHandler.getPins()).contains(tokens[1].toUpperCase())) {
						String arg[] = new String[2];
						arg[0] = tokens[1].replaceAll("p", "");
						arg[1] = "OUTPUT";
						args[0] += StringTemplateHandler.getStringByTemplate("arduinoPinSetup", arg) + "\n";
					}
				}
			}
			if(line.toLowerCase().startsWith("//output begin") || line.toLowerCase().startsWith("// output begin")) {
				inOutputArea = true;
			}
		}
		
		for(Template template : automaton.getTemplates()) { // setup digital input pins
			if(template.getTransitions().size() == 2) {
				String arg[] = new String[2];
				arg[0] = template.getName().replaceAll("P", "");
				arg[1] = "INPUT";
				args[0] += StringTemplateHandler.getStringByTemplate("arduinoPinSetup", arg) + "\n";
			}
		}
		
		String cycleTime = "10";
		String ops[] = automaton.getDeclaration().split(";");
		for(String op : ops) {
			if(op.contains("const int cycleTime")) {
				int index = op.length() - 1;
				boolean end = false;
				String value = "";
				while(!end) {
					if(op.charAt(index) == '=') {
						end = true;
						break;
					}else {
						value += op.charAt(index);
					}
					index--;
				}
				cycleTime = new StringBuilder(value).reverse().toString();
				break;
			}
		}
		args[1] = cycleTime;
		
		mainCode[1] = StringTemplateHandler.getStringByTemplate("mainArduino", args);
		
		return mainCode;
	}

}
