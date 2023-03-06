#!/bin/bash

if [ $1 == "-dot" ]; then
	JAVA_HOME=/lib/jvm/java-17-openjdk/ ant build && /lib/jvm/java-17-openjdk/bin/java --enable-preview -cp bin Main $1 $2 out/tmp.dot && dot -Tpdf out/tmp.dot -o out/dot.pdf && firefox out/dot.pdf
elif [ $1 == "-naive" ]; then
	JAVA_HOME=/lib/jvm/java-17-openjdk/ ant build && /lib/jvm/java-17-openjdk/bin/java --enable-preview -cp bin Main -gen naive "tests/$2" "out/$3"
else
	JAVA_HOME=/lib/jvm/java-17-openjdk/ ant build && /lib/jvm/java-17-openjdk/bin/java --enable-preview -cp bin Main $1 "tests/$2" "out/$3"
fi
