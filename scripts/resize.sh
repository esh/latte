#!/bin/sh

for i in $(ls); do
	if [[ $i =~ [0-9]+ ]] 
	then
		for f in $(ls $i); do
			if [[ $f =~ o\. ]]
			then
				size=`identify -format "%w:%h" $i/$f`
				width=${size%:*}
				height=${size#*:}
				if [ $width -gt $height ]
				then
					`convert -geometry 370x $i/$f $i/p.jpg`
				else
					`convert -geometry x370 $i/$f $i/p.jpg`		
				fi
			fi
		done
	fi
done 


