#!/bin/sh
rsync	--verbose --progress --stats --compress --rsh=/usr/bin/ssh \
	--recursive --times --perms --links --delete \
	--exclude "bin/*" --exclude "latte.properties" --exclude "src/*" --exclude "public/blog/*" \
	--exclude "scripts/*" --exclude "build.xml" --exclude "log/*" --exclude "db" --exclude "LICENSE" \
	* www@edomame.com:~/latte/
