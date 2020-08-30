# TARTS

Tarts is a tool which generates code for a embedded real-time system. You can use it to
work with timed automata from UPPAAL in your projects. It is possibile to auto-generate 
code for your system (Arduino Uno or NUCLEO) by a UPPAAL project. You can also generate a new
UPPAAL project, by a input/output configuration, build your automaton and verify it in UPPAAL.

HELP :

You need a config.txt file to use the TARTS.jar. 
You can generate Code for :
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
