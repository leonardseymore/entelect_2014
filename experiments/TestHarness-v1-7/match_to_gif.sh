#!/bin/bash

rm $1/replay/*.ppm
for file in $1/replay/*.state; do
	./state_to_ppm.sh $file > ${file%.*}.ppm;
done;

cd $1/replay
rename 's/(\d+)/sprintf"%04d",$1/ge' *.ppm
cd ~-
convert -delay 20 -loop 0 $1/replay/*.ppm $1/replay/replay.gif
