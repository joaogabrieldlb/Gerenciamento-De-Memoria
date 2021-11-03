mkdir bin
javac -d bin ./src/*.java
cd bin
jar -cfe ../OS.jar OS .