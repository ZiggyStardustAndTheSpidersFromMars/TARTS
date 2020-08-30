package translating;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import automata.Automaton;
import automata.Template;
import main.StringTemplateHandler;

/**
 * The NucleoF030R8_Builder is a CodeBuilder, which extends the DefaultCodeBuilder.
 * It overwrites the specific code parts for the NUCLEO-F030R8 
 * @author Torben Friedrich Goerner
 */
public class NucleoF030R8_Builder extends DefaultCodeBuilder{
	
	
	public NucleoF030R8_Builder() {
		super();
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
						String arg[] = {tokens[1].toUpperCase()};
						out += StringTemplateHandler.getStringByTemplate("doOutput", arg);
					}
				}
			}
			if(line.toLowerCase().startsWith("//output begin") || line.toLowerCase().startsWith("// output begin")) {
				inOutputArea = true;
			}
		}
		String argLD2[] = {"LD2"};
		out += StringTemplateHandler.getStringByTemplate("doOutput", argLD2);
		args[0] = out;
		
		String inDig = "";
		String inAn = "";
		for(Template template : automaton.getTemplates()) {
			if(Arrays.asList(StringTemplateHandler.getPins()).contains(template.getName())) {
				String arg[] = {template.getName(), ""};
				if(template.getTransitions().size() == 1) { // analogue pin handling
					if(arg[0].equals("PA0")) arg[1] = "ADC_CHANNEL_0";
					else if(arg[0].equals("PA1")) arg[1] = "ADC_CHANNEL_1";
					else if(arg[0].equals("PA4")) arg[1] = "ADC_CHANNEL_4";
					else if(arg[0].equals("PB0")) arg[1] = "ADC_CHANNEL_8";
					else if(arg[0].equals("PC0")) arg[1] = "ADC_CHANNEL_10";
					else if(arg[0].equals("PC1")) arg[1] = "ADC_CHANNEL_11";
					inAn += StringTemplateHandler.getStringByTemplate("getInputAn", arg);
				}else { // digital pin handling
					inDig += StringTemplateHandler.getStringByTemplate("getInputDig", arg);
				}
			}
		}
		args[1] = inDig;
		args[2] = inAn;
		
		return StringTemplateHandler.getStringByTemplate("ta_sensoresActors_c", args);
	}

	@Override
	public String[] createMainCode(Automaton automaton, String path) {
		String main[] = new String[2]; //first element main.h , second element main.c
		
		//---------------main.h---------------
				String includes = StringTemplateHandler.getStringByTemplate("includes_main_h", null);
				String main_h = "";
				
				try {
					File file = new File(path + "/Inc/main.h");
					Scanner reader = new Scanner(file);
					while (reader.hasNextLine()) {
						String line = reader.nextLine();
						if(!includes.contains(line)) main_h += line + "\n";
						
						if(line.contains("/* USER CODE BEGIN Includes */")) {
							main_h += includes;
						}
					}
					reader.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				main[0] = main_h;
				//-------------------------------------
				
				//---------------main.c----------------
				String inits = StringTemplateHandler.getStringByTemplate("main_c_Inits", null);
				String mainFnct = StringTemplateHandler.getStringByTemplate("mainFnct", null);
				String main_c = "";
				
				try {
					File file = new File(path + "/Src/main.c");
					Scanner reader = new Scanner(file);
					boolean inInterruptFunction = false;
					boolean inWhile = false;
					int countOpenTag = 0;
					while (reader.hasNextLine()) {
						String line = reader.nextLine();
						
						//copy line if not matches any of these cases
						if(!inits.contains(line)
								&& !line.contains("bool newCycle = false;")
								&& !line.contains("bool inCycle = false;")
								&& !line.contains("bool inErrorCase = false;")
								&& !line.contains("void HAL_TIM_PeriodElapsedCallback (TIM_HandleTypeDef *htim);")
								&& !line.contains("void HAL_TIM_PeriodElapsedCallback(TIM_HandleTypeDef *htim) {")
								&& !inInterruptFunction
								&& (!inWhile || (inWhile && line.contains("while") || inWhile && line.replaceAll(" ", "").equals("{"))))
									main_c += line + "\n";
						
						if(line.contains("/* USER CODE BEGIN 2 */")) { // add init functions
							main_c += inits;
						}else if(line.contains("/* USER CODE BEGIN PFP */")) { // add prototype functions
							main_c += "void HAL_TIM_PeriodElapsedCallback (TIM_HandleTypeDef *htim);\n";
						}else if(line.contains("/* USER CODE BEGIN PV */")) { // add variables
							main_c += "bool newCycle = false;\nbool inCycle = false;\nbool inErrorCase = false;\n";
						}else if(line.contains("/* USER CODE BEGIN 4 */")) { // add timer ISR function
							String argsInterrupt[] = {"htim6", StringTemplateHandler.getStringByTemplate("cycleEvent", null)};
							String argsInterruptFunction[] = {StringTemplateHandler.getStringByTemplate("interrupt", argsInterrupt)};
							main_c += StringTemplateHandler.getStringByTemplate("interruptFunction", argsInterruptFunction) + "\n";
						}else if(line.contains("void HAL_TIM_PeriodElapsedCallback(TIM_HandleTypeDef *htim) {")) { // in interrupt function area
							inInterruptFunction = true;
						}else if(line.contains("/* USER CODE END WHILE */")) { // add main while code
							main_c += mainFnct + "	/* USER CODE END WHILE */\n";
							inWhile = false;
						}else if(line.contains("/* USER CODE BEGIN WHILE */")) {
							inWhile = true;
						}
						
						//handling for not copying the interrupt function if it is there already 
						if(inInterruptFunction) {
							if(line.contains("{")) countOpenTag++; 
							if(line.contains("}")) countOpenTag--; 
							if(countOpenTag == 0) inInterruptFunction = false;
						}
					}
					reader.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				main[1] = main_c;
				//-------------------------------
				
				return main;
	}

}
