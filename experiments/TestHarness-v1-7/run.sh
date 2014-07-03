#!/bin/bash
rm -rf Match*
./PacManDuel.exe $PWD/a leonard $PWD/b leonard
find . -name match.log -exec cat {} \;
