#!/bin/sh

if [ "$JAVA_HOME" != "" ]; then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA=java
fi

if [ "x$JAVA_OPTS" = "x" ]; then
    JAVA_OPTS="-server -XX:+PrintGCDetails -Xloggc:gc.out -XX:+UseParallelGC -XX:ParallelGCThreads=10 -Xmx2000m -Xms2000m -Xmn200m -Xss1024k"
fi


KILL=kill

PIDFILE='bitch.pid'
MAIN='com.bi7.bitch.BitchApplication'
_DAEMON="bitch.out"
CLASSPATH=.:$CLASSPATH:target/bitch-1.0-SNAPSHOT.jar:conf/

case $1 in
start)

    echo "Starting server process in background."
    if [ -f $PIDFILE ]; then
      if kill -0 `cat $PIDFILE` > /dev/null 2>&1; then
         echo $command already running as process `cat $PIDFILE`.
         exit 0
      fi
    fi
    nohup $JAVA -cp "$CLASSPATH" $JAVA_OPTS $MAIN > "$_DAEMON" 2>&1 < /dev/null &
    if [ $? -eq 0 ]
    then
      if /bin/echo -n $! > "$PIDFILE"
      then
        sleep 1
        echo STARTED
      else
        echo FAILED TO WRITE PID
        exit 1
      fi
    else
      echo SERVER DID NOT START
      exit 1
    fi
    
    ;;
restart)

    shift
    "$0" stop ${@}
    sleep 3
    "$0" start ${@}
    ;;
stop)

    echo -n "Stopping server ... "
    if [ ! -f "$PIDFILE" ]
    then
        echo "no server to stop (could not find file $PIDFILE)"
    else
        $KILL -9 $(cat "$PIDFILE")
        rm "$PIDFILE"
        echo STOPPED
    fi
    ;;

start-foreground)

    $JAVA -cp "$CLASSPATH" $JAVA_OPTS $MAIN
    ;;
print-cmd)

    echo $JAVA -cp "$CLASSPATH" "$JAVA_OPTS" $MAIN
    ;;
*)
    echo "Usage: $0 {start|start-foreground|stop|restart|print-cmd}" >&2
esac
