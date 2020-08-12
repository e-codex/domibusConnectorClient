#!/bin/sh

#
# This scripts assumes that java executable is on the PATH variable!
#
#

# building the Classpath
CLASSPATH=`/usr/bin/pwd`'/bin/*':`/usr/bin/pwd`'/lib/*'
echo "CLASSPATH = $CLASSPATH"

java -cp $CLASSPATH -Dspring.config.location=config/connector-client.properties org.springframework.boot.loader.PropertiesLauncher