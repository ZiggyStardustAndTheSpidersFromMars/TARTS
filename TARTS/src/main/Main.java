package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import automata.Automaton;
import converting.Converter;
import translating.Translator;
import translating.Validator;

/**
 * Thesis SS2020 TH Luebeck
 * Automatisierte Erzeugung von Programmcode aus Realzeitautomaten für eingebettete Echtzeitsysteme
 * 
 * TARTS :
 * Ein Tool fuer die Erzeugung von C Code aus UPPAAL Automatenmodellen
 * und das Ueberfuehren von Automaten in ein spezielles Schema.
 * TARTS steht fuer Timed automaton Real-Time Systems.
 * 
 * @author Torben Friedrich Goerner
 *
 */
public class Main {
	
	/**
	 * target platform
	 */
	private static String board;
	/**
	 * Flag for validating automaton scheme
	 */
	private static boolean validating;

	/**
	 * First argument(args[0]) mode :
	 * '-c' || '-convert' for covert mode
	 * '-t' || '-translate' for translate mode
	 * '-n' || '-new' to create a new automaton
	 * 'help' || 'h' || '-h' || '-help' for help instruction  
	 * 
	 * Second argument(args[1]) : arguments/options for the mode
	 * @param args for program start
	 */
	public static void main(String[] args) {
		readConfig();
		System.out.println("CONFIG : BOARD = " + board + " , VALIDATING = " + validating);
		StringTemplateHandler.setup(board);
		
		if(args.length == 3) {
			if(args[0].equals("-t") || args[0].equals("-translate")) {
				Automaton automaton = Automaton.fromXML(args[1]);
				if(validating ? Validator.validate(automaton) : true) {
					System.out.println("LOG : TRANSLATING STARTED");
					Translator.setup(board);
					Translator.translate(automaton, args[2]);
				}else {
					System.err.println("ERROR : automaton-SCHEME IS NOT VALID");
					System.out.println("LOG : TRANSLATION FAILED");
					System.exit(-1);
				}
			}else {
				System.err.println("ERROR : NO MATCH FOR " + args[0] + " OR WRONG NUMBER OF ARGUMENTS\n\tUSE '-help'");
				System.exit(-1);
			}
		}else if(args.length == 4) {
			if(args[0].equals("-n") || args[0].equals("-new")) {
				System.out.println("LOG : CREATING NEW UPPAAL AUTOMATON");
				NewProjectBuilder.createNewautomaton(args[1].toUpperCase(), args[2].toUpperCase(), args[3].toUpperCase());
			}else {
				System.err.println("ERROR : NO MATCH FOR " + args[0] + " OR WRONG NUMBER OF ARGUMENTS\n\tUSE '-help'");
				System.exit(-1);
			}
		}else if(args.length == 2) {
			if(args[0].equals("-c") || args[0].equals("-convert")) {
				System.out.println("LOG : COVERTING STARTED");
				Converter.convert(args[1]);
			}else {
				System.err.println("ERROR : NO MATCH FOR " + args[0] + " OR WRONG NUMBER OF ARGUMENTS\n\tUSE '-help'");
				System.exit(-1);
			}
		}else if(args.length == 1) {
			if(args[0].equals("help") || args[0].equals("h") || args[0].equals("-h") || args[0].equals("-help")) {
				System.out.println(StringTemplateHandler.getStringByTemplate("helpManual", null));
			}else {
				System.err.println("ERROR : NO MATCH FOR " + args[0] + "\n\tUSE '-help'");
				System.exit(-1);
			}
		}else {
			System.err.println("ERROR : NO MATCH FOR " + args[0] + " OR WRONG NUMBER OF ARGUMENTS\n\tUSE '-help'");
			System.exit(-1);
		}

	}
	
	/**
	 * reads the config file and sets the flags
	 */
	private static void readConfig() {
		String config = "";
		
		try {
			config = new String (Files.readAllBytes(Paths.get("config.txt"))).replaceAll("\r", "");
		} catch (IOException e) {
			System.err.println("ERROR : CONFIG FILE NOT FOUND");
			System.exit(-1);
		}
		
		if(!validateConfig(config)) { // config not valid
			System.out.println("ERROR : CONFIG NOT VALID");
			System.exit(-1);
		}else { // set configurations
			String lines[] = config.replaceAll(" ", "").split("\n");
			for(String line : lines) {
				if(line.contains("board=")) {
					board = line.replaceAll("board=","");
				}else if(line.contains("validating=")) {
					validating = Boolean.parseBoolean(line.replaceAll("validating=",""));
				}
			}
		}
	}
	
	/**
	 * Validates the config file
	 * @param content of the cofig.txt
	 * @return is valid 
	 */
	private static boolean validateConfig(String content) {
		boolean boardIsValid = false;
		boolean validatingIsValid = false;
		
		String lines[] = content.replaceAll(" ", "").split("\n");
		for(String line : lines) {
			if(line.contains("board=")) {
				if(line.replaceAll("board=","").equals("NUCLEO-F030R8") 
						|| line.replaceAll("board=","").equals("ArduinoUnoR3")) {
					
					boardIsValid = true;	
				}else {
					System.out.println("BOARD '" + line.replaceAll("board=", "") + "' UNKNOWN");
				}
			}else if(line.contains("validating=")) {
				if(line.replaceAll("validating=","").equals("true") || line.replaceAll("validating=","").equals("false")) {
					validatingIsValid = true;
				}else {
					System.out.println("VALIDATING = '" + line.replaceAll("board=", "") + "' UNKNOWN\r\nUSE 'true' or 'false'");
				}
			}
		}
		
		return boardIsValid && validatingIsValid;
	}

}
