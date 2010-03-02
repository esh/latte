#!/bin/sh

java -classpath lib/latte.jar:lib/jetty-6.1.12rc1.jar:lib/jetty-util-6.1.12rc1.jar:lib/servlet-api-2.5-6.1.12rc1.jar:lib/commons-pool-1.4.jar:lib/commons-beanutils-1.8.0.jar:lib/commons-codec-1.4.jar:lib/js.jar:app org.latte.Run $@
