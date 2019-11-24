#!/bin/sh
cd `dirname $0`
DIR=`pwd`

JAVA_OPTS="-Djava.net.preferIPv4Stack=true -server -Xms1g -Xmx1g -XX:PermSize=128m"

JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi

CLIENT_NAME='net.jkcode.jkbenchmark.orm.JkormBenchmarkPlayer'

java $JAVA_OPTS $JAVA_DEBUG_OPTS -cp $DIR/conf:$DIR/libs/* net.jkcode.jkbenchmark.BenchmarkApp $CLIENT_NAME