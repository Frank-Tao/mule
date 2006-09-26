#! /bin/sh

# There is no need to call this if you set the MULE_HOME in your environment
if [ -z "$MULE_HOME" ] ; then
  MULE_HOME=../../..
  export MULE_HOME
fi

# Any changes to the files in ./conf will take precedence over those deployed to $MULE_HOME/lib/user
MULE_LIB=./conf
export MULE_LIB

echo "The Hello example is available in three variations:"
echo "  1. Simple Configuration"
echo "  2. Spring-based Configuration"
echo "  3. Receive events via HTTP"
echo "Select the one you wish to execute and press Enter..."
read i

if [ 1 = $i ]
then
    exec $MULE_HOME/bin/mule -config ./conf/hello-config.xml
elif [ 2 = $i ]
then
    exec $MULE_HOME/bin/mule -config ./conf/hello-spring-config.xml -builder spring
elif [ 3 = $i ]
then
    exec $MULE_HOME/bin/mule -config ./conf/hello-http-config.xml
fi
