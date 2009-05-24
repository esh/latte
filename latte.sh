#!/bin/sh

case "$1" in
start)
	echo "starting latte"
	nohup java -server -Xms16m -Xmx16m -server -classpath lib/latte.jar:lib/jetty-6.1.12rc1.jar:lib/jetty-util-6.1.12rc1.jar:lib/servlet-api-2.5-6.1.12rc1.jar:lib/commons-pool-1.4.jar:lib/commons-beanutils-1.8.0.jar:lib/commons-logging-1.1.1.jar:lib/log4j-1.2.15.jar:lib/js.jar:lib/mailapi.jar:lib/imap.jar org.latte.Main &

	echo $! > .pid
	;;

stop)
	echo "stopping latte"
	cat .pid | xargs kill
;;
esac
exit 0