#!/bin/sh
i=1
while [ $i -le 30 ]; do
    echo $i
    i=$(($i+1))
    java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.Runnable.AppMain 10 0.001 49 51 -1 levywalk  graphson wallson 0.01 off mazeoff 1.0 nomazerandomLoc  nosaveLocation
done
