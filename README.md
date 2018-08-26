
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


#Running the Service at startup on a Raspberry Pi
To run the jar as a service on startup, you'll add a script to the etc/init.d directory to launch the jar.
 - create a new file at /etc/init.d/has-publisher
 - add the following lines to the top of the file
 ```
  #!/bin/bash
  # /etc/init.d/sample.py
   ### BEGIN INIT INFO
   # Provides:          sample.py
   # Required-Start:    $remote_fs $syslog
   # Required-Stop:     $remote_fs $syslog
   # Default-Start:     2 3 4 5
   # Default-Stop:      0 1 6
   # Short-Description: Start daemon at boot time
   # Description:       Enable service provided by daemon.
  ### END INIT INFO
 ```
 These lines tell the pi to treat the service as an LSB (Linux Standard Base)
 
 The script below is the script for the publisher.  It assumes the proper raspberry pi rxtx libraries have been copied to /usr/lib/jni/rxtx:
 
 ```
### BEGIN INIT INFO
# Provides:          has-publisher
# Required-Start:
# Required-Stop:
# Should-Start:
# Default-Start:     2 3 4 5
# Default-Stop:
# Short-Description: HAS Publisher Service
# Description:       Intended to start has publisher on system startup.
### END INIT INFO
    
    
SERVICE_NAME=has-publisher
PATH_TO_JAR=/opt/has/has-xbee-publisher-service-1.0-SNAPSHOT.jar
PID_PATH_NAME=/opt/has/has-publisher-pid
case $1 in 
    start)
        echo "Starting $SERVICE_NAME ..."
        if [ ! -f $PID_PATH_NAME ]; then
            nohup java -Djava.library.path="/usr/lib/jni/rxtx" -jar $PATH_TO_JAR /tmp 2>> /dev/null >> /dev/null &
                           echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is already running ..."
        fi
    ;;
    stop)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stoping ..."
            kill $PID;
            echo "$SERVICE_NAME stopped ..."
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
    restart)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ...";
            kill $PID;
            echo "$SERVICE_NAME stopped ...";
            rm $PID_PATH_NAME
            echo "$SERVICE_NAME starting ..."
            nohup java -jar $PATH_TO_JAR /tmp 2>> /dev/null >> /dev/null &
                        echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
esac
```