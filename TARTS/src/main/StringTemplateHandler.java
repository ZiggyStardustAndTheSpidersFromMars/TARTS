package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.stringtemplate.v4.ST;
/**
 * StringTemplateHandler for String handling.
 * Using StringTempalte with Antlr to build Strings.
 * @author Torben Friedrich Goerner
 */
public class StringTemplateHandler {
	
	private static ST helpManual = new ST("///////////////////////////////////////////////////////////////////////////////	\r\n" + 
			"//	.___________.    ___      .______     .___________.    _______.	     //\r\n" + 
			"//	|           |   /   \\     |   _  \\    |           |   /       |	     //\r\n" + 
			"//	`---|  |----`  /  ^  \\    |  |_)  |   `---|  |----`  |   (----`      //\r\n" + 
			"// 	    |  |      /  /_\\  \\   |      /        |  |        \\   \\    	     //\r\n" + 
			"//	    |  |     /  _____  \\  |  |\\  \\----.   |  |    .----)   |         //\r\n" + 
			"//	    |__|    /__/     \\__\\ | _| `._____|   |__|    |_______/          //\r\n" + 
			"///////////////////////////////////////////////////////////////////////////////\r\n" + 
			"\r\n\r\n\r\n" + 
			"	HELP :\r\n\r\n" + 
			"config.txt :\r\n" +
			"	board = \\<board name>\r\n" +
			"	validating = \\<true/false>\r\n" +
			"\r\n\r\n" + 
			"Usage   : java -jar TARTS.jar [-options] [args]\r\n\r\n" + 
			"Options :\r\n\r\n" + 
			"	-c or -convert  	coverts a timed automaton into a specified scheme\r\n" + 
			"	-t or -translate	translates a timed automaton into C code\r\n" +
			"	-n or -new	        creates a new automaton\r\n" +
			"	-h or -help	        help manual / operating instructions\r\n" +
			"\r\nArguments :\r\n" +
			"	convert             arg1 - path to automaton .xml\r\n" +
			"	translate           arg1 - path to automaton .xml\r\n" +
			"	                    arg2 - path to project folder\r\n" +
			"	new                 arg1 - digital input pins. Seperated by ','\r\n" +
			"	                    arg2 - analogue input pins. Seperated by ','\r\n" +
			"	                    arg3 - output pins. Seperated by ','");
	
	//templates -> automaton templates
	//templatenames -> name of the automaton templates
	//input -> input pins declaration
	//output -> output pins declaration
	private static ST newautomaton = new ST("\\<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + 
			"\\<!DOCTYPE nta PUBLIC '-//Uppaal Team//DTD Flat System 1.1//EN' 'http://www.it.uu.se/research/group/darts/uppaal/flat-1_2.dtd'>\r\n" + 
			"\\<nta>\r\n" + 
			"	\\<declaration>// global declarations\r\n" + 
			"\r\n" + 
			"//CYCLETIME\r\n" + 
			"const int cycleTime = 10;\r\n" + 
			"\r\n" +
			"//CLOCKS BEGIN\r\n" +
			"clock x; // This is the main clock. It will be translated.\r\n" +
			"//CLOCKS END\r\n" +
			"\r\n" + 
			"//CHANS BEGIN\r\n" + 
			"broadcast chan cycle;\r\n" + 
			"//CHANS END\r\n" + 
			"\r\n" + 
			"//INPUT BEGIN\r\n" + 
			"<input>\r\n" + 
			"//INPUT END\r\n" + 
			"\r\n" + 
			"//OUTPUT BEGIN\r\n" + 
			"<output>\r\n" + 
			"//OUTPUT END\r\n" + 
			"\r\n" + 
			"//USER VARIABLES BEGIN\r\n" + 
			"/* Place your globale variables here (you can remove this comment)*/\r\n" + 
			"//USER VARIABLES END\\</declaration>\r\n" + 
			"	\\<template>\r\n" + 
			"		\\<name x=\"5\" y=\"5\">Template\\</name>\r\n" + 
			"		\\<declaration>// Place local declarations here.\r\n\r\n" +
			"//VARIABLES FOR TRANSLATION BEGIN\r\n\r\n" +
			"//VARIABLES FOR TRANSLATION END\r\n\r\n" +
			"//FUNCTIONS FOR TRANSLATION BEGIN\r\n\r\n" +
			"//FUNCTIONS FOR TRANSLATION END\r\n" +
			"		\\</declaration>\r\n" + 
			"		\\<location id=\"id0\" x=\"0\" y=\"0\">\r\n" + 
			"		\\</location>\r\n" + 
			"		\\<init ref=\"id0\"/>\r\n" + 
			"	\\</template>\r\n" + 
			"<templates>" +
			"	\\<system>\r\n" + 
			"system Template, Cycle<templatenames>;\r\n" + 
			"    \\</system>\r\n" + 
			"	\\<queries>\r\n" + 
			"		\\<query>\r\n" + 
			"			\\<formula>\\</formula>\r\n" + 
			"			\\<comment>\\</comment>\r\n" + 
			"		\\</query>\r\n" + 
			"	\\</queries>\r\n" + 
			"\\</nta>");
	
	private static ST cycle = new ST("\\<template>\r\n" + 
			"		\\<name>Cycle\\</name>\r\n" + 
			"		\\<declaration>clock y;\\</declaration>\r\n" + 
			"		\\<location id=\"id7\" x=\"0\" y=\"0\">\r\n" + 
			"			\\<name x=\"-10\" y=\"-34\">Cycle\\</name>\r\n" + 
			"			\\<label kind=\"invariant\" x=\"-8\" y=\"-51\">y &lt;= cycleTime\\</label>\r\n" + 
			"		\\</location>\r\n" + 
			"		\\<init ref=\"id7\"/>\r\n" + 
			"		\\<transition>\r\n" + 
			"			\\<source ref=\"id7\"/>\r\n" + 
			"			\\<target ref=\"id7\"/>\r\n" + 
			"			\\<label kind=\"guard\" x=\"25\" y=\"0\">y == cycleTime\\</label>\r\n" + 
			"			\\<label kind=\"synchronisation\" x=\"25\" y=\"17\">cycle!\\</label>\r\n" + 
			"			\\<label kind=\"assignment\" x=\"25\" y=\"34\">y := 0\\</label>\r\n" + 
			"			\\<nail x=\"0\" y=\"51\"/>\r\n" + 
			"			\\<nail x=\"-51\" y=\"51\"/>\r\n" + 
			"			\\<nail x=\"-51\" y=\"0\"/>\r\n" + 
			"		\\</transition>\r\n" + 
			"	\\</template>\r\n");
	
	//name -> name of the template
	//var -> variable name used in template
	private static ST inputAnalogue = new ST("	\\<template>\r\n" + 
			"		\\<name x=\"5\" y=\"5\"><name>\\</name>\r\n" + 
			"		\\<declaration>// size of the array '<var>_values'\r\n" + 
			"const int size = 4;\r\n" + 
			"\r\n" + 
			"// array with selectable values for <var> input\r\n" + 
			"const int <var>_values[size] = {1024, 2048, 3072, 4096};\\</declaration>\r\n" + 
			"		\\<location id=\"id0\" x=\"-892\" y=\"-858\">\r\n" + 
			"		\\</location>\r\n" + 
			"		\\<init ref=\"id0\"/>\r\n" + 
			"		\\<transition>\r\n" + 
			"			\\<source ref=\"id0\"/>\r\n" + 
			"			\\<target ref=\"id0\"/>\r\n" + 
			"			\\<label kind=\"select\" x=\"-850\" y=\"-858\">i : int[0, size - 1]\\</label>\r\n" + 
			"			\\<label kind=\"assignment\" x=\"-850\" y=\"-841\"><var> := <var>_values[i]\\</label>\r\n" + 
			"			\\<nail x=\"-892\" y=\"-824\"/>\r\n" + 
			"			\\<nail x=\"-858\" y=\"-824\"/>\r\n" + 
			"			\\<nail x=\"-858\" y=\"-858\"/>\r\n" + 
			"		\\</transition>\r\n" + 
			"	\\</template>\r\n");
	
	//name -> name of the template
	//var -> variable name used in template
	private static ST inputDigital = new ST("	\\<template>\r\n" + 
			"		\\<name x=\"5\" y=\"5\"><name>\\</name>\r\n" + 
			"		\\<declaration>\\</declaration>\r\n" + 
			"		\\<location id=\"id0\" x=\"-739\" y=\"-331\">\r\n" + 
			"		\\</location>\r\n" + 
			"		\\<init ref=\"id0\"/>\r\n" + 
			"		\\<transition>\r\n" + 
			"			\\<source ref=\"id0\"/>\r\n" + 
			"			\\<target ref=\"id0\"/>\r\n" + 
			"			\\<label kind=\"assignment\" x=\"-680\" y=\"-323\"><var> := true\\</label>\r\n" + 
			"			\\<nail x=\"-722\" y=\"-289\"/>\r\n" + 
			"			\\<nail x=\"-688\" y=\"-289\"/>\r\n" + 
			"			\\<nail x=\"-688\" y=\"-331\"/>\r\n" + 
			"		\\</transition>\r\n" + 
			"		\\<transition>\r\n" + 
			"			\\<source ref=\"id0\"/>\r\n" + 
			"			\\<target ref=\"id0\"/>\r\n" + 
			"			\\<label kind=\"assignment\" x=\"-867\" y=\"-323\"><var> := false\\</label>\r\n" + 
			"			\\<nail x=\"-756\" y=\"-289\"/>\r\n" + 
			"			\\<nail x=\"-790\" y=\"-289\"/>\r\n" + 
			"			\\<nail x=\"-790\" y=\"-331\"/>\r\n" + 
			"		\\</transition>\r\n" + 
			"	\\</template>\r\n");
	
	//pins -> amount of pins
	private static ST ta_types_h = new ST("#ifndef TA_TYPES_H_\r\n" + 
			"#define TA_TYPES_H_\r\n" + 
			"\r\n" + 
			"#include \\<stdbool.h>\r\n" + 
			"\r\n" + 
			"#define PINS_SIZE <pins>\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"typedef struct {\r\n" + 
			"	int location;\r\n" + 
			"	int startTime;\r\n" + 
			"	int pins[PINS_SIZE];\r\n" + 
			"} Automaton;\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"#endif");
	
	
	private static ST ta_functions_h = new ST("#ifndef TA_FUNCTIONS_H_\r\n" + 
			"#define TA_FUNCTIONS_H_\r\n" + 
			"\r\n" + 
			"#include \"ta_types.h\"\r\n" + 
			"\r\n" + 
			"Automaton initAutomaton(int location, int startTime);\r\n" + 
			"\r\n" + 
			"void setAutomatonLocation(Automaton * automaton, int location);\r\n" + 
			"void setAutomatonLocationStartTime(Automaton * automaton, int startTime);\r\n" + 
			"\r\n" + 
			"#endif");
	
	
	private static ST ta_functions_c = new ST("#include \"ta_functions.h\"\r\n" + 
			"#include \\<stdlib.h>\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"Automaton initAutomaton(int location, int startTime){\r\n" + 
			"	Automaton automaton;\r\n" + 
			"	automaton.location = location;\r\n" + 
			"	automaton.startTime = startTime;\r\n" + 
			"	return automaton;\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"void setAutomatonLocation(Automaton * automaton, int location){\r\n" + 
			"	automaton->location = location;\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"void setAutomatonLocationStartTime(Automaton * automaton, int startTime){\r\n" + 
			"	automaton->startTime = startTime;\r\n" + 
			"}\r\n");
	
	//locationEnum -> locations of the automaton ex. : START = 0, 
	//pinEnum -> pins used by automaton ex. : PD12 = 0
	private static ST ta_model_h = new ST("#include \"ta_functions.h\"\r\n" +
			"#include \"ta_userCode.h\"\r\n" +
			"#include \\<stdbool.h>\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"<locationEnum>" + 
			"\r\n" + 
			"<pinEnum>\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"void initAutomatonByModel(int startTime);\r\n" + 
			"\r\n" + 
			"bool doTransition(int time);\r\n" + 
			"void newInput(int id, int value);\r\n" +
			"void doCycle();\r\n" + 
			"\r\n" + 
			"void doOutput(Automaton * automaton);\r\n" + 
			"void getInputDigital();\r\n" + 
			"void getInputAnalogue();" +
			"\r\n" +
			"void errorCase();");
	
	//variables -> variables used in automaton(user variables from UPPAAL xml)
	//setPins -> init pins used by the automaton 
	//doTransition -> if else tree to calculate the next location(calculate the next transition)
	//init -> init location
	private static ST ta_model_c = new ST("#include \"ta_model.h\"\r\n" + 
			"#include \"stm32f0xx_hal.h\"\r\n" +
			"#include \\<stdlib.h>\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"//variables\r\n" + 
			"Automaton automaton;\r\n" +
			"<variables>\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"//init functions\r\n" + 
			"\r\n" + 
			"void initAutomatonByModel(int startTime){\r\n" + 
			"	automaton = initAutomaton(<init>, startTime);\r\n" + 
			"\r\n" + 
			"	<setPins>\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"//automaton functions\r\n" + 
			"\r\n" + 
			"void doCycle(){\r\n" + 
			"	getInputDigital(&automaton);\r\n" + 
			"	getInputAnalogue(&automaton);\r\n" + 
			"\r\n" + 
			"	if (doTransition(HAL_GetTick() - automaton.startTime)) {\r\n" + 
			"		setAutomatonLocationStartTime(&automaton, HAL_GetTick());\r\n" + 
			"		doOutput(&automaton);\r\n" + 
			"	}\r\n" + 
			"}\r\n" +
			"\r\n" +
			"bool doTransition(int time){\r\n" + 
			"	int location = automaton.location;\r\n" + 
			"\r\n" + 
			"	switch (location){\r\n" +
			"	<doTransition>\r\n" +
			"	default : break;\r\n" +
			"	}\r\n" +
			"	return false;\r\n" +  
			"}\r\n" + 
			"\r\n" + 
			"void newInput(int id, int value){\r\n" + 
			"	automaton.pins[id] = value;\r\n" + 
			"}\r\n" +
			"\r\n" +
			"void errorCase(){\r\n" +
			"	automaton.location = ERROR_CASE;\r\n" +
			"	automaton.pins[LD2] = true;\r\n" +
			"	doOutput(&automaton);\r\n" +
			"}");
	
		//variables -> variables used in automaton(user variables from UPPAAL xml)
		//setPins -> init pins used by the automaton 
		//doTransition -> if else tree to calculate the next location(calculate the next transition)
		//init -> init location
		private static ST ta_model_c_Arduino = new ST("#include \"ta_model.h\"\r\n" + 
				"#include \\<stdlib.h>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"//variables\r\n" + 
				"Automaton automaton;\r\n" +
				"<variables>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"//init functions\r\n" + 
				"\r\n" + 
				"void initAutomatonByModel(int startTime){\r\n" + 
				"	automaton = initAutomaton(<init>, startTime);\r\n" + 
				"\r\n" + 
				"	<setPins>\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"//automaton functions\r\n" + 
				"\r\n" + 
				"void doCycle(){\r\n" + 
				"	getInputDigital(&automaton);\r\n" + 
				"	getInputAnalogue(&automaton);\r\n" + 
				"\r\n" + 
				"	if (doTransition((int)millis() - automaton.startTime)) {\r\n" + 
				"		setAutomatonLocationStartTime(&automaton, (int)millis());\r\n" + 
				"		doOutput(&automaton);\r\n" + 
				"	}\r\n" + 
				"}\r\n" +
				"\r\n" +
				"bool doTransition(int time){\r\n" + 
				"	int location = automaton.location;\r\n" + 
				"\r\n" + 
				"	switch (location){\r\n" +
				"	<doTransition>\r\n" +
				"	default : break;\r\n" +
				"	}\r\n" +
				"	return false;\r\n" +  
				"}\r\n" + 
				"\r\n" + 
				"void newInput(int id, int value){\r\n" + 
				"	automaton.pins[id] = value;\r\n" + 
				"}\r\n" +
				"\r\n" +
				"void errorCase(){\r\n" +
				"	automaton.location = ERROR_CASE;\r\n" +
				"}");


	//values -> values with name of the enum ex. : PD12 = 0,
	//name -> name of the enum
	private static ST enumST = new ST("typedef enum {\r\n" +
								"	<values>\r\n" +
								"} <name>;\r\n");
	
	//name -> name of the define
	//value -> value of it
	private static ST define = new ST("#define <name> <value>\r\n");
	
	//pin -> name of the pin
	//value -> init value for the pin
	private static ST initPin = new ST("automaton.pins[<pin>] = <value>; \r\n");
	
	//name -> name of the location
	//transitions -> transition-tree
	private static ST doTransition = new ST("case <name> :\r\n" + 
										"	<transitions>\r\n" + 
										"	break;\r\n");
	
	//case -> Transition-case
	//name -> name of the new location
	//updates -> update Events
	private static ST transitionTree = new ST("if(<case>){\r\n" +
										"	automaton.location = <name>;\r\n" +
										"	<updates>" +
										"	return true;\r\n" + 
										"}\r\n");
	
	
	private static ST ta_sensoresActors_h = new ST("#include \"main.h\"\r\n" +
			"#include \"stm32f0xx_hal.h\"\r\n\r\n");
	
	private static ST ta_sensoresActors_h_Arduino = new ST("#include \"ta_model.h\"\r\n\r\n");
	
	//doOut -> function doing output
	//getInDig -> function getting digital input
	//getInAn -> function getting analogue input
	private static ST ta_sensoresActors_c = new ST("#include \"ta_sensoresActors.h\"\r\n" + 
			"\r\n" + 
			"ADC_ChannelConfTypeDef sConfig = {0};\r\n" + 
			"ADC_HandleTypeDef hadc;\r\n" +
			"\r\n" +
			"void doOutput(Automaton * automaton){\r\n" + 
			"	<doOut>\r\n" +
			"}\r\n" + 
			"\r\n" + 
			"void getInputDigital(){\r\n" + 
			"	<getInDig>\r\n" + 
			"}\r\n" +
			"\r\n" +
			"void getInputAnalogue(){\r\n" +
			"	<getInAn>\r\n" +
			"}");
	
		//doOut -> function doing output
		//getInDig -> function getting digital input
		//getInAn -> function getting analogue input
		private static ST ta_sensoresActors_c_Arduino = new ST("#include \"ta_sensoresActors.h\"\r\n" + 
				"\r\n" +
				"void doOutput(Automaton * automaton){\r\n" + 
				"	<doOut>\r\n" +
				"}\r\n" + 
				"\r\n" + 
				"void getInputDigital(){\r\n" + 
				"	<getInDig>\r\n" + 
				"}\r\n" +
				"\r\n" +
				"void getInputAnalogue(){\r\n" +
				"	<getInAn>\r\n" +
				"}");
	
	//pin -> pin to check state
	private static ST doOutput = new ST("HAL_GPIO_WritePin(<pin>_GPIO_Port, <pin>_Pin, automaton->pins[<pin>]);\r\n");
	
	//pin -> pin in automaton
	private static ST getInputDig = new ST("newInput(<pin>, HAL_GPIO_ReadPin(<pin>_GPIO_Port, <pin>_Pin));\r\n");
	
	//timerInterrupzs -> interrupt functions for timers
	private static ST interruptFunction = new ST("void HAL_TIM_PeriodElapsedCallback(TIM_HandleTypeDef *htim) {\r\n" +
			"	<timerInterrupts>" +
			"}");
	
	//name -> name of the interrupt caller
	//event -> interrupt event
	private static ST interrupt = new ST("if (htim == &<name>) {\r\n" + 
			"	<event>" + 
			"}\r\n");
	
	private static ST cycleEvent = new ST("	if(!inCycle){\r\n" +
			"	newCycle = true;\r\n" +
			"}else {\r\n" +
			"	inErrorCase = true;\r\n" +
			"	newCycle = true;\r\n" +
			"}\r\n");
	
	//pin -> pin for input
	//chan -> channel of pin for adc
	private static ST getInputAn = new ST("	sConfig.Channel = <chan>;\r\n" + 
			"	  HAL_ADC_Start(&hadc);\r\n" + 
			"	  if(HAL_ADC_PollForConversion(&hadc, HAL_MAX_DELAY) == HAL_OK)\r\n" + 
			"		  newInput(<pin>, HAL_ADC_GetValue(&hadc));\r\n" + 
			"	  HAL_ADC_Stop(&hadc);\r\n");
	
	private static ST mainFnct = new ST("	if(newCycle && !inErrorCase) {\r\n" + 
			"		inCycle = true;\r\n" +
			"		doCycle();\r\n" +
			"		inCycle = false;\r\n" +
			"		newCycle = false;\r\n" +
			"	}else if(inErrorCase){\r\n" +
			"		errorCase();\r\n" +
			"	}\r\n");
	
	private static ST includes_main_h = new ST("#include \"ta_model.h\"\r\n");
	
	private static ST main_c_Inits = new ST("\tinitAutomatonByModel(HAL_GetTick());\r\n\tHAL_TIM_Base_Start_IT(&htim6);\r\n");
	
	//variables -> user variables
	//functions -> user functions
	private static ST ta_userCode_c = new ST("#include \"ta_userCode.h\"\r\n" +
			"\r\n" +
			"// Variables\r\n" +
			"<variables>\r\n" +
			"// Functions\r\n" +
			"<functions>");
	
	//functions -> function prototypes
	private static ST ta_userCode_h = new ST("#include \\<stdbool.h>\r\n\r\n\r\n" +
			"<functions>");
	
	//pins -> pin setup
	//cycleTime -> cylceTime from automaton
	private static ST mainArduino = new ST("extern \"C\"{\r\n" + 
			"#include \"ta_model.h\"\r\n" + 
			"};\r\n" + 
			"\r\n" + 
			"const uint16_t t1_load = 0;\r\n" + 
			"const uint16_t t1_comp = 0xF9;\r\n" + 
			"\r\n" + 
			"bool cycle = false;\r\n" +
			"bool inErrorCase = false;\r\n" +
			"int y = 0;\r\n" + 
			"const int cycleTime = <cycleTime>;\r\n" + 
			"\r\n" + 
			"void setup() {\r\n" + 
			"  // timer 1 setup as cycle timer with interrupts\r\n" + 
			"  TCCR1A = 0;\r\n" + 
			"  TCCR1B |= (1 \\<\\< CS12);    \r\n" + 
			"  TCCR1B &= ~(1 \\<\\< CS11);\r\n" + 
			"  TCCR1B &= ~(1 \\<\\< CS10);\r\n" + 
			"  TCNT1 = t1_load;\r\n" + 
			"  OCR1A = t1_comp;\r\n" + 
			"  TIMSK1 = (1 \\<\\< OCIE1A);\r\n" + 
			"  sei();\r\n" + 
			"\r\n" + 
			"  // setup pinmodes\r\n" + 
			"  <pins>" + 
			"\r\n" + 
			"  // init automaton\r\n" + 
			"  initAutomatonByModel((int)millis());\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"void loop() {\r\n" + 
			"  if(cycle && !inErrorCase) {\r\n" + 
			"    doCycle();\r\n" + 
			"    cycle = false;\r\n" + 
			"  }else if(inErrorCase){\r\n" + 
			"    errorCase();\r\n" + 
			"  }\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"// isr callback function\r\n" + 
			"ISR(TIMER1_COMPA_vect){\r\n" + 
			"  TCNT1 = t1_load;\r\n" + 
			"  y++;\r\n" + 
			"  if(y == cycleTime){\r\n" + 
			"    y = 0;\r\n" + 
			"    if(!cycle){\r\n" + 
			"      cycle = true;\r\n" + 
			"    }else {\r\n" + 
			"      inErrorCase = true;\r\n" + 
			"      cycle = true;\r\n" + 
			"    }\r\n" + 
			"  }\r\n" + 
			"}");
	
	// pin -> pin name of the pin (int)
	// usage -> usage of the pin (INPUT/OUTPUT)
	private static ST arduinoPinSetup = new ST("pinMode(<pin>,<usage>);");
	
	//-----------------------------Pins of the board------------------------------
	
	/**
	 * all digital input and output pins
	 */
	private static String pins[];
	
	/**
	 * all analogue input pins
	 */
	private static String pinsAnalogueIn[]; 
	
	//----------------------------------------------------------------------------
	
	/**
	 * Setup for StringTemplateHandler.
	 * Sets pins for a specific board.
	 * @param board target platform
	 */
	public static void setup(String board) {
		String settingsTXT = "";
		
		 try {
			URL url = Main.class.getResource("/resources/board_settings.txt");
			InputStream in = url.openStream();
			BufferedReader bfReader = new BufferedReader(new InputStreamReader(in));
			String l;
			while((l = bfReader.readLine()) != null) {
				settingsTXT += l + "\n";
			}
			in.close();
			bfReader.close();
			
			String lines[] = settingsTXT.split("\n");
			boolean inBoardArea = false;
			int inAreaCounter = 0;
			for (String line : lines) {
				
				if(inBoardArea) {
					if(inAreaCounter == 0) {
						pins = line.split(",");
						inAreaCounter++;
					}else if(inAreaCounter == 1) {
						pinsAnalogueIn = line.split(",");
					}else {
						break;
					}
				}
				
				if (board.equals("NUCLEO-F030R8") && line.contains("NUCLEO-F030R8")) {
					inBoardArea = true;
				}else if(board.equals("ArduinoUnoR3") && line.contains("ArduinoUnoR3")) {
					inBoardArea = true;
				} // add other boards here
		
			}
	     } 
		 catch (IOException e) {
	            System.err.println("ERROR : READ board_settings.txt FAILED");
	            System.exit(-1);
	     }
	}

	/**
	 * Builds a String from template by name and arguments
	 * @param name Name of the template
	 * @param args arguments for the template
	 * @return builded String by arguments 
	 */
	public static String getStringByTemplate(String name, String[] args) {
		
		ST template = null;
		
		switch(name) {
		
		case "helpManual" :
			template = helpManual;
			break;
		case "newautomaton" :
			template = newautomaton;
			template.remove("templates");
			template.remove("templatenames");
			template.remove("input");
			template.remove("output");
			template.add("templates", args[0]);
			template.add("templatenames", args[1]);
			template.add("input", args[2]);
			template.add("output", args[3]);
			break;
		case "Cycle" :
			template = cycle;
			break;
		case "inputAnalogue" : 
			template = inputAnalogue;
			template.remove("name");
			template.remove("var");
			template.add("name", args[0]);
			template.add("var", args[1]);
			break;
		
		case "inputDigital" :
			template = inputDigital;
			template.remove("name");
			template.remove("var");
			template.add("name", args[0]);
			template.add("var", args[1]);
			break;
		case "ta_types_h" :
			template = ta_types_h;
			template.remove("pins");
			template.add("pins", args[0]);
			break;
		case "ta_functions_h" :
			template = ta_functions_h;
			break;
		case "ta_functions_c" :
			template = ta_functions_c;
			break;
		case "ta_model_h" :
			template = ta_model_h;
			template.remove("locationEnum");
			template.remove("pinEnum");
			template.add("locationEnum", args[0]);
			template.add("pinEnum", args[1]);
			break;
		case "ta_model_c" :
			template = ta_model_c;
			template.remove("variables");
			template.remove("setPins");
			template.remove("doTransition");
			template.remove("init");
			template.add("variables", args[0]);
			template.add("setPins", args[1]);
			template.add("doTransition", args[2]);
			template.add("init", args[3]);
			break;
		case "ta_model_c_Arduino" :
			template = ta_model_c_Arduino;
			template.remove("variables");
			template.remove("setPins");
			template.remove("doTransition");
			template.remove("init");
			template.add("variables", args[0]);
			template.add("setPins", args[1]);
			template.add("doTransition", args[2]);
			template.add("init", args[3]);
			break;
		case "enumST" :
			template = enumST;
			template.remove("values");
			template.remove("name");
			template.add("values", args[0]);
			template.add("name", args[1]);
			break;
		case "define" :
			template = define;
			template.remove("values");
			template.remove("name");
			template.add("name", args[0]);
			template.add("value", args[1]);
			break;
		case "initPin" :
			template = initPin;
			template.remove("pin");
			template.remove("value");
			template.add("pin", args[0]);
			template.add("value", args[1]);
			break;
		case "doTransition" :
			template = doTransition;
			template.remove("name");
			template.remove("transitions");
			template.add("name", args[0]);
			template.add("transitions", args[1]);
			break;
		case "transitionTree" :
			template = transitionTree;
			template.remove("case");
			template.remove("name");
			template.remove("updates");
			template.add("case", args[0]);
			template.add("name", args[1]);
			template.add("updates", args[2]);
			break;
		case "ta_sensoresActors_h" :
			template = ta_sensoresActors_h;
			break;
		case "ta_sensoresActors_h_Arduino" :
			template = ta_sensoresActors_h_Arduino;
			break;
		case "ta_sensoresActors_c" :
			template = ta_sensoresActors_c;
			template.remove("doOut");
			template.remove("getInDig");
			template.remove("getInAn");
			template.add("doOut", args[0]);
			template.add("getInDig", args[1]);
			template.add("getInAn", args[2]);
			break;
		case "ta_sensoresActors_c_Arduino" :
			template = ta_sensoresActors_c_Arduino;
			template.remove("doOut");
			template.remove("getInDig");
			template.remove("getInAn");
			template.add("doOut", args[0]);
			template.add("getInDig", args[1]);
			template.add("getInAn", args[2]);
			break;
		case "doOutput" :
			template = doOutput;
			template.remove("pin");
			template.add("pin", args[0]);
			break;
		case "getInputDig" :
			template = getInputDig;
			template.remove("pin");
			template.add("pin", args[0]);
			break;
		case "getInputAn" :
			template = getInputAn;
			template.remove("pin");
			template.remove("chan");
			template.add("pin", args[0]);
			template.add("chan", args[1]);
			break;
		case "interruptFunction" :
			template = interruptFunction;
			template.remove("timerInterrupts");
			template.add("timerInterrupts", args[0]);
			break;
		case "interrupt" :
			template = interrupt;
			template.remove("name");
			template.remove("event");
			template.add("name", args[0]);
			template.add("event", args[1]);
			break;
		case "cycleEvent":
			template = cycleEvent;
			break;
		case "includes_main_h" :
			template = includes_main_h;
			break;
		case "mainFnct" :
			template = mainFnct;
			break;
		case "main_c_Inits" :
			template = main_c_Inits;
			break;
		case "ta_userCode_c" :
			template = ta_userCode_c;
			template.remove("variables");
			template.remove("functions");
			template.add("variables", args[0]);
			template.add("functions", args[1]);
			break;
		case "ta_userCode_h" :
			template = ta_userCode_h;
			template.remove("functions");
			template.add("functions", args[0]);
			break;
		case "mainArduino" :
			template = mainArduino;
			template.remove("pins");
			template.remove("cycleTime");
			template.add("pins", args[0]);
			template.add("cycleTime", args[1]);
			break;
		case "arduinoPinSetup" :
			template = arduinoPinSetup;
			template.remove("pin");
			template.remove("usage");
			template.add("pin", args[0]);
			template.add("usage", args[1]);
			break;
		
		default : return null;
		
		}
		
		return template.render();
	}
	
	
	public static String[] getPins() {return pins;}
	
	public static String[] getAnalogueInPins() {return pinsAnalogueIn;}
	

}
