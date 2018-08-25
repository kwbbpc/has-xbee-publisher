
This project requires RXTXComm.jar to work.  It uses the 2.2pre1 version.

You'll need to pass the path to the .so or .dll files to the jvm as:
-Djava.library.path="D:\src\has\rxtx"

on linux, you'll need permissions to the USB port, /dev/ttyUSB0.  To do this, typically you just need to add yourself to
the dialout group (assuming dialout group has permissions on the usb port). Failing that, you have to make a udev
rule, which blows.

* Building the project
mvn clean package

The jar file will be packed into target.  This jar has everything needed to run except for the OS dependent RXTX
drivers.  This will need to be passed as an argument to the jvm as -Djava.library.path="/usr/lib/jni"

Once you pass this in, you can start the program as follows, subbing in wherever your rxtx .so files are at.:

java -Djava.library.path="/usr/lib/jni" -jar has-service-1.0-SNAPSHOT.jar