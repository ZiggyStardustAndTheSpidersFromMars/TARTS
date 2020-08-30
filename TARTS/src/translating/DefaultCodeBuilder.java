package translating;

import java.util.List;

import automata.Automaton;
import automata.Label;
import automata.Location;
import automata.Template;
import automata.Transition;
import main.StringTemplateHandler;

/**
 * The DefaultCodeBuilder is the a CodeBuilder, that implements the logic, 
 * which is the same in every IDE for all boards.
 * @author Torben Friedrich Goerner
 */
public class DefaultCodeBuilder implements CodeBuilder{
	

	@Override
	public String generateCodeFor_ta_types_h(Automaton automaton, List<String> pins) {
		String args[] = new String[1];
		args[0] = pins.size() + "";
		return StringTemplateHandler.getStringByTemplate("ta_types_h", args);
	}
	
	@Override
	public String generateCodeFor_ta_functions_h() {
		return StringTemplateHandler.getStringByTemplate("ta_functions_h", null);
	}


	@Override
	public String generateCodeFor_ta_functions_c() {
		return StringTemplateHandler.getStringByTemplate("ta_functions_c", null);
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
		
		return StringTemplateHandler.getStringByTemplate("ta_model_h", args);
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
		
		return StringTemplateHandler.getStringByTemplate("ta_model_c", args);
	}
	
	@Override
	public String generateCodeFor_ta_sensoresActors_h() {
		return StringTemplateHandler.getStringByTemplate("ta_sensoresActors_h", null);
	}

	@Override
	public String generateCodeFor_ta_sensoresActors_c(Automaton automaton) {
		return StringTemplateHandler.getStringByTemplate("ta_sensoresActors_c", null);
	}

	@Override
	public String generateCodeFor_ta_userCode_h(Template automatonTemplate) {
		String args[] = {""};
		
		String lines[] = automatonTemplate.getDeclaration().split("\n");
		boolean inFunctions = false;
		boolean inVariables = false;
		for(String line : lines) {
		
			if(line.contains("//FUNCTIONS FOR TRANSLATION END")) inFunctions = false;
			if(line.contains("//VARIABLES FOR TRANSLATION END")) inVariables = false;
			
			if(inFunctions) {
				if((line.contains("void") || line.contains("bool") || line.contains("int") || line.contains("char")) 
						&& line.contains("(") && line.contains(")") && !line.contains("=")) {
					String tokens[] = line.split("");
					String fnct = "";
					for(String token : tokens) {
						if(token.equals(")")) {
							fnct += ");\n";
							break;
						}else {
							fnct += token;
						}
					}
					args[0] += fnct;
				}
			}
			
			if(inVariables) {
				String tokens[] = line.split("");
				String var = line.replaceAll(" ", "").equals("") ? "" : "extern ";
				for(String token : tokens) {
					if(token.equals(";") || token.equals("=")) {
						var += ";\n";
						break;
					}else {
						var += token;
					}
				}
				args[0] += var;
			}
			
			if(line.contains("//FUNCTIONS FOR TRANSLATION BEGIN")) inFunctions = true;
			if(line.contains("//VARIABLES FOR TRANSLATION BEGIN")) inVariables = true;
		}
		
		return StringTemplateHandler.getStringByTemplate("ta_userCode_h", args);
	}

	@Override
	public String generateCodeFor_ta_userCode_c(Template automatonTemplate) {
		String args[] = {"", ""};
		
		String lines[] = automatonTemplate.getDeclaration().split("\n");
		boolean inVariables = false;
		boolean inFunctions = false;
		for(String line : lines) {
			
			if(line.contains("//VARIABLES FOR TRANSLATION END")) inVariables = false;
			if(line.contains("//FUNCTIONS FOR TRANSLATION END")) inFunctions = false;
			
			if(inVariables) {
				args[0] += line + "\n"; 
			}else if(inFunctions) {
				args[1] += line + "\n";
			}
			
			if(line.contains("//VARIABLES FOR TRANSLATION BEGIN")) inVariables = true;
			if(line.contains("//FUNCTIONS FOR TRANSLATION BEGIN")) inFunctions = true;
		}
		
		return StringTemplateHandler.getStringByTemplate("ta_userCode_c", args);
	}

	@Override
	public String[] createMainCode(Automaton automaton, String path) {
		return null;
	}

	@Override
	public String getLabelCode(Label label) {
		String code = "";

		if (label.getKind().equals("guard")) {
			code = label.getContent().replaceAll("x", "time"); // time handling
			for(String pin : StringTemplateHandler.getPins()) { // pin handling
				code = code.replaceAll(pin.toLowerCase(), "automaton.pins[" + pin + "]");
			}

		}else if(label.getKind().equals("assignment")) {
			String ops[] = label.getContent().replaceAll("\n", " ").split(",");
			for (String op : ops) {
				if (!op.contains("x := 0") && !op.contains("x:=0") && !op.contains("x:= 0") && !op.contains("x :=0")
						&&!op.contains("x = 0") && !op.contains("x=0") && !op.contains("x= 0") && !op.contains("x =0")) {
					code += op.replaceAll(":=", "="); // UPPAAL ':=' handling
					for(String pin : StringTemplateHandler.getPins()) { // pin handling
						code = code.replaceAll(pin.toLowerCase(), "automaton.pins[" + pin + "]");
					}
					code += ";\n";
				}
			} 
		}

		return code;
	}
	
}
