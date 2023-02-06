#!/bin/bash

if [ $1 == "-dot" ]; then
   JAVA_HOME=/lib/jvm/java-17-openjdk/ ant build && /lib/jvm/java-17-openjdk/bin/java --enable-preview -cp bin Main $1 $2 out/tmp.dot && dot -Tpdf out/tmp.dot -o out/dot.pdf && firefox out/dot.pdf
else
	JAVA_HOME=/lib/jvm/java-17-openjdk/ ant build && /lib/jvm/java-17-openjdk/bin/java --enable-preview -cp bin Main $1 $2 "out/$3"	
fi
