#!/bin/bash

# This bash file is used to generated nested directories for testing purposes

year="2019"

for month in {01..12}; do
	echo "${year} ${month}"
	for day in {01..30}; do
		for count in {01..10}; do
			DIRECTORY="${1}${year}/${month}/${day}"
			mkdir -p ${DIRECTORY}
			for lineNumber in {1..10}; do
				echo "${year} ${month} ${day} ${count} Line:${lineNumber}" >> ${DIRECTORY}/${count}
			done;
			gzip ${DIRECTORY}/${count}
		done;
	done;
done;

