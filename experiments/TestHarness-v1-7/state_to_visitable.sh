#!/bin/bash
sed 's/[ \.AB\*]/1 /g; s/#/0 /g' $1
