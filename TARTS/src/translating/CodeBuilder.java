package translating;

import java.util.List;

import automata.Automaton;
import automata.Label;
import automata.Template;

/**
 * Interface for a CodeBuilder.
 * A CodeBuilder generates Code for a Board.
 * @author Torben Friedrich Goerner
 */
public interface CodeBuilder {
	
	//-----------Arguments builder functions for String Templates begin--------------- 
	
	/**
	 * builds the ta_types_h StringTemplate by an automaton
	 * @param automaton for translation
	 * @param pins used in automaton
	 * @return ta_types_h
	 */
	public String generateCodeFor_ta_types_h(Automaton automaton, List<String> pins);
	
	/**
	 * builds the ta_functions_h StringTemplate
	 * @return ta_functions_h
	 */
	public String generateCodeFor_ta_functions_h();
	
	/**
	 * builds the ta_functions_c StringTemplate
	 * @return ta_functions_c
	 */
	public String generateCodeFor_ta_functions_c();
	
	/**
	 * builds the ta_model_h StringTemplate by an automaton
	 * @param automaton for translation
	 * @param locationNames of the automaton
	 * @param pins of the automaton
	 * @return ta_model_h
	 */
	public String generateCodeFor_ta_model_h(Automaton automaton, List<String> locationNames, List<String> pins);
	
	/**
	 * builds the ta_model_c StringTemplate by an automaton
	 * @param automaton for translation
	 * @param automatonTemplate main template for the translation
	 * @param pins of the automaton
	 * @param pinValues of the automaton pins (initial values)
	 * @param locationNames of the automaton template
	 * @param locationIds of the automaton template
	 * @return ta_model_c
	 */
	public String generateCodeFor_ta_model_c(Automaton automaton, Template automatonTemplate, List<String> pins, 
			List<Integer> pinValues, List<String> locationNames, List<String> locationIds);
	
	/**
	 * builds the ta_sensoresActors_h StringTemplate
	 * @return ta_sensoresActors_h
	 */
	public String generateCodeFor_ta_sensoresActors_h();
	
	/**
	 * builds the ta_sensoresActors_c StringTemplate by an automaton
	 * @param automaton for translation
	 * @return ta_sensoresActors_c
	 */
	public String generateCodeFor_ta_sensoresActors_c(Automaton automaton);
	
	/**
	 * builds the ta_userCode_h StringTemplate by an automaton
	 * @param automatonTemplate main template for the translation
	 * @return ta_userCode_h
	 */
	public String generateCodeFor_ta_userCode_h(Template automatonTemplate);
	
	/**
	 * builds the ta_userCode_c StringTemplate by an automaton
	 * @param automatonTemplate main template for the translation
	 * @return ta_userCode_c
	 */
	public String generateCodeFor_ta_userCode_c(Template automatonTemplate);
	
	//-----------Arguments builder functions for String Templates end--------------- 
	
	/**
	 * creates the main.c and main.h code
	 * @param automaton for translation
	 * @param path to project
	 * @return main code in Array (main[0] = main.h, main[1] = main.c)
	 */
	public String[] createMainCode(Automaton automaton, String path);
	
	/**
	 * gets the translated Code from a Label (Label parsing)
	 * @param label label to check
	 * @param kind of the label
	 * @return C code
	 */
	public String getLabelCode(Label label);

}
