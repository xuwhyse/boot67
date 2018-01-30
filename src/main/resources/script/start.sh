#!/usr/bin/env bash
if [ -s SERVER_PID ]; then
PID=`cat SERVER_PID`
kill -9 $PID
fi

sleep 5

JAVA_OPTS="-server -XX:+HeapDumpOnOutOfMemoryError -XX:+UseG1GC  -Xms2g -Xmx2g -D=antsc"
java ${JAVA_OPTS} -cp .:lib/*   antsc.Application > log.log 2>&1 &
echo $! > SERVER_PID