#! /bin/sh

JAVA_PATH="/home/jaillot/bin/jdk-9.0.4/bin/java"

IDEA_PATH="/home/jaillot/bin/idea-IC-173.4674.33/lib/idea_rt.jar=37387:/home/jaillot/bin/idea-IC-173.4674.33/bin -Dfile.encoding=UTF-8"

CLASS_PATH="/home/jaillot/IdeaProjects/gamebojML/out/production/gamebojML"

$JAVA_PATH -javaagent:$IDEA_PATH -Dfile.encoding=UTF-8 -classpath $CLASS_PATH ch.epfl.gameboj.DebugMain "01-special.gb" 30000000

$JAVA_PATH -javaagent:$IDEA_PATH -Dfile.encoding=UTF-8 -classpath $CLASS_PATH ch.epfl.gameboj.DebugMain "02-interrupts.gb" 30000000

$JAVA_PATH -javaagent:$IDEA_PATH -Dfile.encoding=UTF-8 -classpath $CLASS_PATH ch.epfl.gameboj.DebugMain "03-op sp,hl.gb" 30000000

$JAVA_PATH -javaagent:$IDEA_PATH -Dfile.encoding=UTF-8 -classpath $CLASS_PATH ch.epfl.gameboj.DebugMain "04-op r,imm.gb" 30000000

$JAVA_PATH -javaagent:$IDEA_PATH -Dfile.encoding=UTF-8 -classpath $CLASS_PATH ch.epfl.gameboj.DebugMain "05-op rp.gb" 30000000

$JAVA_PATH -javaagent:$IDEA_PATH -Dfile.encoding=UTF-8 -classpath $CLASS_PATH ch.epfl.gameboj.DebugMain "06-ld r,r.gb" 30000000

$JAVA_PATH -javaagent:$IDEA_PATH -Dfile.encoding=UTF-8 -classpath $CLASS_PATH ch.epfl.gameboj.DebugMain "07-jr,jp,call,ret,rst.gb" 30000000

$JAVA_PATH -javaagent:$IDEA_PATH -Dfile.encoding=UTF-8 -classpath $CLASS_PATH ch.epfl.gameboj.DebugMain "08-misc instrs.gb" 30000000

$JAVA_PATH -javaagent:$IDEA_PATH -Dfile.encoding=UTF-8 -classpath $CLASS_PATH ch.epfl.gameboj.DebugMain "09-op r,r.gb" 30000000

$JAVA_PATH -javaagent:$IDEA_PATH -Dfile.encoding=UTF-8 -classpath $CLASS_PATH ch.epfl.gameboj.DebugMain "10-bit ops.gb" 30000000

$JAVA_PATH -javaagent:$IDEA_PATH -Dfile.encoding=UTF-8 -classpath $CLASS_PATH ch.epfl.gameboj.DebugMain "11-op a,(hl).gb" 30000000

$JAVA_PATH -javaagent:$IDEA_PATH -Dfile.encoding=UTF-8 -classpath $CLASS_PATH ch.epfl.gameboj.DebugMain "instr_timing.gb" 30000000
