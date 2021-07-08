# TuringInterpreter
A basic turing machine interpreter written in Java (Eclipse project)

Created for GHP 58 final math project. Meant for evaluating Busy Beaver turing machines: https://catonmat.net/busy-beaver

Disclaimer: If I did something illegal, sorry, it was an accident. Tell me and I'll take down the repo.

### How to write turing machine rules:

Create a new turing machine file (.bbtm extension) and open it in the text editor of your choice. small.bbtm (BB(3) code) and schult.bbtm (BB(5) lower bound as of 1982) are included.

A bbtm file looks like this:\
1R2_1RH\
0R3_1R2\
1L3_1L1


Each line is a rule/state, starting with state 1 (default state). The three characters to the left of the underscore are executed when the turing machine head reads 0, and the three to the right are executed when 1 is read. (The tape starts on all 0s)

Looking at the three-character blocks, the first digit is the digit written to the tape in the head's location. Leave this blank to not write anything (this is not allowed in the commonly-accepted busy beaver convention).

The second character is the direction to move the head in. L moves left, R moves right, and N does not move (again, this is not normally allowed unless transitioning to halt state immediately after)

The third character is the state to transition to after moving. H transitions to halt state, stopping the machine.

### How to use the program to visualize turing machines:

When running the program, you can either use config.json to specify your configuration, or (if config.json does not exist) manually type your configuration.

In config.json:

"inputPath" is the filename of the turing machine rules. File extension is not required.\
"runSteps" is the number of steps to take before automatically halting. If 0, runs until halting or OOM.\
"createImage" is whether or not to create a PNG image of the tape history.\
"outputPath" is the filename for the PNG image. File extension is not required.\
"clamp" is whether or not to shrink the image to save filesize. Useful on anything above BB(4). Also shrinks tape history when printed as text.\
"clampHeight" is the height of the image after shrinking it if "clamp" is set to true.\
"printHaltContext" is whether or not to print the branches of instructions leading up to a halt state.\
"printTapeHistory" is whether or not to print an ASCII representation of the tape history.\
"printRealTimeSteps" is whether or not to print an in-depth view of each step as the machine runs./
"printRules" is whether or not to print the rules.
