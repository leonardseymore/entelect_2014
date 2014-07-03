#!/bin/bash
echo P3
echo 19 27
echo 256
sed 's/ /0 0 0 /g;s/#/64 64 64 /g;s/\./127 127 127 /g;s/*/196 196 196 /g;s/A/0 0 255 /g;s/B/0 255 0 /g' $1
cat footer.ppm.part
