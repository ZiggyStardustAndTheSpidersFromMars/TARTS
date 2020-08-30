package translating;

import java.util.Arrays;

import automata.Automaton;
import automata.Label;
import automata.Location;
import automata.Template;
import automata.Transition;
import main.StringTemplateHandler;
/**
 * Validator for timed automaton to check the scheme.
 * @author Torben Friedrich Goerner
 */
public class Validator {

	
	/**
	 * Validates a automaton scheme
	 * @param automaton to check
	 * @return is valid
	 */
	public static boolean validate(Automaton automaton) {
		if(checkDeclarations(automaton) 
				&& checkTemplates(automaton) 
				&& checkTimedTranstions(automaton) 
				&& checkSyncs(automaton)
				&& checkInvariants(automaton)) { 
			System.out.println("LOG : automaton-SCHEME IS VALIDATED");
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * checks the declarations
	 * @param automaton to check
	 * @return is valid
	 */
	private static boolean checkDeclarations(Automaton automaton) {
		boolean isValid = true;
		
		if(!automaton.getDeclaration().contains("chan cycle;")) {
			isValid = false;
			System.out.println("ERROR : VALIDATION FAILED!\\n	- CHANNEL 'cycle' IS MISSING");
		}
		
		if(!automaton.getDeclaration().contains("clock x;")) {
			isValid = false;
			System.out.println("ERROR : VALIDATION FAILED!\\n	- CLOCK 'x' IS MISSING");
		}
		
		if(!automaton.getDeclaration().contains("const int cycleTime")) {
			isValid = false;
			System.out.println("ERROR :  VALIDATION FAILED!\n\t- CONST 'cycleTime' MISSING");
		}
		
		boolean userTemplateExisting = false;
		String automatonTemplates[] = automaton.getSystemDeclaration().replaceAll(" ", "").replaceAll("system", "")
				.replaceAll("\n", "").replaceAll(";", "").split(",");
		for(String template :automatonTemplates) {
			if(!template.equals("Cycle") && !Arrays.asList(StringTemplateHandler.getPins()).contains(template)) {
				if(userTemplateExisting) {
					isValid = false;
					System.out.println("ERROR : VALIDATION FAILED!\\n	- TOO MANY TEMPLATES IN SYSTEM DECLARATION");
				}else {
					userTemplateExisting = true;
				}
			}
		}

		return isValid;
	}
	
	/**
	 * check invariants
	 * @param automaton to check
	 * @return is valid
	 */
	private static boolean checkInvariants(Automaton automaton) {
		boolean isValid = true;
		
		for(Template template : automaton.getTemplates()) {
			for(Location location : template.getLocations()) {
				if(location.getLabel() != null) {
					if(location.getLabel().getContent().contains("x") && !location.getLabel().getContent().contains("cycleTime")) {
						isValid = false;
						System.out.println("ERROR : VALIDATION FAILED!\\n	- INVARIANT INCORRECT AT : " + location.getLabel().getContent());
					}
				}
			}
		}
		
		return isValid;
	}
	
	/**
	 * checks the timed transitions 
	 * @param automaton to check
	 * @return is valid
	 */
	private static boolean checkTimedTranstions(Automaton automaton) {
		boolean isValid = true;
		
		for(Template template : automaton.getTemplates()) {
			if(!Arrays.asList(StringTemplateHandler.getPins()).contains(template.getName()) && !template.getName().equals("Cycle")) {
				for(Transition transition : template.getTransitions()) {
					for(Label label : transition.getlabels()) {
						if(label.getKind().equals("guard")) {
							if(label.getContent().contains("x ==") || label.getContent().contains("x==")) {
								System.out.println("ERROR : VALIDATION FAILED!\\n	- TIMED TRANSITION INVALID AT " 
									+ label.getContent());
								isValid = false;
							}
						}
					}
				}
			}
		}
		
		return isValid;
	}
	
	/**
	 * checks automaton for cycle automaton and input templates
	 * @param automaton to check
	 * @return is valid
	 */
	private static boolean checkTemplates(Automaton automaton) {
		boolean isValid = true;
		boolean hasCycle = false;
		
		for(Template template : automaton.getTemplates()) {
			if(Arrays.asList(StringTemplateHandler.getPins()).contains(template.getName())) {
				if(!automaton.getDeclaration().contains(template.getName().toLowerCase())) {
					isValid = false; 
					System.out.println("ERROR :  VALIDATION FAILED!\n\t- \"" + template.getName().toLowerCase() + "\" MISSING IN DECLARATION");
				}
				if(template.getTransitions().size() == 1 
						&& !Arrays.asList(StringTemplateHandler.getAnalogueInPins()).contains(template.getName())) {
					isValid = false;
					System.out.println("ERROR : VALIDATION FAILED!\n\t '" + template.getName() + "' IS NOT AN ANALOGUE INPUT");
				}
			}
			if(template.getName().equals("Cycle")) {
				hasCycle = true;
				if(!template.getDeclaration().contains("clock y;")) {
					System.out.println("ERROR :  VALIDATION FAILED!\n\t- CLOCK 'y' IN CYCLE automaton MISSING");
					isValid = false;
				}
			}
		}
		
		if(isValid && hasCycle) {
			return true;
		}else {
			if(!hasCycle) {
				System.out.println("ERROR : VALIDATION FAILED!\n\t- CYCLE automaton MISSING");
			}
			return false;
		}
	}
	
	/**
	 * checks all transitions in main automaton for syncs with cycle
	 * @param automaton to check
	 * @return is valid
	 */
	private static boolean checkSyncs(Automaton automaton) {
		boolean isValid = true;
		
		for(Template template : automaton.getTemplates()) {
			if(!Arrays.asList(StringTemplateHandler.getPins()).contains(template.getName()) && !template.getName().equals("Cycle")) {
				for(Transition transition : template.getTransitions()) {
					boolean hasSync = false;
					for(Label label : transition.getlabels()) {
						if(label.getKind().equals("synchronisation") && 
								(label.getContent().contains("cycle ?") || label.getContent().contains("cycle?"))) {
							hasSync = true;
						}
					}
					if(!hasSync) {
						System.out.println("ERROR : VALIDATION FAILED!\n\t- SYNC WITH \"cycle\" MISSING AT " + transition.getSource());
						return false;
					}
				}
			}
		}
		
		return isValid;
	}

}
