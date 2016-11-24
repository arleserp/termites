#!/bin/sh
i=1
while [ $i -le 30 ]; do
    echo $i
    i=$(($i+1))
	

    java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.Runnable.AppMain 10 0.0001 49 51 20000 hybrid  graphson wallson 0.01 off 20 20 beal 1.0 badseqloc  nosaveLocation

	java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.Runnable.AppMain 10 0.0003 49 51 20000 hybrid  graphson wallson 0.01 off 20 20 beal 1.0 badseqloc  nosaveLocation

    java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.Runnable.AppMain 10 0.0005 49 51 20000 hybrid  graphson wallson 0.01 off 20 20 beal 1.0 badseqloc  nosaveLocation

    java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.Runnable.AppMain 10 0.0007 49 51 20000 hybrid  graphson wallson 0.01 off 20 20 beal 1.0 badseqloc  nosaveLocation

	java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.Runnable.AppMain 10 0.0009 49 51 20000 hybrid  graphson wallson 0.01 off 20 20 beal 1.0 badseqloc  nosaveLocation

	java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.Runnable.AppMain 10 0.001 49 51 20000 hybrid  graphson wallson 0.01 off 20 20 beal 1.0 badseqloc  nosaveLocation	
done
