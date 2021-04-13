#!/bin/sh
JAVA_VERSION=`java -fullversion 2>&1 | awk -F[\"\.] '{print $2$3$4}' |awk -F"_" '{print $1}'`
if [ $JAVA_VERSION -lt 180 ]; then
	echo "Error: Java version should >= 1.8.0 "
    exit 1
fi

cd `dirname $0`
DIR=`pwd`

JAVA_OPTS="-Djava.net.preferIPv4Stack=true -server -Xms1g -Xmx1g -XX:MetaspaceSize=128m"

JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n "
fi

CLIENT_CLASS='net.jkcode.jkbenchmark.orm.jkorm.JkormBenchmarkPlayer'

java $JAVA_OPTS $JAVA_DEBUG_OPTS -cp $DIR/conf:$DIR/libs/* net.jkcode.jkbenchmark.BenchmarkApp $CLIENT_CLASS