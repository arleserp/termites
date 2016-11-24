#!/bin/sh
i=1
while [ $i -le 30 ]; do
    echo $i
    i=$(($i+1))
    java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.Runnable.AppMain 10 0 49 51 20000 hybrid  graphson wallson 0.01 off 20 20 beal 1.0 badseqloc  nosaveLocation
	
    java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.Runnable.AppMain 10 0.0001 49 51 20000 hybrid  graphson wallson 0.01 off 20 20 beal 1.0 badseqloc  nosaveLocation

	java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.Runnable.AppMain 10 0.00015 49 51 20000 hybrid  graphson wallson 0.01 off 20 20 beal 1.0 badseqloc  nosaveLocation

    java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.Runnable.AppMain 10 0.0002 49 51 20000 hybrid  graphson wallson 0.01 off 20 20 beal 1.0 badseqloc  nosaveLocation

    java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.Runnable.AppMain 10 0.00025 49 51 20000 hybrid  graphson wallson 0.01 off 20 20 beal 1.0 badseqloc  nosaveLocation	
done
