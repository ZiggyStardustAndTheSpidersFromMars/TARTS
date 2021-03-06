# TARTS

DOWNLOAD TARTS : https://github.com/ZiggyStardustAndTheSpidersFromMars/TARTS/releases/download/1.0/TARTS_1.0.zip

TARTS is a code synthesis tool for timed automata by UPPAAL definition to generate code for embedded real-time systems like the arduino uno. 
The tool uses timed automata from UPPAAL so one can investigate and verify an automaton in UPPAAL and furthermore translate it directly into 
real code for an embedded real-time system. TARTS can only translate a subset with a specific scheme from UPAAL. Automata that are not part 
of aforementioned subset can possibly be converted and furthermore translated using TARTS. One can use TARTS to start a new UPPAAL project 
from an input/output configuration as well. In the following the utilisation of TARTS is described.
 
TARTS was developed as a part of a bachelor thesis at TH Lübeck by Torben Friedrich Görner. 
For questions and comments please regard to: torben.goerner@stud.th-luebeck.de


HELP :

You need a config.txt file to use the TARTS.jar. 
You can generate Code for the following boards:
- ArduinoUnoR3
- NUCLEO-F030R8

config.txt :
    board = <board name>
    validating = <true/false>
  
Usage   : java -jar TARTS.jar [-options] [args]

Options :

    -c or -convert          covert a timed automaton to a specifie scheme
    
    -t or -translate        translate a timed automaton into C code
    
    -n or -new              create a new automaton
    
    -h or -help             help manual / operating instructions
    
Arguments :

    -convert             arg1 - path to automaton .xml
    
    -translate           arg1 - path to automaton .xml
    
    -                    arg2 - path to project folder
    
    -new                 arg1 - digital input pins. Seperated by ','
    
    -                    arg2 - analogue input pins. Seperated by ','
    
    -                    arg3 - output pins. Seperated by ','
    
   
TIMED AUTOMATA :

Use UPPAAL to build and verify your project. UPPAAL projects have to be modeled in 
by an specified scheme. You can start a new UPPAAL project by using TARTS -n / -new 
function. 

Scheme :

- only one clock, must be reseted with every transition. 
- all transitions must be synchronised with channel "cycle ?".
- only one user automaton in an automata network with input automata and a cycle automaton.
 (input and cycle automaton can be generated with -n/-new).
- commited, urgent and sync. is not supported. (sync only with chan "cycle")  


How to verify :

An embedded real-time system in UPPAAL is an automata network with an User automaton (control),
input automata (environment) and a cycle automaton to clock the system (heartbeat). The input automata
will not be translated into program code. These automata are only for verification.

Get UPPAAL : http://www.uppaal.org/
