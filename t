JAVA_HOME=/lib/jvm/java-17-openjdk/ ant build
for FILE in $(ls tests/*.c)
do
	echo TESTING $FILE
	/lib/jvm/java-17-openjdk/bin/java --enable-preview -cp bin Main $1 $FILE "out/out_$(basename $FILE)" | tail -1
done
