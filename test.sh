#!/bin/bash
i=10

until [ $i -lt 1 ]; do
	python Project_2_GenerateInput.py 10
	java -jar wfong_p2.jar 10 > /dev/null
	OUTPUT="$(python Grade.py)"
	echo "${OUTPUT}"
	if [[ $OUTPUT == *ERROR*  ]]
	then
		exit
	fi
	let i-=1
done
