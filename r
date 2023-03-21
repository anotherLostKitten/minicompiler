#!/bin/bash

if [ $1 == "-dot" ]; then
	JAVA_HOME=/lib/jvm/java-17-openjdk/ ant build && /lib/jvm/java-17-openjdk/bin/java --enable-preview -cp bin Main $1 $2 out/tmp.dot && dot -Tpdf out/tmp.dot -o out/dot.pdf && firefox out/dot.pdf
elif [ $1 == "-naive" ]; then
	JAVA_HOME=/lib/jvm/java-17-openjdk/ ant build && /lib/jvm/java-17-openjdk/bin/java --enable-preview -cp bin Main -gen naive "tests/$2" "out/$3" && java -jar desc/part3/Mars4_5.jar "out/$3"
elif [ $1 == "-colour" ]; then
	JAVA_HOME=/lib/jvm/java-17-openjdk/ ant build && /lib/jvm/java-17-openjdk/bin/java --enable-preview -cp bin Main -gen colour "tests/$2" "out/$3" && java -jar desc/part3/Mars4_5.jar "out/$3"
elif [ $1 == "-cfg" ]; then
	JAVA_HOME=/lib/jvm/java-17-openjdk/ ant build && /lib/jvm/java-17-openjdk/bin/java --enable-preview -cp bin Main $1 $2 out/tmp.dot && dot -Tpdf out/tmp.dot -o out/dot.pdf && firefox out/dot.pdf
elif [ $1 == "-infrg" ]; then
	JAVA_HOME=/lib/jvm/java-17-openjdk/ ant build && /lib/jvm/java-17-openjdk/bin/java --enable-preview -cp bin Main $1 $2 out/tmp.dot && dot -Tpdf out/tmp.dot -o out/dot.pdf && firefox out/dot.pdf
else
	JAVA_HOME=/lib/jvm/java-17-openjdk/ ant build && /lib/jvm/java-17-openjdk/bin/java --enable-preview -cp bin Main $1 "tests/$2" "out/$3"
fi
