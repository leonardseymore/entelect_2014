#!/bin/bash
echo P2
echo 19 22
echo 6
sed 's/ /0 /g;s/#/1 /g;s/\./2 /g;s/*/3 /g;s/A/4 /g;s/B/5 /g' $1
