JAVA_HOME=/lib/jvm/java-17-openjdk/ ant build
for FILE in $(ls tests/*.c)
do
	echo TESTING $FILE
	java -cp bin Main $1 $FILE | tail -1
done
